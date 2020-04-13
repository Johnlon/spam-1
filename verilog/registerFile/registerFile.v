/* 4x8 dual port register file
 */

`include "../74670/hct74670.v"
//`include "../pulseGenerator/pulseGenerator.v"
`timescale 1ns/1ns
module registerFile(
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
    
    wire [3:0] wr_data_hi, wr_data_lo;
    wire [3:0] rdL_data_hi, rdL_data_lo, rdR_data_hi, rdR_data_lo;
    
    hct74670 left_bank_lo(
    _wr_en,
    wr_addr,
    wr_data_lo,
    _rdL_en,
    rdL_addr,
    rdL_data_lo
    );
    hct74670 left_bank_hi(
    _wr_en,
    wr_addr,
    wr_data_hi,
    _rdL_en,
    rdL_addr,
    rdL_data_hi
    );
    
    hct74670 bankR_lo(
    _wr_en,
    wr_addr,
    wr_data_lo,
    _rdR_en,
    rdR_addr,
    rdR_data_lo
    );
    hct74670 bankR_hi(
    _wr_en,
    wr_addr,
    wr_data_hi,
    _rdR_en,
    rdR_addr,
    rdR_data_hi
    );
    
    assign {wr_data_hi, wr_data_lo} = wr_data;
    assign rdL_data                 = {rdL_data_hi, rdL_data_lo};
    assign rdR_data                 = {rdR_data_hi, rdR_data_lo};

    always @(posedge _wr_en) begin
        $display("%8d REGFILE : LATCHING REG[%d] = %d (%02x)", $time, wr_addr, wr_data, wr_data);
    end
    always @(*) begin 
        $display("%8d REGFILE : READ X[%d]=>%d  Y[%d]=>%d" , $time, rdL_addr, rdL_data, rdR_addr, rdR_data);
    end

    
endmodule
