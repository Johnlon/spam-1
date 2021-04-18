`ifndef  V_RF
`define  V_RF


/* 4x8 dual port register file
 */

`include "../74670/hct74670.v"
//`include "../pulseGenerator/pulseGenerator.v"
`timescale 1ns/1ns
module registerFile #(parameter LOG=0) (
                    input _wr_en, 
                    input [1:0] wr_addr,
                    input [7:0] wr_data,

                    input _rdA_en,
                    input [1:0] rdA_addr,
                    output [7:0] rdA_data,

                    input _rdB_en,
                    input [1:0] rdB_addr,
                    output [7:0] rdB_data
	    );

    
    wire [3:0] wr_data_hi, wr_data_lo;
// verilator lint_off UNOPTFLAT
    wire [3:0] rdA_data_hi, rdA_data_lo, rdB_data_hi, rdB_data_lo;
// verilator lint_on UNOPTFLAT

    assign {wr_data_hi, wr_data_lo} = wr_data;
    assign rdA_data                 = {rdA_data_hi, rdA_data_lo};
    assign rdB_data                 = {rdB_data_hi, rdB_data_lo};

    hct74670 bankA_lo(
        _wr_en,
        wr_addr,
        wr_data_lo,
        _rdA_en,
        rdA_addr,
        rdA_data_lo
    );
    hct74670 bankA_hi(
        _wr_en,
        wr_addr,
        wr_data_hi,
        _rdA_en,
        rdA_addr,
        rdA_data_hi
    );
    
    hct74670 bankB_lo(
        _wr_en,
        wr_addr,
        wr_data_lo,
        _rdB_en,
        rdB_addr,
        rdB_data_lo
    );
    hct74670 bankB_hi(
        _wr_en,
        wr_addr,
        wr_data_hi,
        _rdB_en,
        rdB_addr,
        rdB_data_hi
    );
    
    if (LOG) always @(negedge _wr_en) begin 
        $display("%9t REGFILE : BEGIN WRITE _wr_en=%1b,  write[%d]=%-3d     _rdX_en=%1b, X[%d]=>%-3d    _rdY_en=%1b, Y[%d]=>%-3d" , $time, 
                    _wr_en, wr_addr, wr_data, 
                    _rdA_en, rdA_addr, rdA_data, 
                    _rdA_en, rdB_addr, rdB_data);
    end
    if (LOG) always @(posedge _wr_en) begin 
        $display("%9t REGFILE : END WRITE _wr_en=%1b,  write[%d]=%-3d     _rdX_en=%1b, X[%d]=>%-3d    _rdY_en=%1b, Y[%d]=>%-3d" , $time, 
                    _wr_en, wr_addr, wr_data, 
                    _rdA_en, rdA_addr, rdA_data, 
                    _rdA_en, rdB_addr, rdB_data);
    end

    function [7:0] get([1:0] r);
        get = {bankA_hi.registers[r], bankA_lo.registers[r]};
    endfunction

    // only need to bind to L or R as they have the same value
    if (LOG) always @(   
                    _wr_en//, wr_addr, wr_data, 
                    //_rdA_en, rdA_addr, rdA_data, 
                    //_rdA_en, rdB_addr, rdB_data
    ) begin
        $display("%9t ", $time, "REGFILE : _wr_en=%1b", //,  write[%d]=%-3d     _rdX_en=%1b, X[%d]=>%-3d    _rdY_en=%1b, Y[%d]=>%-3d" , 
                    _wr_en//, wr_addr, wr_data, 
                    //_rdA_en, rdA_addr, rdA_data, 
                    //_rdA_en, rdB_addr, rdB_data
                    );
    end

`ifndef verilator
    if (LOG) always @(   
                bankB_hi.registers[0] or bankB_lo.registers[0] or
                bankB_hi.registers[1] or bankB_lo.registers[1] or
                bankB_hi.registers[2] or bankB_lo.registers[2] or
                bankB_hi.registers[3] or bankB_lo.registers[3]
    ) begin
        $display("%9t ", $time, "REGFILE : DATA UPDATE A=%-3d(%-2x) B=%-3d(%-2x) C=%-3d(%-2x) D=%-3d(%-2x)", 
                get(0), get(0), 
                get(1), get(1), 
                get(2), get(2), 
                get(3), get(3));
    end
`endif

/*
    if (LOG) always @(posedge _wr_en) begin
        $display("%8d REGFILE-A : _wr_en +vs edge - STORING write[%d] = %d", $time, wr_addr, wr_data);
    end
*/
    
endmodule

`endif
