
`ifndef ROM_V
`define  ROM_V

`timescale 1ns/1ns

// https://raw.githubusercontent.com/DoctorWkt/CSCvon8/master/rom.v
// ROM component
// (c) 2019 Warren Toomey, GPL3
// verilator lint_off UNOPTFLAT

module rom (A, D, _CS, _OE);

  parameter DWIDTH=8,AWIDTH=16, DEPTH= 1 << AWIDTH;
  localparam DEFAULT_FILENAME = "";
  parameter FILENAME = DEFAULT_FILENAME;
  parameter DELAY_RISE = 45;
  parameter DELAY_FALL = 45;
  parameter DELAY = 120;
  parameter LOG = 0;

  input  [AWIDTH-1:0] A;
  input _CS, _OE;
  output  [DWIDTH-1:0] D;

  reg [DWIDTH-1:0] Mem [0:DEPTH-1];

  // Initialise ROM from file lazily
    initial begin
//  always @(_CS)
 //   if (!_CS) 
    if (DEFAULT_FILENAME != FILENAME)
        $readmemh(FILENAME , Mem);
    end

/* verilator lint_off ASSIGNDLY */
  assign #(DELAY) D = (!_CS && !_OE) ? Mem[A] : {DWIDTH{1'bz}};
/* verilator lint_on ASSIGNDLY */

    if (LOG) always @(*) begin
        $display("%9t ROM %m : A=x%x (%b) D=%b _CS=%1b, _OE=%1b", $time, A, A,  D, D, _CS, _OE);
    end

endmodule

`endif
