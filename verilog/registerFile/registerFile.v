/* 4x8 dual port register file
 */

`include "../74670/hct74670.v"
//`include "../pulseGenerator/pulseGenerator.v"
`timescale 1ns/1ns
module registerFile #(parameter LOG=0) (
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

    assign {wr_data_hi, wr_data_lo} = wr_data;
    assign rdL_data                 = {rdL_data_hi, rdL_data_lo};
    assign rdR_data                 = {rdR_data_hi, rdR_data_lo};

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
    

    if (LOG) always @(*) begin 
        $display("%9t REGFILE-A : _wr_en=%1b  write[%d]=%d     _rdX_en=%1b X[%d]=>%d    _rdY_en=%1b Y[%d]=>%d" , $time, 
                    _wr_en, wr_addr, wr_data, 
                    _rdL_en, rdL_addr, rdL_data, 
                    _rdL_en, rdR_addr, rdR_data);
    end

    always @(
                bankR_hi.registers[0] or bankR_lo.registers[0] or
                bankR_hi.registers[1] or bankR_lo.registers[1] or
                bankR_hi.registers[2] or bankR_lo.registers[2] or
                bankR_hi.registers[3] or bankR_lo.registers[3]
    ) begin
        $display("%9t ", $time, "REGFILE-A : A=%3d B=%3d C=%3d D=%3d", 
                bankR_hi.registers[0]*8 + bankR_lo.registers[0],
                bankR_hi.registers[1]*8 + bankR_lo.registers[1],
                bankR_hi.registers[2]*8 + bankR_lo.registers[2],
                bankR_hi.registers[3]*8 + bankR_lo.registers[3]
        );
    end

/*
    if (LOG) always @(posedge _wr_en) begin
        $display("%8d REGFILE-A : _wr_en +vs edge - STORING write[%d] = %d", $time, wr_addr, wr_data);
    end
*/

    if (LOG) always @(*) begin
        if (!_wr_en) $display("%9t REGFILE-A : WRITING write[%d] = %d", $time, wr_addr, wr_data);
    end

    
endmodule
