// https://media.digikey.com/pdf/Data%20Sheets/NXP%20PDFs/74HC(T)670.pdf
// active low enable inputs

`timescale 1ns/100ps
module hct74670 (input _wr_en,
                 input [1:0] wr_addr,
                 input [3:0] wr_data,
                 input _rd_en,
                 input [1:0] rd_addr,
                 output [3:0] rd_data);
    
    parameter LOG=0;

    // Register file storage
    reg [3:0] registers[3:0];
    
    specify
    (_rd_en *> rd_data)   = (18:18:35);
    (_wr_en *> rd_data)   = (28:28:50);
    (rd_addr *> rd_data) = (21:21:40);
    (wr_data => rd_data) = (27:27:50);
    endspecify
    
    // write to register file
    always @(*) begin
        if (!_wr_en) begin
            registers[wr_addr] <= wr_data;
            if (LOG) $display(" writing @ " , wr_addr , "  <= ",wr_data);
        end
    end
        
    // reading from register file
    always_comb
    // always @(*)  << this gives warning: ./hct74670.v:32: warning: @* is sensitive to all 4 words in array 'registers'.
    begin
        out_val = registers[rd_addr];
    end
    
    
    reg [7:0] out_val;
    
    assign rd_data = _rd_en ? 4'bz : out_val;
    
endmodule
