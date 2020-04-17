`ifndef V_SYNC_REGFILE
`define V_SYNC_REGFILE
/* 4x8 dual port register file
* latches write data on postive edge, all other inputs remain async.
 */

`include "../registerFile/registerFile.v"
`include "../74574/hct74574.v"
`timescale 1ns/1ns
module syncRegisterFile #(parameter LOG=0) (
    	input CP,

        input _wr_en, 
        input [1:0] wr_addr,
        input [7:0] wr_data,

        input _rdL_en,
        input [1:0] rdL_addr,
        output [7:0] rdL_data,

        input _rdR_en,
        input [1:0] rdR_addr,
        output [7:0] rdR_data
);
    
	logic [7:0] D, Q;
	wire [7:0] wr_data_latched;
	logic CLK, OE_N;
    
    hct74574 register(
	.D(wr_data), .Q(wr_data_latched), .CLK(CP), ._OE(1'b0)
    );
    
    registerFile #(.LOG(LOG)) regFile (
        ._wr_en,
        .wr_addr,
        .wr_data(wr_data_latched),
        ._rdL_en,
        .rdL_addr,
        .rdL_data,
        ._rdR_en,
        .rdR_addr,
        .rdR_data
    );

    if (LOG) always @(*) begin
        $display("%8d REGFILE : STATUS REG[%d] = %d  (non-latched %d) _wr_en=%1b CP=%1b", $time, wr_addr, wr_data_latched, wr_data, _wr_en, CP);
    end

    if (LOG) always @(posedge CP) begin
        $display("%8d REGFILE : LATCHING %d", $time, wr_data);
    end

    
endmodule

`endif
