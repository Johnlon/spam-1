`ifndef  V_74573
`define  V_74573
/* octal d-type transparent latch.
 Same model as 74373 but with timings from HCT573
 
 LE = H is transparent
 OE_N = H is Z
 
 Timings have are for 74HCT573
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT573.pdf
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT373.pdf
 */

`timescale 1ns/100ps
module hct74573 (
    input LE,
    input OE_N,
	input [7:0] D,
    output [7:0] Q
);

    reg [7:0] data;
    
    specify
    (D => Q) = (17);
    (LE *> Q) = (15);
    (OE_N *> Q) = (18);
    endspecify
    
    always @(D or LE)
        if (LE)
        begin 
            data <= D;
        end
    
    assign Q = OE_N ? 8'bz : data;
    
endmodule

`endif
