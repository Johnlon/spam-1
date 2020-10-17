`ifndef V_SR
`define V_SR

/* verilator lint_off UNOPT */

`timescale 1ns/1ns

// going straight from 11 to 00 will cause oscillation.
// if set no propagation delay then it runs and doesn't oscillate but drops to q/_q=01 presumanbly due to some simulation factor
module sr(input r, input s, output q, output _q);
    parameter LOG=0;

    nor #(9) (q, r, _q);
    nor #(9) (_q, s, q);

    if (LOG) always @*
        $display("%9t", $time, " SR  s=%1b r=%1b   q=%1b _q=%1b   %m", s, r, q,  _q);

endmodule

/* verilator lint_on UNOPT */
`endif 
