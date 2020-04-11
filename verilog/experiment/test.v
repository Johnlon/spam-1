`include "../pulseGenerator/pulseGenerator.v"

`default_nettype none
`timescale 1ns/1ns

module test();
    logic CP;
    logic _en;
    logic gated_clk;


    logic clk_en=1;
    wire pulse;
    pulseGenerator gen( CP, clk_en, pulse);

    //BAD clock gating, can cause glitches in output
    //assign #5 gated_clk = _en || CP;
    //assign #5 gated_clk = _en && CP;
    //assign #15 gated_clk = (!_en) && CP;
    assign #15 gated_clk = !(_en || pulse);


    always begin

        #100 
        if (CP == 0) 
            $display("%6d CPU Clock=%1b +++", $time, CP);
        else
            $display("%6d CPU Clock=%1b ---", $time, CP);

        CP = ! CP; 

    end

    always @(negedge CP) begin
        #10 _en=!_en;
    end

    initial begin
        CP=0;
        _en=1;
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );
        $monitor(0, " CP=%1b _en=%1b gated_clk=%1b", CP, _en, gated_clk );

        #2000  $finish; 
    end

endmodule
