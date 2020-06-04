`ifndef  V_74377
`define  V_74377
// based on https://raw.githubusercontent.com/TimRudy/ice-chips-verilog/master/source-7400/74377.v
// timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT377.pdf

// Octal D flip-flop with enable

`timescale 1ns/1ns

module hct74377 #(parameter WIDTH = 8, LOG = 0)
(
  input _EN,
  input CP,
  input [WIDTH-1:0] D,
  output [WIDTH-1:0] Q
);

if (LOG)  
  always @* begin
      $display("%9t", $time, " REGISTER %m   CP=%1b _EN=%1b D=%08b Q=%08b (Q_current=%08b)", CP, _EN, D, Q, Q_current);
  end

//------------------------------------------------//
reg [WIDTH-1:0] Q_current='x;

always @(posedge CP)
begin
  if (!_EN) begin
      $display("%9t", $time, " REGISTER %m   ASSIGNING D=%08b to Q_current", D);
    Q_current <= D;
  end
end
//------------------------------------------------//

assign #14 Q = Q_current;

endmodule

`endif
