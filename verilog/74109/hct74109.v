// NOT implemented fully https://assets.nexperia.com/documents/data-sheet/74HC_HCT109.pdf
// Missing _SD/_RD
`timescale 1ns/1ns

module hct74109 ( 
    input j,
    input _k,
    input clk,
    output q, 
    output _q
);

reg Q;
 
/*
    specify
        (clk => q) = (17);
        (clk => _q) = (17);
    endspecify
*/
 
    always @(posedge clk)
    begin
        case ({j,_k})
            2'b01 :  Q <= Q;
            2'b00 :  Q <= 0;
            2'b11 :  Q <= 1;
            2'b10 :  Q <= ~Q;
        endcase
        //$display(" j ", j, " _k ", _k, " q ", Q);
    end

    assign q = Q;
    assign _q = !Q;

endmodule

