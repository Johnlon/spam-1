`ifndef hct74574
`define hct74574

/* Timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT574.pdf
 * */
`timescale 1ns/100ps
module hct74574 (
input CLK,
input _OE,        // negative enable
input [7:0] D,
output [7:0] Q
);
    reg [8:0] data;
    
    specify
    (CLK *> Q) = (15);
    (_OE *> Q) = (19);
    endspecify
    
    always @(posedge CLK)
        data <= D;
    
    assign #(19) Q = _OE ? 8'bz: data;
    
endmodule

`endif
