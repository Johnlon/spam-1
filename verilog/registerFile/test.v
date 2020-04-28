`include "registerFile.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
    logic _wr_en;
    logic [1:0] wr_addr;
    logic [7:0] wr_data;
    
    logic _rdL_en;
    logic [1:0] rdL_addr;
    logic [7:0] rdL_data;
    
    logic _rdR_en;
    logic [1:0] rdR_addr;
    logic [7:0] rdR_data;
    
    logic we;
    
    registerFile regFile(
        _wr_en,
        wr_addr,
        wr_data,
        
        _rdL_en,
        rdL_addr,
        rdL_data,
        
        _rdR_en,
        rdR_addr,
        rdR_data
    );
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data);
        
        $display ("");
        $display ($time, "   _____                       ______                           ______");
        $display ($time, "   _wr_en wr_addr   wr_data  |  _rdL_en   rdL_addr  rdL_data  |   _rdR_en  rdR_addr  rdR_data");
        $monitor ($time, "   %1b           %2b  %8b  |         %1b        %2b  %8b  |          %1b       %2b  %8b",
        _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data
        );
        /*
         $display ($time, "   _wr_en  |  ");
         $monitor ($time, "   %b    |  %1b   ",
         _wr_en, pulse
         );
         */
    end
    
    initial begin
        parameter disabled       = 1;
        parameter enabled        = 0;
        parameter pulse_disabled = 0;
        parameter pulse_enabled  = 1;
        parameter cycle          = 25;
        
        $display("defaults: write enabled at 0 with 255, no read");
        _wr_en   = enabled;
        wr_addr = 0;
        wr_data = 255;
        
        _rdL_en   = disabled;
        rdL_addr = 0;
        
        _rdR_en   = disabled;
        rdR_addr = 0;
        
        #10;
        `assertEquals(rdL_data, 8'bx);
        `assertEquals(rdR_data, 8'bx);
        
        $display("write enabled at 0 with 1, readL disabled, readR en at 0");
        _wr_en   = enabled;
        _rdR_en  = enabled;
        wr_data = 1;
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'd1);
        
        $display("write enabled at 2 with 2, readL at 2, readR en at 0");
        wr_data  = 2;
        wr_addr  = 2;
        _rdR_en   = enabled;
        rdL_addr = 2;
        _rdL_en   = enabled;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd1);
        
        $display("write enabled at 1 with 255, readL at 2, readR en at 0");
        wr_addr = 1;
        wr_data = 255;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd1);
        
        $display("write enabled at 1 with 255, readL at 2, readR disabled");
        _rdR_en=disabled;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'dz);
        
        $display("write enabled at 1 with 255, readL at 2, readR at 2");
        _rdR_en=enabled;
        rdR_addr = 2;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        $display("write disabled at 1 with 1, readL at 2, readR at 2");
        _wr_en = disabled;
        wr_data = 1;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        $display("write disabled, readL at 1, readR at 2");
        _wr_en = disabled;
        wr_data = 1;
        rdL_addr = 1;
        #cycle;
        `assertEquals(rdL_data, 8'd255);
        `assertEquals(rdR_data, 8'd2);
        
        #cycle;
        $finish;
    end
    
endmodule
