`include "syncRegisterFile.v"
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
    
    logic clk;
    
    syncRegisterFile regFile(
    clk,
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
        $dumpvars(0, clk, wr_en, wr_addr, wr_data, rdL_en, rdL_addr, rdL_data, rdR_en, rdR_addr, rdR_data);
        
        $display ("");
        $display ($time, "       _____                       ______                           _____");
        $display ($time, "   clk wr_en wr_addr   wr_data  |  rdL_en   rdL_addr  rdL_data  |   rdR_en  rdR_addr  rdR_data");
        $monitor ($time, "   %1b  %1b          %2b  %8b  |       %1b         %2b  %8b  |        %1b        %2b  %8b",
        clk, wr_en, wr_addr, wr_data, rdL_en, rdL_addr, rdL_data, rdR_en, rdR_addr, rdR_data
        );
    end
    
    initial begin
        parameter disabled       = 1;
        parameter enabled        = 0;
        parameter pulse_disabled = 0;
        parameter pulse_enabled  = 1;
        parameter cycle          = 100;
        
        $display("defaults: write enabled at 0 with 255, no read");
		clk=0;

        wr_en   = enabled;
        wr_addr = 0;
        wr_data = 255;
        
        rdL_en   = disabled;
        rdL_addr = 0;
        
        rdR_en   = disabled;
        rdR_addr = 0;
        
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'bz);
        
        $display("write enabled at 0 with 1, readL disabled, readR en at 0, but no clock");
        wr_en   = enabled;
        rdL_en  = disabled;
        rdR_en  = enabled;
        wr_data = 1;
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'dx);

        $display("clock");
		// needs clock to be triggered
		clk=0; #1; clk=1; 
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'd1);

        $display("clock 255intoboth");
		clk=0; #1; clk=1; 
        wr_en   = enabled;
        rdL_en  = enabled;
        rdR_en  = enabled;
        wr_data = 8'hff;
        #cycle;
        `assertEquals(rdL_data, 8'hff);
        `assertEquals(rdR_data, 8'hff);


        #cycle;
        $finish;
    end
    
endmodule
