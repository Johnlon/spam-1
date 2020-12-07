// FIXME MAKE ALL THE tri WIRES tri0
// verilator lint_off PINMISSING
// verilator lint_off MULTITOP


`ifndef V_CONTROLLER_WIDE
`define V_CONTROLLER_WIDE

`include "../74151/hct74151.v"
`include "../74139/hct74139.v"
`include "../74138/hct74138.v"
`include "../lib/cast.v"
`include "../rom/rom.v"
`include "../alu/alu.v"
`include "control_lines.v"

`timescale 1ns/1ns

`timescale 1ns/1ns

module controller(
    input [15:0] pc,
    input [7:0] _flags_czonGLEN,
    input _flag_di, _flag_do,

    output _addrmode_register, _addrmode_direct,

    // selection wires
`define OUT_ADEV_SEL(DNAME) output _adev_``DNAME``
`define OUT_BDEV_SEL(DNAME) output _bdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

    `CONTROL_WIRES(OUT, `COMMA),
    output [7:0] direct_address_lo, direct_address_hi,
    output [7:0] immed8,
    output [4:0] alu_op,
    output [2:0] bbus_dev, abus_dev,
    output [3:0] targ_dev,
    output _set_flags
);

    logic _do_exec;
     
    rom #(.AWIDTH(16)) rom_6(._CS(1'b0), ._OE(1'b0), .A(pc));
    rom #(.AWIDTH(16)) rom_5(._CS(1'b0), ._OE(1'b0), .A(pc));
    rom #(.AWIDTH(16)) rom_4(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_3(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_2(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_1(._CS(1'b0), ._OE(1'b0), .A(pc));
   
    wire [7:0] instruction_6 = rom_6.D; // aliases
    wire [7:0] instruction_5 = rom_5.D;
    wire [7:0] instruction_4 = rom_4.D;
    wire [7:0] instruction_3 = rom_3.D;
    wire [7:0] instruction_2 = rom_2.D;
    wire [7:0] instruction_1 = rom_1.D;

    wire [47:0] instruction = {rom_6.D, rom_5.D, rom_4.D, rom_3.D, rom_2.D, rom_1.D};


    // instruction decompose
    assign alu_op   = {instruction_6[7:3]};
    assign targ_dev = {instruction_6[2:0],instruction_5[7]};
    assign abus_dev = instruction_5[6:4];
    assign bbus_dev = instruction_5[3:1];
    wire [3:0] condition = {instruction_5[0],instruction_4[7:5]};
    wire _set_flags_bit = instruction_4[4];
    wire amode_bit = instruction_4[0];

    assign direct_address_hi = instruction_3;
    assign direct_address_lo = instruction_2;
    assign immed8 = instruction_1;


    // only set flags is executing instruction
    or #(9) (_set_flags, _set_flags_bit, _do_exec);

    // addressing mode
    assign _addrmode_register = amode_bit; // low = reg
    nand #(9) (_addrmode_direct, amode_bit, amode_bit);  // NAND GATE

    // device decoders
    hct74138 abus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(abus_dev[2:0]));
    hct74138 bbus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(bbus_dev[2:0]));

    hct74138 targ_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(_do_exec), .Enable1_bar(targ_dev[3]), .A(targ_dev[2:0]));
    hct74138 targ_dev_16_demux(.Enable3(targ_dev[3]), .Enable2_bar(_do_exec), .Enable1_bar(1'b0),        .A(targ_dev[2:0]));

    // control lines for device selection
    wire [7:0] lsel = {abus_dev_08_demux.Y};
    wire [7:0] rsel = {bbus_dev_08_demux.Y};
    wire [15:0] tsel = {targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    
    `define HOOKUP_ADEV_SEL(DNAME) assign _adev_``DNAME`` = lsel[ADEV_``DNAME``]
    `define HOOKUP_BDEV_SEL(DNAME) assign _bdev_``DNAME`` = rsel[BDEV_``DNAME``]
    `define HOOKUP_TDEV_SEL(DNAME) assign _``DNAME``_in = tsel[TDEV_``DNAME``]
    
    `CONTROL_WIRES(HOOKUP, `SEMICOLON);


    wire [7:0] _flags_hi = {
            5'b0,
            _flag_do, //DO
            _flag_di, // DI
            _flags_czonGLEN[0]  // LT
            };

    wire [7:0] _flags_lo = {
            _flags_czonGLEN[1], // GT
            _flags_czonGLEN[2],
            _flags_czonGLEN[3],
            _flags_czonGLEN[4],
            _flags_czonGLEN[5],
            _flags_czonGLEN[6],
            _flags_czonGLEN[7], // Carry
            1'b0}; // Always

    // organise two 8-to-1 multiplexers as a 16-1 multiulexer
    wire conditionTopBit = condition[3];
    wire #(8) _conditionTopBit = !conditionTopBit; // NAND GATE
    hct74151 #(.LOG(0)) do_exec_lo(._E(conditionTopBit),  .S(condition[2:0]), .I(_flags_lo));
    hct74151 #(.LOG(0)) do_exec_hi(._E(_conditionTopBit), .S(condition[2:0]), .I(_flags_hi));

    nand #(9) (_do_exec, do_exec_lo._Y, do_exec_hi._Y); 

endmodule


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
