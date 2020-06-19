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
//    	input _MR,
    	input clk,

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
    
    function [7:0] get([1:0] r);
        get = regFile.get(r);
    endfunction

	wire [7:0] wr_data_latched;
	//wire _pulse;

    hct74574 #(.LOG(LOG)) input_register( .D(wr_data), .Q(wr_data_latched), .CLK(clk), ._OE(1'b0)); // registers data on clk +ve & _pulse goes low slightly later.

    // MONOSTABLE MULTIVIBRATOR 
    // Make delay long enough the internal settling of the latch and register file has had time,
    // but short enough so it doesn't excessively impact cycle time.
    // Signal "_pulse" goes low after monostable's propagation delay, stays low for RC time defined by PulseWidth
    /* verilator lint_off PINMISSING */
 //   hct74423 #(.PulseWidth(100)) monostable(._A(_wr_en), .B(clk), ._R(_MR), ._Q(_pulse)); // reset is async low and forces _pulse high, +ve B edge sends _pulse low for delay period
    /* verilator lint_on PINMISSING */

    registerFile #(.LOG(LOG)) regFile (
        //._wr_en(_pulse),
        ._wr_en(_wr_en),
        .wr_addr,
        .wr_data(wr_data_latched),
        ._rdL_en,
        .rdL_addr,
        .rdL_data,
        ._rdR_en,
        .rdR_addr,
        .rdR_data
    );

    if (LOG) always @(posedge clk) begin
        $display("%9t ", $time, "REGFILE-S : REGISTERED input data %08b", wr_data);
    end

    if (LOG) always @(*) begin
        //$display("%9t ", $time, "REGFILE-S : ARGS : _wr_en=%1b _pulse=%1b write[%d]=%d     _rdX_en=%1b X[%d]=>%d    _rdY_en=%1b Y[%d]=>%d   (preletch=%d)  _MR=%1b" ,
         //    _wr_en, _pulse, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdL_en, rdR_addr, rdR_data, wr_data_latched, _MR);

        if (!_wr_en) $display("%9t ", $time, "REGFILE-S : UPDATING write[%d] = %d", wr_addr, wr_data_latched);
    end

    if (LOG) always @(posedge _wr_en) begin
        $display("%9t ", $time, "REGFILE-S : LATCHED write[%d]=%d", wr_addr, wr_data_latched);
    end

    
endmodule

`endif
