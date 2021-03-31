
`define OUT_ADEV_SEL(DNAME) output _adev_``DNAME``
`define OUT_BDEV_SEL(DNAME) output _bdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

`define DIRECT 1'b1
`define REGISTER 1'b0
`define NA_AMODE 1'b0


`define SET_FLAGS 1'b0
`define NA_FLAGS 1'b1

// PSEUDO ASSEMBLER
`define ROM(A) { CPU.ctrl.rom_6.Mem[A], CPU.ctrl.rom_5.Mem[A], CPU.ctrl.rom_4.Mem[A], CPU.ctrl.rom_3.Mem[A], CPU.ctrl.rom_2.Mem[A], CPU.ctrl.rom_1.Mem[A] }

`define DEFINE_CODE_VARS(size) \
    string_bits CODE [size]; \
    string CODE_NUM [size]; \
    string TEMP_STRING; \
    string_bits CODE_TEXT [size];

// Instruction populates the ROM and adds a text version of the instruction to the CODE array
`define TEXT(LOCN,TXT)    CODE_TEXT[LOCN] = TXT;

// in the same order as the instruction layout - args as numeric values already cast to correct size
`define INSTRUCTION_NN(LOCN, ALUOP, TARGET, SRCA, SRCB, CONDITION, FLAG_CTL, AMODE, ADDRESS, IMMED) \
    `ROM(LOCN) = { \
         (ALUOP), \
         (TARGET), \
         (SRCA), \
         (SRCB), \
         (CONDITION), \
         (FLAG_CTL), \
         3'bz, \
         (AMODE), \
         (ADDRESS), \
         (IMMED) }; \
        $sformat(TEMP_STRING, "aluop:%x  target:%x  a:%x  b:%x  cond:%x setf:%x amode:%x immed8:%x addr:%x", \
                    ALUOP, TARGET, SRCA, SRCB, CONDITION, FLAG_CTL, AMODE, IMMED, ADDRESS); \
        CODE_NUM[LOCN] = TEMP_STRING;

// in the same order as the instruction layout - args as numeric values already cast to correct size
`define DISASSEMBLE(LOCN, INSTRUCTION) { \
    { \
         wire i_aluop = INSTRUCTION[47:43]; \
         wire i_target = INSTRUCTION[42:39]; \
         wire i_srca = INSTRUCTION[38:36]; \
         wire i_srcb = INSTRUCTION[35:33]; \
         wire i_cond = INSTRUCTION[32:29]; \
         wire i_flag = INSTRUCTION[28]; \
         wire i_nu   = INSTRUCTION[27:25]; \
         wire i_amode= INSTRUCTION[24]; \
         wire i_addr = INSTRUCTION[23:8]; \
         wire i_immed= INSTRUCTION[7:0]; \
        $sformat(TEMP_STRING, "aluop:%s  target:%s  a:%s  b:%s  cond:%x setf:%s amode:%s immed8:%x addr:%x", \
                    aluopname(i_aluop), \
                    tdevname(i_target), \
                    adevname(i_srca), \
                    bdevname(i_srcb), \
                    condname(i_cond), \
                    (i_flag "NOSET" : "SET"), \
                    (i_amode  "DIR": "REG"), \
                    i_addr, \
                    i_immed); \
    }

// in the same order as the instruction layout - args as numeric values 
`define INSTRUCTION_N(LOCN, ALUOP, TARGET, SRCA, SRCB, CONDITION, FLAG_CTL, AMODE, ADDRESS, IMMED) \
    `INSTRUCTION_NN(LOCN, \
         cast.to5(ALUOP), \
         cast.to4(TARGET), \
         cast.to3(SRCA), \
         cast.to3(SRCB), \
         cast.to4(CONDITION), \
         cast.to1(FLAG_CTL), \
         cast.to1(AMODE), \
         cast.to16(ADDRESS), \
         cast.to8(IMMED) );

// in same order as instruction - symbolic args
`define INSTRUCTION_SYM(LOCN, ALUOP, TARGET, SRCA, SRCB, CONDITION, FLAG_CTL, AMODE, ADDRESS, IMMED) \
    `INSTRUCTION_N(LOCN, \
         `toALUOP(ALUOP), \
         `toTDEV(TARGET), \
         `toADEV(SRCA), \
         `toBDEV(SRCB), \
         `COND(CONDITION), \
         FLAG_CTL, \
         AMODE, \
         ADDRESS, \
         IMMED ); \
        CODE[LOCN] = "TARGET=SRCA(ALUOP)SRCB  cond=CONDITION setf=FLAG_CTL amode=AMODE immed8=IMMED addr=ADDRESS";


// in the order old scripts use
`define INSTRUCTION_S(LOCN, TARGET, SRCA, SRCB, ALUOP, CONDITION, FLAG_CTL, AMODE, ADDRESS, IMMED) \
    `INSTRUCTION_N(LOCN, \
         `toALUOP(ALUOP), \
         `toTDEV(TARGET), \
         `toADEV(SRCA), \
         `toBDEV(SRCB), \
         `COND(CONDITION), \
         FLAG_CTL, \
         AMODE, \
         ADDRESS, \
         IMMED ); \
        CODE[LOCN] = "TARGET=SRCA(ALUOP)SRCB  cond=CONDITION setf=FLAG_CTL amode=AMODE immed8=IMMED addr=ADDRESS";

// always sets the flags
`define INSTRUCTION(LOCN, TARGET, SRCA, SRCB, ALUOP,                   AMODE, ADDRESS, IMMED) \
     `INSTRUCTION_S(LOCN, TARGET, SRCA, SRCB, ALUOP, A,    `SET_FLAGS, AMODE, ADDRESS, IMMED);

`define NA 'z

`define DEV_EQ_XI_ALU(INST, TARGET, SRCA, IMMED8, ALUOP) `INSTRUCTION(INST, TARGET, SRCA,   immed,  ALUOP, `REGISTER, `NA,     IMMED8)

`define DEV_EQ_XY_ALU(INST, TARGET, SRCA, SRCB, ALUOP) `INSTRUCTION(INST, TARGET, SRCA,     SRCB, ALUOP, `REGISTER, `NA,     `NA)
`define DEV_EQ_ROM_IMMED(INST,TARGET, ADDRESS)         `INSTRUCTION(INST, TARGET, not_used, immed B,     `x,        ADDRESS, `NA)
`define DEV_EQ_IMMED8(INST,TARGET, IMMED8)             `INSTRUCTION(INST, TARGET, not_used, immed,B,     `REGISTER, `NA,     IMMED8) // src is the immed8 but target if ram is via MAR
`define DEV_EQ_RAM_DIRECT(INST,TARGET, ADDRESS)        `INSTRUCTION(INST, TARGET, not_used, ram,  B,     `DIRECT,   ADDRESS, `NA)
`define DEV_EQ_RAM_REGISTER(INST,TARGET, ADDRESS)      `INSTRUCTION(INST, TARGET, not_used, ram,  B,     `REGISTER, ADDRESS, `NA)
`define RAM_DIRECT_EQ_DEV(INST,ADDRESS, SRC)           `INSTRUCTION(INST, ram,    not_used, SRC,  B,     `DIRECT,   ADDRESS, `NA)
`define RAM_DIRECT_EQ_IMMED8(INST,ADDRESS, IMMED8)     `INSTRUCTION(INST, ram,    not_used, immed,B,     `DIRECT,   ADDRESS, IMMED8)

`define CLEAR_CARRY(INST)   `DEV_EQ_IMMED8(INST, not_used, 0); // assign zero to a non-reg to clear
`define SET_CARRY(INST)     `DEV_EQ_XI_ALU(INST, not_used, not_used, 255, B_PLUS_1)  // FIXME BROKEN COS B_PLUS_1 doesn't add

// prep jump sourcing the PCHI from the immed8
`define JMP_UNCONDITIONAL_PCHITMP_IMMED(INST, ADDRESS_HI)   `INSTRUCTION_S(INST, pchitmp, not_used, immed,    B,A, `NA_FLAGS,  `NA_AMODE, `NA, ADDRESS_HI) 
// jump sourcing the PCLO from the immed8
`define JMP_IMMED(INST, ADDRESS_LO)        `INSTRUCTION_S(INST, pc, not_used, immed, B, A, `NA_FLAGS, `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPZ_IMMED(INST, ADDRESS_LO)       `INSTRUCTION_S(INST, pc, not_used, immed, B, Z, `NA_FLAGS,     `NA_AMODE, `NA, ADDRESS_LO) 
// conditional jump sourcing the PCLO from the immed8
`define JMPC_IMMED(INST, ADDRESS_LO)       `INSTRUCTION_S(INST, pc, not_used, immed, B, C, `NA_FLAGS,    `NA_AMODE, `NA, ADDRESS_LO) 
`define JMPDO_IMMED(INST, ADDRESS_LO)      `INSTRUCTION_S(INST, pc, not_used, immed, B, DO, `NA_FLAGS,     `NA_AMODE, `NA, ADDRESS_LO) 
`define JMPDI_IMMED(INST, ADDRESS_LO)      `INSTRUCTION_S(INST, pc, not_used, immed, B, DI, `NA_FLAGS,     `NA_AMODE, `NA, ADDRESS_LO) 

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// !!!!!!!! PCHITMP MUST ALWAYS BE UNCONDITIONAL OTHERWISE RISK THAT CONTROL LINE LIKE DI/DO MIGHT GO HIGH BETWEEN THE PCHITMP AND PC INSTRUCTIONS...
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
`define JMP_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_UNCONDITIONAL_PCHITMP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMP_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPC_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_UNCONDITIONAL_PCHITMP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPC_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPDO_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_UNCONDITIONAL_PCHITMP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPDO_IMMED(INST+1, ADDRESS_LONG & 8'hff)

`define JMPDI_IMMED16(INST, ADDRESS_LONG)       \
  `JMP_UNCONDITIONAL_PCHITMP_IMMED(INST, ADDRESS_LONG >>  8) \
  `JMPDI_IMMED(INST+1, ADDRESS_LONG & 8'hff)

