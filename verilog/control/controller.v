// FIXME MAKE ALL THE tri WIRES tri0

`ifndef V_CONTROLLER
`define V_CONTROLLER

//`include "../74138/hct74138.v"
`include "../74139/hct74139.v"
//`include "../74245/hct74245.v"
`include "../74573/hct74573.v"
`include "../rom/rom.v"
`include "../cpu/cast.v"
`include "../control/control.v"
`include "../control/op_decoder.v"
`include "../control/memory_address_mode_decoder.v"
//`include "../alu/alu_func.v"
//`include "op_decoder.v"
//`include "memory_address_mode_decoder.v"


// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off MULTITOP
// verilator lint_off DECLFILENAME
// verilator lint_off UNUSED

`define OUT_LDEV_SEL(DNAME) output _ldev_``DNAME``
`define OUT_RDEV_SEL(DNAME) output _rdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

`timescale 1ns/1ns

// PSEUDO ASSEMBLER
`define DEV_EQ_ROM_DIRECT(TARGET, ADDRESS)       { control.OP_dev_eq_rom_direct, cast.to5(`toDEV(TARGET)), cast.to16(ADDRESS) }
`define DEV_EQ_CONST8(TARGET, CONST8)            { control.OP_dev_eq_const8, cast.to5(`toDEV(TARGET)), 8'hx, cast.to8(CONST8) }
`define DEV_EQ_XY_ALU(TARGET, SRCA, SRCB, ALUOP) { control.OP_dev_eq_xy_alu, cast.to5(`toDEV(TARGET)), 3'bzzz, cast.to4(`toDEV(SRCA)), cast.to4(`toDEV(SRCB)), cast.to5(`toALUOP(ALUOP))}
`define DEV_EQ_RAM_DIRECT(TARGET, ADDRESS)       { control.OP_dev_eq_ram_direct, cast.to5(control.DEV_``TARGET``), cast.to16(ADDRESS) }
`define RAM_DIRECT_EQ_DEV(ADDRESS, SRC)          { control.OP_ram_direct_eq_dev, cast.to5(`toDEV(SRC)), cast.to16(ADDRESS) }

`define ROM(A) {ctrl.rom_hi.Mem[A], ctrl.rom_mid.Mem[A], ctrl.rom_lo.Mem[A]}


module controller(
    input _mr,
    input phaseFetch, phaseDecode, phaseExec, _phaseFetch, _phaseExec,
    input [15:0] address_bus,

    output _addrmode_register, _addrmode_pc, _addrmode_direct,

    // selection wires
    `CONTROL_WIRES(OUT, `COMMA),
    output [7:0] direct_address_lo, direct_address_hi,
    output [7:0] direct8,
    output [7:0] immed8,
    output [4:0] alu_op,
    output [3:0] rbus_dev, lbus_dev,
    output [4:0] targ_dev
);
     
    wire [7:0] instruction_hi, instruction_mid, instruction_lo;
    wire [2:0] op_ctrl;

    rom #(.AWIDTH(16)) rom_hi(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_mid(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_lo(._CS(1'b0), ._OE(1'b0), .A(address_bus)); 
   
    // instruction reg buffer
    hct74573 rom_hi_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_hi.D),
         .Q(instruction_hi) 
    );

    hct74573 rom_mid_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_mid.D),
         .Q(instruction_mid) 
    );

    hct74573 rom_lo_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_lo.D),
         .Q(instruction_lo) 
    );

    assign direct8 = rom_lo.D;
    assign immed8 = instruction_lo;
    assign direct_address_lo = instruction_lo;
    assign direct_address_hi = instruction_mid;

    //assign op_ctrl = 3'bx === instruction_hi[7:5]? 3'b0 : instruction_hi[7:5]; // FIXME: HACK
    assign op_ctrl = instruction_hi[7:5];

    op_decoder #(.LOG(0)) op_decode(.data_hi(instruction_hi), .data_mid(instruction_mid), .data_lo(instruction_lo), .rbus_dev, .lbus_dev, .targ_dev, .alu_op);

    memory_address_mode_decoder #(.LOG(1)) addr_decode( 
        ._mr,
        .ctrl(op_ctrl),
        .phaseFetch, ._phaseFetch, .phaseDecode, .phaseExec, 
        ._addrmode_pc, ._addrmode_register, ._addrmode_direct 
    );


    // device decoders
    hct74138 lbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(lbus_dev[3]), .A(lbus_dev[2:0]));
    hct74138 lbus_dev_16_demux(.Enable3(lbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(lbus_dev[2:0]));
    
    hct74138 rbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(rbus_dev[3]), .A(rbus_dev[2:0]));
    hct74138 rbus_dev_16_demux(.Enable3(rbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(rbus_dev[2:0]));

    wire [3:0] _targ_dev_block_sel, un4; 
    hct74139 targ_dev_block_demux(._Ea(1'b0), ._Eb(1'b0), .Aa(targ_dev[4:3]), .Ab(2'b0), ._Ya(_targ_dev_block_sel), ._Yb(un4));
    hct74138 targ_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[0]), .A(targ_dev[2:0]));
    hct74138 targ_dev_16_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[1]), .A(targ_dev[2:0]));
    hct74138 targ_dev_24_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[2]), .A(targ_dev[2:0]));
    hct74138 targ_dev_32_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[3]), .A(targ_dev[2:0]));

    // control lines for device selection
    wire [31:0] tsel = {targ_dev_32_demux.Y, targ_dev_24_demux.Y, targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    wire [15:0] lsel = {lbus_dev_16_demux.Y, lbus_dev_08_demux.Y};
    wire [15:0] rsel = {rbus_dev_16_demux.Y, rbus_dev_08_demux.Y};
    
    `define HOOKUP_LDEV_SEL(DNAME) wire _ldev_``DNAME`` = lsel[control.DEV_``DNAME``]
    `define HOOKUP_RDEV_SEL(DNAME) wire _rdev_``DNAME`` = rsel[control.DEV_``DNAME``]
    `define HOOKUP_TDEV_SEL(DNAME) wire _``DNAME``_in = tsel[control.TDEV_``DNAME``]
    
    `CONTROL_WIRES(HOOKUP, `SEMICOLON);

endmodule: controller


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
