// Quad 2-input multiplexer
// https://assets.nexperia.com/documents/data-sheet/74HC_HCT157.pdf
               
`ifndef hct74157
`define hct74157

`timescale 1ns/1ns

module hct74157(_E, S, I0, I1, Y);
    input S, _E;
    input [3:0] I0;
    input [3:0] I1;
    output [3:0] Y;

    assign #(19) Y = _E? 4'b0 : S? I1: I0;

    //assign #(19) Y[0] = _E==1'b1? 1'b0 : (S & B[0]) | (!S & A[0]);
    //assign #(19) Y[1] = _E==1'b1? 1'b0 : (S & B[1]) | (!S & A[1]);
    //assign #(19) Y[2] = _E==1'b1? 1'b0 : (S & B[2]) | (!S & A[2]);
    //assign #(19) Y[3] = _E==1'b1? 1'b0 : (S & B[3]) | (!S & A[3]);
endmodule


`endif
