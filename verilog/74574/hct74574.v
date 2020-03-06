/* Timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT574.pdf
 * */
`timescale 1ns/100ps
module hct74574 (
input CLK,
input OE_N,        // negative enable
input [7:0] D,
output [7:0] Q
);
    reg [8:0] data;
    
    specify
    (CLK *> Q) = (15);
    (OE_N *> Q) = (19);
    endspecify
    
    always @(posedge CLK)
        data <= D;
    
    assign #(19) Q = OE_N ? 8'bz: data;
    
endmodule
