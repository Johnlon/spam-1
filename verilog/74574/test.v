`include "hct74574.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
	logic [7:0] D, Q;
	logic CLK, OE_N;
    
	hct74574 register(
	.D, .Q,
	.CLK, .OE_N
	);
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  D, Q, CLK, OE_N);
        
        $display ("");
        $display ($time, "   D         Q         CLK  OE_N");
        $monitor ($time, "   %8b  %8b  %b   %b", D,   Q,   CLK,  OE_N);
    end

    initial begin
        parameter zero      = 8'b0;
        parameter aa     = 8'b10101010;
        parameter undefined = 8'bx;
        parameter zed       = 8'bz;
        parameter low  = 0;
        parameter high  = 1;
        parameter disabled = 1;
        parameter enabled = 0;
        parameter tPD = 19+1;

	D=zero;
	CLK=low;
	#tPD 
        `equals(Q , undefined, "initial");

	OE_N=disabled;
	#tPD 
        `equals(Q , zed, "output disabled");

	OE_N=enabled;
	#tPD 
        `equals(Q , undefined, "unclocked - D = X");

	D=aa;
	CLK=high;
	#10
        `equals(Q , undefined, "clocked - but not yet propagated");

	D=1;
	CLK=high;
	#tPD
        `equals(Q , aa, "transition to high - wth AA");

	D=255;
	#tPD
        `equals(Q , aa, "remain high - wth 255");

	D=zero;
	CLK=low;
	#tPD
        `equals(Q , aa, "transition to low - wth zero");

	D=255;
	CLK=high;
	OE_N=disabled;
	#tPD
        `equals(Q , zed, "transition to high - wth 255 but OE disabled");

	OE_N=enabled;
	#tPD
        `equals(Q , 255, "oe enabled");


    end
endmodule
