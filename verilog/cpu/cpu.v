// FIXME: Add random number generator - eg use an unused device as a readonly source - connect it to a 8 bit counter running at an arbitraty speed
// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "../cpu/wide_controller.v"
`include "../reset/reset.v"
`include "../phaser/phaser.v"
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

`define MAX_INST_LEN 100
typedef reg[`MAX_INST_LEN:0][7:0] string_bits ;

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
    wire [3:0] bbus_dev, abus_dev;
    wire [4:0] targ_dev;
    wire [4:0] alu_op;
    wire [7:0] _flags;

    wire _mr, _mrN, clk;

    reset RESET(
        .system_clk,
        ._RESET_SWITCH,
        .clk,
        ._mr,
        ._mrN
    );


    // CLOCK ===================================================================================
    //localparam T=1000;

    //always begin
    //   #CLOCK_INTERVAL clk = !clk;
    //end

    wire #8 _clk = ! clk; // GATE + PD


    wire  phaseFetch = clk;
    wire _phaseExec = clk;
    wire  #(10) _phaseFetch = !phaseFetch;
    wire  #(10) phaseExec = !_phaseExec;
    

    // CONTROL ===========================================================================================
    wire _addrmode_register, _addrmode_direct;
    wire [7:0] direct_address_hi, direct_address_lo;
    wire [7:0] immed8;

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

    wide_controller ctrl(
        .pc(pc_addr),
        ._flags(_flags),

        ._addrmode_register, ._addrmode_direct,
        `CONTROL_WIRES(BIND, `COMMA),
        .direct_address_hi, .direct_address_lo,
        .immed8,
        .alu_op,
        .bbus_dev, .abus_dev, .targ_dev
    );

    // PROGRAM COUNTER ======================================================================================

    wire #(8) _do_jmpc = _jmpc_in | _flag_c;
    wire #(8) _do_jmpz = _jmpz_in | _flag_z;
    wire #(8) _do_jmpo = _jmpo_in | _flag_o;
    wire #(8) _do_jmpn = _jmpn_in | _flag_n;
    wire #(8) _do_jmpeq = _jmpeq_in | _flag_eq;
    wire #(8) _do_jmpne = _jmpne_in | _flag_ne;
    wire #(8) _do_jmpgt = _jmpgt_in | _flag_gt;
    wire #(8) _do_jmplt = _jmplt_in | _flag_lt;
    wire #(8) _do_jmpdi = _jmpdi_in | _flag_di;
    wire #(8) _do_jmpdo = _jmpdo_in | _flag_do;

    wire #(8) _long_jump = _pc_in & _do_jmpc & _do_jmpz & _do_jmpn & _do_jmpo & _do_jmpeq & _do_jmpne & _do_jmpgt & _do_jmplt & _do_jmpdi & _do_jmpdo; // use two 7411 triple 3-input AND
    // PC reset is sync with +ve edge of clock
    pc #(.LOG(0))  PC (
        .clk(clk),
        ._MR(_mrN),
        ._long_jump(_long_jump),  // load both
        ._local_jump(_pclo_in), // load lo
        ._pchitmp_in(_pchitmp_in), // load tmp
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    // ROM =============================================================================================

    
    // ROM OUT to BBUS when immed rom addressing is being used
    hct74245ab rom_bbus_buf(.A(immed8), .B(bbus), .nOE(_bdev_immed)); // ?ROM?

    hct74245ab #(.LOG(0)) rom_addbbuslo_buf(.A(direct_address_lo), .B(address_bus[7:0]), .nOE(_addrmode_direct)); // optional - needed for direct addressing
    hct74245ab #(.LOG(0)) rom_addbbushi_buf(.A(direct_address_hi), .B(address_bus[15:8]), .nOE(_addrmode_direct)); // optional - needed for direct addressing

    // RAM =============================================================================================

    wire #(8) _gated_ram_in = _phaseExec | _ram_in;
    ram #(.AWIDTH(16), .LOG(0)) ram64(._WE(_gated_ram_in), ._OE(1'b0), .A(address_bus)); // OK to leave _OE enabled as ram data sheet makes WE override it
    
    hct74245ab ram_alubus_buf(.A(alu_result_bus), .B(ram64.D), .nOE(_ram_in));
    hct74245ab ram_bbus_buf(.A(ram64.D), .B(bbus), .nOE(_bdev_ram));

    // MAR =============================================================================================
    hct74377 #(.LOG(0)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(0)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));

    hct74245ab marlo_abus_buf(.A(MARLO.Q), .B(abus), .nOE(_adev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marlo_bbus_buf(.A(MARLO.Q), .B(bbus), .nOE(_bdev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab marhi_abus_buf(.A(MARHI.Q), .B(abus), .nOE(_adev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marhi_bbus_buf(.A(MARHI.Q), .B(bbus), .nOE(_bdev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab #(.LOG(0)) marhi_addbbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register));
    hct74245ab #(.LOG(0)) marlo_addbbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register));

    // ALU ==============================================================================================
    wire _flag_c_out, _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out;
    wire _flag_c, _flag_z, _flag_n, _flag_o, _flag_gt, _flag_lt, _flag_eq, _flag_ne;

	alu #(.LOG(1)) Alu(
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

    // don't set flags on a jump - preserve them - makes for two stage jumps if I need them
    // NOTE perhaps simpler if I use a spare bit to select which operations set the flags explicitely like ARM.
    wire #(9) gated_flags_clk = phaseExec & _pclo_in & _pchitmp_in & _long_jump;

    hct74574 #(.LOG(0)) flags_czonGLEN( .D({_flag_c_out , _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out}),
                                       .Q(_flags),
                                        //.CLK(phaseExec), 
                                        .CLK(gated_flags_clk), 
                                        ._OE(1'b0)); 

    assign {_flag_c, _flag_z, _flag_n, _flag_o, _flag_gt, _flag_lt, _flag_eq, _flag_ne} = _flags;

    // REGISTER FILE =====================================================================================
    // INTERESTING THAT THE SELECTION LOGIC DOESN'T CONSIDER REGD - THIS SIMPLIFIED VALUE DOMAIN CONSIDERING ONLY THE FOUR ACTIVE LOW STATES NEEDS JUST THIS SIMPLE LOGIC FOR THE ADDRESSING
    wire #(8) _gated_regfile_in = _phaseExec | (_rega_in & _regb_in & _regc_in & _regd_in);
    wire #(8) _regfile_rdL_en = _adev_rega &_adev_regb &_adev_regc &_adev_regd ;
    wire #(8) _regfile_rdR_en = _bdev_rega &_bdev_regb &_bdev_regc &_bdev_regd ;
    wire [1:0] regfile_rdL_addr = abus_dev[1:0];
    wire [1:0] regfile_rdR_addr = bbus_dev[1:0];
    wire [1:0] regfile_wr_addr = targ_dev[1:0];

    if (1) always @* $display("regfile gated in=", _gated_regfile_in, " wr addr  ", regfile_wr_addr, " in : a=%b b=%b c=%b d=%b " , _rega_in , _regb_in , _regc_in , _regd_in);
    if (1) always @* $display("regfile abus out=", _regfile_rdL_en, " rd addr  ", regfile_rdL_addr, " in : a=%b b=%b c=%b d=%b " , _adev_rega , _adev_regb , _adev_regc , _adev_regd);
    if (1) always @* $display("regfile bbus out=", _regfile_rdR_en, " rd addr  ", regfile_rdR_addr, " in : a=%b b=%b c=%b d=%b " , _bdev_rega , _bdev_regb , _bdev_regc , _bdev_regd);


    syncRegisterFile #(.LOG(0)) regFile(
        .clk(phaseExec), // only on the execute otherwise we will clock in results during fetch and decode and act more like a combinatorial circuit
        ._wr_en(_gated_regfile_in),
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
    wire _flag_di;
    wire _flag_do;
    wire #(10) _gated_uart_wr = _uart_in | _phaseExec;   // sync clock data into uart -- FIXME gate with EXEC

    wire [7:0] uart_d;

    um245r #(.LOG(0), .HEXMODE(1))  uart (
        .D(uart_d),
        .WR(_gated_uart_wr),// Writes data on -ve edge
        ._RD(_adev_uart),	// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
        ._TXE(_flag_do),	// When high do NOT write data using WR, when low write data by strobing WR
        ._RXF(_flag_di)		// When high to NOT read from D, when low then data is available to read by strobing RD low
      );

    hct74245ab uart_alubus_buf(.A(alu_result_bus), .B(uart_d), .nOE(_uart_in));
    hct74245ab uart_abus_buf(.A(uart_d), .B(abus), .nOE(_adev_uart));

endmodule : cpu
