
`timescale 1ns/100ps
`default_nettype none

module hct74245( 
    input dir,
    input nOE,
    inout [7:0] A,
    inout [7:0] B
);
    // HCT typical @ 5v according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf
        specify
            (A => B) = (10);
            (B => A) = (10);
            (dir *> A) = (16);
            (dir *> B) = (16);
            (nOE *> A) = (16);
            (nOE *> B) = (16);
        endspecify


    assign A=nOE? 8'bzzzzzzzz :dir?8'bzzzzzzzz:B;
    assign B=nOE? 8'bzzzzzzzz :dir?A:8'bzzzzzzzz;

    always @(*)
        $display("%6d", $time,  " BUF : A=%8b ", A, "B=%-8b ", B, "dir=%1b", dir, " nOE=%1b", nOE);
   
endmodule: hct74245



module test();

    wire AtoB=1'b1;     // direction A to B

    logic nOEvar=0;      // output enabled
    wire nOE=nOEvar;      // output enabled

    wire [7:0] A = 8'b10101010;       // I expect this same value on B !!!!!!
    wire  [7:0] B;

    hct74245 bufTest(.A, .B, .dir(AtoB), .nOE); 

    pulldown pullTo0[7:0](B);

    always @(*) 
        $display("%6d", $time,  " TEST: A=%8b ", A, "B=%-8b ", B);

    initial begin

        // output should follow input because nOE is low
        nOEvar=1'b0;      // output enabled
        #300
        if (B !== 8'b10101010) begin 
            $display("FAILED 1: '%b' is not '%b' ", B, 8'b10101010);
            $finish_and_return(1); 
        end

        // output should be 0 because nOE is high and B should be Z therefore pulldown should pull to 0
        nOEvar=1'b1;      // output disabled
        #300
        if (B !== 8'b00000000) begin 
            $display("FAILED 2: '%8b' is not '%8b' ", B, 8'b00000000);
            $finish_and_return(1); 
        end
    end
endmodule : test
