
`ifndef V_PHASED_CLOCK
`define V_PHASED_CLOCK

`include "../74109/hct74109.v" 
`include "../74139/hct74139.v"
`include "../lib/assertion.v"
`timescale 1ns/1ns

// http://www.brannonelectronics.com/images/4%20Phase%20Clock%20Generator.pdf
// relies on PD of demux being less than the PD of flipflops

module phased_clock ( 
    input _MR,
    input clk,
    output clk_1,
    output clk_2,
    output clk_3,
    output clk_4
);

    parameter LOG=0;

    wire _sd=1;

    wire q0, _q0;
    wire q1, _q1;

    hct74109  jk0 (.j(_q1), ._k(_q1), ._sd(_sd), ._rd(_MR), .clk(clk), .q(q0), ._q(_q0));

    hct74109  jk1 (.j(q0), ._k(q0), ._sd(_sd), ._rd(_MR), .clk(clk), .q(q1), ._q(_q1));

    wire [1:0] Ab;
    logic [3:0] _Yb;

    hct74139 #(.LOG(0)) decoder( ._Ea(clk | !_MR), .Aa({q1, q0}), ._Ya({clk_4, clk_3, clk_2, clk_1}), ._Eb(1'b1), .Ab, ._Yb(_Yb)); 

endmodule  
 

`endif
