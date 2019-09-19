`include "hct74573.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps
`default_nettype none

module icarus_tb();
    
	logic D0, D1, D2, D3, D4, D5, D6, D7;
	logic Q0, Q1, Q2, Q3, Q4, Q5, Q6, Q7;
	logic LE, OE_N;
    
	hct74573 latch(
	.D0, .D1, .D2, .D3, .D4, .D5, .D6, .D7,
	.Q0, .Q1, .Q2, .Q3, .Q4, .Q5, .Q6, .Q7,
	.LE, .OE_N
	);
    
    
    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
		D0, D1, D2, D3, D4, D5, D6, D7,
		Q0, Q1, Q2, Q3, Q4, Q5, Q6, Q7,
		LE, OE_N
	);
        
        $display ("");
        $display ($time, "   D0 D1 D2 D3 D4 D5 D6 D7   Q0 Q1 Q2 Q3 Q4 Q5 Q6 Q7   LE  OE_N");
        $monitor ($time, "   %d  %d  %d  %d  %d  %d  %d  %d    %d  %d  %d  %d  %d  %d  %d  %d    %d   %d",
		D0, D1, D2, D3, D4, D5, D6, D7,   Q0, Q1, Q2, Q3, Q4, Q5, Q6, Q7,   LE,  OE_N
	);
    end

    initial begin
        parameter low      = 1'b0;
        parameter high     = 1'b1;
        parameter undefined = 1'bx;
        parameter zed       = 1'bz;

        parameter latched  = low;
        parameter transparent  = high;

        parameter disabled = high;
        parameter enabled = low;
        

	D0=low;
	#30 
        `equals(Q0 , undefined, "initial");

	OE_N=disabled;
	LE=transparent;
	#16 
        `equals(Q0 , zed, "latch transparent - output enabled");

	OE_N=enabled;
	#19 
        `equals(Q0 , low, "latch transparent - D0 low");

	D0=high;
	#18
        `equals(Q0 , high, "D0 high");

	LE=latched;
	D0=low;
	#50
        `equals(Q0 , high, "D0 high because latched");

	LE=transparent;
	#16
        `equals(Q0 , low, "D0 low because transparent");

	OE_N=disabled;
	#19
        `equals(Q0 , zed, "D0 low because transparent");

    end
endmodule
