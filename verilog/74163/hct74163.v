// Hate the TI docs, so changed naming to... https://assets.nexperia.com/documents/data-sheet/74HC_HCT163.pdf
// 4 bit fully synchronous binary counter, parallel loadable
// HCT timings
`timescale 1ns/100ps

module hct74163
(
  input CP,       // clock
  input _MR,      // master reset
  input CEP,       // count enable input
  input CET,       // count enable carry input
  input _PE,        // parallel enable
  input [3:0] D,   // 4-bit parallel input

  output [3:0] Q,  // Parallel outputs
  output TC         // terminal count output
);

// Timing from HCT https://assets.nexperia.com/documents/data-sheet/74HC_HCT163.pdf
specify
  (CP *> Q) = (20);
  (CP *> TC) = (25);
  (CET *> TC) = (14);
endspecify

reg [3:0] count = 0;

always @(posedge CP)
begin
  if (~_MR)
  begin
    count <= 4'b0000;
  end
  else if (~_PE)
  begin
    count <= D;
  end
  else if (CEP & CET)
  begin
    count <= count + 1;
  end
end

assign Q = count;
assign TC = (count == 4'b1111) & CET;

endmodule
