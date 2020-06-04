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
parameter [2:0] op_RAM_ABS_eq_DEV_sel   = 6; // == RBUSDEV=[19:16]     LBUSDEV=XXXX        ALUOP='PASSL'    TARG='RAM'      ADDRMODE=IMMEDIATE // LBUS WILL TRANSMIT A REGISTER
// op 7 unused
*/


`ifndef V_CONTROL
`define V_CONTROL

`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../alu/alu_func.v"
`include "control.v"

`timescale 1ns/1ns

`define DECODE_PHASES   (phaseFetch ? "fetch" : phaseDecode?  "decode" : phaseExec? "exec": "---")
`define DECODE_PHASE   logic [6*8-1:0] sPhase; assign sPhase = `DECODE_PHASES;

// unlike an assign this executes instantaneously but not referentially transparent
`define DECODE_ADDRMODES (!_addrmode_pc ? "pc" : !_addrmode_register?  "reg" : !_addrmode_immediate? "imm": "---") 
`define DECODE_ADDRMODE  logic [3*8-1:0] sAddrMode; assign sAddrMode = `DECODE_ADDRMODES;

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off MULTITOP
// verilator lint_off DECLFILENAME
// verilator lint_off UNUSED

module control;
 
    localparam PHASE_NONE = 3'b000;
    localparam PHASE_FETCH = 3'b100;
    localparam PHASE_DECODE = 3'b010;
    localparam PHASE_EXEC = 3'b001;
   
    localparam _AMODE_NONE=3'b111;
    localparam _AMODE_PC=3'b011;
    localparam _AMODE_REG=3'b101;
    localparam _AMODE_IMM=3'b110;

    // ops
    localparam [2:0] OP_dev_eq_xy_alu =0;
    localparam [2:0] OP_dev_eq_const8 =1;
    localparam [2:0] OP_dev_eq_const16 =2;
    localparam [2:0] OP_3_unused =3;
    localparam [2:0] OP_dev_eq_rom_immed =4;
    localparam [2:0] OP_dev_eq_ram_immed =5;
    localparam [2:0] OP_ram_immed_eq_dev =6;
    localparam [2:0] OP_7_unused =7;

    // sources
    localparam [3:0] DEV_ram = 0;
    localparam [3:0] DEV_rom = 1;
    localparam [3:0] DEV_marlo = 2;
    localparam [3:0] DEV_marhi = 3;
    localparam [3:0] DEV_uart = 4;
    localparam [3:0] DEV_rega = 5;
    localparam [3:0] DEV_regb = 6;
    localparam [3:0] DEV_regc = 7;
    localparam [3:0] DEV_regd = 8;
    localparam [3:0] DEV_rege = 9;
    localparam [3:0] DEV_regf = 10;
    localparam [3:0] DEV_regg = 11;
    localparam [3:0] DEV_regh = 12;
    localparam [3:0] DEV_regi = 13;
    localparam [3:0] DEV_regnu1 = 14;
    localparam [3:0] DEV_regnu2 = 15;

    // targets
    function [4:0] TDEV([3:0] x);
        TDEV = {1'b0, x};
    endfunction

    `define TARG(DNAME) localparam [4:0] TDEV_``DNAME`` = {1'b0, DEV_``DNAME``};
    `define TARGN(DNAME, N) localparam [4:0] TDEV_``DNAME`` = N;

    `TARG(ram)
    `TARG(rom)
    `TARG(marlo)
    `TARG(marhi)
    `TARG(uart)
    `TARG(rega)
    `TARG(regb)
    `TARG(regc)
    `TARG(regd)
    // 9-15 - todo
    `TARGN(pchitmp, 16)
    `TARGN(pclo, 17)
    `TARGN(pc, 18)
    `TARGN(jmpo, 19)
    `TARGN(jmpz, 20)
    `TARGN(jmpc, 21)
    `TARGN(jmpdi, 22)
    `TARGN(jmpdo, 23)
    // 24-32 - todo

    function string devname([3:0] dev); 
    begin
        case (dev)
            00: devname = "RAM";
            01: devname = "ROM";
            02: devname = "MARLO";
            03: devname = "MARHI";
            04: devname = "REGA";
            05: devname = "REGB";
            06: devname = "REGC";
            07: devname = "REGD";
            08: devname = "REGE";
            09: devname = "REGF";
            10: devname = "REGF";
            11: devname = "REGH";
            13: devname = "REGI";
            14: devname = "REGJ";
            15: devname = "FLAGS";
            default: begin
                string n; 
                $sformat(n,"??(unknown %4b)", dev);
                devname = n;
            end
        endcase
    end
    endfunction    

    function string tdevname([4:0] tdev); 
    begin
        if (tdev[4] == 0) 
        begin 
            tdevname = devname(tdev[3:0]);
        end
        else 
        begin
            case (tdev)
                00: tdevname = "PCHITMP";
                01: tdevname = "PCLO";
                02: tdevname = "PC";
                03: tdevname = "JMPO";
                04: tdevname = "JMPZ";
                05: tdevname = "JPMC";
                06: tdevname = "JMPDI";
                07: tdevname = "JMPDO";
                08: tdevname = "JMPEQ";
                09: tdevname = "JMPNE";
                10: tdevname = "JMPGT";
                11: tdevname = "JMPLT";
                13: tdevname = "NU13";
                14: tdevname = "NU14";
                15: tdevname = "NU15";
                default: begin
                    string n; 
                    $sformat(n,"??(unknown %5b)", tdev);
                    tdevname = n;
                end
            endcase
        end
    end
    endfunction    

    function string fPhase(phaseFetch, phaseDecode, phaseExec); 
    begin
            fPhase = `DECODE_PHASES;
    end
    endfunction

    function string fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate); 
    begin
            fAddrMode = `DECODE_ADDRMODES;
    end
    endfunction


    function string opName([2:0] opcode); 
    begin
        string ret;

        begin
            case(opcode)
                 control.OP_dev_eq_xy_alu : opName = "dev_eq_xy_alu";
                 control.OP_dev_eq_const8 : opName = "dev_eq_const8";
                 control.OP_dev_eq_const16 : opName = "dev_eq_const16";
                 control.OP_3_unused : opName = "3_unused";
                 control.OP_dev_eq_rom_immed : opName = "dev_eq_rom_immed";
                 control.OP_dev_eq_ram_immed : opName = "dev_eq_ram_immed";
                 control.OP_ram_immed_eq_dev : opName = "ram_immed_eq_dev";
                 control.OP_7_unused : opName = "7_unused";

                 default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    opName = ret;
                 end
            endcase
        end
    end
    endfunction

endmodule: control

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
    // `DECODE_ADDRMODE
    
    // ADDRESS MODE DECODING =====    
    // as organised above then OPS0/1/2 are all REGISTER and OPS 4/5/6 are all IMMEDIATE 

    wire isImm, isReg;
    assign isImm = ctrl[2];
    nand #(10) o0(isReg, ctrl[2], ctrl[2]);
    nand #(10) o1(_addrmode_register , _phaseFetch , isReg);
    nand #(10) o2(_addrmode_immediate , _phaseFetch , isImm);
    assign _addrmode_pc = _phaseFetch;

    if (0)    
    always @ * begin;
         $display("%9t ADDRMODE_DECODE", $time,
            " ctrl=%3b", ctrl, 
            " phase(FDE=%1b%1b%1b) ", phaseFetch, phaseDecode, phaseExec, 
            "    _addrmode(pc=%b,reg=%b,imm=%b)", _addrmode_pc, _addrmode_register, _addrmode_immediate,
            " _amode=%3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate)
            );
    end

    task DUMP; 
    begin
         $display("%9t AMODE_DECODER", $time,
            " phase(FDE=%1b%1b%1b) ", phaseFetch, phaseDecode, phaseExec
            );
         $display("%9t AMODE_DECODER", $time,
            " ctrl=%3b", ctrl, 
            " _addrmode(pc=%b,reg=%b,imm=%b)", _addrmode_pc, _addrmode_register, _addrmode_immediate,
            " _amode=%3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate)
            );
    end
    endtask


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
    hct74245ab tdev_eq_ram(.A({3'b0, control.TDEV_ram}), .B(targ_dev_out), .nOE(_op_ram_immed_eq_dev)); // only op_ram_immed_eq_dev has targ forced to RAM
    assign targ_dev = targ_dev_out[4:0];

    // l device sel
    assign lbus_dev = rom_data[12:9];

    // r device sel
    tri [7:0] rbus_dev_out;
    wire #(10) _force_source_rom = _op_dev_eq_const8 &  _op_dev_eq_const16 &  _op_dev_eq_rom_immed; // 3 INPUT AND GATE
    hct74245ab rdev_from_instruction_aluop(.A({4'b0, rom_data[8:5]}), .B(rbus_dev_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab rdev_from_instruction_ramimmed(.A({4'b0, rom_data[19:16]}), .B(rbus_dev_out), .nOE(_op_ram_immed_eq_dev));
    hct74245ab rdev_eq_ram(.A({4'b0, control.DEV_ram}), .B(rbus_dev_out), .nOE(_op_dev_eq_ram_immed));
    hct74245ab rdev_eq_rom(.A({4'b0, control.DEV_rom}), .B(rbus_dev_out), .nOE(_force_source_rom));
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
                " ctrl=%3b (%s)\t ", ctrl, control.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y,
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " aluop=%5b(%s)", aluop, alu_func.aluopName(aluop)
            );
    end

    task DUMP; begin
        $display("%9t OP_DECODER", $time,
                " data=%8b:%8b:%8b", data_hi, data_mid, data_lo,
                " ctrl=%3b (%s)", ctrl, control.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y
            );
        $display("%9t OP_DECODER", $time,
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " aluop=%5b(%s)", aluop, alu_func.aluopName(aluop)
            );
    end 
    endtask

endmodule : op_decoder

`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP