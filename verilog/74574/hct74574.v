`ifndef hct74574
`define hct74574

/* Timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT574.pdf
 * */
`timescale 1ns/1ns
module hct74574 (
input CLK,
input _OE,        // negative enable
input [7:0] D,
output [7:0] Q
);
    parameter LOG=0;
    parameter SETUP_TIME=12;
    parameter PD_CLK_Q=15;
    parameter PD_OE_Q=19;

    reg [7:0] data;
    wire [7:0] dSetup;

    // setup time 
    assign #(SETUP_TIME) dSetup = D;
    
    specify
        (CLK *> Q) = (PD_CLK_Q);
        (_OE *> Q) = (PD_OE_Q);
    endspecify
    
    always @(posedge CLK) begin
        if (LOG) $display("%9t", $time, " REGISTER %m   CLK=+ve _OE=%1b Data=%08b D=%08b oldQ=%08b", _OE, data, D, Q);
        data <= dSetup;
    end

    always @(data)
        if (LOG)
        //$display("%9t", $time, " REGISTER %m changed  CLK=%1b _OE=%1b Data=%08b D=%08b Q=%08b", CLK, _OE, data, D, Q);
        $display("%9t", $time, " REGISTER %m updated  Data=%08b", data);
    
    assign Q = _OE ? 8'bz: data;
    
endmodule

`endif
