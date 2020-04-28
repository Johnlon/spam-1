`include "syncRegisterFile.v"
`include "../lib/assertion.v"

`timescale 1ns/1ns

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
    
    logic clk;
    logic _MR=1;

    parameter disabled       = 1;
    parameter enabled        = 0;

    syncRegisterFile #(.LOG(1)) regFile(
    _MR,
    clk,
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
    

    parameter cycle          = 100+25; // must be longer than the pulse width + prop delay inside the syncRegFile pulse generator
    
    initial begin
        $dumpfile("dumpfileSync.vcd");
        $dumpvars(0, clk, _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data);
        
        $display ("");
        $display ("%9t ", $time, " clk=%1b _wr_en=%1b wr_addr=%d   wr_data=%d  _rdL_en=%1b   rdL_addr=%d  rdL_data=%d  _rdR_en=%1b  rdR_addr=%d  rdR_data=%d",
                                    clk, _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data);
    end
    
    initial begin
        
        $display("defaults: write enabled at 0 with 255, no read");
		clk=0;

        _wr_en   = enabled;
        wr_addr = 0;
        wr_data = 255;
        
        _rdL_en   = disabled;
        rdL_addr = 0;
        
        _rdR_en   = disabled;
        rdR_addr = 0;
        
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'bz);
        
        $display("write enabled with data=1, readL disabled, readR enabled, but no clock");
        _wr_en   = enabled;
        _rdL_en  = disabled;
        _rdR_en  = enabled;
        wr_data = 1;
        #cycle
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'dx);
        #1000
        `assertEquals(rdL_data, 8'bz);

        $display("clock");
		// needs clock to be triggered
		clk=0; #200; clk=1; 
        #cycle
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'd1);

        $display("clock 255 into both");
		clk=0; #1; clk=1; 
        _wr_en   = enabled;
        _rdL_en  = enabled;
        _rdR_en  = enabled;
        wr_data = 8'hff;
        #cycle
        `assertEquals(rdL_data, 8'hff);
        `assertEquals(rdR_data, 8'hff);

        $display("should not load 255 into other reg if address changes - no clock so no load");
        wr_addr = 1;
        rdL_addr = 1;
        rdR_addr = 1;

        #cycle
        `assertEquals(rdL_data, 8'bx);
        `assertEquals(rdR_data, 8'bx);

        $finish;
    end
    
endmodule
