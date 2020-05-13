/*
24 bit instruction

*/
`include "../lib/assertion.v"

`default_nettype none

`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../control/control_selector.v"
`include "../control/control_decode.v"
`include "../alu/alu.v"
`include "../pc/pc.v"
`include "../7474/hct7474.v"
`include "../74377/hct74377.v"
`include "../uart/um245r.v"
`include "../registerFile/syncRegisterFile.v"

`timescale 1ns/1ns

module test();

`include "../alu/aluopName.v"
`include "devName.v"
`include "disassembler.v"

    event RESET_DONE;

    logic CP=1'b0;
    wire _CP = ! CP; // EXTRA GATE

    tri [7:0] data_bus; // <<<<<<<<<<<<<<<<<<PULLED DOWN


    // PROGRAM COUNTER 
    logic _POWER_ON_RESET=1'b0;
    logic _MR;
    wire [7:0] PCHI, PCLO;

    // ROM IO
    logic [15:0] rom_address;
    tri [7:0] rom_hi_data;
    tri [7:0] rom_lo_data;
    
    // RAM addressing - AT27C512R 64kx8 or AT27C256 32kx8
    logic [15:0] ram_address;
    wire [7:0] MARHI, MARLO;

    // CONTROL LINES
    logic _flag_z;
    logic _flag_nz; // FIXME IMPLEMENT ME PLEASE - OR PERHAPS IMPLEMENT ALL INVERSES???
    logic _flag_c;
    logic _flag_o;
    logic _flag_eq;
    logic _flag_ne;
    logic _flag_gt;
    logic _flag_lt;
    logic _uart_in_ready;
    logic _uart_out_ready;

    logic _rom_r_out;
    logic _ram_r_out;
    logic _uart_l_out;

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

    wire #11 _regfile_in = _reg_in || device_in[3] || device_in[2];
    wire [1:0] regfile_wr_addr = device_in[1:0];
    wire [1:0] rdL_addr = regfile_wr_addr;
    wire [1:0] rdR_addr = rom_lo_data[1:0];
    wire _rdL_en = 1'b0;
    wire _rdR_en = 1'b0;
    wire [7:0] rdL_data;
    wire [7:0] rdR_data;

    wire [1:0] waddr;
    assign #11 waddr = device_in[3] || device_in[2];

    wire #11 _gated_regfile_in = _regfile_in;

    /*
    always @(*) begin
        $display("!!!!! _reg_in %1b waddr %d _regfile_in %1b  regfile_wr_addr %d  regfile_wr_data %1x", _reg_in, waddr, _regfile_in, regfile_wr_addr, data_bus);
    end
    */

    syncRegisterFile #(.LOG(0)) registerFileABCD(
        ._MR(_MR),
        .CP(CP),
        //._wr_en(_gated_regfile_in),
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
    wire [7:0] x = rdL_data;
    wire [7:0] y = rdR_data;
    logic [4:0] alu_op;
    logic [8*8:0] alu_op_name;
    logic force_alu_op_to_passx;
    logic force_x_val_to_zero;
    wire _flag_z_alu, _flag_c_alu, _flag_o_alu, _flag_eq_alu, _flag_ne_alu, _flag_gt_alu, _flag_lt_alu;
    wire  [7:0] alu_result;

    // RESET CIRCUIT

    // Quote : "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
    wire _RESET, RESET;
    hct7474 #(.BLOCKS(1), .NAME("RESETFF (sensitivity = _CP)"), .LOG(0)) resetFF(
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

    always @(posedge CP) begin
        if (!_RESET) $display("\n>>>>>>>>>>>>>>>>>> RESET RELEASED <<<<<<<<<<<<<<<<<<<<<<<<<\n");
    end
    
    pc #(.LOG(0)) PC (
      .CP(CP), // CP - Latch registers 
      ._CP(_CP), // INVERSE CP - PC ADVANCE ON -Ve CP
      ._MR(_RESET),
      ._pc_in,
      ._pclo_in,
      ._pchitmp_in,
      .D(data_bus),

      .PCLO(PCLO),
      .PCHI(PCHI)
    );

    // uart =====================================================
    um245r #(.HEXMODE(0),.LOG(1)) ioDevice(.D(data_bus), .WR(_uart_in), ._RD(_uart_out), ._TXE(_uart_out_ready), ._RXF(_uart_in_ready));

    // rom =====================================================
    assign rom_address = { PCHI[6:0], PCLO };
    rom #(.AWIDTH(15), .Filename("hi.rom")) rom_hi(._CS(1'b0), ._OE(1'b0), .A(rom_address), .D(rom_hi_data));
    rom #(.AWIDTH(15), .Filename("lo.rom")) rom_lo(._CS(1'b0), ._OE(1'b0), .A(rom_address), .D(rom_lo_data));

    hct74245 #(.LOG(0), .NAME("ROMBUS")) bufROMBUS(.A(rom_lo_data), .B(data_bus), .dir(1'b1), .nOE(_rom_out));

    // control =====================================================
    control_selector #(.LOG(0)) CtrlSelect(
        .hi_rom(rom_hi_data), 

        ._rom_out, ._ram_out,	._alu_out, ._uart_out,
        .force_alu_op_to_passx, .force_x_val_to_zero,
        ._ram_zp,
        .device_in
    );
 
    control_decode #(.LOG(1)) CtrlDecode(
        .device_in,
        ._flag_z, ._flag_c, ._flag_o, ._flag_eq, ._flag_ne, ._flag_gt, ._flag_lt,
        ._uart_in_ready, ._uart_out_ready,

        ._ram_in, ._marlo_in, ._marhi_in, ._uart_in, ._pchitmp_in, ._pclo_in, ._pc_in, ._reg_in
    );

    // ram =====================================================
    wire #11 _gated_marlo_in = _marlo_in;
    wire #11 _gated_marhi_in = _marhi_in;

    hct74377 MAR_lo(._EN(_gated_marlo_in), .CP(CP),  .D(data_bus), .Q(MARLO));
    hct74377 MAR_hi(._EN(_gated_marhi_in), .CP,      .D(data_bus), .Q(MARHI));

    tri0 [7:0] muxed_marhi; // PULLDOWN 
    hct74245 #(.NAME("ZPHi")) bufRAMZPHi(.A(MARHI), .B(muxed_marhi), .dir(1'b1), .nOE(!_ram_zp)); // EXTRA GATE

    wire [7:0] muxed_marlo;
    hct74245 #(.NAME("ZPLoMAR")) bufRAMZPLoMAR(.A(MARLO),       .B(muxed_marlo), .dir(1'b1), .nOE(!_ram_zp)); // EXTRA GATE
    hct74245 #(.NAME("ZPLoROM")) bufRAMZPLoROM(.A(rom_lo_data), .B(muxed_marlo), .dir(1'b1), .nOE(_ram_zp));

    assign ram_address = { muxed_marhi, muxed_marlo };

    always @(*) begin
        $display("%9t ", $time, "CPU: MARHI %2x MARLO %2x Effective MARHI %02x Effective MARLO %02x _ZP %1b   _gatedMARLOin=%1b   _gatedMARHIin=%1b  ", MARHI, MARLO, muxed_marhi, muxed_marlo, _ram_zp, _gated_marlo_in, _gated_marhi_in);
    end

    ram #(.AWIDTH(16), .LOG(1)) Ram(._WE(_ram_in), ._OE(_ram_out), .A(ram_address), .D(data_bus));


    // alu ==========================================================
    assign alu_op = { rom_hi_data[0], rom_lo_data[7:4] };

    alu #(.LOG(0)) Alu(
        .alu_op,
        .o(alu_result), .x, .y,
        .force_alu_op_to_passx,
        .force_x_val_to_zero,
        ._flag_cin(_flag_c), 
        ._flag_cout(_flag_c_alu),
        ._flag_z(_flag_z_alu),
        .OP_OUT(alu_op_name)
    );

    hct74245 #(.LOG(0), .NAME("ALUBUS")) bufALUBUS(.A(alu_result), .B(data_bus), .dir(1'b1), .nOE(_alu_out));
    if (0) always @* begin
        $display("%9t ", $time, "ALU: RESULT %2x _alu_out %1b BUS %2x", alu_result, _alu_out, data_bus);
    end


    // flags =====================================================
    wire _flags_in = _alu_out;
    wire [7:0] flags_reg_out;
    assign { _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt} = flags_reg_out[6:0];
    wire [7:0] flags_reg_in = {1'bx, _flag_z_alu, _flag_c_alu, _flag_o_alu, _flag_eq_alu, _flag_ne_alu, _flag_gt_alu, _flag_lt_alu};

    hct74377 flags_reg(._EN(_flags_in), .CP, .D(flags_reg_in), .Q(flags_reg_out));

    always @(*)
        $display("%9t FLAGS ", $time, " FLAGS OUT ZCOENGL=%7b" , flags_reg_out[6:0], "   FLAGS IN ZCOENGL=%7b" , flags_reg_in[6:0]);
    
//    assign _flag_cin = flags_reg_out[5]; // wire to ALU


    // rules =====================================================
    integer LOG=0;

           // " _regfile_in=%1b ", _regfile_in, \
           // " _gated_regfile_in=%1b ", _gated_regfile_in, \
    `define LOG_CPU \
        if (LOG) $display("%9t CPU (%1d) : ", $time, cpcount, \
            "CP=%1b PC=x%4x => ROM=%8b,%8b", CP, rom_address, rom_hi_data, rom_lo_data, \
            " BUS=%8b", data_bus,  \
            " RRAU=%4b", {_rom_out, _ram_out, _alu_out, _uart_out}, \
            " DEVIN=%2d", device_in, \
            " _reg_in=%1b ", _reg_in, \
            " _pcX_in(hitmp=%1b", _pchitmp_in, \
            " lo=%1b ", _pclo_in, \
            " pc=%1b)", _pc_in, \
            " ZCOENGL=%7b IO=%1b%1b", \
               {_flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt}, \
               _uart_in_ready, _uart_out_ready, \
            "  ALUOP=%-s X=%08b Y=%08b R=%08b _C(IN=%1b, OUT=%1b) FPassX=%1b FX2Z=%1b ",  \
               alu_op_name, x,y,alu_result, _flag_c, _flag_c_alu, Alu.force_alu_op_to_passx, Alu.force_x_val_to_zero, \
            " ALU-ZCOENGL=%7b ", \
               {_flag_z_alu, _flag_c_alu, _flag_o_alu, _flag_eq_alu, _flag_ne_alu, _flag_gt_alu, _flag_lt_alu}, \
            " RAMD=h%02x RAMA=h%4x", Ram.D, ram_address\
        );


    always @* begin
        //assert (^data_bus !== 'x)
        assert (
            data_bus[7] !== 1'bx ||
            data_bus[6] !== 1'bx ||
            data_bus[5] !== 1'bx ||
            data_bus[4] !== 1'bx ||
            data_bus[3] !== 1'bx ||
            data_bus[2] !== 1'bx ||
            data_bus[1] !== 1'bx ||
            data_bus[0] !== 1'bx
         )
        else begin
            if (LOG) $error("Detected potential contention on data bus\n");
            //`LOG_CPU
            //$display("--------\n");
            //$finish_and_return(1); 
        end
    end

    wire [3:0] rrau = {_rom_out, _ram_out, _alu_out, _uart_out};

    integer contentionStart=0;
    integer took;
    always @* begin
        took = $time - contentionStart;

        assert (rrau == 4'b1111 || rrau == 4'b1110 || rrau == 4'b1101 || rrau == 4'b1011 || rrau == 4'b0111) begin
            if (contentionStart != 0 && took > 11) begin
                $error("RESOLVED  %d ns Contention on data control lines %4b            !!!!!!!!!!!!!!!!!!!!!!!!\n", took, rrau);
            end
            contentionStart=0;
        end
        else begin
            if (contentionStart != 0) begin
                // ns units ONLY if units for this files are ns
                if (took > 20 /*ns*/) begin 
                    $error("LONG %d ns Contention on data control lines %4b            !!!!!!!!!!!!!!!!!!!!!!!!\n", took, rrau);
                    $finish_and_return(1); 
                end else begin
                    $error("ONGOING %d ns Contention on data control lines %4b            !!!!!!!!!!!!!!!!!!!!!!!!\n", took, rrau);
                end
            end 
            else begin
                //$error("DETECTED Contention on data control lines %4b            !!!!!!!!!!!!!!!!!!!!!!!!\n", rrau);
                contentionStart=$time;
            end
        end
    end



    // THREADS ...
    integer cpcount=0;

    localparam CYCLE_TIME=1000;

    `define CLOCK_DOWN(MSG) \
        #CYCLE_TIME  \
        cpcount = cpcount + 1; \
        if (LOG) $write("\n%-5d begin %-s\n", cpcount, MSG); \
        if (CP == 1)  begin \
            if (LOG) $display("%9t CPU  CLK=%1b       ------------------------------------------------   ---------------- \n", $time, CP); \
        end \
        CP = 0;

       // disassambler(rom_hi_data, rom_lo_data); 
    `define CLOCK_UP(MSG) \
        #CYCLE_TIME  \
        if (LOG) $write("\n%-5d latched %-s\n", cpcount, MSG); \
        if (CP == 0)  begin \
            if (LOG) $display("%9t CPU  CLK=%1b       ++++++++++++++++++++++++++++++++++++++++++++++++   ++++++++++++++++ \n", $time, CP); \
        end \
        CP = 1;


    `define CLOCK_CYCLE(XXX) \
            `CLOCK_UP("++ XXX") \
            `CLOCK_DOWN("-- XXX")

    always @* begin
        disassambler(rom_hi_data, rom_lo_data);
    end


/*
    always begin
        `LOG_CPU

        `CLOCK_UP    
        `CLOCK_DOWN    
    end
*/
    
        
    always @* `LOG_CPU

    always @(rom_hi_data) begin
        if(rom_hi_data === 8'bxxxxxxxx) begin
            $display("  <<<<<<<<<<<<<<< BREAKOUT - ROMHI=X >>>>>>>>>>>>>>>>>>>>>");
            $finish; 
        end
    end



    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );

        // WANT PC RESET TO OCCUR DURING THE LOW PHASE SO THAT THE RESULT IS LATCHED ON THE NEXT +VE
        CP = 0;
        _MR = _POWER_ON_RESET;

        #CYCLE_TIME 
        $display("\nMR timeout");
        _MR=1'b1; // release reset after delay

        `CLOCK_UP("initial +ve ignored")

        `CLOCK_DOWN("1 - first operation   -  -ve resets PC and clears RESET latch and starts program")
        `CLOCK_UP("")

        `CLOCK_DOWN("2---- ")
        `CLOCK_UP("")
/*

        `CLOCK_DOWN("3---- ")
        `CLOCK_UP("")

        `CLOCK_DOWN("4---- ")
        `CLOCK_UP("")


        `CLOCK_DOWN("")
        `CLOCK_UP("")

        `CLOCK_DOWN("")
        `CLOCK_UP("uart=1")

        `CLOCK_DOWN("")
        `CLOCK_UP("inc a")

        `CLOCK_DOWN("")
        `CLOCK_UP("uart=2")
        // run to end of rom

        `CLOCK_DOWN("")
        `CLOCK_UP("")

*/
        while(rom_hi_data !== 8'bxxxxxxxx) begin
            `CLOCK_DOWN("")
            `CLOCK_UP("")
        end

        #10000  

        $display("  <<<<<<<<<<<<<<< BREAK END OF CLOCK >>>>>>>>>>>>>>>>>>>>>");
        $display("  ");

        #1000  $finish; 
    end

endmodule

