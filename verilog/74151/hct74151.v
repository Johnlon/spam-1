// https://assets.nexperia.com/documents/data-sheet/74HC_HCT151_Q100.pdf
// The data sheet says this chips glitches during transitions - this shows up in the test
`timescale 1ns/1ns
module hct74151(_E, I, S, Y, _Y);
    parameter LOG=0;
    output Y, _Y;
    input [2:0] S;
    input [7:0] I;
    input _E;

    // setup timing be delaying the signals according to the data PD then combine them at last minute
    wire [7:0] #19 Id = I;
    wire [2:0] #20 Sd = S;
    // according to nexperia _E->_Y is slower than _E->Y
    wire #13 _Ed = _E;
    wire #18 Ed = !_E;

    // combine
    wire O =
        (Sd==0 & Id[0]) ||
        (Sd==1 & Id[1]) ||
        (Sd==2 & Id[2]) ||
        (Sd==3 & Id[3]) ||
        (Sd==4 & Id[4]) ||
        (Sd==5 & Id[5]) ||
        (Sd==6 & Id[6]) ||
        (Sd==7 & Id[7]);

    assign Y = _Ed==0 ? O : 0;
    assign _Y = Ed==1 ? !O : 1;

    if (LOG)
    always @* begin
        $display("%9t ", $time, "_E=%1b  I=%8b  S=%1d   Y=%b _Y=%b (_Ed=%b, Ed=%b)", _E, I, S, Y, _Y, _Ed, Ed);
    end

endmodule

