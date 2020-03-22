`ifndef  V_COUNTER_REG
`define  V_COUNTER_REG

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../74163/hct74163.v"

`timescale 1ns/100ps

module counterReg(
input CP,
input _MR,
input CEP,
input CET,
input _PE,
input [7:0] D,

output [7:0] Q,
output TC
);

// internal wiring 
logic CEPlo;
logic CEPhi;
logic CETlo;
logic CEThi;
logic [3:0] Dlo;
logic [3:0] Dhi;
logic [3:0] Qlo;
logic [3:0] Qhi;
wire TClo;
wire TChi;

// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
hct74163 LO
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEPlo),
  .CET(CETlo),
  ._PE(_PE),
  .D(Dlo),

  .Q(Qlo),
  .TC(TClo)
);

hct74163 HI
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEPhi),
  .CET(CEThi),
  ._PE(_PE),
  .D(Dhi),

  .Q(Qhi),
  .TC(TChi)
);

  
  assign CEPlo = CEP;
  assign CETlo = CET;
  assign CEPhi = CEP;
  assign CEThi = TClo; // CHAIN 
  
  assign Dlo = D[3:0];
  assign Dhi = D[7:4];

  assign Q = {Qhi, Qlo};
  assign TC = TChi;


endmodule

`endif
