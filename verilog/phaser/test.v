
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./phaser.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    `include "../lib/display_snippet.sv"

    logic clk;
    logic mr;

    wire [9:0] seq;

// verilator lint_off PINMISSING
// verilator lint_off UNOPTFLAT
    wire _phaseFetch, phaseFetch , _phaseExec, phaseExec;
	phaser #(.LOG(1)) ctrl( .clk, .mr, .seq, ._phaseFetch, .phaseFetch , ._phaseExec, .phaseExec);
// verilator lint_on UNOPTFLAT
// verilator lint_on PINMISSING

    wire [1:0] FE = {phaseFetch , phaseExec};    

    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $monitor ("%9t ", $time,  "TEST    clk=%1b", clk, 
                " seq=%10b", seq,
                " phase(FE=%1b%1b)", phaseFetch , phaseExec,
                " mr=%1b", mr,
                " %s", label
                );

        `endif
    end
 // constraints
     always @* begin
         // only one may be low at a time
         if (phaseFetch + phaseExec >1 ) begin
             $display("\n%9t ", $time, " ERROR CONFLICTING PHASE  F=%1b/E=%1b", phaseFetch , phaseExec);
             $finish();
         end
         if (phaseFetch === 1'bx |  phaseExec === 1'bx) begin
             $display("\n%9t ", $time, " ERROR ILLEGAL INDETERMINATE PHASE F=%1b/E=%1b", phaseFetch , phaseExec );
             $finish();
         end
     end

    integer count;
    integer pFetch_count=1;

    initial begin
        localparam T=100;   // clock cycle
        localparam SMALL_DELAY=20; // come gate delay

        `DISPLAY("init");
        mr = 1;
        clk = 0;

        #T
        `Equals( seq, 10'b1);
        `Equals( FE, 2'b00);

        #T
        `DISPLAY("mr no clocking is ineffective = stay in NONE mode")
        for (count=0; count<3; count++) begin
            $display("count %-5d", count);
            clk = 1;
            #T
            `Equals( FE, 2'b00);

            clk = 0;
            #T
            `Equals( FE, 2'b00);
        end
        `Equals( seq, 10'd1);
        
        `DISPLAY("mr released = still in NONE mode after settle")
         mr = 0;
         #T
         `Equals( FE, 2'b10);
         `Equals( seq, 10'b1);

        `DISPLAY("FETCH mode ")
        for (count=0; count<pFetch_count; count++) begin
            $display("count %-5d", count);
            clk = 1;
            #T
            `Equals( FE, 2'b01);

            clk = 0;
            #T
            `Equals( FE, 2'b01);
        end
        `Equals( seq, 10'd2);
        

        `DISPLAY("EXEC mode ");
        clk = 1;
        #T
        clk = 0;
        #T
        `Equals( FE, 2'b10);
        `Equals( seq, 10'd1);

        `DISPLAY("no mode for 1 clocks");
        clk = 1;
        #T
        clk = 0;
        #T
        `Equals( FE, 2'b01);
        `Equals( seq, 10'd2);

        `DISPLAY("return to FETCH mode");
        clk = 1;
        #T
        `Equals( FE, 2'b10);
        clk = 0;
        #T
        `Equals( FE, 2'b10);
        `Equals( seq, 10'd1);

        // free run

        for (count = 0; count < 20; count++) begin
            #T
            clk = 1;
            #T
            clk = 0;
        end

        $display("testing end");
    end

endmodule : test
