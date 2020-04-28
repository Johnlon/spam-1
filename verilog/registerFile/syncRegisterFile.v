`ifndef V_SYNC_REGFILE
`define V_SYNC_REGFILE
/* 4x8 dual port register file
* latches write data on postive edge, all other inputs remain async.
 */

`include "../74423/hct74423.v"
`include "../registerFile/registerFile.v"
`include "../74574/hct74574.v"
`timescale 1ns/1ns
module syncRegisterFile #(parameter LOG=0, PulseWidth=100) (
    	input _MR,
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
    
	wire [7:0] wr_data_latched;
	wire _pulse;

	hct74423 monostable(._A(_wr_en), .B(CP), ._R(_MR), ._Q(_pulse));
    hct74574 #(.LOG(LOG)) register( .D(wr_data), .Q(wr_data_latched), .CLK(CP), ._OE(1'b0));
    
    registerFile #(.LOG(LOG)) regFile (
        ._wr_en(_pulse),
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
        $display("%9t ", $time, "REGFILE-S : ARGS : _wr_en=%1b _pulse=%1b write[%d]=%d     _rdX_en=%1b X[%d]=>%d    _rdY_en=%1b Y[%d]=>%d   (preletch=%d)  _MR=%1b" ,
             _wr_en, _pulse, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdL_en, rdR_addr, rdR_data, wr_data_latched, _MR);

        if (!_pulse) $display("%9t ", $time, "REGFILE-S : UPDATING write[%d] = %d", wr_addr, wr_data_latched);
    end

    if (LOG) always @(posedge _pulse) begin
        $display("%9t ", $time, "REGFILE-S : LATCHED write[%d]=%d", wr_addr, wr_data_latched);
    end

    
endmodule

`endif
