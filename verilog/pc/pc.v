
// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "../74163/hct74163.v"

module pc();
input CP;
input _MR;
input COUNTen;
input _PCLOin;
input _PCHIin;
input _PCHITMPin;

input [7:0] D;
output [7:0] PCHI;
output [7:0] PCLO;
output [7:0] PCLO;


logic CP;
logic _MR;
logic CEPlo;
logic CEPhi;
logic CETlo;
logic CEThi;
logic _PE;
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

initial begin

    CEPlo = COUNTen
    CEPhi = COUNTen

    CETlo = 1'b1;
    assign CEThi = TClo; // CHAIN 
  


end

endmodule: test
