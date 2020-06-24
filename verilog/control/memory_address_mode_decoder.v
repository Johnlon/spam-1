
`ifndef V_CONTROL_MEM_ADDR_MODE
`define V_CONTROL_MEM_ADDR_MODE

`include "../74138/hct74138.v"
`include "../alu/alu_func.v"

`timescale 1ns/1ns

module memory_address_mode_decoder #(parameter LOG=1) 
(
    input _mr,
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
    // HACK hct74138 decode_op( .Enable1_bar(!_mr), .Enable2_bar(1'b0), .Enable3(_phaseFetch), .A(ctrl), .Y(_decoded)); 
    hct74138 decode_op( .Enable1_bar(1'b0), .Enable2_bar(1'b0), .Enable3(_phaseFetch), .A(ctrl), .Y(_decoded)); 

    assign _addrmode_pc = _phaseFetch;

    // op 3 & 7 not defined yet
    and #(10) o1(_addrmode_register, _decoded[0], _decoded[1], _decoded[2]); 
    and #(10) o2(_addrmode_direct , _decoded[4], _decoded[5], _decoded[6]);


    if (LOG)    
        always @ * begin;
         $display("%9t ADDRMODE_DECODE", $time,
            " _mr=%b ", _mr,
            " ctrl=%3b _decoded=%08b", ctrl, _decoded,
            " _addrmode_pc=%1b, _phaseFetch=%1b , _decoded[1]=%1b", _addrmode_pc, _phaseFetch , _decoded[1],
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

`endif
