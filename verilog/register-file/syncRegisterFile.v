/* 4x8 dual port register file
* latches write data on postive edge, all other inputs remain async.
 */

`include "registerFile.v"
`timescale 1ns/100ps
module syncRegisterFile(
    	input clk;
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
    

	logic 

    registerFile regFile(
    wr_en,
    wr_addr_latched,
    wr_data,
    rdL_en,
    rdL_addr,
    rdL_data
    );
    
endmodule
