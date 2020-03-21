// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "pc.v"

module test();
input CP;
input _MR;
input CONTen;
input _PCLOin;
input _PCHIin;
input _PCHITMPin;

input [7:0] D;
wire [7:0] PCHI;
wire [7:0] PCLO;

hct74163 LO
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

hct74163 HI
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
    $display("Q1=%4b", Q1, " Q2=%4b", Q2);

initial begin
  
  CEP1 = 1'b1;
  CET1 = 1'b1;
  CEP2 = 1'b1;
  assign CET2 = TC1; // CHAIN 
  
  _PE = 1'b1;
  _MR = 1'b1;
  CP = 1'b1;
  D1 = 4'b1110;
  D2 = 4'b0000;

  #100;
  _PE = 1'b0;
  
  clkpulse;
  
  #100;
  _PE = 1'b1;
  `Equals(Q1, 4'b1110);
  `Equals(Q2, 4'b0000);

  clkpulse;
  
  #100;
  `Equals(Q1, 4'b1111);
  `Equals(Q2, 4'b0000);

  clkpulse;
  
  #100;
  `Equals(Q1, 4'b0000);
  `Equals(Q2, 4'b0001);
end

endmodule: test
