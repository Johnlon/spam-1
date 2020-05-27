
`ifndef V_DECODING
`define V_DECODING

`define DECODE_PHASE   logic [6*8-1:0] sPhase; assign sPhase = phaseFetch ? "fetch" : phaseDecode?  "decode" : phaseExec? "exec": "---";
`define DECODE_ADDRMODE  logic [3*8-1:0] aAddrMode; assign aAddrMode = !_addrmode_pc ? "pc" : !_addrmode_register?  "reg" : !_addrmode_immediate? "imm": "---";

`endif