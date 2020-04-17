`ifndef  V_74377
`define  V_74377
// based on https://raw.githubusercontent.com/TimRudy/ice-chips-verilog/master/source-7400/74377.v
// timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT377.pdf

// Octal D flip-flop with enable

`timescale 1ns/1ns

module hct74377 #(parameter WIDTH = 8, DELAY_RISE = 7, DELAY_FALL = 7)
(
  input _EN,
  input CP,
  input [WIDTH-1:0] D,
  output [WIDTH-1:0] Q
);

//------------------------------------------------//
reg [WIDTH-1:0] Q_current;

always @(posedge CP)
begin
  if (!_EN)
    Q_current <= D;
end
//------------------------------------------------//

assign #(DELAY_RISE, DELAY_FALL) Q = Q_current;

endmodule

`endif
