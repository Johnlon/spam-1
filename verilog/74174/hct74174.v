// License: Mozilla Public License : Version 2.0
// Author : John Lonergan
`ifndef hct74174
`define hct74174

/* Timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT174.pdf
 * */
`timescale 1ns/1ns
module hct74174 (
input CLK,
input _MR,        // negative enable
input [5:0] D,
output [5:0] Q
);
    parameter LOG=0;
    parameter PD_SETUP=4; // tSU
    parameter PD_CLK_Q=17;  // tPD
    parameter PD_MR_Q=13;   // rPHL

    reg [7:0] dSetup;
    reg [7:0] dCurrent;

    // setup time 
    assign #(PD_SETUP) dSetup = D;
    
    specify
        (CLK *> Q) = (PD_CLK_Q);
        (_MR *> Q) = (PD_MR_Q);
    endspecify

    always @(posedge CLK) begin
//        if (LOG) $display("%9t", $time, " REGISTER %m   CLK=+ve _OE=%1b dCurrent=%08b D=%08b Q=%08b", _OE, dCurrent, D, Q);
        dCurrent <= dSetup;
    end

    always @(Q) begin
        if (LOG) $display("%9t", $time, " REGISTER %m   updated Q=%06b   data=%06b", Q, dCurrent);
    end

    assign Q = _MR ? dCurrent : 6'b000000;
    
endmodule

`endif
