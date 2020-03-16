
`timescale 1ns/100ps

/* verilator lint_off ASSIGNDLY */
/* verilator lint_off UNOPTFLAT */

// I don't know how to make this birectional
module hct74245( 
    input dir,
    input nOE,
    inout [7:0] A,
    inout [7:0] B
);

    // HCT typical @ 5v according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf
    // specify
    // (A *> B) = (10);
    // (B *> A) = (10);
    // (dir *> A) = (16);
    // (dir *> B) = (16);
    // (nOE *> A) = (16);
    // (nOE *> B) = (16);
    // endspecify

    // always @(*) 
    //     $display("74245 dir=%1b", dir, " nOE=%1b", nOE, " A= %-8b ", A, " B =%-8b ", B);

    assign #5 A=nOE? 8'bzzzzzzzz :dir?8'bzzzzzzzz:B;
    assign #5 B=nOE? 8'bzzzzzzzz :dir?A:8'bzzzzzzzz;

endmodule: hct74245

