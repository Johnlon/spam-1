// https://www.ftdichip.com/Support/Documents/DataSheets/Modules/DS_UM245R.pdf
/* verilator lint_off ASSIGNDLY */

module um245r #(parameter FILENAME="", DEPTH=0)  (
        inout [7:0] D,	// Input data
	input WR,		// Writes data on -ve edge
	input _RD,		// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
	output _TXE,		// When high do NOT write data using WR, when low write data by strobing WR
	output _RXF		// When high to NOT read from D, when low then data is available to read by strobing RD low
  );

  reg [7:0] Mem [0:DEPTH-1];

  // Initialise ROM from file lazily
  initial begin
    if (DEPTH > 0)
      $readmemh(FILENAME, Mem);
  end

  // UART output
  assign _TXE=0; // always ready to transmit

  always @(negedge WR) begin
    $write("%c", D);
  end

  integer i=-1;

  
  // posedge so advance after reading
  always @(posedge _RD) begin
    i = i + 1;
  end

  /// small delay necessary to prevent previous value of Mem[i] appearing on wire 
  assign #1 _RXF=!(i<DEPTH);
  assign #5 D = _RD? 8'bzzzzzzzz: _RXF? 8'bxxxxxxxx: Mem[i];

  if (0)  always @*
    $display("D=", D, ", WR=", WR, ",_RD=", _RD, ", i=", i, " MEM[%2d]=h%2x", i, Mem[i], " _RXF=%1b", _RXF);


endmodule

