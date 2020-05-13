
`include "phased_clock.v" 
`include "../lib/assertion.v"
`timescale 1ns/1ns

// http://www.brannonelectronics.com/images/4%20Phase%20Clock%20Generator.pdf
// relies on PD of demux being less than the PD of flipflops

module test;
    wire phase0;
    wire phase1;
    wire phase2;
    wire phase3;
   reg clk=0;
   reg _reset=1;

    // must be longer than PD of device else malfunction
   parameter CLK_T=30;

   parameter TEST_INTERVAL=CLK_T * 10;

   phased_clock  ph(
    .clk,
    ._reset,
    .phase0,
    .phase1,
    .phase2,
    .phase3
    );
 
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
    end

    always #CLK_T clk=!clk;

   initial begin
        #100 
        $display($time, " reset");
        _reset <= 0;
        #60 
        $display($time, " reset release ");
        _reset <= 1;

        #4000 $finish;
   end

   wire [3:0] phased = {phase3, phase2, phase1, phase0};
   initial
      $monitor ($time, " clk ", clk, " phase=%4b", phased);

endmodule  
 

