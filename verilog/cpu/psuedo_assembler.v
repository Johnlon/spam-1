
`define OUT_ADEV_SEL(DNAME) output _adev_``DNAME``
`define OUT_BDEV_SEL(DNAME) output _bdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

`define DIRECT 1'b1
`define REGISTER 1'b0
`define NA_AMODE 1'b0

// PSEUDO ASSEMBLER
`define ROM(A) { CPU.ctrl.rom_6.Mem[A], CPU.ctrl.rom_5.Mem[A], CPU.ctrl.rom_4.Mem[A], CPU.ctrl.rom_3.Mem[A], CPU.ctrl.rom_2.Mem[A], CPU.ctrl.rom_1.Mem[A] }

// Instruction populates the ROM and adds a text version of the instruction to the CODE array
`define INSTRUCTION(LOCN, TARGET, SRCA, SRCB, ALUOP, AMODE, ADDRESS, IMMED) \
    `ROM(LOCN) = { \
    `toALUOP(ALUOP), \
     cast.to4(`toTDEV(TARGET)), \
     cast.to3(`toADEV(SRCA)), \
     cast.to3(`toBDEV(SRCB)), \
     8'bz, \
     1'(AMODE), \
     cast.to16(ADDRESS), \
     cast.to8(IMMED) }; \
    CODE[LOCN] = "TARGET=SRCA(ALUOP)SRCB  amode=AMODE immed8=IMMED addr=ADDRESS";

`define NA 'z

`define DEV_EQ_XI_ALU(INST, TARGET, SRCA, IMMED8, ALUOP) `INSTRUCTION(INST, TARGET, SRCA,   immed,  ALUOP, `REGISTER, `NA,     IMMED8)

`define DEV_EQ_XY_ALU(INST, TARGET, SRCA, SRCB, ALUOP) `INSTRUCTION(INST, TARGET, SRCA,     SRCB, ALUOP, `REGISTER, `NA,     `NA)
`define DEV_EQ_ROM_IMMED(INST,TARGET, ADDRESS)         `INSTRUCTION(INST, TARGET, not_used, immed B,     `x,        ADDRESS, `NA)
`define DEV_EQ_IMMED8(INST,TARGET, IMMED8)             `INSTRUCTION(INST, TARGET, not_used, immed,B,     `REGISTER, `NA,     IMMED8) // src is the immed8 but target if ram is via MAR
`define DEV_EQ_RAM_DIRECT(INST,TARGET, ADDRESS)        `INSTRUCTION(INST, TARGET, not_used, ram,  B,     `DIRECT,   ADDRESS, `NA)
`define DEV_EQ_RAM_REGISTER(INST,TARGET, ADDRESS)      `INSTRUCTION(INST, TARGET, not_used, ram,  B,     `REGISTER, ADDRESS, `NA)
`define RAM_DIRECT_EQ_DEV(INST,ADDRESS, SRC)           `INSTRUCTION(INST, ram,    not_used, SRC,  B,     `DIRECT,   ADDRESS, `NA)
`define RAM_DIRECT_EQ_IMMED8(INST,ADDRESS, IMMED8)     `INSTRUCTION(INST, ram,    not_used, immed,B,     `DIRECT,   ADDRESS, IMMED8)

`define CLEAR_CARRY(INST)   `DEV_EQ_IMMED8(INST, not_used, 0);
`define SET_CARRY(INST)     `DEV_EQ_XI_ALU(INST, not_used, not_used, 255, B_PLUS_1)  // FIXME BROKEN COS B_PLUS_1 doesn't add

// prep jump sourcing the PCHI from the immed8
`define JMP_PREP_IMMED(INST, ADDRESS_HI)   `INSTRUCTION(INST, pchitmp, not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_HI) 
// jump sourcing the PCLO from the immed8
`define JMP_IMMED(INST, ADDRESS_LO)        `INSTRUCTION(INST, pc,      not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPZ_IMMED(INST, ADDRESS_LO)       `INSTRUCTION(INST, jmpz,    not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPC_IMMED(INST, ADDRESS_LO)       `INSTRUCTION(INST, jmpc,    not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
`define JMPDO_IMMED(INST, ADDRESS_LO)      `INSTRUCTION(INST, jmpdo,   not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_LO) 
`define JMPDI_IMMED(INST, ADDRESS_LO)      `INSTRUCTION(INST, jmpdi,   not_used, immed,    B,     `NA_AMODE, `NA, ADDRESS_LO) 

`define JMP_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMP_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPC_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPC_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPDO_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPDO_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPDI_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_PREP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPDI_IMMED(INST+1, ADDRESS_LONG & 8'hff)

