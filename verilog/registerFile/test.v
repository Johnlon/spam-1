`include "registerFile.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
    logic wr_en;
    logic [1:0] wr_addr;
    logic [7:0] wr_data;
    
    logic rdL_en;
    logic [1:0] rdL_addr;
    logic [7:0] rdL_data;
    
    logic rdR_en;
    logic [1:0] rdR_addr;
    logic [7:0] rdR_data;
    
    logic we;
    
    registerFile regFile(
    wr_en,
    wr_addr,
    wr_data,
    
    rdL_en,
    rdL_addr,
    rdL_data,
    
    rdR_en,
    rdR_addr,
    rdR_data
    );
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, wr_en, wr_addr, wr_data, rdL_en, rdL_addr, rdL_data, rdR_en, rdR_addr, rdR_data);
        
        $display ("");
        $display ($time, "   _____                       ______                           ______");
        $display ($time, "   wr_en wr_addr   wr_data  |  rdL_en   rdL_addr  rdL_data  |   rdR_en  rdR_addr  rdR_data");
        $monitor ($time, "   %1b          %2b  %8b  |       %1b         %2b  %8b  |        %1b        %2b  %8b",
        wr_en, wr_addr, wr_data, rdL_en, rdL_addr, rdL_data, rdR_en, rdR_addr, rdR_data
        );
        /*
         $display ($time, "   wr_en  |  ");
         $monitor ($time, "   %b    |  %1b   ",
         wr_en, pulse
         );
         */
    end
    
    initial begin
        parameter disabled       = 1;
        parameter enabled        = 0;
        parameter pulse_disabled = 0;
        parameter pulse_enabled  = 1;
        parameter cycle          = 100;
        
        $display("defaults: write enabled at 0 with 255, no read");
        wr_en   = enabled;
        wr_addr = 0;
        wr_data = 255;
        
        rdL_en   = disabled;
        rdL_addr = 0;
        
        rdR_en   = disabled;
        rdR_addr = 0;
        
        #10;
        `assertEquals(rdL_data, 8'bx);
        `assertEquals(rdR_data, 8'bx);
        
        $display("write enabled at 0 with 1, readL disabled, readR en at 0");
        wr_en   = enabled;
        rdR_en  = enabled;
        wr_data = 1;
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'd1);
        
        $display("write enabled at 2 with 2, readL at 2, readR en at 0");
        wr_data  = 2;
        wr_addr  = 2;
        rdR_en   = enabled;
        rdL_addr = 2;
        rdL_en   = enabled;
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
        rdR_en=disabled;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'dz);
        
        $display("write enabled at 1 with 255, readL at 2, readR at 2");
        rdR_en=enabled;
        rdR_addr = 2;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        $display("write disabled at 1 with 1, readL at 2, readR at 2");
        wr_en = disabled;
        wr_data = 1;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        $display("write disabled, readL at 1, readR at 2");
        wr_en = disabled;
        wr_data = 1;
        rdL_addr = 1;
        #cycle;
        `assertEquals(rdL_data, 8'd255);
        `assertEquals(rdR_data, 8'd2);
        
        #cycle;
        $finish;
    end
    
endmodule
