
`include "phased_clock.v" 
`include "../lib/assertion.v"
`timescale 1ns/1ns

// http://www.brannonelectronics.com/images/4%20Phase%20Clock%20Generator.pdf
// relies on PD of demux being less than the PD of flipflops

module test;
    wire clk_1, clk_2, clk_3, clk_4;
    reg clk=0;
    reg _MR=1;

    // must be longer than PD of device else malfunction
   parameter CLK_T=30;

   parameter TEST_INTERVAL=CLK_T * 10;

   phased_clock  ph(
    .clk,
    ._MR,
    .clk_1,
    .clk_2,
    .clk_3,
    .clk_4
    );
 
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
    end

    always #CLK_T clk=!clk;

   initial begin
        #100 
        $display($time, " MR");
        _MR <= 0;
        #60 
        $display($time, " MR release ");
        _MR <= 1;

        #4000 $finish;
   end

   wire [3:0] phased = {clk_1, clk_2, clk_3, clk_4};

`ifndef verilator
   initial
      $monitor ($time, " clk ", clk, " phase=%4b", phased);
`endif

endmodule  
 

