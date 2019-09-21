/* 4x8 dual port register file
* latches write data on postive edge, all other inputs remain async.
 */

`include "registerFile.v"
`include "../74574/hct74574.v"
`timescale 1ns/100ps
module syncRegisterFile(
    	input clk,

        input wr_en, 
        input [1:0] wr_addr,
        input [7:0] wr_data,

        input rdL_en,
        input [1:0] rdL_addr,
        output [7:0] rdL_data,

        input rdR_en,
        input [1:0] rdR_addr,
        output [7:0] rdR_data
);
    
	logic [7:0] D, Q;
	wire [7:0] wr_data_latched;
	logic CLK, OE_N;
    
	hct74574 register(
	.D(wr_data), .Q(wr_data_latched), .CLK(clk), .OE_N(1'b0)
	);
    
    registerFile regFile(
    wr_en,
    wr_addr,
    wr_data_latched,
    rdL_en,
    rdL_addr,
    rdL_data,
    rdR_en,
    rdR_addr,
    rdR_data
    );
    
endmodule
