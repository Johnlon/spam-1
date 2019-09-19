`include "hct74670.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
    logic wr_en;
    logic rd_en;
    logic [1:0] wr_addr, rd_addr;
    logic [3:0] wr_data, rd_data;
    
    hct74670 regfile(
    wr_en, wr_addr,  wr_data,
    rd_en, rd_addr,  rd_data
    );
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  wr_en, rd_en,   wr_addr, rd_addr, wr_data, rd_data);
        
        $display ("");
        $display ($time,"   %s   %s %s <    = %s  out %s    = > %s", "wr_en", "rd_en",   "wr_addr", "wr_data", "rd_addr", "rd_data");
        $monitor ($time,"   %5b   %5b %7b < = %7b  out %7b = > %7b", wr_en, rd_en,   wr_addr, wr_data, rd_addr, rd_data);
    end
    
    
    // uncoment to see periodic reports
    //always
    //	#10 $display ($time,"   %5b   %5b %7b < = %7b  out %7b = > %7b", wr_en, rd_en,   wr_addr, wr_data, rd_addr, rd_data);
    
    initial begin
        parameter low      = 1'b0;
        parameter high     = 1'b1;
        parameter enabled  = low;
        parameter disabled = high;
        
        parameter undefined_data = 4'bx;
        parameter zed_data       = 4'bz;
        
        rd_en   = enabled;
        wr_addr = 0;
        wr_data = 10;
        rd_addr = 0;
        #10
        `equals(rd_data , undefined_data, "nothing written so all data should be x");
        
        wr_en = enabled;
        #5
        `equals(rd_data , undefined_data, "nothing written so all data should be x");
        #24
        `equals(rd_data , 10, "write enabled");
        
        wr_data = 1;
        #5
        `equals(rd_data , 10, "write enabled - data changed - output not changed yet");
        #24
        `equals(rd_data , 1, "write enabled and write data changed - should propagate to read");
        
        #5
        
        wr_data = 15;
        wr_en   = disabled;
        #29
        `equals(rd_data , 1, "write disabled - retains value");
        
        wr_en = enabled;
        #29
        `equals(rd_data , 15, "write enabled location 2");
        
        rd_en = disabled;
        #29
        `equals(rd_data , zed_data, "read disabled");
        
        #29
        $finish;
    end
    
    
endmodule
