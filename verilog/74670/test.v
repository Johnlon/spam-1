`include "hct74670.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
    logic _wr_en;
    logic _rd_en;
    logic [1:0] wr_addr, rd_addr;
    logic [3:0] wr_data, rd_data;
    
    hct74670 regfile(
    _wr_en, wr_addr,  wr_data,
    _rd_en, rd_addr,  rd_data
    );
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  _wr_en, _rd_en,   wr_addr, rd_addr, wr_data, rd_data);
        
        $display ("");
        $display ($time,"   %s   %s | %s <= %s  | %s => %s", "_wr_en", "_rd_en",   "wr_addr", "wr_data", "rd_addr", "rd_data");
        $monitor ($time,"   %5b   %5b | %7b <= %7b  | %7b => %7b", _wr_en, _rd_en,   wr_addr, wr_data, rd_addr, rd_data);
    end
    
    
    // uncoment to see periodic reports
    //always
    //	#10 $display ($time,"   %5b   %5b %7b < = %7b  out %7b = > %7b", _wr_en, _rd_en,   wr_addr, wr_data, rd_addr, rd_data);
    
    initial begin
        parameter low      = 1'b0;
        parameter high     = 1'b1;
        parameter enabled  = low;
        parameter disabled = high;
        
        parameter undefined_data = 4'bx;
        parameter zed_data       = 4'bz;
        
        _rd_en   = enabled;
        wr_addr = 0;
        wr_data = 10;
        rd_addr = 0;
        #10
        `equals(rd_data , undefined_data, "nothing written so all data should be x");
        
        _wr_en = enabled;
        #5
        `equals(rd_data , undefined_data, "nothing written yet due to PD so all data should be x");
        #25
        `equals(rd_data , 10, "write propagated");
        
        wr_data = 1;
        #5
        `equals(rd_data , 10, "write enabled - data changed - output not propagated yet");
        #24
        `equals(rd_data , 1, "write enabled and write data changed - should have propagated to read");
        
        #5
        
        wr_data = 15;
        _wr_en   = disabled;
        #1
        `equals(rd_data , 1, "write disabled - retains value");
        
        _wr_en = enabled;
        #29
        `equals(rd_data , 15, "write enabled location 2");
        
        _rd_en = disabled;
        #29
        `equals(rd_data , zed_data, "read disabled");
        
        #29
        $finish;
    end
    
    
endmodule
