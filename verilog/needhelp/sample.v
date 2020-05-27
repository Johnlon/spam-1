
// `timescale 1ns/1ns
`default_nettype none

`define Equals(ACTUAL, expected) \
if (ACTUAL === expected) begin \
  $display("%d passed:  %b == %b - ACTUAL", `__LINE__, ACTUAL, expected); \
end \
else \
begin  \
  $display("%d failed: '%b' is not '%b' - ACTUAL", `__LINE__, ACTUAL, expected); 	\
  $finish;  \
end

module hct74245( 
    input dir,
    input nOE,
    inout [7:0] A,
    inout [7:0] B
);

    // HCT typical @ 5v according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf
    // specify
    //     (A *> B) = (10);
    //     (B *> A) = (10);
    //     (dir *> A) = (16);
    //     (dir *> B) = (16);
    //     (nOE *> A) = (16);
    //     (nOE *> B) = (16);
    // endspecify

    // always @(*) 
    //     $display("74245: A=%8b ", A, "B=%-8b ", B, "dir=%1b", dir, " nOE=%1b", nOE);

    assign A=nOE? 8'bzzzzzzzz :dir?8'bzzzzzzzz:B;
    assign B=nOE? 8'bzzzzzzzz :dir?A: 8'bzzzzzzzz;

    // assign B=nOE? 8'bzzzzzzzz :A;

endmodule: hct74245


module alu(
    output [7:0] o,
    input  [7:0] x,
    input  [7:0] y,
    input  force_to_zero
    );
    wire [7:0] xout;
    wire AtoB=1'b1;


    always @(*) 
        $display("alu  : x=%-8b y=%-8b o=%-8b xin=%8b xout=%8b", x,y,o,xin,xout);

    // when force_to_zero=1 x becomes 0 otherwise original arg value  is used 
    
    wire [7:0] xin = x;
    hct74245 bufX(.A(xin), .B(xout), .dir(AtoB), .nOE(force_to_zero)); 
    
    pulldown pullToZero[7:0](xout);
    
    assign o = xout | y;

endmodule: alu


module test();

	wire  [7:0] o;
	logic [7:0] x;
	logic [7:0] y;
	logic force_to_zero;

	alu Alu(.o, .x, .y, .force_to_zero);

    initial begin
        `ifndef verilator
        $monitor ("test:  x=%8b y=%8b o=%8b force_to_zero=%1b", x,y,o, force_to_zero);
        `endif
    end


    initial begin

        assign x = 8'b10101010;
        assign y = 8'b11110000;
        assign force_to_zero = 1'b0;
       
        #10
        $display ("test:  x=%8b y=%8b o=%8b force_to_zero=%1b", x,y,o, force_to_zero);
        
        // #10
        // $display($time, o);
        // assign force_to_zero = 1'b1;
       
        // #10
        // $display($time, o);
        // #10
        // $display($time, o);

    //     #1
    //     assign force_to_zero = 1'b0;
    //    // `Equals(o, 8'b10101010);
    //     $display(o);

    end
endmodule : test
