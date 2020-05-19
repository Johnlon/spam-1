`ifndef V_CONTROL_SELECT
`define V_CONTROL_SELECT


`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../phaser/phaser.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module control_selector #(parameter LOG=0) 
(
    input clk, 
    input _mr,

    input [7:0] hi_rom,

    output [9:0] seq,

    output _addrmode_pc, // enable PC onto address bus - register direct addressing - ????
    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_direct, // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing

    output [3:0] rbus_dev,
    output [3:0] lbus_dev,
    output [4:0] targ_dev,

    output [4:0] aluop
);
    wire mr = ! _mr;

    wire _co; 

    // PHASING ======
    wire _phaseFetch, phaseFetch , phaseDecode , phaseExec;
    wire [9:0] seq;

    phaser ph(.clk, .mr, .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec);

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
            " regmode=%1b _phaseFetch=%1b/phaseFetch=%1b", addr_mode, _phaseFetch, phaseFetch
            );

/*
if (LOG)    always @ * 
         $display("%8d CTRL_SEL", $time,
          " hi=%08b", hi_rom, 
            " _rom_out=%1b, _ram_out=%1b, _alu_out=%1b, _uart_out=%1b", _rom_out, _ram_out, _alu_out, _uart_out,
            " device_in=%5b", device_in, 
            " force_x_0=%1b", force_x_val_to_zero, 
            " force_alu_passx=%1b", force_alu_op_to_passx, 
            " _ram_zp=%1b", _ram_zp
    ,implied_dev_top_bit , _is_non_reg_override, _is_reg_override, " %05b %05b %08b", device_sel_pre, device_in, device_sel_out
*/


endmodule : control_selector
// verilator lint_on ASSIGNDLY

`endif

