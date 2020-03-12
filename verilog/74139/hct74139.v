// from 
// https://books.google.co.uk/books?id=pkW5DwAAQBAJ&pg=PA362&lpg=PA362&dq=verilog+%2274139%22&source=bl&ots=p5Lb0vS6Gc&sig=ACfU3U39tOIUbv0jCeofuICsNqxWo7PJJA&hl=en&sa=X&ved=2ahUKEwjVp6ecno3oAhXEoFwKHdR-BX0Q6AEwAXoECDIQAQ#v=onepage&q=verilog%20%2274139%22&f=false

// Following naming used by https://assets.nexperia.com/documents/data-sheet/74HC_HCT139.pdf
`timescale 1ns/100ps
module hct74139(
input _Ea, 
input _Eb, 
input [1:0] Aa,
input [1:0] Ab,
output [3:0] _Ya,
output [3:0] _Yb
); 

// https://assets.nexperia.com/documents/data-sheet/74HC_HCT139.pdf
specify
  (_Ea *> _Ya) = (10);
  (Aa *> _Ya) = (14);
  (_Eb *> _Yb) = (10);
  (Ab *> _Yb) = (14);
endspecify

function [3:0] f74139(input [1:0] A, input _E); 

    logic [3:0] f74139;

    // data sheet shows Y0-Y1-Y2-Y3 in that order with HH=>1111, HL=>1110, LH=>1101, LL=>0111
    if (_E == 0) begin 
        case (A) 
            // per https://assets.nexperia.com/documents/data-sheet/74HC_HCT139.pdf _Y0 is lsb
            0: f74139 = 4'b1110;
            1: f74139 = 4'b1101;
            2: f74139 = 4'b1011; 
            3: f74139 = 4'b0111; 
        endcase
    end 
    else
    begin
        f74139 = 4'b1111;
    end

endfunction

assign _Ya = f74139(Aa,_Ea);
assign _Yb = f74139(Ab,_Eb);

endmodule
