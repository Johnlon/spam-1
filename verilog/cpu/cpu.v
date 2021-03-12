// FIXME = don't set flags if instruction doesn't exec

// FIXME: Add random number generator - eg use an unused device as a readonly source - connect it to a 8 bit counter running at an arbitraty speed
// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "../cpu/controller.v"
`include "../reset/reset.v"
`include "../registerFile/syncRegisterFile.v"
`include "../pc/pc.v"
`include "../lib/assertion.v"
`include "../74245/hct74245.v"
`include "../74573/hct74573.v"
`include "../7474/hct7474.v"
`include "../74139/hct74139.v"
`include "../74377/hct74377.v"
`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../alu/alu.v"
`include "../uart/um245r.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

`define SEMICOLON ;
`define COMMA ,

`define MAX_INST_LEN 200
typedef reg[`MAX_INST_LEN:0][7:0] string_bits;
typedef reg[15:0] reg16;

// "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
module cpu(
    input _RESET_SWITCH,
    input system_clk
);

    parameter LOG=0;
    
    tri0 [15:0] address_bus;
    tri0 [7:0] abus; // when NA device is selected we don't want Z going into ALU sim as this is not a value so we get X out
    tri0 [7:0] bbus;
    tri [7:0] alu_result_bus;
    wire [2:0] bbus_dev, abus_dev;
    wire [3:0] targ_dev;
    wire [4:0] alu_op;
    wire [7:0] _registered_flags_czonGLEN;
    wire _flag_di;
    wire _flag_do;
    wire _set_flags;

    wire _mrPC;

    wire _phaseExec;
    wire phaseExec;

    reset RESET(
        .system_clk,
        ._RESET_SWITCH,
        ._phase_clk(_phaseExec),
        .phase_clk(phaseExec),
        ._mrNeg(_mrPC)
    );



    // CONTROL ===========================================================================================
    wire _addrmode_register;

    // selection wires
    `define WIRE_ADEV_SEL(DNAME) wire _adev_``DNAME``
    `define WIRE_BDEV_SEL(DNAME) wire _bdev_``DNAME``
    `define WIRE_TDEV_SEL(DNAME) wire _``DNAME``_in

    `CONTROL_WIRES(WIRE, `SEMICOLON);

    `define BIND_ADEV_SEL(DNAME) ._adev_``DNAME``
    `define BIND_BDEV_SEL(DNAME) ._bdev_``DNAME``
    `define BIND_TDEV_SEL(DNAME) ._``DNAME``_in

    wire [7:0] PCHI, PCLO; // output of PC
    wire [15:0] pc_addr = {PCHI, PCLO}; 

    // ROM =============================================================================================
    controller ctrl(
        .pc(pc_addr),
        ._flags_czonGLEN(_registered_flags_czonGLEN),
        ._flag_di, ._flag_do,

        ._addrmode_register, 
        `CONTROL_WIRES(BIND, `COMMA),
        .address_bus,
        .bbus,
        .alu_op,
        .bbus_dev, .abus_dev, .targ_dev,
        ._set_flags
    );

    

    // PROGRAM COUNTER ======================================================================================
    wire #(8) _long_jump = _pc_in; // FIXME - need to include _do_Exec somehow

    // PC reset is sync with +ve edge of clock
    pc #(.LOG(0))  PC (
        //.clk(clk),
        .clk(system_clk),
        ._MR(_mrPC),
        ._long_jump(_long_jump),  // load both
        ._local_jump(_pclo_in), // load lo
        ._pchitmp_in(_pchitmp_in), // load tmp
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    // RAM =============================================================================================

// verilator lint_off PINMISSING
    wire #(8) _gated_ram_in = _phaseExec | _ram_in;
    ram #(.AWIDTH(16), .LOG(0)) ram64(._WE(_gated_ram_in), ._OE(1'b0), .A(address_bus)); // OK to leave _OE enabled as ram data sheet makes WE override it
// verilator lint_on PINMISSING
    
`ifndef verilator
    // verilator complains about tristate
    hct74245 ram_alubus_buf(.A(alu_result_bus), .B(ram64.D), .nOE(_ram_in), .dir(1'b1));
`endif
    hct74245 ram_bbus_buf(.A(ram64.D), .B(bbus), .nOE(_bdev_ram), .dir(1'b1));

    // MAR =============================================================================================
// verilator lint_off PINMISSING
    // clocks data in as we enter phase exec - on the +ve edge - so use positive logic phaseExec here
    hct74377 #(.LOG(0)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(0)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));
// verilator lint_on PINMISSING

    hct74245 marlo_abus_buf(.A(MARLO.Q), .B(abus), .nOE(_adev_marlo), .dir(1'b1)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245 marlo_bbus_buf(.A(MARLO.Q), .B(bbus), .nOE(_bdev_marlo), .dir(1'b1)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245 marhi_abus_buf(.A(MARHI.Q), .B(abus), .nOE(_adev_marhi), .dir(1'b1)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245 marhi_bbus_buf(.A(MARHI.Q), .B(bbus), .nOE(_bdev_marhi), .dir(1'b1)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245 #(.LOG(0)) marhi_addbbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register), .dir(1'b1));
    hct74245 #(.LOG(0)) marlo_addbbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register), .dir(1'b1));

    // ALU ==============================================================================================
    wire _flag_c_out, _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out;
    wire _flag_c, _flag_z, _flag_n, _flag_o, _flag_gt, _flag_lt, _flag_eq, _flag_ne;

	alu #(.LOG(0)) Alu(
        .o(alu_result_bus), 
        .a(abus),
        .b(bbus),
        .alu_op(alu_op),
        ._flag_c_in(_flag_c),
        ._flag_c(_flag_c_out),
        ._flag_z(_flag_z_out),
        ._flag_o(_flag_o_out),
        ._flag_n(_flag_n_out),
        ._flag_gt(_flag_gt_out),
        ._flag_lt(_flag_lt_out),
        ._flag_eq(_flag_eq_out),
        ._flag_ne(_flag_ne_out)
    );

    wire gated_flags_clk;
    nor #(9) gating( gated_flags_clk , _phaseExec , _set_flags);

    wire [7:0] alu_flags_czonGLEN = {_flag_c_out , _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out};

    hct74574 #(.LOG(LOG)) status_register_czonGLEN( .D(alu_flags_czonGLEN),
                                       .Q(_registered_flags_czonGLEN),
                                        .CLK(gated_flags_clk), 
                                        ._OE(1'b0)); 

    assign {_flag_c, _flag_z, _flag_o, _flag_n, _flag_gt, _flag_lt, _flag_eq, _flag_ne} = _registered_flags_czonGLEN;

    // REGISTER FILE =====================================================================================
    // INTERESTING THAT THE SELECTION LOGIC DOESN'T CONSIDER REGD - THIS SIMPLIFIED VALUE DOMAIN CONSIDERING ONLY THE FOUR ACTIVE LOW STATES NEEDS JUST THIS SIMPLE LOGIC FOR THE ADDRESSING
    // NOTE !!!! THIS CODE USES _phaseExec AS THE REGFILE GATING MEANING _WE IS LOW ONLY ON SECOND PHASE OF CLOCK - THIS PREVENTS A SPURIOUS WRITE TO REGFILE FROM IT'S INPUT LATCH
    wire #(2*8) _gated_regfile_in = _phaseExec | (_rega_in & _regb_in & _regc_in & _regd_in);
    wire #(8) _regfile_rdL_en = _adev_rega &_adev_regb &_adev_regc &_adev_regd ;
    wire #(8) _regfile_rdR_en = _bdev_rega &_bdev_regb &_bdev_regc &_bdev_regd ;
    wire [1:0] regfile_rdL_addr = abus_dev[1:0];
    wire [1:0] regfile_rdR_addr = bbus_dev[1:0];
    wire [1:0] regfile_wr_addr = targ_dev[1:0];

    if (LOG) begin
        always @* $display("regfile _gated_regfile_in = ", _gated_regfile_in, " wr addr  ", regfile_wr_addr, " in : a=%b b=%b c=%b d=%b " , _rega_in , _regb_in , _regc_in , _regd_in);
        always @* $display("regfile _regfile_rdL_en   = ", _regfile_rdL_en, " rd addr  ", regfile_rdL_addr, " in : a=%b b=%b c=%b d=%b " , _adev_rega , _adev_regb , _adev_regc , _adev_regd);
        always @* $display("regfile _regfile_rdR_en   = ", _regfile_rdR_en, " rd addr  ", regfile_rdR_addr, " in : a=%b b=%b c=%b d=%b " , _bdev_rega , _bdev_regb , _bdev_regc , _bdev_regd);
    end


    // clocks data in as we enter phase exec - on the +ve edge - so use positive logic phaseExec here
    syncRegisterFile #(.LOG(LOG)) regFile(
        .clk(phaseExec), // only on the execute phase edge otherwise we will clock in results during fetch and decode and act more like a combinatorial circuit
        ._wr_en(_gated_regfile_in), // only enabled for input during the execute phase 
        .wr_addr(regfile_wr_addr),
        .wr_data(alu_result_bus),
        
        ._rdL_en(_regfile_rdL_en),
        .rdL_addr(regfile_rdL_addr),
        .rdL_data(abus),
        
        ._rdR_en(_regfile_rdR_en),
        .rdR_addr(regfile_rdR_addr),
        .rdR_data(bbus)
    );


    // UART =============================================================
    wire #(10) _gated_uart_wr = _uart_in | _phaseExec;   // sync clock data into uart - must occur AFTER uart_alubuf_buf has been enabled

    wire [7:0] uart_d;

    um245r #(.LOG(LOG*2))  uart (
        .D(uart_d),
        .WR(_gated_uart_wr),// Writes data on -ve edge
        ._RD(_adev_uart),	// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
        ._TXE(_flag_do),	// When high do NOT write data using WR, when low write data by strobing WR
        ._RXF(_flag_di)		// When high to NOT read from D, when low then data is available to read by strobing RD low
      );

    hct74245 uart_alubus_buf(.A(alu_result_bus), .B(uart_d), .nOE(_uart_in), .dir(1'b1));
    hct74245 uart_abus_buf(.A(uart_d), .B(abus), .nOE(_adev_uart), .dir(1'b1));



    always @(pc_addr) begin
        if ($isunknown(pc_addr)) begin // just check leftmost but as this is part of the op and is mandatory
            $display ("%9t ", $time,  "CPU HALT");
            $error("CPU : ERROR - UNKNOWN PC %4h", pc_addr); 
            `FINISH_AND_RETURN(1);
        end
        if ($isunknown(ctrl.rom_6.Mem[pc_addr][7])) begin // just check leftmost but as this is part of the op and is mandatory
            $display ("%9t ", $time,  "CPU HALT");
            $error("CPU : END OF PROGRAM - NO CODE FOUND AT PC %4h = %8b", pc_addr, ctrl.rom_6.Mem[pc_addr][7]); 
//            `FINISH_AND_RETURN(1);
        end
    end
    always @(negedge system_clk) begin
        if (_mrPC && $isunknown(alu_result_bus)) begin // just check leftmost but as this is part of the op and is mandatory
            $display ("%9t ", $time,  "CPU HALT");
            $error("CPU : ERROR - ALU RESULT BUS UNDEFINED AT EXECUTE = %8b", alu_result_bus); 
            `FINISH_AND_RETURN(1);
        end
    end


    int halt_code;
    always @(negedge system_clk) begin
        if (_halt_in == 0) begin
            halt_code = (CPU.MARHI.Q  << 8) + CPU.MARLO.Q;

            // leave space around the code as Verification.scala looks for them to extract the value
            $display("----------------------------- HALTED %1d h:%04x ---------------------------", halt_code, 16'(halt_code));
            $finish();
        end
    end


endmodule : cpu
