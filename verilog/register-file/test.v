`include "registerFile.v"
`include "pulseGenerator.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
	input clk;
	input [1:0] wr_addr;
	input [7:0] wr_data;

	input rd1_en;
	input [1:0] rd1_addr;
	output [7:0] rd1_data;

	input rd2_en;
	input [1:0] rd2_addr;
	output [7:0] rd2_data;
    
	registerFile latch(
		clk,
		wr_addr,
		wr_data,

		rd1_en,
		rd1_addr,
		rd1_data,

		rd2_en,
		rd2_addr,
		rd2_data
	);

	wire pulse;
    
	pulseGenerator gen( clk, pulse );
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
		clk,
		wr_addr,
		wr_data,

		rd1_en,
		rd1_addr,
		rd1_data,

		rd2_en,
		rd2_addr,
		rd2_data
	);
        
        $display ("");
        $display ($time, "   clk  |  wr_addr   wr_data  |  rd1_en   rd1_addr  rd1_data  |   rd2_en  rd2_addr  rd2_data");
        $monitor ($time, "   %b    |       %2b  %8b  |       %b         %2b  %8b  |        %b        %2b  %8b",
		clk, pulse, wr_addr, wr_data, rd1_en, rd1_addr, rd1_data, rd2_en, rd2_addr, rd2_data
	);
    end

    initial begin
        parameter low      = 1'b0;
        parameter high     = 1'b1;
        parameter undefined = 1'bx;
        parameter zed       = 1'bz;

        parameter latched  = low;
        parameter transparent  = high;

        parameter disabled = high;
        parameter enabled = low;
        
        $finish;
    end

endmodule
