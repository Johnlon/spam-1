
`ifndef V_CONTROL_DECODING
`define V_CONTROL_DECODING

`include "control_params.v"

`define DECODE_PHASES   (phaseFetch ? "fetch" : phaseDecode?  "decode" : phaseExec? "exec": "---")
`define DECODE_PHASE   logic [6*8-1:0] sPhase; assign sPhase = `DECODE_PHASES;

// unlike an assign this executes instantaneously but not referentially transparent
`define DECODE_ADDRMODES (!_addrmode_pc ? "pc" : !_addrmode_register?  "reg" : !_addrmode_immediate? "imm": "---") 
`define DECODE_ADDRMODE  logic [3*8-1:0] sAddrMode; assign sAddrMode = `DECODE_ADDRMODES;

`timescale 1ns/1ns

module control_decoding;
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
                 control_params.OP_dev_eq_xy_alu : opName = "dev_eq_xy_alu";
                 control_params.OP_dev_eq_const8 : opName = "dev_eq_const8";
                 control_params.OP_dev_eq_const16 : opName = "dev_eq_const16";
                 control_params.OP_3_unused : opName = "3_unused";
                 control_params.OP_dev_eq_rom_immed : opName = "dev_eq_rom_immed";
                 control_params.OP_dev_eq_ram_immed : opName = "dev_eq_ram_immed";
                 control_params.OP_ram_immed_eq_dev : opName = "ram_immed_eq_dev";
                 control_params.OP_7_unused : opName = "7_unused";

                 default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    opName = ret;
                 end
            endcase
        end
    end
    endfunction

endmodule: control_decoding
    
`endif
