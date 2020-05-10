`include "hct74109.v" 
`timescale 1ns/1ns

module tb_jk;
   reg j;
   reg _k;
   reg clk=0;

   wire q, _q;
 
   always #5 clk = ~clk;
 
   hct74109    jk0 ( .j(j),
                  ._k(_k),
                  .clk(clk),
                  .q(q),
                  ._q(_q)
    );
 
   initial begin
            // hold
      #30 
        $display("hold mode");
        j <= 0;
        _k <= 1;
 
        // load reset Q=L
      #30 
        $display("reset mode");
        j <= 0;
        _k <= 0;

        // load set Q=H
      #30 
        $display("load mode");
        j <= 1;
        _k <= 1;

        // toggle
      #30 
        $display("toggle mode");
        j <= 1;
        _k <= 0;

      #30 $finish;
   end
 
   initial
      $monitor ($time, " clk ", clk, " j=%0d _k=%0d q=%0d", j, _k, q);
      //$monitor ($time, " j=%0d _k=%0d q=%0d", j, _k, q);

endmodule  
 

