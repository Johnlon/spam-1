`ifndef  V_PC
`define  V_PC

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "../counterReg/counterReg.v"
`include "../74377/hct74377.v"
`include "../7474/hct7474.v"

`timescale 1ns/100ps

module pc(
    input CP,
    input _CP,
    input _MR,
    input _pchitmp_in,
    input _pclo_in,
    input _pc_in,       // load hi and lo
    input [7:0] D,

    output [7:0] PCLO,
    output [7:0] PCHI
);
  parameter LOG = 0;

wire [7:0] PCHI, PCLO, PCHITMP;

// low is loaded if separately loaded or both loaded
wire #11 _pclo_load = _pclo_in & _pc_in;

//wire #11 _gated_pchitmp_in = _pchitmp_in | CP;
    
hct74377 PCHiTmpReg(
  .D, .Q(PCHITMP), .CP, ._EN(_pchitmp_in)
);

// count disabled for this clock cycle if we've just loaded PC
wire countEn;

/*
hct7474 #(.BLOCKS(1), .NAME("RESETFF (sensitivity = _CP)"), .LOG(0)) resetFF(
  ._SD(1'b1),
  ._RD(_pclo_load),     // ASYNC !! gets set as soon as _pclo fires TODO FIXME << SPURIOUS RESET RISK??
  .D(1'b1),
  .CP(_CP),             // gets cleared on next neg clk
  .Q(countEn)
);
*/

// 74163 counts when CEP/CET/PE are all high
// _pclo_load is synchronous and must be held low DURING a +ve CP

wire TC;

assign #11 countEn = _pclo_load; // _pclo_load is always involved in a jump so the inverse of this signal can enable count

counterReg LO
(
  .CP(_CP),
  ._MR(_MR),
  .CEP(countEn),
  .CET(1'b1),
  ._PE(_pclo_load),
  .D(D),

  .Q(PCLO),
  .TC(TC)
);

counterReg HI
(
  .CP(_CP),
  ._MR(_MR),
  .CEP(countEn),
  .CET(TC),
  ._PE(_pc_in),
  .D(PCHITMP),

  .Q(PCHI),
  .TC() // ignored out
);


if (LOG) always @(*) begin
  $display("%9t PC   : _MR=%1b countEn=%1b _pclo_in=%1b _pc_in=%1b _pclo_load=%1b _pchitmp_in=%1b CP=%1b  D=%8b PCLO=%8b PCHI=%8b PCHITMP=%8b ", $time, _MR, countEn, _pclo_in, _pc_in, _pclo_load, _pchitmp_in, CP, D, PCLO, PCHI, PCHITMP);
end

endmodule

`endif
