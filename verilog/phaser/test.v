
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
    logic mr;

    wire [9:0] seq;

    wire _phaseFetch, phaseFetch , phaseDecode , phaseExec;
	phaser #(.LOG(1)) ctrl( .clk, .mr, .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec);

    wire [2:0] FDE = {phaseFetch , phaseDecode , phaseExec};    

    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $monitor ("%9t ", $time,  "TEST    clk=%1b", clk, 
                " seq=%10b", seq,
                " phase(FDE=%1b%1b%1b)", phaseFetch , phaseDecode , phaseExec,
                " mr=%1b", mr, 
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
        mr <= 1;
        clk <= 0;

        #T
        `Equals( seq, 10'b1);
        `Equals( FDE, 3'b000);

        #T
        `DISPLAY("mr no clocking is ineffective = stay in NONE mode")
        count = 0;
        while (count++ < 3) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( FDE, 3'b000);

            clk <= 0;
            #T
            `Equals( FDE, 3'b000);
        end
        `Equals( seq, 10'b1);
        
        `DISPLAY("mr released = still in NONE mode after settle")
         mr <= 0;
         #T
         `Equals( FDE, 3'b000);
         `Equals( seq, 10'b1);

        `DISPLAY("stay in FETCH mode for 3 clocks")
        count = 0;
        while (count++ < pFetch_count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( FDE, 3'b100);

            clk <= 0;
            #T
            `Equals( FDE, 3'b100);
        end
        `Equals( seq, 10'b1000);
        

        `DISPLAY("stay in DECODE mode for 4 clocks");
        count = 0;
        while (count++ < pDecode_count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( FDE, 3'b010);

            clk <= 0;
            #T
            `Equals( FDE, 3'b010);
        end
        `Equals( seq, 10'b10000000);

        `DISPLAY("stay in DECODE mode for 1 clocks");
        clk <= 1;
        #T
        `Equals( FDE, 3'b001);
        clk <= 0;
        #T
        `Equals( FDE, 3'b001);
        `Equals( seq, 10'b100000000);

        `DISPLAY("no mode for 1 clocks");
        clk <= 1;
        #T
        `Equals( FDE, 3'b000);
        clk <= 0;
        #T
        `Equals( FDE, 3'b000);
        `Equals( seq, 10'b1000000000);

        `DISPLAY("return to UNUSED mode");
        clk <= 1;
        #T
        `Equals( FDE, 3'b000);
        clk <= 0;
        #T
        `Equals( FDE, 3'b000);
        `Equals( seq, 10'b0000000001);

        `DISPLAY("return to FETCH mode");
        clk <= 1;
        #T
        `Equals( FDE, 3'b100);
        clk <= 0;
        #T
        `Equals( FDE, 3'b100);
        `Equals( seq, 10'b0000000010);

        $display("testing end");
    end

endmodule : test
