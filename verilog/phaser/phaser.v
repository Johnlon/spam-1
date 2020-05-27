/*
clock 0 is dead
clock 1/2/3 fetch
clock 4/5/6/7 decode
clock 8 reg
clock 10 is dead

**/
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
    input mr,

    output [9:0] seq,

    output _phaseFetch, phaseFetch , phaseDecode , phaseExec
);

    wire _co; 

    // MR=H is async reset
    hc744017 decade(.cp0(clk), ._cp1(1'b0), .mr(mr), .q(seq), ._co);

    // construct using 3 input nor gates so we can OR mr into the trigger
    // first clock (seq=1) does nothing - used to allow fetch +ve edge once cycle AFTER MR released otherwise that edge is missed
    wire phaseFetch_begin = !mr & seq[0]; // 3 clocks 
    wire phaseFetch_end = mr | seq[4];
    wire phaseDecode_begin = seq[4]; // 4 clocks
    wire phaseDecode_end = mr |seq[8]; // ensure phase is reset when MR triggers
    wire phaseExec_begin = seq[8];   // 2 clock
    wire phaseExec_end = mr |seq[0]; // ensure phase is reset when MR triggers
/*
    wire phaseFetch_begin = seq[1]; // 3 clocks 
    wire phaseFetch_end = mr | seq[4];
    wire phaseDecode_begin = seq[4]; // 4 clocks
    wire phaseDecode_end = mr |seq[8]; // ensure phase is reset when MR triggers
    wire phaseExec_begin = seq[8];   // 1 clock
    wire phaseExec_end = mr |seq[9]; // ensure phase is reset when MR triggers
*/
    sr phase1(.s(phaseFetch_begin), .r(phaseFetch_end), .q(phaseFetch));
    sr phase2(.s(phaseDecode_begin), .r(phaseDecode_end), .q(phaseDecode));
    sr phase3(.s(phaseExec_begin), .r(phaseExec_end), .q(phaseExec));

    assign #(9) _phaseFetch = !phaseFetch; // INVERTER : dont use _Q as this is NOT guaranteed to be invcerse of Q
    
    if (LOG)    
    always @* 
         $display("%9t PHASER ", $time,
            //" clk=%1b", clk, 
            " seq=%10b", seq,
            " phase(FDE=%3b) _phase(F=%b)", {phaseFetch, phaseDecode, phaseExec}, _phaseFetch ,
            " trigs(Fsr=%2b Dsr=%2b Esr=%2b) ",
                {phaseFetch_begin, phaseFetch_end} ,
                {phaseDecode_begin, phaseDecode_end} ,
                {phaseExec_begin,  phaseExec_end },
            " mr=%1b ", mr
            );

endmodule : phaser
// verilator lint_on ASSIGNDLY

`endif

