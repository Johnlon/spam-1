/* 4x8 dual port register file
 */

`include "../74670/hct74670.v"
`include "../74573/hct74573.v"
`timescale 1ns/100ps
module registerFile(
	input clk,
	input [1:0] wr_addr,
	input [7:0] wr_data,

	input rd1_en,
	input [1:0] rd1_addr,
	output [7:0] rd1_data,

	input rd2_en,
	input [1:0] rd2_addr,
	output [7:0] rd2_data
	);
    
    reg [3:0] wr_dataLo;
    reg [3:0] wr_dataHi;
    
    assign {wr_dataHi, wr_dataLo} = wr_data;

endmodule
