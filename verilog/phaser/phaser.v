`ifndef V_PHASER
`define V_PHASER


`include "../744017/hc744017.v"
`include "../srflipflop/sr.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module phaser #(parameter LOG=0) 
(
    input clk, 
    input _mr,

    input [7:0] hi_rom,

    output [9:0] seq,

    output _addrmode_pc, // enable PC onto address bus - register direct addressing - ????
    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_direct // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing
);
    wire mr = ! _mr;

    wire _co; 

    // negative non-overlapping 3 phase clock (ignore phase 4)
    wire _phaseFetch, phaseFetch , phaseDecode , phaseExec;

    hc744017 decade(.cp0(clk), .mr, .q(seq));

    // construct using 3 input nor gates so we can OR mr into the trigger
    wire phaseFetch_begin = seq[0];
    wire phaseFetch_end = seq[4];
    wire phaseDecode_begin = seq[4];
    wire phaseDecode_end = mr |seq[8]; // ensure phase is reset when MR triggers
    wire phaseExec_begin = seq[8];
    wire phaseExec_end = mr |seq[9]; // ensure phase is reset when MR triggers

    sr phase1(.s(phaseFetch_begin), .r(phaseFetch_end), .q(phaseFetch), ._q(_phaseFetch));
    sr phase2(.s(phaseDecode_begin), .r(phaseDecode_end), .q(phaseDecode));
    sr phase3(.s(phaseExec_begin), .r(phaseExec_end), .q(phaseExec));
    
    // constants 
    // bit 23 can dictate addressing mode as we only have 6 op codes and only 3 use either mode
    parameter [2:0] op_DEV_eq_ALU_sel       = 0; // == RBUSDEV=ROM[8:5]    LBUSDEV=ROM[12:9]   ALUOP=ROM[4:0]   TARG=IR[20:16]  ADDRMODE=REGISTER  // ie mar
    parameter [2:0] op_DEV_eq_CONST8_sel    = 1; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=REGISTER  
    parameter [2:0] op_DEVP_eq_CONST16_sel  = 2; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=REGISTER  // stretch objective - load some fixed reg pair (eg if A is targ then A*B, if MARLO then its MARLO+HI)
    // op 3 unused
    parameter [2:0] op_DEV_eq_ROM_ABS_sel   = 4; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=IMMEDIATE // MUST BE VIA IR for all 3 bytes otherwise indexing the ROM using ROM[15:0] will change the logic mid exec
    parameter [2:0] op_DEV_eq_RAM_ABS_sel   = 5; // == RBUSDEV='RAM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=IMMEDIATE 
    parameter [2:0] op_RAM_ABS_eq_DEV_sel   = 6; // == RBUSDEV=XXXX        LBUSDEV=ROM[19:16]  ALUOP='PASSL'    TARG='RAM'      ADDRMODE=IMMEDIATE // LBUS WILL TRANSMIT A REGISTER
    // op 7 unused


    // as organised above then 0/1/2 are all REGISTER and 4/5/6 are all IMMEDIATE 
    // addr_mode = 0 means REGISTER, 1 means IMMEDIATE
    wire addr_mode = hi_rom[7];
    wire #(10) _addr_mode = ! addr_mode;


//    phased_clock phclk(._mr, .clk, .clk_1 , .clk_2 , .clk_3 , .clk_4);

    
    assign _addrmode_pc = _phaseFetch;
    assign _addrmode_register = phaseFetch | addr_mode;
    assign _addrmode_direct =  phaseFetch | _addr_mode;
    

    if (LOG)    
    always @ * 
         $display("%9t CTRL_SEL", $time,
          " hi=%08b", hi_rom, 
            " clk=%1b", clk, 
            " seq=%10b phase(fir=%3b)", seq, {phaseFetch, phaseDecode, phaseExec} ,
            " amode(pc=%1b,reg=%1b,dir=%1b)", _addrmode_pc, _addrmode_register, _addrmode_direct, 
            " regmode=%1b _phaseFetch=%1b/phaseFetch=%1b", addr_mode, _phaseFetch, phaseFetch,
            " trigs=%6b ",
            {
                phaseFetch_begin ,
                phaseFetch_end ,
                phaseDecode_begin ,
                phaseDecode_end ,
                phaseExec_begin ,
                phaseExec_end 
            }
            );

endmodule : phaser
// verilator lint_on ASSIGNDLY

`endif

