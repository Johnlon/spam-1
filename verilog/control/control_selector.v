`ifndef V_CONTROL_SELECT
`define V_CONTROL_SELECT


`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../4PhaseClock/phased_clock.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps

module control_selector #(parameter LOG=0) 
(
    input clk, 
    input _MR,

    input [7:0] hi_rom,

    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_pc, // enable PC onto address bus - register direct addressing - ????
    output _addrmode_direct, // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing

    output [3:0] rbus_dev,
    output [3:0] lbus_dev,
    output [4:0] targ_dev,

    output [4:0] aluop
);

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

    // negative non-overlapping 3 phase clock (ignore phase 4)
    wire clk_1 , clk_2 , clk_3 , clk_4;

    phased_clock phclk(._MR, .clk, .clk_1 , .clk_2 , .clk_3 , .clk_4);

    
    assign _addrmode_pc = clk_1;
    



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
);
*/

endmodule : control_selector
// verilator lint_on ASSIGNDLY

`endif
