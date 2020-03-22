// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "pc.v"

`timescale 1ns/100ps

module test();
logic CP;
logic _MR;
logic _PCLOin;
logic _PCHIin;
logic _PCHITMPin;
logic [7:0] D;
wire [7:0] PCHI, PCLO;


pc PC (
  .CP(CP),
  ._MR(_MR),
  ._PCHIin(_PCHIin),
  ._PCLOin(_PCLOin),
  ._PCHITMPin(_PCHITMPin),
  .D(D),

  .PCLO(PCLO),
  .PCHI(PCHI)
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
    $display("PC=%8b,%8b", PCHI, PCLO);

initial begin
  
  _PCHIin = 1'b1;
  _PCHITMPin = 1'b1;
  _PCLOin = 1'b1;
  CP = 1'b1;
  _MR = 1'b0;
  
  clkpulse();
  #50
  _MR = 1'b1;

  #50
  `Equals(PCLO, 8'b00000000);
  `Equals(PCHI, 8'b00000000);


// Set PCHITmp
  D = 8'b11111111;
  $display("load PCHITmp with %8b", D);
  _PCHIin = 1'b1;
  _PCHITMPin = 1'b0;
  _PCLOin = 1'b1;
  clkpulse();
  #50
  `Equals(PCLO, 8'b00000000);
  `Equals(PCHI, 8'b00000000);

// Set PCHI
  $display("load PCHI with %8b", D);
  _PCHIin = 1'b0;
  _PCHITMPin = 1'b1;
  _PCLOin = 1'b1;
  clkpulse();
  #50
  `Equals(PCLO, 8'b00000000);
  `Equals(PCHI, 8'b11111111);

// Set PCLO
  D = 8'b11111110;
  $display("load PCLO with %8b", D);
  _PCHIin = 1'b1;
  _PCHITMPin = 1'b1;
  _PCLOin = 1'b0;
  clkpulse();
  #50
  `Equals(PCLO, 8'b11111110);
  `Equals(PCHI, 8'b11111111);

// count PC
  $display("count");
  _PCHIin = 1'b1;
  _PCHITMPin = 1'b1;
  _PCLOin = 1'b1;
  clkpulse();
  #50
  `Equals(PCLO, 8'b11111111);
  `Equals(PCHI, 8'b11111111);

  clkpulse();
  #50
  `Equals(PCLO, 8'b00000000);
  `Equals(PCHI, 8'b00000000);


end

endmodule: test
