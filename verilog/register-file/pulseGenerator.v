
`timescale 1ns/100ps
module pulseGenerator (
		input clk,
		output pulse,
		output not_clk
	);
    
	// 74HCT670 write pulse width is 20ns -- HOW IN PRACTICE? RC? 
	// MORE GATES?
	
	assign #(25) not_clk =  (! clk);

	assign pulse = clk & not_clk;

endmodule
