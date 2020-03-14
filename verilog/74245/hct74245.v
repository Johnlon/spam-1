
`timescale 1ns/100ps
//module hct74245( A , B , dir , nOE );

// I don't know how to make this birectional
module hct74245( 
    input dir,
    input nOE,
    inout [7:0] A,
    inout [7:0] B
);

    // always @(*) 
    //     $display("74245 dir ", dir, " nOE ", nOE, " A  %8b ", A, " B  %8b ", B);

    assign #5 A=nOE?8'bzzzzzzzz:dir?8'bzzzzzzzz:B;
    assign #5 B=nOE?8'bzzzzzzzz:dir?A:8'bzzzzzzzz;

endmodule: hct74245

