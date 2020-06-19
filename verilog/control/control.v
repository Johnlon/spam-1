// FIXME MAKE ALL THE tri WIRES tri0

/*
This code generates a momentary address mode conflict during propagation of the signals when transitioning back to fetch.
Wasn't able to avoid it without a lot more h/w. 
*/

/*
// constants 
// bit 23 can dictate addressing mode as we only have 6 op codes and only 3 use either mode
parameter [2:0] op_DEV_eq_ALU_sel       = 0; // == RBUSDEV=ROM[8:5]    LBUSDEV=ROM[12:9]   ALUOP=ROM[4:0]   TARG=IR[20:16]  ADDRMODE=REGISTER  // ie mar
parameter [2:0] op_DEV_eq_CONST8_sel    = 1; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=REGISTER  
parameter [2:0] op_DEVP_eq_CONST16_sel  = 2; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=REGISTER  // stretch objective - load some fixed reg pair (eg if A is targ then A*B, if MARLO then its MARLO+HI)
// op 3 unused
parameter [2:0] op_DEV_eq_ROM_ABS_sel   = 4; // == RBUSDEV='ROM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=DIRECT // MUST BE VIA IR for all 3 bytes otherwise indexing the ROM using ROM[15:0] will change the logic mid exec
parameter [2:0] op_DEV_eq_RAM_ABS_sel   = 5; // == RBUSDEV='RAM'       LBUSDEV=XXXX        ALUOP='PASSR'    TARG=IR[20:16]  ADDRMODE=DIRECT 
parameter [2:0] op_RAM_ABS_eq_DEV_sel   = 6; // == RBUSDEV=[19:16]     LBUSDEV=XXXX        ALUOP='PASSL'    TARG='RAM'      ADDRMODE=DIRECT // LBUS WILL TRANSMIT A REGISTER
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
`define DECODE_ADDRMODES (!_addrmode_pc ? "pc" : !_addrmode_register?  "reg" : !_addrmode_direct? "dir": "---") 
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
    localparam _AMODE_DIR=3'b110;

    // ops
    localparam [2:0] OP_dev_eq_xy_alu =0;
    localparam [2:0] OP_dev_eq_const8 =1;
    localparam [2:0] OP_dev_eq_const16 =2;
    localparam [2:0] OP_3_unused =3;
    localparam [2:0] OP_dev_eq_rom_direct =4;
    localparam [2:0] OP_dev_eq_ram_direct =5;
    localparam [2:0] OP_ram_direct_eq_dev =6;
    localparam [2:0] OP_7_unused =7;

    // sources or dests
    localparam [3:0] DEV_rega = 0; 
    localparam [3:0] DEV_regb = 1; 
    localparam [3:0] DEV_regc = 2; 
    localparam [3:0] DEV_regd = 3; 
    localparam [3:0] DEV_rege = 4; 
    localparam [3:0] DEV_regf = 5; 
    localparam [3:0] DEV_regg = 6; 
    localparam [3:0] DEV_regh = 7; 
    localparam [3:0] DEV_flags =8;
    localparam [3:0] DEV_instreg = 9;
    localparam [3:0] DEV_ram = 10;
    localparam [3:0] DEV_rom = 11;
    localparam [3:0] DEV_marlo = 12;
    localparam [3:0] DEV_marhi = 13;
    localparam [3:0] DEV_uart = 14;
    localparam [3:0] DEV_reg_not_used_1 = 15;

    // dests only
    localparam [4:0] DEV_pchitmp = 16;
    localparam [4:0] DEV_pclo= 17;
    localparam [4:0] DEV_pc= 18;
    localparam [4:0] DEV_jmpo= 19;
    localparam [4:0] DEV_jmpz= 20;
    localparam [4:0] DEV_jmpc= 21;
    localparam [4:0] DEV_jmpdi= 22;
    localparam [4:0] DEV_jmpdo= 23;

    // targets
    function [4:0] TDEV([3:0] x);
        TDEV = {1'b0, x};
    endfunction

    // these can be src or dest
    `define TARGL(DNAME) localparam [4:0] TDEV_``DNAME`` = {1'b0, DEV_``DNAME``};

    // dest only
    `define TARGH(DNAME) localparam [4:0] TDEV_``DNAME`` = DEV_``DNAME``;

    `TARGL(ram)
    `TARGL(rom)
    `TARGL(marlo)
    `TARGL(marhi)
    `TARGL(uart)
    `TARGL(rega)
    `TARGL(regb)
    `TARGL(regc)
    `TARGL(regd)

    // 9-15 - todo
    `TARGH(pchitmp)
    `TARGH(pclo)
    `TARGH(pc)
    `TARGH(jmpo)
    `TARGH(jmpz)
    `TARGH(jmpc)
    `TARGH(jmpdi)
    `TARGH(jmpdo)
    // 24-32 - todo

    function string devname([3:0] dev); 
    begin
        case (dev)
            00: devname = "RAM";
            01: devname = "ROM";
            02: devname = "MARLO";
            03: devname = "MARHI";
            04: devname = "UART";
            05: devname = "REGA"; // 1
            06: devname = "REGB"; // 2
            07: devname = "REGC"; // 3
            08: devname = "REGD"; // 4
            09: devname = "REGE"; // 5
            10: devname = "REGF"; // 6
            11: devname = "REGG"; // 7
            12: devname = "REGH"; // 8
            13: devname = "FLAGS";
            14: devname = "NOT_USED_1";
            15: devname = "INSTREG";
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

    function string fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct); 
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
                 control.OP_dev_eq_rom_direct : opName = "dev_eq_rom_direct";
                 control.OP_dev_eq_ram_direct : opName = "dev_eq_ram_direct";
                 control.OP_ram_direct_eq_dev : opName = "ram_direct_eq_dev";
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

module memory_address_mode_decoder #(parameter LOG=1) 
(
    input [2:0] ctrl,

    input phaseFetch,
    input phaseDecode, 
    input phaseExec, 
    input _phaseFetch,

    output _addrmode_pc, // enable PC onto address bus
    output _addrmode_register, // enable MAR onto address bus - register direct addressing - op 0
    output _addrmode_direct // enable ROM[15:0] onto address bus, needs an IR in implementation - direct addressing
);

    // ADDRESS MODE DECODING =====    
    // as organised above then OPS0/2 are all REGISTER and OPS 4/5/6 are all DIRECT and OP 1 is PC
    wire [7:0] _decoded;
    hct74138 decode_op( .Enable1_bar(1'b0), .Enable2_bar(1'b0), .Enable3(_phaseFetch), .A(ctrl), .Y(_decoded)); 

    assign _addrmode_pc = _phaseFetch;

    // op 3 & 7 not defined yet
    and #(10) o1(_addrmode_register, _decoded[0], _decoded[1], _decoded[2]); 
    and #(10) o2(_addrmode_direct , _decoded[4], _decoded[5], _decoded[6]);


    if (0)    
    always @ * begin;
         $display("%9t ADDRMODE_DECODE", $time,
            " ctrl=%3b _decoded=%08b", ctrl, _decoded,
    "_addrmode_pc=%1b, _phaseFetch=%1b , _decoded[1]=%1b", _addrmode_pc, _phaseFetch , _decoded[1],
//            " isDir=%b ", isDir,
 //           " isReg=%b ", isReg,
            " phase(FDE=%1b%1b%1b) ", phaseFetch, phaseDecode, phaseExec, 
            "    _addrmode(pc=%b,reg=%b,dir=%b)", _addrmode_pc, _addrmode_register, _addrmode_direct,
            " _amode=%3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct)
            );
    end

    task DUMP; 
    begin
         $display("%9t AMODE_DECODER", $time,
            " phase(FDE=%1b%1b%1b) ", phaseFetch, phaseDecode, phaseExec
            );
         $display("%9t AMODE_DECODER", $time,
  //          " isDir=%b ", isDir,
   //         " isReg=%b ", isReg,
            " ctrl=%3b _decoded=%08b", ctrl, _decoded,
            " _addrmode(pc=%b,reg=%b,dir=%b)", _addrmode_pc, _addrmode_register, _addrmode_direct,
            " _amode=%3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct)
            );
    end
    endtask


endmodule : memory_address_mode_decoder

module op_decoder #(parameter LOG=1) 
(
    input [7:0] data_hi, data_mid, data_lo,

    output tri [3:0] rbus_dev,
    output tri [3:0] lbus_dev,
    output tri [4:0] targ_dev,
    output tri [4:0] alu_op
);

    wire [23:0] rom_data = {data_hi, data_mid, data_lo};
    wire [2:0] ctrl = rom_data[23:21];

    hct74138 op_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(ctrl));

    wire _op_dev_eq_xy_alu;
    wire _op_dev_eq_const8;
    wire _op_dev_eq_const16;
    wire _op_3_unused;
    wire _op_dev_eq_rom_direct;
    wire _op_dev_eq_ram_direct;
    wire _op_ram_direct_eq_dev;
    wire _op_7_unused;
    assign {
        _op_7_unused,
        _op_ram_direct_eq_dev,
        _op_dev_eq_ram_direct,
        _op_dev_eq_rom_direct,
        _op_3_unused,
        _op_dev_eq_const16,
        _op_dev_eq_const8,
        _op_dev_eq_xy_alu
    } = op_demux.Y;


     // target device sel
    tri [7:0] targ_dev_out; 
    wire #(10) op_ram_direct_eq_dev = ! _op_ram_direct_eq_dev;  // NOT GATE
    hct74245ab tdev_from_instruction(.A({3'bz, rom_data[20:16]}), .B(targ_dev_out), .nOE(op_ram_direct_eq_dev)); // ie when NOT a ram direct then user the 20:16
    hct74245ab tdev_eq_ram(.A({3'b0, control.TDEV_ram}), .B(targ_dev_out), .nOE(_op_ram_direct_eq_dev)); // only op_ram_direct_eq_dev has targ forced to RAM
    assign targ_dev = targ_dev_out[4:0];

    // l device sel 
    tri [7:0] lbus_dev_out;
    hct74245ab ldev_from_instruction(.A({4'b0, rom_data[12:9]}), .B(lbus_dev_out), .nOE(op_ram_direct_eq_dev));
    hct74245ab ldev_from_instruction_ramdirect_eq_dev(.A({4'b0, rom_data[19:16]}), .B(lbus_dev_out), .nOE(_op_ram_direct_eq_dev));
    assign lbus_dev = lbus_dev_out[3:0];

    // r device sel
    tri [7:0] rbus_dev_out;
    wire _force_source_rom = _op_dev_eq_rom_direct; // JUST A WIRE
    wire #(10) _force_source_instreg = _op_dev_eq_const8 &  _op_dev_eq_const16; // 2 INPUT AND GATE
    hct74245ab rdev_from_instruction_for_aluop(.A({4'b0, rom_data[8:5]}), .B(rbus_dev_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab rdev_eq_ram(.A({4'b0, control.DEV_ram}), .B(rbus_dev_out), .nOE(_op_dev_eq_ram_direct));
    hct74245ab rdev_eq_rom(.A({4'b0, control.DEV_rom}), .B(rbus_dev_out), .nOE(_force_source_rom));
    hct74245ab rdev_eq_instreg(.A({4'b0, control.DEV_instreg}), .B(rbus_dev_out), .nOE(_force_source_instreg));
    assign rbus_dev = rbus_dev_out[3:0]; 

    // aluop
    tri [7:0] alu_op_out;
    wire #(10) _force_passr = _force_source_rom & _op_dev_eq_ram_direct & _force_source_instreg; // source ram or rom or ireg means passr : 3 INPUT AND GATE
    hct74245ab aluopfrom_instruction(.A({3'b0, rom_data[4:0]}), .B(alu_op_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab aluop_eq_passl(.A({3'b0, alu_func.ALUOP_PASSL}), .B(alu_op_out), .nOE(_op_ram_direct_eq_dev));
    hct74245ab aluop_eq_passr(.A({3'b0, alu_func.ALUOP_PASSR}), .B(alu_op_out), .nOE(_force_passr));
    assign alu_op = alu_op_out[4:0];

    if (1)    
    always @ *  begin
         $display("%9t OP_DECODER", $time,
                " data=%8b:%8b:%8b", data_hi, data_mid, data_lo,
                " ctrl=%3b (%s)\t ", ctrl, control.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y,
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " alu_op=%5b(%s)", alu_op, alu_func.aluopName(alu_op)
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
                " alu_op=%5b(%s)", alu_op, alu_func.aluopName(alu_op)
            );
    end 
    endtask

endmodule : op_decoder

`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
