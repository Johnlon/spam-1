`timescale 1ns/100ps

module hct74245( 
    input dir,
    input nOE,
    inout [7:0] A,
    inout [7:0] B
);
    parameter [8*10:0] NAME="74245";
    parameter LOG=0;

    // HCT typical @ 5v according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf
    if (0)
        specify
            (A => B) = (10);
            (B => A) = (10);
            (dir *> A) = (16);
            (dir *> B) = (16);
            (nOE *> A) = (16);
            (nOE *> B) = (16);
        endspecify

    if (LOG) always @(*) begin
        $display("%6d", $time,  " BUF %-s: A=%8b ", NAME, A, "B=%-8b ", B, "dir=%1b", dir, " nOE=%1b", nOE);
    end

    assign A=nOE? 8'bzzzzzzzz :dir?8'bzzzzzzzz:B;
    assign B=nOE? 8'bzzzzzzzz :dir?A:8'bzzzzzzzz;

endmodule: hct74245

