/* verilator lint_off ASSIGNDLY */
/* verilator lint_off STMTDLY */

`include "um245r.v"
`include "../lib/assertion.v"
`timescale 1ns/1ns

module test();

    integer verbose = 0;

    // 33 exclamation mark
    tri [7:0] D;
    logic [7:0] Dtx;
    logic WR;
    logic _RD;
    wire _TXE;
    wire _RXF;
    reg [7:0] RECEIVED;

    localparam T3 = 51;
    localparam T9 = 20;
    localparam T10 = 0;

    assign D=Dtx;

    reg [12*8:0] expected;
    integer countTx=0;
    integer countRx=0;
    integer MAX_RX=-1;

//    logic clk;

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        Dtx=8'bzzzzzzzz;
        $display("[%9t] TEST: START", $time);

        _RD=1;
        WR=1; 

/*
        clk=0;
        forever begin
            #100  clk =  ! clk; 
        end
*/

    end

    //um245r #(.OUTPUT_FILE("out.txt"), .INPUT_FILE("data.mem"), .INPUT_FILE_DEPTH(12))  uart (
    um245r #(.LOG(1), .HEXMODE(1), .OUTPUT_FILE("/dev/stdout"), .INPUT_FILE("/dev/stdin"), .INPUT_FILE_DEPTH(12))  uart (
        .D,	                // Input data
        .WR,		// Writes data on -ve edge
        ._RD,		// When goes from high to low then the FIFO data is placed onto D (equates to _OE)
        ._TXE,		// When high do NOT write data using WR, when low write data by strobing WR
        ._RXF		// When high to NOT read from D, when low then data is available to read by strobing RD low
      );

    always @(negedge _RXF) begin
        countRx ++;
        if (verbose) $display("================================= READ ==================", countRx);
        #10
        if (verbose) $display("[%9t] TEST: Start Read : _RD goes low", $time);
        _RD=0;

        #T3 // T3 delay -- why so big?? Writew down reason !
        RECEIVED=D;
        $display("[%9t] TEST: ", $time, "RECEIVED     Dec=%3d Bin=%8b Hex=%02x Char=%c", D, D, D, D);

        #10
        _RD=1;
        if (verbose) $display("[%9t] TEST: Finished Read : _RD goes high", $time);

        if (MAX_RX > -1 && countRx == MAX_RX) begin
            $display("[%9t] TEST: ABORT", $time);
            $finish();
        end
    end

    always @(negedge _TXE) begin

        // WR is -ve edge triggered - strobe it H then L
        if (verbose) $display("TEST: Strobing WR=1 BEGIN write of countTx=%-d", countTx);
        WR=1;

        Dtx=countTx;
        #T9
        if (verbose) $display("TEST: Strobing WR=0 LATCH TRANSMIT");

        // dont print ctrl chars
        if (Dtx <=31 || Dtx == 127)
            $display("[%9t] TEST: ", $time, "TRANSMITTED  Dec=%3d Bin=%8b Hex=%02x Char=ctrlchar", Dtx, Dtx, Dtx);
        else
            $display("[%9t] TEST: ", $time, "TRANSMITTED  Dec=%3d Bin=%8b Hex=%02x Char=%c", Dtx, Dtx, Dtx, Dtx);
        WR=0;

        #T10
        if (verbose) $display("TEST: Strobing END - BUS should be Z");
        Dtx=8'bzzzzzzzz;

        countTx = countTx + 1;

    end

    always @(*)
        if (verbose)
        $display("[%9t] TEST: ", $time, 
//           "CLK=%1b", clk, 
            "D=%8b", D, 
            ", WR=%1b", WR, 
            ",_RD=%1b", _RD, 
            " _RXF=%1b", _RXF, 
            " _TXE=%1b", _TXE, 
            " Dtx=%8b", Dtx
            );

endmodule

