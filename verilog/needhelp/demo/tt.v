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



// THE FIRST TEST FAILS ON MASTER

module test();

    wire AtoB=1'b1;     // direction A to B

    logic nOEvar=0;      // output enabled
    wire nOE=nOEvar;      // output enabled

    // I expect this same value on B !!!!!!
    wire [7:0] A = 8'b10101010;       

    // NOTE : Remove the pulldown and change this from tri to tri0 and then the test passes
    tri0  [7:0] B;                                   
    
     // transceiver - B should either follow A or be pulled low 
     hct74245 bufTest(.A, .B, .dir(AtoB), .nOE); 

    // pull B to 0 when output of buffer is disabled.
    // NOTE:  Remove this pulldown and change the B wire from tri to tri0 and then the test passes
//    pulldown pullTo0[7:0](B);

    always @(*) 
        $display("%6d", $time,  " TEST: A=%8b ", A, "B=%-8b ", B);

    initial begin

        // output B should follow input A because nOE is low
        nOEvar=1'b0;      // output enabled
        #100
        if (B !== 8'b10101010) begin 
            $display("FAILED 1: '%b' is not '%b' ", B, 8'b10101010);
            $finish_and_return(1); 
        end

        // output B should be 0 because nOE is high, and B should be Z therefore pulldown should pull to 0
        nOEvar=1'b1;      // output disabled
        #100
        if (B !== 8'b00000000) begin 
            $display("FAILED 2: '%8b' is not '%8b' ", B, 8'b00000000);
            $finish_and_return(1); 
        end
    end
endmodule : test


