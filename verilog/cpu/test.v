
`include "../lib/assertion.v"

`default_nettype none

`include "../pulseGenerator/pulseGenerator.v"
`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../control/control_selector.v"
`include "../control/control_decode.v"
`include "../alu/alu.v"
`include "../pc/pc.v"
`include "../7474/hct7474.v"
`include "../74377/hct74377.v"
`include "../74574/hct74574.v"
`include "../registerFile/registerFile.v"

`timescale 1ns/1ns

module test();

    event RESET_DONE;

    logic CP=1'b0;
    wire _CP = ! CP; // EXTRA GATE

    tri0 [7:0] data_bus; // <<<<<<<<<<<<<<<<<<PULLED DOWN

    // PULSED -ve ON +ve CLOCK EDGE 
    logic clk_en=1;
    wire _CP_pulse; // short -ve pulse on each +CP edge
    pulseGenerator gen( CP, clk_en, _CP_pulse);

    // PROGRAM COUNTER 
    logic _POWER_ON_RESET=1'b0;
    logic _MR;
    logic _PCLOin = 1'b1;
    logic _PCHIin = 1'b1;
    logic _PCHITMPin = 1'b1;
    wire [7:0] PCHI, PCLO;

    // ROM IO
    logic [14:0] rom_address;
    tri [7:0] rom_hi_data;
    tri [7:0] rom_lo_data;
    
    // RAM addressing
    logic [15:0] ram_address;
    wire [7:0] MARHI, MARLO;

    // CONTROL LINES
    logic _flag_z;
    logic _flag_c;
    logic _flag_o;
    logic _flag_eq;
    logic _flag_ne;
    logic _flag_gt;
    logic _flag_lt;
    logic _uart_in_ready;
    logic _uart_out_ready;

    logic _rom_out;
    logic _ram_out;
    logic _alu_out;
    logic _uart_out;
    logic [4:0] device_in;
        
    logic _ram_in;
    logic _marlo_in;
    logic _marhi_in;
    logic _uart_in;
    logic _pchitmp_in;
    logic _pclo_in;
    logic _pc_in;       // load both

    logic _reg_in;
    logic _ram_zp;

    // REGISTERS:
    // A-D registers at device address 16+[0-3]
    // NB: _reg_in=!device_in[4]

    wire _regfile_in = _reg_in || device_in[3] || device_in[2];
    wire [1:0] regfile_wr_addr = device_in[1:0];
    wire [1:0] rdL_addr = regfile_wr_addr;
    wire [1:0] rdR_addr = rom_lo_data[1:0];
    wire _rdL_en = 1'b0;
    wire _rdR_en = 1'b0;
    wire [7:0] rdL_data; // TODO send to ALU
    wire [7:0] rdR_data; // TODO send to ALU

    registerFile regABCD(
        ._wr_en(_regfile_in), 
        .wr_addr(regfile_wr_addr),
        .wr_data(data_bus),

        ._rdL_en,
        .rdL_addr,
        .rdL_data,

        ._rdR_en,
        .rdR_addr,
        .rdR_data
    );


    

    // ALU ================================================================
    logic [7:0] x = 8'b11111110;
    logic [7:0] y = 8'b00000011;
    logic [4:0] alu_op;
    logic [12*8:0] alu_op_name;
    wire  [7:0] alu_result;
    logic force_alu_op_to_passx;
    logic force_x_val_to_zero;
    wire _flag_cin, _flag_cout;

    // RESET CIRCUIT

    // names: https://assets.nexperia.com/documents/data-sheet/74HC_HCT74.pdf
    wire _RESET, RESET;
    hct7474 #(.BLOCKS(1), .NAME("RESETFF (sensitivity = _CP)"), .LOG(1)) resetFF(
      ._SD(1'b1),
      ._RD(_MR),
      .D(1'b1),
      .CP(_CP),
      .Q(_RESET),
      ._Q(RESET)
    );


    // COMPONENTS
    //pulldown databusPullDown[7:0](data_bus);

    // program counter =====================================================
    //always @(*)
    //    $display("%6d CPU ", $time, "_MR=%1b _RESET=%1b =============================", _MR, _RESET);

    always @(posedge _CP) begin
        if (!_RESET) $display(">>>>>>>>>>>>>>>>>> RESET RELEASED <<<<<<<<<<<<<<<<<<<<<<<<<");
    end

    pc PC (
      .CP(_CP), // INVERSE CP - PC ADVANCE ON -Ve CP
      ._MR(_RESET),
      ._PCHIin(_PCHIin),
      ._PCLOin(_PCLOin),
      ._PCHITMPin(_PCHITMPin),
      .D(data_bus),

      .PCLO(PCLO),
      .PCHI(PCHI)
    );

    // rom =====================================================
    assign rom_address = { PCHI[6:0], PCLO };
    rom #(.AWIDTH(15), .Filename("hi.rom")) rom_hi(._CS(1'b0), ._OE(1'b0), .A(rom_address), .D(rom_hi_data));
    rom #(.AWIDTH(15), .Filename("lo.rom")) rom_lo(._CS(1'b0), ._OE(1'b0), .A(rom_address), .D(rom_lo_data));

    hct74245 #(.LOG(0), .NAME("ROMBUS")) bufROMBUS(.A(rom_lo_data), .B(data_bus), .dir(1'b1), .nOE(_rom_out));

    // ram =====================================================
    wire _gated_marlo_in = _marlo_in || CP; 
    wire _gated_marhi_in = _marhi_in || CP; 

    hct74574 MAR_lo(.CLK(_gated_marlo_in), ._OE(1'b0), .D(data_bus), .Q(MARLO));
    hct74574 MAR_hi(.CLK(_gated_marhi_in), ._OE(1'b0), .D(data_bus), .Q(MARHI));

    wire [7:0] muxed_marhi;
    hct74245 #(.NAME("ZPHi")) bufRAMZPHi(.A(MARHI), .B(muxed_marhi), .dir(1'b1), .nOE(!_ram_zp));
    pulldown MARHIPullDown[7:0](muxed_marhi);

    wire [7:0] muxed_marlo;
    hct74245 #(.NAME("ZPLoMAR")) bufRAMZPLoMAR(.A(MARLO), .B(muxed_marlo), .dir(1'b1), .nOE(!_ram_zp)); // EXTRA GATE
    hct74245 #(.NAME("ZPLoROM")) bufRAMZPLoROM(.A(rom_lo_data), .B(muxed_marlo), .dir(1'b1), .nOE(_ram_zp));

    assign ram_address = { muxed_marhi[6:0], muxed_marlo };

    always @(*) begin
        $display("%8d ", $time, "RAM: MARHI %2x MARLO %2x MuxMARHI %02x MuxMARLO %02x _ZP %1b", MARHI, MARLO, muxed_marhi, muxed_marlo, _ram_zp);
    end

    ram #(.AWIDTH(16)) Ram(._WE(_ram_in), ._OE(_ram_out), .A(ram_address), .D(data_bus));

    // control =====================================================
    control_selector #(.LOG(0)) CtrlSelect(
        .hi_rom(rom_hi_data), 

        ._rom_out, ._ram_out,	._alu_out, ._uart_out,
        .force_alu_op_to_passx, .force_x_val_to_zero,
        ._ram_zp,
        .device_in
    );
 
    control_decode #(.LOG(0)) CtrlDecode(
        .device_in,
        ._flag_z, ._flag_c, ._flag_o, ._flag_eq, ._flag_ne, ._flag_gt, ._flag_lt,
        ._uart_in_ready, ._uart_out_ready,

       ._pulse_clk(_CP_pulse), 

        ._ram_in, ._marlo_in, ._marhi_in, ._uart_in, ._pchitmp_in, ._pclo_in, ._pc_in, ._reg_in
    );

    // alu ==========================================================
    assign alu_op = { rom_hi_data[0], rom_lo_data[7:4] };

    alu #(.LOG(0)) Alu(
        .alu_op,
        .o(alu_result), .x, .y,
        .force_alu_op_to_passx,
        .force_x_val_to_zero,
        ._flag_cin, 
        ._flag_cout,
        .OP_OUT(alu_op_name)

    );

    hct74245 #(.LOG(0), .NAME("ALUBUS")) bufALUBUS(.A(alu_result), .B(data_bus), .dir(1'b1), .nOE(_alu_out));
    if (0) always @* begin
        $display("%8d ", $time, "ALU: RESULT %2x _alu_out %1b BUS %2x", alu_result, _alu_out, data_bus);
    end


    // flags =====================================================
    logic [7:0] flags_reg_in, flags_reg_out;
    wire _flags_in = _alu_out;
    hct74377 flags_reg(._EN(_flags_in), .CP, .D(flags_reg_in), .Q(flags_reg_out));

    always @(flags_reg_out)
        $display("%8d FLAGS ", $time, "ZCOENGL=%8b" , flags_reg_out[6:0]);
    
    assign flags_reg_in = {2'b11, _flag_cout, 5'b11111};
    assign {_flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt} = flags_reg_out[6:0];
    assign _flag_cin = flags_reg_out[5]; // wire to ALU


    // rules =====================================================

    always @* begin
        assert (^data_bus !== 'X)
        else $error("Detected potential contention on data bus");
    end


    // THREADS ...

    `define LOG_CPU \
        $display("%8d CPU ", $time, \
            "CP=%1b PC=x%4x   ROM=%8b,%8b ", CP, rom_address, rom_hi_data, rom_lo_data, \
            " BUS=h%2x ", data_bus,  \
            " RRAU=%4b ", {_rom_out, _ram_out, _alu_out, _uart_out}, \
            "   ZCOENGL=%7b UIO=%2b ", \
                {_flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt}, \
                {_uart_in_ready, _uart_out_ready}, \
            "   ALUOP=%-s X=%03d Y=%03d R=%03d _CIN=%1b _COUT=%1b   FPassX=%1b FX2Z=%1b ",  \
                alu_op_name, x,y,alu_result, _flag_cin, _flag_cout, Alu.force_alu_op_to_passx, Alu.force_x_val_to_zero, \
            " _ZP=%1b", _ram_zp, \
            " RAM=h%02x MAR=h%02x%02x RAMAddr=h%4x", Ram.D, MARHI, MARLO, ram_address,\
            " - " \
        );
        
    always @* `LOG_CPU

    `define CLOCK_DOWN(MSG) \
        #1000  \
        $write(MSG); \
        CP = 0; \
        if (CP == 0)  \
            $display("%8d CPU CLK=%1b       ----------------", $time, CP); \
        else \
            $display("%8d CPU CLK=%1b       ++++++++++++++++", $time, CP); \

    `define CLOCK_UP(MSG) \
        #1000  \
        $write(MSG); \
        CP = 1; \
        if (CP == 0)  \
            $display("%8d CPU CLK=%1b       ----------------", $time, CP); \
        else \
            $display("%8d CPU CLK=%1b       ++++++++++++++++", $time, CP); \


    `define CLOCK_CYCLE(XXX) CLOCK_UP("++ XXX") \
                        CLOCK_DOW("-- XXX")N
/*
    always begin
        `LOG_CPU

        `CLOCK_UP    
        `CLOCK_DOWN    
    end
*/
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );

        // WANT PC RESET TO OCCUR DURING THE LOW PHASE SO THAT THE RESULT IS LATCHED ON THE NEXT +VE
        CP = 0;
        _MR = _POWER_ON_RESET;

        #1000 
        $display("\nMR timeout");
        _MR=1'b1; // release reset after delay

        `CLOCK_UP("\ninitial +ve ignored\n")
        `CLOCK_DOWN("\ninitial -ve resets PC and clears RESET latch\n")


        `CLOCK_UP("\ninstruction 1 latched\n")

        `CLOCK_DOWN("\ninstruction 2 start\n")
/*
        `CLOCK_UP("\ninitial +ve ignored\n")
        `CLOCK_DOWN("\ninitial -ve resets PC and clears RESET latch\n")
*/
        /*

        `CLOCK_UP    

        `CLOCK_DOWN    
        `CLOCK_UP    
*/

        #700  $finish; 
    end

endmodule

