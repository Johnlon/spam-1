`include "um245r.v"
`include "../lib/assertion.v"
`timescale 1ns/1ns

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

reg [12*8:0] expected;

initial begin

    _RD=1;

`define CLK_WR \
    #100 \
    WR=0; \
    #100 \
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
    #100
    WR=1;
    Dv=8'bz;
    
`define CLK_RD(EXPECTED) \
    #100 \
    `Equals(_RXF, 1'b0); \
    _RD=0; \
    #100 \
    if ( D !== 8'bxxxxxxxx ) \
        $write("%c", D); \
    else \
        $write("%c(%2x) %c(%2x)", D, D, EXPECTED, EXPECTED); \
    `Equals(D, EXPECTED); \
    #100 \
    _RD=1;

    // data available?
    #100 
    `Equals(_RXF, 1'b0); 
    #100 
    
    // read data
    expected = "Hello World!";
    `CLK_RD(expected[12*8-1:(12*8)-8])
    `CLK_RD(expected[11*8-1:(11*8)-8])
    `CLK_RD(expected[10*8-1:(10*8)-8])
    `CLK_RD(expected[09*8-1:(09*8)-8])
    `CLK_RD(expected[08*8-1:(08*8)-8])
    `CLK_RD(expected[07*8-1:(07*8)-8])
    `CLK_RD(expected[06*8-1:(06*8)-8])
    `CLK_RD(expected[05*8-1:(05*8)-8])
    `CLK_RD(expected[04*8-1:(04*8)-8])
    `CLK_RD(expected[03*8-1:(03*8)-8])
    `CLK_RD(expected[02*8-1:(02*8)-8])
    `CLK_RD(expected[01*8-1:(01*8)-8])

    $display("\nREAD ALL");

    // no data left
    #100 
    `Equals(_RXF, 1'b1); 

    $display("\nEND");
end

endmodule

