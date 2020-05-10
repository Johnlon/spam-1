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

    parameter PROP_DELAY=17; 
    parameter SETUP_TIME=8;  // JK must be setup AHEAD fo the clock - see datasheet

    reg Q,J,_K;
 
    assign #(SETUP_TIME) J = j;
    assign #(SETUP_TIME) _K = _k;

/*
    specify
        (clk => Q) = (PROP_DELAY);
    endspecify
*/
 
    always @(posedge clk)
    begin
        case ({J,_K})
            2'b01 :  Q <= Q;
            2'b00 :  Q <= 0;
            2'b11 :  Q <= 1;
            2'b10 :  Q <= ~Q;
        endcase

        //$display(" j ", J, " _k ", _K, " q ", Q);
    end

    assign #(PROP_DELAY) q = Q;
    assign #(PROP_DELAY) _q = !Q;

endmodule

