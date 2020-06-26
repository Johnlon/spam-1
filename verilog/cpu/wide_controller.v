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

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off MULTITOP
// verilator lint_off DECLFILENAME
// verilator lint_off UNUSED

`define OUT_LDEV_SEL(DNAME) output _ldev_``DNAME``
`define OUT_RDEV_SEL(DNAME) output _rdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

`timescale 1ns/1ns
`define DIRECT 1'b1
`define REGISTER 1'b0
`define NA_AMODE 1'b0

// PSEUDO ASSEMBLER
`define ROM(A) { CPU.ctrl.rom_6.Mem[A], CPU.ctrl.rom_5.Mem[A], CPU.ctrl.rom_4.Mem[A], CPU.ctrl.rom_3.Mem[A], CPU.ctrl.rom_2.Mem[A], CPU.ctrl.rom_1.Mem[A] }

// Instruction populates the ROM and adds a text version of the instruction to the CODE array
`define INSTRUCTION(LOCN, TARGET, SRCA, SRCB, ALUOP, AMODE, ADDRESS, IMMED) \
    `ROM(LOCN) = { `toALUOP(ALUOP), cast.to5(`toDEV(TARGET)), cast.to4(`toDEV(SRCA)), cast.to4(`toDEV(SRCB)), 5'bz, AMODE, cast.to16(ADDRESS), cast.to8(IMMED) }; \
    CODE[LOCN] = "Code: TARGET=SRCA(ALUOP)SRCB  amode=AMODE immed8=IMMED addr=ADDRESS";

`define NA 'z

`define DEV_EQ_XY_ALU(INST, TARGET, SRCA, SRCB, ALUOP) `INSTRUCTION(INST, TARGET, SRCA,     SRCB,    ALUOP, `REGISTER, `NA,     `NA)
`define DEV_EQ_ROM_DIRECT(INST,TARGET, ADDRESS)        `INSTRUCTION(INST, TARGET, not_used, rom,     B,     `DIRECT,   ADDRESS, `NA)
`define DEV_EQ_IMMED8(INST,TARGET, IMMED8)             `INSTRUCTION(INST, TARGET, not_used, instreg, B,     `REGISTER, `NA,     IMMED8) // src is the immed8 but target if ram is via MAR
`define DEV_EQ_RAM_DIRECT(INST,TARGET, ADDRESS)        `INSTRUCTION(INST, TARGET, not_used, ram,     B,     `DIRECT,   ADDRESS, `NA)
`define RAM_DIRECT_EQ_DEV(INST,ADDRESS, SRC)           `INSTRUCTION(INST, ram,    not_used, SRC,     B,     `DIRECT,   ADDRESS, `NA)

// prep jump sourcing the PCHI from the immed8
`define JMP_PREP_IMMED(INST, ADDRESS_HI)    `INSTRUCTION(INST, pchitmp, not_used, instreg,    B,     `NA_AMODE, `NA, ADDRESS_HI) 
// jump sourcing the PCLO from the immed8
`define JMP_IMMED(INST, ADDRESS_LO)        `INSTRUCTION(INST, pc,      not_used, instreg,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPZ_IMMED(INST, ADDRESS_LO)       `INSTRUCTION(INST, jmpz,    not_used, instreg,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPC_IMMED(INST, ADDRESS_LO)       `INSTRUCTION(INST, jmpc,    not_used, instreg,    B,     `NA_AMODE, `NA, ADDRESS_LO) 

`define JMP_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMP_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPC_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPC_IMMED(INST+1, ADDRESS_LONG & 8'hff)

/*
    `PREPJMP_IMMED(INST, cast.hi8(ADDRESS_LONG)) \
    `JMP_IMMED(INST+1, cast.lo8(ADDRESS_LONG))
*/

module wide_controller(
    input _mr,
    input phaseFetch, phaseDecode, phaseExec, _phaseFetch, _phaseExec,
    input [15:0] address_bus,
    input [15:0] pc,
    input [7:0] _flags,

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
     
    rom #(.AWIDTH(16)) rom_6(._CS(1'b0), ._OE(1'b0), .A(pc));
    rom #(.AWIDTH(16)) rom_5(._CS(1'b0), ._OE(1'b0), .A(pc));
    rom #(.AWIDTH(16)) rom_4(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_3(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_2(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_1(._CS(1'b0), ._OE(1'b0), .A(address_bus));
   
    wire [7:0] instruction_6 = rom_6.D; // aliases
    wire [7:0] instruction_5 = rom_5.D;
    wire [7:0] instruction_4 = rom_4.D;
    wire [7:0] instruction_3 = rom_3.D;
    wire [7:0] instruction_2 = rom_2.D;
    wire [7:0] instruction_1;
    hct74573 inst_reg_1( .LE(phaseFetch), ._OE(1'b0), .D(rom_1.D), .Q(instruction_1) ); // capture immediate value so still available despite direct addressing


    assign direct8 = rom_1.D;
    assign immed8 = instruction_1;
    assign direct_address_lo = instruction_2;
    assign direct_address_hi = instruction_3;

    assign rbus_dev = {instruction_5[1:0], instruction_4[7:6]};
    assign lbus_dev = instruction_5[5:2];
    assign targ_dev = {instruction_6[2:0],instruction_5[7:6]};
    wire [4:0] alu_op   = {instruction_6[7:3]};


    wire amode_bit = instruction_4[0];
    wire amode_direct = amode_bit; 
    wire #(10) amode_register = ! amode_bit;  // NAND GATE
    nand #(10) o1(_addrmode_register, _phaseFetch, amode_register);  // _phaseFetch is high when NOT in fatch
    nand #(10) o2(_addrmode_direct , _phaseFetch, amode_direct); 
    assign _addrmode_pc = _phaseFetch;

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

endmodule: wide_controller


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
