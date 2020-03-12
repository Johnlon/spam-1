

// 3-line to 8-line decoder/demultiplexer (inverted outputs)
// (c) Tim Rudy, GPL3

/* verilator lint_off DECLFILENAME */
// These timings are similar to 'typical' values for LS series - however MAX can be >2x worse
// Adjust for HCT? 
`timescale 1ns/100ps
module hct74138 #(parameter WIDTH_OUT = 8, WIDTH_IN = $clog2(WIDTH_OUT))
(
  input Enable1_bar,
  input Enable2_bar,
  input Enable3,
  input [WIDTH_IN-1:0] A,
  output [WIDTH_OUT-1:0] Y
);

// according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT138.pdf
specify
  (Enable1_bar *> Y) = (14);
  (Enable2_bar *> Y) = (14);
  (Enable3 *> Y) = (14);
  (A *> Y) = (12);
endspecify
  
//------------------------------------------------//
reg [WIDTH_OUT-1:0] computed;
integer i;

always @(*)
begin
  for (i = 0; i < WIDTH_OUT; i++)
  begin
/* verilator lint_off WIDTH */
    if (!Enable1_bar && !Enable2_bar && Enable3 && i == A)
/* verilator lint_on WIDTH */
      computed[i] = 1'b0;
    else
      computed[i] = 1'b1;
  end
end
//------------------------------------------------//

/* verilator lint_off ASSIGNDLY */
assign Y = computed;
/* verilator lint_on ASSIGNDLY */

endmodule
/* verilator lint_on DECLFILENAME */
