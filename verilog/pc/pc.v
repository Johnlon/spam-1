`ifndef  V_PC
`define  V_PC

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "../counterReg/counterReg.v"
`include "../74573/hct74573.v"

`timescale 1ns/100ps

module pc(
    input CP,
    input _MR,
    input _PCLOin,
    input _PCHIin,
    input _PCHITMPin,
    input [7:0] D,

    output [7:0] PCLO,
    output [7:0] PCHI
);

wire COUNTen = _PCLOin && _PCHIin && _PCHITMPin;

wire [7:0] PCHI, PCLO, PCHITMP;
    
hct74573 PCHITMPReg(
  .D, .Q(PCHITMP), .LE(_PCHITMPin), .OE_N(1'b0)
);

wire TC;

// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
counterReg LO
(
  .CP(CP),
  ._MR(_MR),
  .CEP(COUNTen),
  .CET(1'b1),
  ._PE(_PCLOin),
  .D(D),

  .Q(PCLO),
  .TC(TC)
);

counterReg HI
(
  .CP(CP),
  ._MR(_MR),
  .CEP(COUNTen),
  .CET(TC),
  ._PE(_PCHIin),
  .D(PCHITMP),

  .Q(PCHI),
  .TC() // ignored out
);

  
endmodule

`endif
