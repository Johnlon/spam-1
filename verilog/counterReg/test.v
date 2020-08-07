
// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "counterReg.v"

`timescale 1ns/1ns

module test();
logic CP;
logic _MR;
logic CEP1;
logic CEP2;
logic CET1;
logic CET2;
logic _PE;
logic [7:0] D1;
logic [7:0] D2;
logic [7:0] Q1;
logic [7:0] Q2;

wire TC1;
wire TC2;

// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
counterReg LO
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEP1),
  .CET(CET1),
  ._PE(_PE),
  .D(D1),

  .Q(Q1),
  .TC(TC1)
);

counterReg HI
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEP2),
  .CET(CET2),
  ._PE(_PE),
  .D(D2),

  .Q(Q2),
  .TC(TC2)
);

task clkpulse;
  begin
  #50
  CP= 1'b0;
  #50
  CP= 1'b1;
  end
endtask


  always @(*)
    $display("Q1=%8b", Q1, " Q2=%8b", Q2);

initial begin
  
  CEP1 = 1'b1;
  CET1 = 1'b1;
  CEP2 = 1'b1;
  assign CET2 = TC1; // CHAIN 
  
  _PE = 1'b1;
  _MR = 1'b1;
  CP = 1'b1;
  D1 = 8'b11111110;
  D2 = 8'b00000000;

  #100;
  _PE = 1'b0;
  
  clkpulse;
  
  #100;
  _PE = 1'b1;
  `Equals(Q1, 8'b11111110);
  `Equals(Q2, 8'b00000000);

  clkpulse;
  
  #100;
  `Equals(Q1, 8'b11111111);
  `Equals(Q2, 8'b00000000);

  clkpulse;
  
  #100;
  `Equals(Q1, 8'b00000000);
  `Equals(Q2, 8'b00000001);
end

endmodule: test
