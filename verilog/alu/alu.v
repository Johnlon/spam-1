/* verilator lint_off ASSIGNDLY */

/// FIXME NEED TO USE CARRY IN CONSISTENTLY ON ARITH AND ROTATES (SHIFTS??)
/// FIXME NEED TO USE XY/AB/LR consistently in design !!
/// EG USING ROM 28C512

/// MANY OPS NOT REQUIRED IF R CAN BE IMMEDIATE eg "A+1" is same as "A+immediate 1" as long as both treat carry the same
/// HMMMM .. But can't do "B+immediate 1" unless instreg is available on A bus too.

// FIXME - see my notes on using lower range of A+/-B as no carry in and upper range as the shiftable ones taking cin into account.

`ifndef  V_ALU
`define  V_ALU
`include "alu_func.v"

`timescale 1ns/1ns

// need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
// use a sip5 10k resistor pull down to get 0. 
// else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
//assign reg_x_addr = device_sel[3:0]; // top bit of device sel ignored

`define toALUOP(OPNAME) alu_ops.OP_``OPNAME``
    
module alu_ops;
    localparam [4:0] OP_A=0;
    localparam [4:0] OP_B=1;
    localparam [4:0] OP_0=2;
    localparam [4:0] OP_MINUS_A=3;
    localparam [4:0] OP_MINUS_B=4;
    localparam [4:0] OP_A_PLUS_1=5;
    localparam [4:0] OP_B_PLUS_1=6;
    localparam [4:0] OP_A_MINUS_1=7;
    localparam [4:0] OP_B_MINUS_1=8;
    localparam [4:0] OP_A_PLUS_B=9;
    localparam [4:0] OP_A_MINUS_B=10;
    localparam [4:0] OP_B_MINUS_A=11;

    localparam [4:0] OP_A_TIMES_B_HI=16;
    localparam [4:0] OP_A_TIMES_B_LO=17;

    localparam [4:0] OP_A_AND_B=25;
    localparam [4:0] OP_A_OR_B=26;

endmodule

/* 
    Inputs to arithmentic must be two's complement.

    The comparator outputs GT/LT are only valid for logical values (not twos complement).
    For contrast: 74AS885 permits selection of logical or arithmetic magnitude comparison https://www.ti.com/lit/ds/symlink/sn74as885.pdf?ts=1592517566383&ref_url=https%253A%252F%252Fwww.google.com%252F

    If I wanted to do a signed magnitude check then I have to do maths. 
    If do A-B with carry-cleared and then look at the Z and C flags. 
    
    Z set means they were equal, 
    if Overflow is not set and
    C set means B>A, 
    Z and C unset means A>B  
    ... but only as long as O is not set
    what about -2 and 3 which will become -5 which is not Z and not C
*/

module alu #(parameter LOG=0, PD=120) (
    output [7:0] o,
    output _flag_c,
    output _flag_z,
    output _flag_n,
    output _flag_o,
    output _flag_gt,
    output _flag_lt,
    output _flag_eq,
    output _flag_ne,
    input  [7:0] a,
    input  [7:0] b,
    input  [4:0] alu_op,
    input  _flag_c_in
);
// | A           | B-1               | A*B (high bits)   | A ROR B       |
// | B           | __A+B+Cin (0)__   | A*B (low bits)    | A AND B       |
// | 0           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B arithmetic | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B logical    | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

// II items could be deleted due to immediates in any R side instruction
// JJ could be deleted additionally if Immediate can be on A or B bus
// | PASSA       | B-1   JJ          | A*B (high bits)   | A ROR B       |
// | PASSB       | __A+B+Cin (0)__   | A*B (low bits)    | A AND B       |
// | 0   II      | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1 II      | __A+B+Cin (1)__   | A >> B arithmetic | NOT B         |
// | B+1 JJ      | __A-B-Cin (1)__   | A >> B logical    | A+B (BCD)     |
// | A-1 II      | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |


    logic [7:0] ALU_Result;
    logic [15:0] TimesResult;
    assign #(PD) o = ALU_Result;

/*
- No overflow when adding a +ve and a -ve number
- No overflow when signs are the same for subtraction (because -- means a +)

Overflow occurs when the value affects the sign:
- overflow when adding two +ves yields a -ve
- or, adding two -ves gives a +ve
- or, subtract a -ve from a +ve and get a -ve
- or, subtract a +ve from a -ve and get a +ve

Can Overflow double as a divide / 0 flag ?
*/
    logic [8:0] tmp = 0; // long enough for result and carry 

    function [8:0] to9([7:0] i);
        to9 = i;
    endfunction

    wire signA=a[7];
    wire signB=b[7];

    logic force_not_o;

    assign #(PD) _flag_c = ! tmp[8];
    assign #(PD) _flag_n = !ALU_Result[7]; // top bit set indicates negative in signed arith
    assign #(PD) _flag_z = !(ALU_Result == 8'b0);

    logic _sign_changed;
    logic _out_of_range;
    assign #(PD) _flag_o = force_not_o | (_out_of_range & | _sign_changed); // & (a[7] == b[7]) & (a[7] != o[7]); //  FIXME !!!!!!! NOT IMPLEMENTED !!!
    assign #(PD) _flag_eq = !(a == b);    
    assign #(PD) _flag_ne = !(a != b);  

    // unsigned magnitude comparison of the input values.
    // if the bytes are eg two complement signed then this will produce incorrect results.
    // if this is the case then use a subtract operation instead
    assign #(PD) _flag_gt = !(a > b);
    assign #(PD) _flag_lt = !(a < b);

    wire [7:0] cin8 = {7'b0, !_flag_c_in};

    function signed [7:0] asSigned([7:0] in);
        asSigned = in;
    endfunction

    //if (LOG) 
    always @(*) 
        $display("%9t ALU", $time,
        " aluop=%-10s (%d)", alu_func.aluopName(alu_op), alu_op, // %1s causes string to lose trailing space
        "  ",
        " a=%08b (%3d/%4d) ", a, a, asSigned(a),
        " b=%08b (%3d/%4d) ", b, b, asSigned(b),
        " _c_in=%1b ", _flag_c_in,
        "  ",
  //      " _out_of_range=%b ", _out_of_range,
   //     " force_not_o=%b ", force_not_o,
    //    " _sign_changed=%b ", _sign_changed,
        "  ",
        " result=%08b (%3d/%4d) ", o, o, asSigned(o),
        " _c=%1b",  _flag_c,
        " _z=%1b",  _flag_z,
        " _n=%1b",  _flag_n,
        " _o=%1b",  _flag_o,
        " _gt=%1b", _flag_gt,
        " _lt=%1b", _flag_lt,
        " _eq=%1b", _flag_eq,
        " _ne=%1b", _flag_ne
         );


    always @* begin

        force_not_o=0;
        _sign_changed = 1;
        _out_of_range = 1;

        case (alu_op)
            alu_ops.OP_A: begin // this is not the same as "A+0 immediate" because + takes carry into account and what we want is PASSA so maybe call it PASSA?
                tmp=a;
            end
            alu_ops.OP_B: begin // this is not the same as "B+0 immediate" because + takes carry into account and what we want is PASSB so maybe call it PASSB?
                tmp=b;
            end
            alu_ops.OP_0: begin // not needed anymore cos immed allows 0 value into ALU
                tmp=0;
            end
            alu_ops.OP_MINUS_A: begin // should set overflow - same as 0-A surely
                tmp = -to9(a);
                _out_of_range = a > 127 | a < -128; // too big/small
            end
            alu_ops.OP_MINUS_B: begin // should set overflow - same as 0-B surely
                tmp = -to9(b);
                _out_of_range = a > 127 | a < -128; // too big/small
            end
            alu_ops.OP_A_PLUS_1: begin // UNLIKE A_PLUS_B this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first
                tmp = to9(a)+1;
            end
            alu_ops.OP_B_PLUS_1: begin // UNLIKE B_PLUS_A this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first
                tmp = to9(b)+1;
            end
            alu_ops.OP_A_MINUS_1: begin // UNLIKE A_MINUS_B this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first
                tmp = to9(a)-1;
            end
            alu_ops.OP_B_MINUS_1: begin // UNLIKE B_MINUS_A this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first
                tmp = to9(b)-1;
            end
            alu_ops.OP_A_OR_B: begin
                tmp=a | b;
            end
            alu_ops.OP_A_AND_B: begin
                tmp=a & b;
            end
            alu_ops.OP_A_PLUS_B: begin  // uses cin
                tmp = to9(a) + to9(b) + cin8;
                _sign_changed = !((a[7] == b[7]) & (a[7] != o[7]));
                force_not_o = a[7] != b[7]; // never overflow if sign is diff
            end
            alu_ops.OP_A_MINUS_B: begin // uses cin
                tmp = to9(a) - to9(b) - cin8;
                _sign_changed = !((a[7] != b[7]) & (a[7] != o[7]));
                force_not_o = a[7] == b[7]; // never overflow if sign is same
            end

            alu_ops.OP_A_TIMES_B_HI: begin // how do I do long multiplications?
                TimesResult = (a * b);
                tmp=TimesResult[15:8];
            end

            alu_ops.OP_A_TIMES_B_LO: begin // how do I do long multiplications?
                TimesResult = (a * b);
                tmp[7:0] = TimesResult[7:0];
                tmp[8] = (TimesResult[15:8] > 0); // set carry to indicate whether the upper byte has a value
            end

            default: begin
                ALU_Result = 8'bxzxzxzxz;
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!! UNHANDLED alu_op=%5b", $time, alu_op);
            end

        endcase
        ALU_Result = tmp;
    end

endmodule: alu

`endif
