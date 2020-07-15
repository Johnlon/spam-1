
// FIXME Use Warrens ALU ROM and my external logic
// TODO option: Implement conditional instructions with spare ROM bits
// TODO option: If use 16 immediate then can do a direct jump - but needs an alternative route into the PC for that
// FIXME: Can I make this single cycle - dual cycle at least?


// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "../cpu/wide_controller.v"
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
    input clk
);

    parameter PHASE_FETCH_LEN=4;
    parameter PHASE_DECODE_LEN=4;
    parameter PHASE_EXEC_LEN=2;
    
    tri [15:0] address_bus;

    tri [7:0] bbus, abus, alu_result_bus;
    wire [3:0] bbus_dev, abus_dev;
    wire [4:0] targ_dev;
    wire [4:0] alu_op;
    wire [7:0] _flags;


    wire phaseFetch, phaseDecode, phaseExec, _phaseFetch, _phaseExec;
    wire [2:0] phase = {phaseFetch, phaseDecode, phaseExec};

    // CLOCK ===================================================================================
    //localparam T=1000;

    //always begin
    //   #CLOCK_INTERVAL clk = !clk;
    //end

    wire #8 _clk = ! clk; // GATE + PD
    
    // RESET CIRCUIT ===================================================================================

    wire mrPC, _mrPC;
    wire mrPH, _mrPH;

    // syncs the reset with the clock.
    // mrPH forces phase to 000 so clock to PC will be low.
    // at this point _mrPC will also be low so when the clock releases _mrPH then 
    // there will be a phase transition to 100 which the PC will see as a clock pulse that resets the PC   
    hct7474 #(.BLOCKS(1), .LOG(0)) resetPH(
          ._SD(1'b1),
          ._RD(_RESET_SWITCH),
          .D(1'b1),
          .CP(clk),
          .Q(_mrPH),
          ._Q(mrPH)
        );

    hct7474 #(.BLOCKS(1), .LOG(0)) resetPCFF(
          ._SD(1'b1),
          ._RD(_mrPH), // reset released after clock on resetPH
          .D(1'b1),
          .CP(phaseFetch),
          .Q(_mrPC),
          ._Q(mrPC)
        );


    // CLOCK PHASING ===================================================================================

    wire [9:0] seq;
    `define SEQ(x) (10'd2 ** (x-1))

    // releasing reset allows phaser to go from 000 to 100 whilst _mrPC is low which resets the PC
    phaser #(.LOG(0), .PHASE_FETCH_LEN(PHASE_FETCH_LEN), .PHASE_DECODE_LEN(PHASE_DECODE_LEN), .PHASE_EXEC_LEN(PHASE_EXEC_LEN)) ph(.clk, .mr(mrPH), .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec, ._phaseExec);

    // CONTROL ===========================================================================================
    wire _addrmode_register, _addrmode_pc, _addrmode_direct;
    wire [7:0] direct_address_hi, direct_address_lo;
    wire [7:0] direct8;
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
        ._mr(_mrPC),
        .phaseFetch, .phaseDecode, .phaseExec, ._phaseFetch, ._phaseExec,
        .pc(pc_addr),
        .address_bus,
        ._flags(_flags),

        ._addrmode_register, ._addrmode_pc, ._addrmode_direct,
        `CONTROL_WIRES(BIND, `COMMA),
        .direct_address_hi, .direct_address_lo,
        .direct8,
        .immed8,
        .alu_op,
        .bbus_dev, .abus_dev, .targ_dev // for regfile
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

   wire #(8) _do_jmp = _pc_in & _do_jmpc & _do_jmpz & _do_jmpn & _do_jmpo & _do_jmpeq & _do_jmpne & _do_jmpgt & _do_jmplt & _do_jmpdi & _do_jmpdo; // use two 7411 triple 3-input AND
    
    // PC reset is sync with +ve edge of clock
    pc #(.LOG(0))  PC (
        .clk(phaseFetch),
        ._MR(_mrPC),
        //._pc_in(_pc_in), // load both
        ._pc_in(_do_jmp),  // load both
        ._pclo_in(_pclo_in), // load lo
        ._pchitmp_in(_pchitmp_in), // load tmp
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    hct74245ab pchi_addbbushi_buf(.A(PCHI), .B(address_bus[15:8]), .nOE(_addrmode_pc));
    hct74245ab pclo_addbbuslo_buf(.A(PCLO), .B(address_bus[7:0]), .nOE(_addrmode_pc));

    // ROM =============================================================================================

    
    // ROM OUT to BBUS when direct rom addressing is being used
    hct74245ab rom_bbus_buf(.A(direct8), .B(bbus), .nOE(_bdev_rom));

    // ROM OUT TO BBUS VIA IR is immediate addressing of that operand, and we can be simultaneously register (MAR) addressing the RAM
    hct74245ab rom_instreg_bbus_buf(.A(immed8), .B(bbus), .nOE(_bdev_instreg));

    hct74245ab rom_addbbuslo_buf(.A(direct_address_lo), .B(address_bus[7:0]), .nOE(_addrmode_direct)); // optional - needed for direct addressing
    hct74245ab rom_addbbushi_buf(.A(direct_address_hi), .B(address_bus[15:8]), .nOE(_addrmode_direct)); // optional - needed for direct addressing

    // RAM =============================================================================================

    wire #(8) _gated_ram_in = _phaseExec | _ram_in;
    ram #(.AWIDTH(16)) ram64(._WE(_gated_ram_in), ._OE(1'b0), .A(address_bus));
    
    hct74245ab ram_alubus_buf(.A(alu_result_bus), .B(ram64.D), .nOE(_ram_in));
    hct74245ab ram_bbus_buf(.A(ram64.D), .B(bbus), .nOE(_bdev_ram));

    // MAR =============================================================================================
    hct74377 #(.LOG(0)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(0)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));

    hct74245ab marlo_abus_buf(.A(MARLO.Q), .B(abus), .nOE(_adev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marlo_bbus_buf(.A(MARLO.Q), .B(bbus), .nOE(_bdev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab marhi_abus_buf(.A(MARHI.Q), .B(abus), .nOE(_adev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marhi_bbus_buf(.A(MARHI.Q), .B(bbus), .nOE(_bdev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab marhi_addbbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register));
    hct74245ab marlo_addbbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register));

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

    wire #(9) gated_flags_clk = phaseExec & _pclo_in & _pchitmp_in & _do_jmp;

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

    if (0) always @* $display("regfile gated in=", _gated_regfile_in, " wr addr  ", regfile_wr_addr, " in : a=%b b=%b c=%b d=%b " , _rega_in , _regb_in , _regc_in , _regd_in);
    if (0) always @* $display("regfile abus out=", _regfile_rdL_en, " rd addr  ", regfile_rdL_addr, " in : a=%b b=%b c=%b d=%b " , _adev_rega , _adev_regb , _adev_regc , _adev_regd);
    if (0) always @* $display("regfile bbus out=", _regfile_rdR_en, " rd addr  ", regfile_rdR_addr, " in : a=%b b=%b c=%b d=%b " , _bdev_rega , _bdev_regb , _bdev_regc , _bdev_regd);


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
    wire _gated_uart_wr = _uart_in | _phaseExec;   // sync clock data into uart -- FIXME gate with EXEC
    wire [7:0] uart_d;

    um245r #(.LOG(1), .HEXMODE(1))  uart (
        .D(uart_d),
        .WR(_gated_uart_wr),// Writes data on -ve edge
        ._RD(_adev_uart),	// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
        ._TXE(_flag_do),	// When high do NOT write data using WR, when low write data by strobing WR
        ._RXF(_flag_di)		// When high to NOT read from D, when low then data is available to read by strobing RD low
      );

    hct74245ab uart_alubus_buf(.A(alu_result_bus), .B(uart_d), .nOE(_uart_in));
    hct74245ab uart_abus_buf(.A(uart_d), .B(abus), .nOE(_adev_uart));

endmodule : cpu
