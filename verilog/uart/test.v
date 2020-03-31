`include "um245r.v"
`include "../lib/assertion.v"

module test();

// 33 exclamation mark
tri [7:0] D;
logic [7:0] Dv;
logic WR;
logic _RD;
wire _TXE;
wire _RXF;

um245r #(.FILENAME("data.mem"), .DEPTH(12))  uart (
    .D,	                // Input data
    .WR,		// Writes data on -ve edge
    ._RD,		// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
    ._TXE,		// When high do NOT write data using WR, when low write data by strobing WR
    ._RXF		// When high to NOT read from D, when low then data is available to read by strobing RD low
  );

assign D=Dv;

integer c;
initial begin

    _RD=1;

`define CLK_WR \
    #10 \
    WR=0; \
    #10 \
    WR=1;

    $display("WRITING ....");
    //'!'=33;
    Dv=33;
    `CLK_WR
    `CLK_WR
    `CLK_WR
    `CLK_WR
    `CLK_WR

    // READING ....
    $display("\nREADING ....");
    #10
    WR=1;
    Dv=8'bz;
    
    for (c=0; c<19; c++) begin
        #10
        _RD=0;
        #10
        $write("%c", D);
        #10
        _RD=1;
        #10
        `Equals(D, 8'bz); // bus is Z unless reading
    end
    $display("\nEND");
end

endmodule

