// FIXME MAKE ALL THE tri WIRES tri0

`ifndef V_CONTROLLER_WIDE
`define V_CONTROLLER_WIDE

`include "../74139/hct74139.v"
`include "../74138/hct74138.v"
`include "../74573/hct74573.v"
`include "../cpu/cast.v"
`include "../rom/rom.v"
`include "../alu/alu.v"
`include "../control/control.v"

`include "psuedo_assembler.v"

`timescale 1ns/1ns

module wide_controller(
    input _mr,
    input [15:0] pc,
    input [7:0] _flags,

    output _addrmode_register, _addrmode_direct,

    // selection wires
    `CONTROL_WIRES(OUT, `COMMA),
    output [7:0] direct_address_lo, direct_address_hi,
    output [7:0] direct8,
    output [7:0] immed8,
    output [4:0] alu_op,
    output [3:0] bbus_dev, abus_dev,
    output [4:0] targ_dev
);
     
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

    assign direct8 = rom_1.D;
    assign immed8 = instruction_1;
    assign direct_address_lo = instruction_2;
    assign direct_address_hi = instruction_3;

    assign bbus_dev = {instruction_5[1:0], instruction_4[7:6]};
    assign abus_dev = instruction_5[5:2];
    assign targ_dev = {instruction_6[2:0],instruction_5[7:6]};
    wire [4:0] alu_op   = {instruction_6[7:3]};

    wire amode_bit = instruction_4[0];
    wire _addrmode_register = amode_bit; // low = reg
    wire #(10) _addrmode_direct = ! amode_bit;  // NAND GATE

    // device decoders
    hct74138 abus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(abus_dev[3]), .A(abus_dev[2:0]));
    hct74138 abus_dev_16_demux(.Enable3(abus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(abus_dev[2:0]));
    
    hct74138 bbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(bbus_dev[3]), .A(bbus_dev[2:0]));
    hct74138 bbus_dev_16_demux(.Enable3(bbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(bbus_dev[2:0]));

    wire [3:0] _targ_dev_block_sel, un4; 
    hct74139 targ_dev_block_demux(._Ea(1'b0), ._Eb(1'b0), .Aa(targ_dev[4:3]), .Ab(2'b0), ._Ya(_targ_dev_block_sel), ._Yb(un4));
    hct74138 targ_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[0]), .A(targ_dev[2:0]));
    hct74138 targ_dev_16_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[1]), .A(targ_dev[2:0]));
    hct74138 targ_dev_24_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[2]), .A(targ_dev[2:0]));
    hct74138 targ_dev_32_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[3]), .A(targ_dev[2:0]));

    // control lines for device selection
    wire [31:0] tsel = {targ_dev_32_demux.Y, targ_dev_24_demux.Y, targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    wire [15:0] lsel = {abus_dev_16_demux.Y, abus_dev_08_demux.Y};
    wire [15:0] rsel = {bbus_dev_16_demux.Y, bbus_dev_08_demux.Y};
    
    `define HOOKUP_ADEV_SEL(DNAME) wire _adev_``DNAME`` = lsel[DEV_``DNAME``]
    `define HOOKUP_BDEV_SEL(DNAME) wire _bdev_``DNAME`` = rsel[DEV_``DNAME``]
    `define HOOKUP_TDEV_SEL(DNAME) wire _``DNAME``_in = tsel[TDEV_``DNAME``]
    
    `CONTROL_WIRES(HOOKUP, `SEMICOLON);

endmodule: wide_controller


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
