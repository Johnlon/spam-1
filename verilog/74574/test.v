`include "hct74574.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
	logic [7:0] D, Q;
	logic CLK, _OE;
    
	hct74574 register(
	.D, .Q,
	.CLK, ._OE
	);
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  D, Q, CLK, _OE);
        
        $display ("");
        $display ($time, "   D         Q         CLK  _OE");
        $monitor ($time, "   %8b  %8b  %b   %b", D,   Q,   CLK,  _OE);
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

	_OE=disabled;
	#tPD 
        `equals(Q , zed, "output disabled");

	_OE=enabled;
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
	_OE=disabled;
	#tPD
        `equals(Q , zed, "transition to high - wth 255 but _OE disabled");

	_OE=enabled;
	#tPD
        `equals(Q , 255, "oe enabled");


    end
endmodule
