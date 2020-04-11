
`default_nettype none
`timescale 1ns/1ns

module test();
    logic CP;
    logic _en;
    logic _clocked_en_or;
    logic _clocked_en_and;
    logic gated_clock;
    event glitch;
    logic glitch_or;

    //BAD clock gating, can cause glitches in output

    // This or gating of an active low en line had glitches
    assign #5 _clocked_en_or = _en || CP;
    assign #5 _clocked_en_and = _en && CP;
    assign #5 gated_clock = (!_en) && CP;

    // instanteneous - condition that must NEVER be true
    assign glitch_or = (! _clocked_en_or && ! CP) && _en;
    always @(posedge glitch_or) 
        -> glitch;

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
        _en=0;
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );
        $monitor(0, " CP=%1b _en=%1b _clocked_en=%1b", CP, _en, _clocked_en_or, _clocked_en_and, gated_clock );

        #2000  $finish; 

    end

endmodule
