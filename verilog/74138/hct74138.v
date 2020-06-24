`ifndef V_74138
`define V_74138

// 3-line to 8-line decoder/demultiplexer (inverted outputs)
// (c) Tim Rudy, GPL3

/* verilator lint_off DECLFILENAME */
// These timings are similar to 'typical' values for LS series - however MAX can be >2x worse
// Adjust for HCT? 
`timescale 1ns/1ns
module hct74138 #(parameter WIDTH_OUT = 8, WIDTH_IN = $clog2(WIDTH_OUT))
(
  input Enable1_bar,
  input Enable2_bar,
  input Enable3,
  input [WIDTH_IN-1:0] A,
  output [WIDTH_OUT-1:0] Y
);

// timings according to nexperia seem short https://assets.nexperia.com/documents/data-sheet/74HC_HCT138.pdf
// these timings are longer https://www.diodes.com/assets/Datasheets/74HCT138.pdf
// TI is longer still https://www.diodes.com/assets/Datasheets/74HCT138.pdf
// using longer timings here ...
specify
  (Enable1_bar *> Y) = (19); // NEXPERIA 14 // DIODES 19 // TI 18-30
  (Enable2_bar *> Y) = (19); // NEXPERIA 14 // DIODES 19 // TI 18-30
  (Enable3 *> Y) = (19); // NEXPERIA 14 // DIODES 19 // TI 18-30
  (A *> Y) = (17); // NEXPERIA 12 //DIODES 17  // TI 17-32
endspecify
  
//------------------------------------------------//
reg [WIDTH_OUT-1:0] computed;
integer i;

always @(*)
begin
  for (i = 0; i < WIDTH_OUT; i++)
  begin
/* verilator lint_off WIDTH */
    // BUG FIX - ORIGINAL VERSION OF THIS CODE SKIRTS OVER A being x or z
    if (!Enable1_bar && !Enable2_bar && Enable3) begin
        if (A === 'z) computed[i] = 1'bz;
        else if (A === 'x) computed[i] = 1'bx;
        // END BUG FIX
        else if (i == A)
    /* verilator lint_on WIDTH */
          computed[i] = 1'b0;
        else
          computed[i] = 1'b1;
    end
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

`endif
