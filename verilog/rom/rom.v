
`ifndef ROM_V
`define  ROM_V

`timescale 1ns/100ps

// https://raw.githubusercontent.com/DoctorWkt/CSCvon8/master/rom.v
// ROM component
// (c) 2019 Warren Toomey, GPL3
// verilator lint_off UNOPTFLAT

module rom (A, D, _CS, _OE);

  parameter DWIDTH=8,AWIDTH=16, DEPTH= 1 << AWIDTH;
  parameter Filename = "data.rom";
  parameter DELAY_RISE = 45;
  parameter DELAY_FALL = 45;
  parameter DELAY = 150;

  input  [AWIDTH-1:0] A;
  inout  [DWIDTH-1:0] D;
  input _CS, _OE;

  reg [DWIDTH-1:0] Mem [0:DEPTH-1];

  // Initialise ROM from file lazily
    initial begin
//  always @(_CS)
 //   if (!_CS) 
      $readmemh(Filename, Mem);
    end

/* verilator lint_off ASSIGNDLY */
  assign #(DELAY) D = (!_CS && !_OE) ? Mem[A] : {DWIDTH{1'bz}};
/* verilator lint_on ASSIGNDLY */

    always @(*) begin
        $display("%8d ROM %m : A=%x D=%b _CS=%1b, _OE=%1b", $time, A, D, _CS, _OE);
    end

endmodule

`endif
