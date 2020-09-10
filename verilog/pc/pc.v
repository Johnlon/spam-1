`ifndef  V_PC
`define  V_PC

// ADVANCE ON +CLK

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`include "../lib/assertion.v"
`include "../counterReg/counterReg.v"
`include "../74377/hct74377.v"

`timescale 1ns/1ns

module pc(
    input clk,
    input _MR,
    input _pchitmp_in,  // load tmp
    input _pclo_in,     // load lo
    input _pc_in,       // load hi and lo
    input [7:0] D,

    output [7:0] PCLO,
    output [7:0] PCHI
);

parameter LOG = 0;

wire [7:0] PCHITMP;

// low is loaded if separately loaded or both loaded
wire #11 _pclo_load = _pclo_in & _pc_in;

//wire #11 _gated_pchitmp_in = _pchitmp_in | clk;
    
hct74377 PCHiTmpReg(
  .D, .Q(PCHITMP), .CP(clk), ._EN(_pchitmp_in)
);

// count disabled for this clock cycle if we've just loaded PC
wire countEn;

// 74163 counts when CEP/CET/PE are all high
// _pclo_load is synchronous and must be held low DURING a +ve clk

assign countEn = _pclo_load; // _pclo_load is always involved in a jump so the inverse of this signal can enable count

// see applications here https://www.ti.com/lit/ds/symlink/sn54ls161a-sp.pdf?ts=1599773093420&ref_url=https%253A%252F%252Fwww.google.com%252F
// see ripple mode approach - CEP/CET can be tied high because _PE overrides those and so they can be left enabled.
// TC/CET chains the count enable as per the data sheet for > 4 bits counting.

counterReg LO
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(1'b1),
  ._PE(_pclo_load),
  .D(D),

  .Q(PCLO)
);

counterReg HI
(
  .CP(clk),
  ._MR(_MR),
  .CEP(1'b1),
  .CET(LO.TC),
  ._PE(_pc_in),
  .D(PCHITMP),

  .Q(PCHI),
  .TC() // ignored out
);


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
      " countEn=%1b _pclo_in=%1b _pc_in=%1b _pclo_load=%1b _pchitmp_in=%1b     Din=%8b ",
      countEn, _pclo_in, _pc_in, _pclo_load, _pchitmp_in, D 
      );
end

endmodule :pc

`endif
