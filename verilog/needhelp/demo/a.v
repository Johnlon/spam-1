`include "hct74245.v"
`timescale 1ns/100ps

module alu (
    output [7:0] o,
    input  [7:0] x
);

    always @(*) 
         $display("%6d : ", $time,
         " o=%08b ", o,
         " x=%08b ", x,
         " xin=%08b ", xin,
         " xout=%08b ", xout,
         );

    wire AtoB=1'b1;
    wire nOE=1'b0;

    wire [7:0] xout;
    wire [7:0] xin = x;

    hct74245 bufTest(.A(xin), .B(xout), .dir(AtoB), .nOE(nOE)); 

    pulldown pullTo0[7:0](xout);

    assign o = xout;

endmodule

