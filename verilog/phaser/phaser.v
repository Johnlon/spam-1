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

    output [9:0] seq,

    output _phaseFetch, phaseFetch , phaseDecode , phaseExec
);
    wire mr = ! _mr;

    wire _co; 

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
    
    if (LOG)    
    always @ * 
         $display("%9t CTRL_SEL", $time,
            " clk=%1b", clk, 
            " seq=%10b ", seq,
            " phase(FDE=%3b)", seq, {phaseFetch, phaseDecode, phaseExec} ,
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

