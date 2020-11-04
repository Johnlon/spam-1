`ifndef V_74157 
`define V_74157 

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "./hct74157.v"
`timescale 1ns/1ns

module tb();

      wire [3:0]I0;
      wire [3:0]I1;
      wire [3:0]Y;

      logic [3:0] VI1='0;
      logic [3:0] VI0='1;

      logic _E;
      logic S;

      assign I1=VI1;
      assign I0=VI0;

      hct74157 mux(.I0, .I1, .S, ._E, .Y);

    always @*
        $display($time, " => S=%1b", S, " _E=%1b", _E, " I0=%4b", VI0, " I1=%4b ", I1," Y=%4b ", Y);
     
    initial begin
      
      VI0='x;
      VI1='x;
      S = 1; // a->b
      _E = 1;
      #2 // not enought time to stabilise
      `Equals(Y , 4'bxxxx)

      #20
      `Equals(Y , 4'b0)

      VI0=4'b1010;
      VI1=4'b0101;

      S = 1; 
      _E = 0;
      #20
      `Equals(Y , 4'b0101)

      S = 0; 
      #20 
      `Equals(Y , 4'b1010)
      
    end

endmodule: tb


`endif