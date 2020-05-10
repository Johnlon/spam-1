/* verilator lint_off ASSIGNDLY */
/* verilator lint_off STMTDLY */

`include "../lib/assertion.v"
`include "./hct74157.v"
`timescale 1ns/100ps

module tb();

      wire [3:0]A;
      wire [3:0]B;
      wire [3:0]Y;

      logic [3:0] Vb='0;
      logic [3:0] Va='1;

      logic _E;
      logic S=1;

      assign B=Vb;
      assign A=Va;

      hct74157 mux(.A, .B, .S, ._E, .Y);

    always @*
        $display($time, " => S=%1b", S, " _E=%1b", _E, " A=%4b", Va, " B=%4b ", B," Y=%4b ", Y);
     
    initial begin
      
      Va='x;
      Vb='x;
      S <= 1; // a->b
      _E <= 1;
      #2 // not enought time to stabilise
      `Equals(Y , 4'bxxxx)

      #20
      `Equals(Y , 4'b0)

      Va=4'b1010;
      Vb=4'b0101;

      S <= 1; 
      _E <= 0;
      #20
      `Equals(Y , 4'b0101)

      S <= 0; 
      #20 
      `Equals(Y , 4'b1010)


    end

endmodule : tb

