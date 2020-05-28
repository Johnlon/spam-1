/*
This code generates a momentary conflict during propagation of the signals when transitioning back to fetch.
Wasn't able to avoid it without a lot more h/w. 
*/

`ifndef V_CONTROL_SELECT
`define V_CONTROL_SELECT

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

/*
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
*/

module control #(parameter LOG=1) 
(
    input clk, 
    input _mr,

    input [2:0] ctrl,

    input phaseFetch, 
    input phaseDecode, 
    input phaseExec, 
    input _phaseFetch,

    output _addrmode_pc, // enable PC onto address bus
    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_immediate, // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing

    output [3:0] rbus_dev,
    output [3:0] lbus_dev,
    output [4:0] targ_dev,

    output [4:0] aluop
);

    `include "decoding.v"
    // `DECODE_PHASE
    `DECODE_ADDRMODE


    // ADDRESS MODE DECODING =====    
    // as organised above then OPS0/1/2 are all REGISTER and OPS 4/5/6 are all IMMEDIATE 

    wire isImm, isReg;
    assign isImm = ctrl[2];
    nand #(10) o0(isReg, ctrl[2], ctrl[2]);
    nand #(10) o1(_addrmode_register , _phaseFetch , isReg);
    nand #(10) o2(_addrmode_immediate , _phaseFetch , isImm);
    assign _addrmode_pc = _phaseFetch;

/*
    wire #(10) _programPhase = ! _phaseFetch;
    wire #(10) isReg = rom_hi[7];
    wire #(10) isImm = ! rom_hi[7];
    or #(10) o1(_addrmode_register , _programPhase , isReg);
    or #(10) o2(_addrmode_immediate ,  _programPhase , isImm);
    assign #(10) _addrmode_pc = _phaseFetch;
*/
/*
    logic _Ea=1'b0;
    logic _Eb=1'b0;
    logic [1:0] Aa;
    logic [1:0] Ab='0;
    wire [3:0] _Ya;
    wire [3:0] _Yb;

    hct74139 #(.LOG(1)) demux(
                    ._Ea,
                    ._Eb,
                    .Aa,
                    .Ab,
                    ._Ya,
                    ._Yb
                    );
    
    assign Aa={_phaseFetch, rom_hi[7]};
    assign _addrmode_register = _Ya[1];
    assign _addrmode_immediate = _Ya[2];
    assign _addrmode_pc = _phaseFetch;
*/

    if (1)    
    always @ * 
         $display("%9t CTRL_SEL", $time,
            " clk=%1b", clk, 
            "    hibit=%b", ctrl[2], 
            //" phase FDE=%3b _phaseFetch=%b", {phaseFetch, phaseDecode, phaseExec}, _phaseFetch,
            " phase FDE=%3b ", {phaseFetch, phaseDecode, phaseExec}, 
//            "    _programPhase=%1b", _programPhase,  
//            " isReg=%b isImm=%b",isReg, isImm,
            "    _addrmode pri=%1b/%1b/%1b", _addrmode_pc, _addrmode_register, _addrmode_immediate,
            //" phase=%-s", sPhase,
            " _amode=%-3s", aAddrMode
            );


endmodule : control
// verilator lint_on ASSIGNDLY

`endif

