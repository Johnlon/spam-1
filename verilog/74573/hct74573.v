`ifndef  V_74573
`define  V_74573
/* octal d-type transparent latch.
 Same model as 74373 but with timings from HCT573
 
 LE = H is transparent
 _OE = H is Z
 
 Timings have are for 74HCT573
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT573.pdf
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT373.pdf
 */

`timescale 1ns/1ns
module hct74573 (
    input LE, // transparent when high 
    input _OE,
	input [7:0] D,
    output [7:0] Q
);
    parameter LOG=0;

    reg [7:0] data;
    
    specify
    (D => Q) = (17);
    (LE *> Q) = (15);
    (_OE *> Q) = (18);
    endspecify
    
    always @(D or LE)
        if (LE)
        begin 
            data <= D;
        end
    
    assign Q = _OE ? 8'bz : data;

    if (LOG) always @* 
        $display("%09t ", $time, 
                " le=%1b", le,
                " _oe=%1b", _oe,
                " d=%8b", d,
                " q=%8b", q,
                "%m");
        
    
endmodule: hct74573

`endif
