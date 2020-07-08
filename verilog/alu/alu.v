/* verilator lint_off ASSIGNDLY */

/// FIXME NEED TO USE CARRY IN CONSISTENTLY ON ARITH AND ROTATES (SHIFTS??)
/// FIXME NEED TO USE XY/AB/LR consistently in design !!
/// EG USING ROM 28C512

/// MANY OPS NOT REQUIRED IF R CAN BE IMMEDIATE eg "A+1" is same as "A+immediate 1" as long as both treat carry the same
/// HMMMM .. But can't do "B+immediate 1" unless instreg is available on A bus too.

// FIXME - see my notes on using lower range of A+/-B as no carry in and upper range as the shiftable ones taking cin into account.

`ifndef  V_ALU
`define  V_ALU

`timescale 1ns/1ns

// need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
// use a sip5 10k resistor pull down to get 0. 
// else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
//assign reg_x_addr = device_sel[3:0]; // top bit of device sel ignored

`define toALUOP(OPNAME) alu_ops.OP_``OPNAME``
    
module alu_ops;
    localparam [4:0] OP_0=0; // not needed as I can do PASSA with IMMED(0)
    localparam [4:0] OP_A=1;
    localparam [4:0] OP_B=2;
    localparam [4:0] OP_NEGATE_A=3;  
    localparam [4:0] OP_NEGATE_B=4;  
    localparam [4:0] OP_A_PLUS_1=5; // not needed as I can do 'A+IMMED(1)'
    localparam [4:0] OP_B_PLUS_1=6; // needed because if using IREG then can't also use RAM as both are B bus
    localparam [4:0] OP_A_MINUS_1=7; // not needed as I can do 'A-IMMED(1)'

    localparam [4:0] OP_B_MINUS_1=8; // needed
    localparam [4:0] OP_A_PLUS_B=9;
    localparam [4:0] OP_A_MINUS_B=10;
    localparam [4:0] OP_B_MINUS_A=11;
    localparam [4:0] OP_A_MINUS_B_SIGNEDMAG=12;
    localparam [4:0] OP_A_PLUS_B_PLUS_C=13;
    localparam [4:0] OP_A_MINUS_B_MINUS_C=14;
    localparam [4:0] OP_B_MINUS_A_MINUS_C=15;

    localparam [4:0] OP_A_TIMES_B_LO=16;
    localparam [4:0] OP_A_TIMES_B_HI=17;
    localparam [4:0] OP_A_DIV_B=18;
    localparam [4:0] OP_A_MOD_B=19;
    localparam [4:0] OP_A_SHL_B=20;
    localparam [4:0] OP_A_SHR_B_L=21;
    localparam [4:0] OP_A_SHR_B_A=22;
    localparam [4:0] OP_A_ROL_B=23;

    localparam [4:0] OP_A_ROR_B=24;
    localparam [4:0] OP_A_AND_B=25;
    localparam [4:0] OP_A_OR_B=26;
    localparam [4:0] OP_A_XOR_B=27; // XOR Can synthesise NOT A by setting B to 1, but we lose ability to invert the B bus because I can't set A to 1 as IREG is B only - also ROM and RAM only write to B bus and some reg only write to A bus
    localparam [4:0] OP_NOT_A=28; // Change to A NAND B? which can synthesise NOT A when A points to desired reg and B is set to 1
    localparam [4:0] OP_NOT_B=29; // Change to A NOR B? we lose ability to invert the B bus because IREG and ROM and RAM only write to B bus so I can't do "RAM[] NOR RAM[]"
    localparam [4:0] OP_A_PLUS_B_BCD=30;
    localparam [4:0] OP_A_MINUS_B_BCD=31;

    
    function string aluopName;
        input [4:0] opcode;
        
        string ret;
        begin
            case(opcode)
                 0 : aluopName =    "0";
                 1 : aluopName =    "A";
                 2 : aluopName =    "B";
                 3 : aluopName =    "-A";
                 4 : aluopName =    "-B";
                 5 : aluopName =    "A+1";
                 6 : aluopName =    "B+1";
                 7 : aluopName =    "A-1";

                 8 : aluopName =    "B-1";
                 9 : aluopName =    "A+B";   // CarryIn not considered
                10 : aluopName =    "A-B";   // CarryIn not considered
                11 : aluopName =    "B-A";   // CarryIn not considered
                12 : aluopName =    "A-B signedmag"; // CarryIn not considered
                13 : aluopName =    "A+B+C"; // If CarryIn=N then this op is automatically updated to A+B
                14 : aluopName =    "A-B-C"; // If CarryIn=N then this op is automatically updated to A-B
                15 : aluopName =    "B-A-C"; // If CarryIn=N then this op is automatically updated to B-A

                16 : aluopName =    "A*B LO";
                17 : aluopName =    "A*B HI";
                18 : aluopName =    "A/B";
                19 : aluopName =    "A%B";
                20 : aluopName =    "A<<B";
                21 : aluopName =    "A>>B log";
                22 : aluopName =    "A>>B arith" ;
                23 : aluopName =    "A ROL B";

                24 : aluopName =    "A ROR B";
                25 : aluopName =    "A AND B";
                26 : aluopName =    "A OR B";
                27 : aluopName =    "A XOR B"; 
                28 : aluopName =    "NOT A";
                29 : aluopName =    "NOT B";
                30 : aluopName =    "A+B BCD";
                31 : aluopName =    "A-B BCD";
                default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    aluopName = ret;
                end
            endcase

        end
    endfunction

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

//// THIS IS THE MOST RECENT Jun 2020 ALU layout from CSCVon8
// | 0           | B-1               | A*B (low bits)    | A ROR B       |
// | A           | __A+B+Cin (0)__   | A*B (high bits)   | A AND B       |
// | B           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B logical    | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B arithmetic | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

// My wiring here is ....
// | A           | B-1               | A*B (low bits)    | A ROR B       |
// | B           | __A+B+Cin (0)__   | A*B (high bits)   | A AND B       |
// | 0           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B signedmag     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B logical    | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B arithmetic | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

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
    logic [4:0] alu_op_effective;

    function [8:0] to9([7:0] i);
        to9 = i;
    endfunction

    wire signA=a[7];
    wire signB=b[7];

    wire signed [7:0] signed_a = a;
    wire signed [7:0] signed_b = b;

    logic _sign_changed;

    assign #(PD) _flag_c = ! tmp[8];
    assign #(PD) _flag_n = !ALU_Result[7]; // top bit set indicates negative in signed arith
    assign #(PD) _flag_z = !(ALU_Result == 8'b0);
    assign #(PD) _flag_o = _sign_changed;
    assign #(PD) _flag_eq = !(a == b);    
    assign #(PD) _flag_ne = !(a != b);  

    // unsigned magnitude comparison of the input values.
    // if the bytes are eg two complement signed then this will produce incorrect results.
    // if this is the case then use a subtract operation instead
    logic unsigned_magnitude=1;

    //assign #(PD) _flag_gt = unsigned_magnitude ? !(signed_a > signed_b) : !(to9(a) > to9(b));
    //assign #(PD) _flag_lt = unsigned_magnitude ? !(signed_a < signed_b) : !(to9(a) < to9(b));
    assign #(PD) _flag_gt = unsigned_magnitude ? !(a>b) : !(signed_a > signed_b);
    assign #(PD) _flag_lt = unsigned_magnitude ? !(a<b) : !(signed_a < signed_b);

    wire [7:0] cin8 = {7'b0, !_flag_c_in};

    function signed [7:0] asSigned([7:0] in);
        asSigned = in;
    endfunction

    if (LOG) 
    always @(*) 
        $display("%9t ALU", $time,
        " aluop=%-10s (op:%d)", alu_ops.aluopName(alu_op), alu_op, // %1s causes string to lose trailing space
        " aluop_effective=%-10s", alu_ops.aluopName(alu_op_effective), // %1s causes string to lose trailing space
        "  ",
        " a=%08b (uns %3d/sign %4d) ", a, a, asSigned(a),
        " b=%08b (uns %3d/sign %4d) ", b, b, asSigned(b),
        " _c_in=%1b ", _flag_c_in,
        "  ",
        "  ",
        " result=%08b (uns %3d/sign %4d) ", o, o, asSigned(o),
        " _c=%1b",  _flag_c,
        " _z=%1b",  _flag_z,
        " _n=%1b",  _flag_n,
        " _o=%1b",  _flag_o,
        " _gt=%1b", _flag_gt,
        " _lt=%1b", _flag_lt,
        " _eq=%1b", _flag_eq,
        " _ne=%1b", _flag_ne,
        "      ",
        " _sign_changed=%b ", _sign_changed,
        " unsigned_magnitude=%b ", unsigned_magnitude
         );

    // http://class.ece.iastate.edu/arun/Cpre381/lectures/arithmetic.pdf
    // pass sign bits in for subtraction overflow
    // - No overflow when adding a +ve and a -ve number
    // - No overflow when signs are the same for subtraction (because -- means a +)
    //- overflow when adding two +ves yields a -ve
    //- or, adding two -ves gives a +ve
    //- or, subtract a -ve from a +ve and get a -ve
    //- or, subtract a +ve from a -ve and get a +ve
    function _subOv(left, right, o);
        if (left==right) return 1; // signs same subtraction
        if (!left & right & o) return 0; // pos sub neg eq neg
        if (left & !right & !o) return 0; // neg sub pos eq pos
        return 1;
    endfunction

    // http://class.ece.iastate.edu/arun/Cpre381/lectures/arithmetic.pdf
    // pass sign bits in for addition overflow
    // - No overflow when adding a +ve and a -ve number
    // - No overflow when signs are the same for subtraction (because -- means a +)
    //- overflow when adding two +ves yields a -ve
    //- or, adding two -ves gives a +ve
    //- or, subtract a -ve from a +ve and get a -ve
    //- or, subtract a +ve from a -ve and get a +ve
    function _addOv(left, right, o);
        if (left!=right) return 1; // signs diff 
        if (left & right & !o) return 0; // pos add neg eq neg
        if (!left & !right & o) return 0; // neg add pos eq pos
        return 1;
    endfunction

    always @* begin

        _sign_changed = 1;
        unsigned_magnitude=1;

        // FIXME TODO CHANGE TO LOGIC and delays etc
        if (_flag_c_in) begin
            case (alu_op)
                alu_ops.OP_A_PLUS_B_PLUS_C: begin  
                    alu_op_effective=alu_ops.OP_A_PLUS_B;
                end
                alu_ops.OP_A_MINUS_B_MINUS_C: begin 
                    alu_op_effective=alu_ops.OP_A_MINUS_B;
                end
                alu_ops.OP_B_MINUS_A_MINUS_C: begin 
                    alu_op_effective=alu_ops.OP_B_MINUS_A;
                end
                default: begin
                    alu_op_effective=alu_op;
                end
            endcase
        end
        else begin
             alu_op_effective=alu_op;
        end


        case (alu_op_effective)
            alu_ops.OP_0: begin // not needed anymore cos immed allows 0 value into ALU
                tmp=0;
            end
            alu_ops.OP_A: begin // this is not the same as "A+0 immediate" because + takes carry into account and what we want is PASSA so maybe call it PASSA?
                tmp=a;
            end
            alu_ops.OP_B: begin // this is not the same as "B+0 immediate" because + takes carry into account and what we want is PASSB so maybe call it PASSB?
                tmp=b;
            end
            alu_ops.OP_NEGATE_A: begin // should set overflow - same as 0-A surely
                tmp = {1'b0, -a}; // 0 no carry
            end
            alu_ops.OP_NEGATE_B: begin // should set overflow - same as 0-B surely
                tmp = {1'b0, -b}; // 0 no carry
            end
            alu_ops.OP_A_PLUS_1: begin // UNLIKE A_PLUS_B this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first  FIXME CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH AS LONG AS IMMED CAN BE ON BOTH BUSSES
                tmp = to9(a)+1;
            end
            alu_ops.OP_B_PLUS_1: begin // UNLIKE B_PLUS_A this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first  FIXME CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                tmp = to9(b)+1;
            end
            alu_ops.OP_A_MINUS_1: begin // UNLIKE A_MINUS_B this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first  FIXME CAN BE DONE USING "LOWER" A_-_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                tmp = to9(a)-1;
            end

            ///// 8 ...
            alu_ops.OP_B_MINUS_1: begin // UNLIKE B_MINUS_A this sets carry but doesn't consume it - useful for low byte of a counter where we always want CLC first  FIXME CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                tmp = to9(b)-1;
            end

            // low bank is when CIN=0 or these ops were directly selected
            alu_ops.OP_A_PLUS_B: begin  
                tmp = to9(a) + to9(b);
                _sign_changed = _addOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_A_MINUS_B: begin 
                tmp = to9(a) - to9(b);
                _sign_changed = _subOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_B_MINUS_A: begin 
                tmp = to9(b) - to9(a);
                _sign_changed = _subOv(b[7], a[7], o[7]);
            end

            alu_ops.OP_A_MINUS_B_SIGNEDMAG: begin 
                unsigned_magnitude=0;
                tmp = to9(a) - to9(b);
                _sign_changed = _subOv(a[7], b[7], o[7]);
            end

            alu_ops.OP_A_PLUS_B_PLUS_C: begin  
                // OP ONLY USED WHEN CARRY IS ACTIVE
                tmp = (to9(a) + to9(b)) + 1; 
                _sign_changed = _addOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_A_MINUS_B_MINUS_C: begin 
                // OP ONLY USED WHEN CARRY IS ACTIVE
                tmp = (to9(a) - to9(b)) - 1;
                _sign_changed = _subOv(a[7],b[7],o[7]);
            end
            alu_ops.OP_B_MINUS_A_MINUS_C: begin 

                // OP ONLY USED WHEN CARRY IS ACTIVE
                tmp = (to9(b) - to9(a)) - 1;
                _sign_changed = _subOv(b[7],a[7],o[7]);
            end

            // 24 .............................................................
            alu_ops.OP_A_TIMES_B_HI: begin // how do I do long multiplications?
                TimesResult = (a * b);
                tmp={1'b0, TimesResult[15:8]};
            end

            alu_ops.OP_A_TIMES_B_LO: begin // how do I do long multiplications?
                TimesResult = (a * b);
                tmp[7:0] = TimesResult[7:0];
                tmp[8] = (TimesResult[15:8] > 0); // set carry to indicate whether the upper byte has a value
            end

            alu_ops.OP_A_OR_B: begin
                tmp=a | b;
            end
            alu_ops.OP_A_AND_B: begin
                tmp=a & b;
            end
            default: begin
                tmp = 9'bxzxzxzxzx;
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!! UNHANDLED alu_op=%5b : SpecifiedOp:%-s EffectiveOp=%-s", $time, alu_op, 
                        alu_ops.aluopName(alu_op),
                        alu_ops.aluopName(alu_op_effective)
                        );
            end

        endcase
        ALU_Result = tmp;
    end

endmodule: alu

`endif
