
`ifndef ROM_V
`define  ROM_V

`timescale 1ns/100ps

// https://raw.githubusercontent.com/DoctorWkt/CSCvon8/master/rom.v
// ROM component
// (c) 2019 Warren Toomey, GPL3
// verilator lint_off UNOPTFLAT

module rom (Address, Data, _CS, _OE);

  parameter DWIDTH=8,AWIDTH=16, DEPTH= 1 << AWIDTH;
  parameter Filename = "data.rom";
  parameter DELAY_RISE = 45;
  parameter DELAY_FALL = 45;

  input  [AWIDTH-1:0] Address;
  inout  [DWIDTH-1:0] Data;
  input _CS, _OE;

  reg [DWIDTH-1:0] Mem [0:DEPTH-1];

  // Initialise ROM from file lazily
  always @(_CS)
    if (!_CS) 
      $readmemh(Filename, Mem);

/* verilator lint_off ASSIGNDLY */
  assign #(DELAY_RISE, DELAY_FALL)
	Data = (!_CS && !_OE) ? Mem[Address] : {DWIDTH{1'bz}};
/* verilator lint_on ASSIGNDLY */

endmodule

`endif
