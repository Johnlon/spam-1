// https://www.ftdichip.com/Support/Documents/DataSheets/Modules/DS_UM245R.pdf

// Simulation of UM245R UART
// (c) 2018 Warren Toomey, GPL3

module um245r (
        input [7:0] D,	// Input data
	input WR,		// Writes data on -ve edge
	input _RD,		// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
	output _TXE,		// When high do NOT write data using WR, when low write data by strobing WR
	output _RXF		// When high to NOT read from D, when low then data is available to read by strobing RD low
  );

  // UART output
  assign _TXE=0;
  assign _RXF=1;

  always @(negedge WR) begin
    $write("UART >> %c", D);
  end

endmodule

