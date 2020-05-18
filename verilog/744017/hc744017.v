
`timescale 1ns/1ns
// https://assets.nexperia.com/documents/data-sheet/74HC_HCT4017.pdf

module hc744017(cp0, _cp1, mr, q, _co);
    input cp0, _cp1, mr;
    output [9:0] q;
    output _co;

    reg [9:0] o;

    always @*
        $monitor("%9t decade ", $time, " cp0=", cp0, " _cp1=", _cp1, " mr=", mr, " q=%1d", q, " _co=", _co, " o=%1d", o);

    always@(posedge cp0 , negedge _cp1)
    begin
        o <= mr ? o : (o + 1) < 10 ? o+1: 0;
    end

    always @* begin
        if (mr) begin
            o <= 0;
        end
    end

    assign _co = o <= 4; //2^5

    assign q = 1 << o;

endmodule

