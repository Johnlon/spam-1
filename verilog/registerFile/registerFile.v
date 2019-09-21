/* 4x8 dual port register file
 */

`include "../74670/hct74670.v"
`include "../pulseGenerator/pulseGenerator.v"
`timescale 1ns/100ps
module registerFile(
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
    
    wire [3:0] wr_data_hi, wr_data_lo;
    wire [3:0] rdL_data_hi, rdL_data_lo, rdR_data_hi, rdR_data_lo;
    
    hct74670 left_bank_lo(
    wr_en,
    wr_addr,
    wr_data_lo,
    rdL_en,
    rdL_addr,
    rdL_data_lo
    );
    hct74670 left_bank_hi(
    wr_en,
    wr_addr,
    wr_data_hi,
    rdL_en,
    rdL_addr,
    rdL_data_hi
    );
    
    hct74670 bankR_lo(
    wr_en,
    wr_addr,
    wr_data_lo,
    rdR_en,
    rdR_addr,
    rdR_data_lo
    );
    hct74670 bankR_hi(
    wr_en,
    wr_addr,
    wr_data_hi,
    rdR_en,
    rdR_addr,
    rdR_data_hi
    );
    
    assign {wr_data_hi, wr_data_lo} = wr_data;
    assign rdL_data                 = {rdL_data_hi, rdL_data_lo};
    assign rdR_data                 = {rdR_data_hi, rdR_data_lo};
    
endmodule
