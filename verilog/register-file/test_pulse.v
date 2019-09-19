`include "registerFile.v"
`include "pulseGenerator.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
	logic clk;
   
     initial begin
	 #1  clk = 0; 
	 forever begin
		#100  clk =  ! clk; 
	end
     end 

	wire pulse;
	wire np;
    
	pulseGenerator gen( clk, pulse, np );
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
		clk,
		pulse
	);
        
        $display ("");
        $display ($time, "   clk  pulse");
	$monitor ($time, "   %b   %b   %b", clk, pulse, np);

    end

    initial begin
        
        #3000 $finish;
    end

endmodule
