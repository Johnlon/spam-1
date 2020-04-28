`include "registerFile.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps

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
    
    parameter disabled       = 1;
    parameter enabled        = 0;
    parameter pulse_disabled = 0;
    parameter pulse_enabled  = 1;
    parameter cycle          = 25;
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data);
        
/*
        $display ("");
        $display ($time, "   _____                       ______                           ______");
        $display ($time, "   _wr_en wr_addr   wr_data  |  _rdL_en   rdL_addr  rdL_data  |   _rdR_en  rdR_addr  rdR_data");
        $monitor ($time, "   %1b           %2b  %8b  |         %1b        %2b  %8b  |          %1b       %2b  %8b",
        _wr_en, wr_addr, wr_data, _rdL_en, rdL_addr, rdL_data, _rdR_en, rdR_addr, rdR_data
        );
*/
        /*
         $display ($time, "   _wr_en  |  ");
         $monitor ($time, "   %b    |  %1b   ",
         _wr_en, pulse
         );
         */
    end

    task check;
        input [7:0] A,B,C,D;
    begin
        _rdL_en  = enabled;
        _rdR_en  = enabled;

        rdL_addr = 0;
        rdR_addr = 0;
        #cycle;
        `Equals(rdL_data, A);
        `Equals(rdR_data, A);
        
        rdL_addr = 1;
        rdR_addr = 1;
        #cycle;
        `Equals(rdL_data, B);
        `Equals(rdR_data, B);
        
        rdL_addr = 2;
        rdR_addr = 2;
        #cycle;
        `Equals(rdL_data, C);
        `Equals(rdR_data, C);
        
        rdL_addr = 3;
        rdR_addr = 3;
        #cycle;
        `Equals(rdL_data, D);
        `Equals(rdR_data, D);
        
    end
    endtask

    
    initial begin
        
        $display("0: defaults: write enabled at 0 with 255, no read");
        _wr_en   = enabled;
        wr_addr = 0;
        wr_data = 255;
        
        _rdL_en   = disabled;
        _rdR_en   = disabled;
        rdL_addr = 0;
        rdR_addr = 0;
        
        #10;
        `assertEquals(rdL_data, 8'bx);
        `assertEquals(rdR_data, 8'bx);
        
        $display("1: write enabled at 0 with 1, readL disabled, readR en at 0");
        _wr_en   = enabled;
        wr_data = 1;
        _rdL_en  = disabled;
        _rdR_en  = enabled;
        rdL_addr = 0;
        rdR_addr = 0;
        #cycle;
        `assertEquals(rdL_data, 8'bz);
        `assertEquals(rdR_data, 8'd1);
        

        $display("2: write enabled at 2 with 2, readL at 2, readR en at 0");
        wr_data  = 2;
        wr_addr  = 2;
        _rdR_en   = enabled;
        _rdL_en   = enabled;
        rdL_addr = 2;
        rdR_addr = 0;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd1);

        check(1, 'x, 2, 'x);
        
        $display("3: write enabled at 1 with 255, readL at 2, readR en at 0");
        wr_addr = 1;
        wr_data = 255;
        rdL_addr = 2;
        rdR_addr = 0;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd1);

        check(1, 255, 2, 'x);
        
        $display("4: write enabled at 1 with 255, readL at 2, readR disabled");
        _rdL_en=enabled;
        _rdR_en=disabled;
        rdL_addr = 2;
        rdR_addr = 0;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'dz);

        check(1, 255, 2, 'x);
        
        $display("5: write enabled at 1 with 255, readL at 2, readR at 2");
        _rdL_en=enabled;
        _rdR_en=enabled;
        rdL_addr = 2;
        rdR_addr = 2;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        check(1, 255, 2, 'x);

        $display("6: write disabled at 1 with 1, readL at 2, readR at 2");
        _wr_en = disabled;
        wr_data = 1;
        _rdL_en=enabled;
        _rdR_en=enabled;
        rdL_addr = 2;
        rdR_addr = 2;
        #cycle;
        `assertEquals(rdL_data, 8'd2);
        `assertEquals(rdR_data, 8'd2);
        
        check(1, 255, 2, 'x);

        $display("7: write disabled, readL at 1, readR at 2");
        _wr_en = disabled;
        wr_data = 1;
        _rdL_en=enabled;
        _rdR_en=enabled;
        rdL_addr = 1;
        rdR_addr = 2;
        #cycle;
        `assertEquals(rdL_data, 8'd255);
        `assertEquals(rdR_data, 8'd2);
        
        check(1, 255, 2, 'x);

        #cycle;
        $finish;
    end
    
endmodule
