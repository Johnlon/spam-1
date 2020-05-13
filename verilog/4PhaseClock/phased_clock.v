
`include "../74109/hct74109.v" 
`include "../74139/hct74139.v"
`include "../lib/assertion.v"
`timescale 1ns/1ns

// http://www.brannonelectronics.com/images/4%20Phase%20Clock%20Generator.pdf
// relies on PD of demux being less than the PD of flipflops

module phased_clock ( 
    input clk,
    input _reset,
    output phase0,
    output phase1,
    output phase2,
    output phase3);

    parameter LOG=0;

   wire _sd=1;

   wire q0, _q0;
   wire q1, _q1;
 
   hct74109  jk0 (.j(_q1),
                  ._k(_q1),
                  ._sd(_sd), ._rd(_reset),
                  .clk(clk),
                  .q(q0),
                  ._q(_q0)
    );

   hct74109  jk1 (.j(q0),
                  ._k(q0),
                  ._sd(_sd), ._rd(_reset),
                  .clk(clk),
                  .q(q1),
                  ._q(_q1)
    );

    wire [1:0] unusedI = 0;
    wire [3:0] unusedO = 0;

    hct74139 decoder(
        ._Ea(clk | !_reset), 
        .Aa({q1, q0}),
        ._Ya({phase2, phase3, phase1, phase0}),
        ._Eb(1'b1), 
        .Ab(unusedI),
        ._Yb(unusedO)
    ); 

endmodule  
 

