
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./phaser.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    reg [60*8:0] label;
    `define DISPLAY(x) label=x; $display("\n>>>> %-s", label);

    logic clk;
    logic _mr;

    wire [9:0] seq;

    wire _phaseFetch, phaseFetch , phaseDecode , phaseExec;
	phaser #(.LOG(1)) ctrl( .clk, ._mr, .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec);
    

    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $monitor ("%9t ", $time,  "clk=%1b _mr=%2b ", clk, _mr, 
                " seq=%10b", seq,
                " phase(Fetch=%1b Decode=%1b Exec=%1b)", phaseFetch , phaseDecode , phaseExec,
                " %s", label
                );

        `endif
    end

    integer count;
    integer pFetch_count=3;
    integer pDecode_count=4;

    initial begin
        localparam T=100;   // clock cycle
        localparam SMALL_DELAY=20; // come gate delay

        `DISPLAY("init");
        _mr <= 0;
        clk <= 0;

        #T
        `Equals( seq, 10'b1);
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);

        #T
        `DISPLAY("_mr no clocking is ineffective = stay in FETCH mode")
        count = 0;
        while (count++ < 3) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);

            clk <= 0;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);
        end
        `Equals( seq, 10'b1);
        
        `DISPLAY("_mr released = still in FETCH mode after settle")
         _mr <= 1;
         #T
         `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);
         `Equals( seq, 10'b1);

        `DISPLAY("stay in FETCH mode for 3 clocks")
        count = 0;
        while (count++ < pFetch_count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);

            clk <= 0;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);
        end
         `Equals( seq, 10'b1000);
        

        `DISPLAY("stay in DECODE mode for 4 clocks");
        count = 0;
        while (count++ < pDecode_count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b010);

            clk <= 0;
            #T
            `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b010);
        end
         `Equals( seq, 10'b10000000);

        `DISPLAY("stay in DECODE mode for 1 clocks");
        clk <= 1;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b001);
        clk <= 0;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b001);
         `Equals( seq, 10'b100000000);

        `DISPLAY("no mode for 1 clocks");
        clk <= 1;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b000);
        clk <= 0;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b000);
         `Equals( seq, 10'b1000000000);

        `DISPLAY("return to FETCH mode");
        clk <= 1;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);
        clk <= 0;
        #T
        `Equals( {phaseFetch, phaseDecode, phaseExec}, 3'b100);
         `Equals( seq, 10'b1);

        $display("testing end");
    end

endmodule : test
