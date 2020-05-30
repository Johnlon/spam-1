/*
This code generates a momentary conflict during propagation of the signals when transitioning back to fetch.
Wasn't able to avoid it without a lot more h/w. 
*/

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


`ifndef V_CONTROL_SELECT
`define V_CONTROL_SELECT

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../alu/alu_func.v"
`include "control_decoding.v"
`include "control_params.v"

`timescale 1ns/1ns

module address_mode_decoder #(parameter LOG=1) 
(
    input [2:0] ctrl,

    input phaseFetch,
    input phaseDecode, 
    input phaseExec, 
    input _phaseFetch,

    output _addrmode_pc, // enable PC onto address bus
    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_immediate // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing
);


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

    if (1)    
    always @ * begin;
         $display("%9t ADDRMODE_DECODE", $time,
            " ctrl=%3b", ctrl, 
            " phase(FDE=%1b%1b%1b) ", phaseFetch, phaseDecode, phaseExec, 
            "    _addrmode(pc=%b,reg=%b,imm=%b)", _addrmode_pc, _addrmode_register, _addrmode_immediate,
            " _amode=%3s", control_decoding.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate)
            );
    end


endmodule : address_mode_decoder

module op_decoder #(parameter LOG=1) 
(
    input [7:0] data_hi, data_mid, data_lo,

    output tri [3:0] rbus_dev,
    output tri [3:0] lbus_dev,
    output tri [4:0] targ_dev,
    output tri [4:0] aluop
);

    wire [23:0] rom_data = {data_hi, data_mid, data_lo};
    wire [2:0] ctrl = rom_data[23:21];

    hct74138 op_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(ctrl));

    wire _op_dev_eq_xy_alu;
    wire _op_dev_eq_const8;
    wire _op_dev_eq_const16;
    wire _op_3_unused;
    wire _op_dev_eq_rom_immed;
    wire _op_dev_eq_ram_immed;
    wire _op_ram_immed_eq_dev;
    wire _op_7_unused;
    assign {
        _op_7_unused,
        _op_ram_immed_eq_dev,
        _op_dev_eq_ram_immed,
        _op_dev_eq_rom_immed,
        _op_3_unused,
        _op_dev_eq_const16,
        _op_dev_eq_const8,
        _op_dev_eq_xy_alu
    } = op_demux.Y;


     // target device sel
    tri [7:0] targ_dev_out; 
    wire #(10) op_ram_immed_eq_dev = ! _op_ram_immed_eq_dev;  // NOT GATE
    hct74245ab tdev_from_instruction(.A({3'bz, rom_data[20:16]}), .B(targ_dev_out), .nOE(op_ram_immed_eq_dev));
    hct74245ab tdev_eq_ram(.A({3'b0, control_params.TDEV_ram}), .B(targ_dev_out), .nOE(_op_ram_immed_eq_dev)); // only op_ram_immed_eq_dev has targ forced to RAM
    assign targ_dev = targ_dev_out[4:0];

    // l device sel
    assign lbus_dev = rom_data[12:9];

    // r device sel
    tri [7:0] rbus_dev_out;
    wire #(10) _force_source_rom = _op_dev_eq_const8 &  _op_dev_eq_const16 &  _op_dev_eq_rom_immed; // 3 INPUT AND GATE
    hct74245ab rdev_from_instruction_aluop(.A({4'b0, rom_data[8:5]}), .B(rbus_dev_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab rdev_from_instruction_ramimmed(.A({4'b0, rom_data[19:16]}), .B(rbus_dev_out), .nOE(_op_ram_immed_eq_dev));
    hct74245ab rdev_eq_ram(.A({4'b0, control_params.DEV_ram}), .B(rbus_dev_out), .nOE(_op_dev_eq_ram_immed));
    hct74245ab rdev_eq_rom(.A({4'b0, control_params.DEV_rom}), .B(rbus_dev_out), .nOE(_force_source_rom));
    assign rbus_dev = rbus_dev_out[3:0]; 

    // aluop
    tri [7:0] aluop_out;
    wire #(10) _force_passr = _force_source_rom & _op_dev_eq_ram_immed; // source ram or rom means passr : 2 INPUT AND GATE
    hct74245ab aluopfrom_instruction(.A({3'b0, rom_data[4:0]}), .B(aluop_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab aluop_eq_passl(.A({3'b0, alu_func.ALUOP_PASSL}), .B(aluop_out), .nOE(_op_ram_immed_eq_dev));
    hct74245ab aluop_eq_passr(.A({3'b0, alu_func.ALUOP_PASSR}), .B(aluop_out), .nOE(_force_passr));
    assign aluop = aluop_out[4:0];

    if (1)    
    always @ *  begin
         $display("%9t OP_DECODER", $time,
                " data=%8b:%8b:%8b", data_hi, data_mid, data_lo,
                " ctrl=%3b (%s)", ctrl, control_decoding.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y,
                " tdev=%5b", targ_dev,
                " ldev=%4b", lbus_dev,
                " rdev=%4b", rbus_dev,
                " aluop=%5b(%s)", aluop, alu_func.aluopName(aluop),
                " _op_ram_immed_eq_dev=%b", _op_ram_immed_eq_dev
            );
    end


endmodule : op_decoder

`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
