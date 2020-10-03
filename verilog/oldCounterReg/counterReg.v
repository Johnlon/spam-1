`ifndef  V_COUNTER_REG
`define  V_COUNTER_REG

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../74163/hct74163.v"

`timescale 1ns/1ns

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
logic [3:0] Dlo;
logic [3:0] Dhi;
logic [3:0] Qlo;
logic [3:0] Qhi;
// cascaded as per http://upgrade.kongju.ac.kr/data/ttl/74163.html
// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
hct74163 LO4
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEP),
  .CET(CET),
  ._PE(_PE),
  .D(Dlo),

  .Q(Qlo)
);

hct74163 HI4
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEP),
  .CET(LO4.TC),
  ._PE(_PE),
  .D(Dhi),

  .Q(Qhi)
);

  
  assign Dlo = D[3:0];
  assign Dhi = D[7:4];

  assign Q = {Qhi, Qlo};
  assign TC = HI4.TC;


endmodule

`endif
