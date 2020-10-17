`ifndef  V_PC
`define  V_PC

// ADVANCE ON +CLK

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off PINMISSING

`include "../lib/assertion.v"
`include "../74377/hct74377.v"
`include "../74163/hct74163.v"

`timescale 1ns/1ns

module pc(
    input clk,
    input _MR,
    input _pchitmp_in,      // load tmp
    input _local_jump,      // load lo
    input _long_jump,       // load hi and lo
    input [7:0] D,

    output [7:0] PCLO,
    output [7:0] PCHI
);

parameter LOG = 0;

wire [7:0] PCHITMP;

hct74377 PCHiTmpReg(
  .D, .Q(PCHITMP), .CP(clk), ._EN(_pchitmp_in)
);

// _do_jump is synchronous and must be held low DURING a +ve clk
wire #11 _do_jump = _local_jump & _long_jump;

// see applications here https://www.ti.com/lit/ds/symlink/sn54ls161a-sp.pdf?ts=1599773093420&ref_url=https%253A%252F%252Fwww.google.com%252F
// see ripple mode approach - CEP/CET can be tied high because _PE overrides those and so they can be left enabled.
// feed of TC->CET chains the count enable as per the data sheet for > 4 bits counting.

// cascaded as per http://upgrade.kongju.ac.kr/data/ttl/74163.html
// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
hct74163 PCLO_3_0
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(1'b1),
  ._PE(_do_jump),
  .D(D[3:0])
);
hct74163 PCLO_7_4
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(PCLO_3_0.TC),
  ._PE(_do_jump),
  .D(D[7:4])
);

hct74163 PCHI_3_0
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(PCLO_7_4.TC),
  ._PE(_long_jump),
  .D(PCHITMP[3:0])
);
hct74163 PCHI_7_4
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(PCHI_3_0.TC),
  ._PE(_long_jump),
  .D(PCHITMP[7:4])
);

assign PCLO = {PCLO_7_4.Q, PCLO_3_0.Q};
assign PCHI = {PCHI_7_4.Q, PCHI_3_0.Q};

if (LOG) always @(posedge clk)
begin
  if (~_MR)
  begin
    $display("%9t ", $time, "PC RESET ");
  end
    else
  begin
    $display("%9t ", $time, "PC TICK _MR=%1b ", _MR);
  end
end

if (LOG) always @(*) begin
  $display("%9t ", $time, "PC       ",
      "PC=%2x:%2x PCHITMP=%2x ",
      PCHI, PCLO, PCHITMP,
      "clk=%1b ",  clk, 
      "_MR=%1b ",  _MR, 
      " _local_jump=%1b _long_jump=%1b _do_jump=%1b _pchitmp_in=%1b     Din=%8b ",
      _local_jump, _long_jump, _do_jump, _pchitmp_in, D 
      );
end
// verilator lint_on ASSIGNDLY
// verilator lint_on STMTDLY
// verilator lint_on PINMISSING

endmodule :pc

`endif
