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
    localparam [4:0] OP_A_PLUS_1=5; // not needed as I can do 'A+IMMED(1)'  - use for ROR or RCR?
    localparam [4:0] OP_B_PLUS_1=6; // needed because if using IREG then can't also use RAM as both are B bus
    localparam [4:0] OP_A_MINUS_1=7; // not needed as I can do 'A-IMMED(1)' - use for ROL or RCL?

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
    localparam [4:0] OP_A_LSL_B=20;
    localparam [4:0] OP_A_LSR_B=21; // logical shift right - simple bit wise
    localparam [4:0] OP_A_ASR_B=22; // arith shift right - preserves top bit and fills with top bit as shift right   ie same as "CMP #80/ROR A" on 6502
// this is not a ROL as t uses carry in. This is a RCP rotate thru carry left
// rotate thru carry left can be sythesised as per 6502 using "asl/adc #0" - OTHER ROTATE TYPES DONT GO THRU CARRY
    localparam [4:0] OP_A_ROL_B=23; 

// this is not an ROR as ROR rotate within the byte. http://vitaly_filatov.tripod.com/ng/asm/asm_000.101.html
// instead this is a 8086 RCR rotate thru carry - http://vitaly_filatov.tripod.com/ng/asm/asm_000.93.html
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
                20 : aluopName =    "A LSL B";
                21 : aluopName =    "A LSR B";
                22 : aluopName =    "A ASL B" ;
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
//    logic [8:0] tmp = 'x; // long enough for result and carry 

    logic signed [9:0] c_buf_c;

    task set_ctop(c);
        c_buf_c[9] =c;
    endtask
    task set_cbot(c);
        c_buf_c[0] =c;
    endtask
    task set_result([7:0] r);
        c_buf_c = {1'b0, r, 1'b0};
    endtask
    task set_result9([8:0] r);
        c_buf_c = {r, 1'b0};
    endtask

    function [7:0] alu_result();
         alu_result = c_buf_c[8:1];
    endfunction

    function result_sign();
         result_sign = c_buf_c[8];
    endfunction

    function c_top();
        c_top = c_buf_c[9];
    endfunction

    function c_bot();
        c_bot = c_buf_c[0];
    endfunction

    logic [4:0] alu_op_effective;

    logic _overflow;

    assign #(PD) o = c_buf_c[8:1];
    assign #(PD) _flag_c = !c_buf_c[9];
    assign #(PD) _flag_n = !c_buf_c[8]; // top bit set indicates negative in signed arith
    assign #(PD) _flag_z = !(c_buf_c[8:1] == 8'b0);
    assign #(PD) _flag_o = _overflow;
    assign #(PD) _flag_eq = !(a == b);    
    assign #(PD) _flag_ne = !(a != b);  

    // unsigned magnitude comparison of the input values.
    // if the bytes are eg two complement signed then this will produce incorrect results.
    // if this is the case then use a subtract operation instead
    logic unsigned_magnitude=1;

    // cast to signed numbers
    wire signed [7:0] signed_a = a;
    wire signed [7:0] signed_b = b;
    wire signed [7:0] signed_o = o;

    // optionally perform signed/unsigned mag comparison
    assign #(PD) _flag_gt = unsigned_magnitude ? !(a>b) : !(signed_a > signed_b);
    assign #(PD) _flag_lt = unsigned_magnitude ? !(a<b) : !(signed_a < signed_b);

    wire [7:0] cin8 = {7'b0, !_flag_c_in};
    logic [15:0] TimesResult;

    if (LOG) 
    always @(*) 
        $display("%9t ALU", $time,
        " aluop=%-10s (op:%d)", alu_ops.aluopName(alu_op), alu_op, // %1s causes string to lose trailing space
        " aluop_effective=%-10s", alu_ops.aluopName(alu_op_effective), // %1s causes string to lose trailing space
        "  ",
        " a=%08b (uns %3d/sign %4d) ", a, a, signed_a,
        " b=%08b (uns %3d/sign %4d) ", b, b, signed_b,
        " _c_in=%1b ", _flag_c_in,
        "  ",
        "  ",
        " result=%08b (uns %3d/sign %4d) ", o, o, signed_o,
        " _c=%1b",  _flag_c,
        " _z=%1b",  _flag_z,
        " _n=%1b",  _flag_n,
        " _o=%1b",  _flag_o,
        " _gt=%1b", _flag_gt,
        " _lt=%1b", _flag_lt,
        " _eq=%1b", _flag_eq,
        " _ne=%1b", _flag_ne,
        "      ",
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
        //$display("_subOv l=%b r=%b o=%b", left, right, o);
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
        //$display("_addOv l=%b r=%b o=%b", left, right, o);
        if (left!=right) return 1; // signs diff 
        if (left & right & !o) return 0; // pos add neg eq neg
        if (!left & !right & o) return 0; // neg add pos eq pos
        return 1;
    endfunction

    int count;

    always @* begin

        c_buf_c = 1;

        _overflow = 1;
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
                set_result(0);
            end
            alu_ops.OP_A: begin // this is not the same as "A+0 immediate" because + takes carry into account and what we want is PASSA so maybe call it PASSA?
                set_result(a);
            end
            alu_ops.OP_B: begin // this is not the same as "B+0 immediate" because + takes carry into account and what we want is PASSB so maybe call it PASSB?
                set_result(b);
            end
            alu_ops.OP_NEGATE_A: begin  // eg switches -1 to 255 and 255 to -1
                set_result(-a); 
            end
            alu_ops.OP_NEGATE_B: begin 
                set_result(-b);
            end
            alu_ops.OP_A_PLUS_1: begin 
                // UNLIKE A_PLUS_B this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                // FIXME - not needed?  CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH AS LONG AS IMMED CAN BE ON BOTH BUSSES
                set_result9(a + 1);
                _overflow = _addOv(a[7], 1'b0, result_sign());
            end
            alu_ops.OP_B_PLUS_1: begin 
                // UNLIKE B_PLUS_A this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                // FIXME - not needed?  CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                set_result9(b + 1);
                _overflow = _addOv(b[7], 1'b0, result_sign());
            end
            alu_ops.OP_A_MINUS_1: begin 
                // UNLIKE A_MINUS_B this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                // FIXME - not needed?  CAN BE DONE USING "LOWER" A_-_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                set_result9(9'(a) - 1);
                _overflow = _subOv(a[7], 1'b0, result_sign());
            end

            ///// 8 ...
            alu_ops.OP_B_MINUS_1: begin 
                // UNLIKE B_MINUS_A this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                //FIXME - not needed? FIXME CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                set_result9(b - 1);
                _overflow = _subOv(b[7], 1'b0, result_sign());
            end

            // low bank is when CIN=0 or these ops were directly selected
            alu_ops.OP_A_PLUS_B: begin  
                set_result9(a + b);
                _overflow = _addOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_A_MINUS_B: begin 
                set_result9(a - b);
                _overflow = _subOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_B_MINUS_A: begin 
                set_result9(b - a);
                _overflow = _subOv(b[7], a[7], o[7]);
            end

            alu_ops.OP_A_MINUS_B_SIGNEDMAG: begin 
                set_result9(a - b);
                unsigned_magnitude=0;
                _overflow = _subOv(a[7], b[7], o[7]);
            end

            alu_ops.OP_A_PLUS_B_PLUS_C: begin  // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((a + b) + 1); 
                _overflow = _addOv(a[7], b[7], o[7]);
            end
            alu_ops.OP_A_MINUS_B_MINUS_C: begin // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((a - b) - 1); 
                _overflow = _subOv(a[7],b[7],o[7]);
            end
            alu_ops.OP_B_MINUS_A_MINUS_C: begin // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((b - a) - 1); 
                _overflow = _subOv(b[7],a[7],o[7]);
            end

            // 24 .............................................................
            alu_ops.OP_A_TIMES_B_HI: begin 
                TimesResult = (a * b);
                set_result(TimesResult[15:8]);
            end

            alu_ops.OP_A_TIMES_B_LO: begin 
                TimesResult = (a * b);
//                tmp[7:0] = TimesResult[7:0];
//                tmp[8] = (TimesResult[15:8] > 0); // set carry to indicate whether the upper byte has a value

                set_result9( {(TimesResult[15:8] > 0), TimesResult[7:0] });
            end

            alu_ops.OP_A_DIV_B: begin 
                set_result( a/ b );
                if (b == 0) begin
                    // div/0
                    // result will be 'x
                    _overflow=0; // force overflow
                    set_ctop(1); // force carry
                end
            end

            alu_ops.OP_A_MOD_B: begin 
                set_result( a % b );
                if (b == 0) begin
                    // div/0
                    // result will be 'x
                    _overflow=0; // force overflow
                    set_ctop(1); // force carry
                end
            end

            alu_ops.OP_A_LSL_B: begin 
                c_buf_c = {a, 1'b0};
                c_buf_c = c_buf_c << b;
                _overflow = !(a[7] != result_sign());
            end

            alu_ops.OP_A_LSR_B: begin
                c_buf_c = {a, 1'b0};
                c_buf_c = c_buf_c >> b;
                set_ctop(c_bot()); // move the carry-out bit to the return value position
                _overflow = !(a[7] != result_sign());
            end

            alu_ops.OP_A_ASR_B: begin
                c_buf_c = {a[7], a, 1'b0}; // extend the sign bit left
                c_buf_c = c_buf_c >>> b;
                set_ctop(c_bot()); // move the carry-out bit to the return value position
                _overflow = !(a[7] != result_sign());
            end

            alu_ops.OP_A_ROL_B: begin 
                c_buf_c = {a, !_flag_c_in};
                for (count = 0; count < b; count++) begin
                    c_buf_c = c_buf_c << 1;
                    set_cbot(c_top());
                end
                _overflow = !(a[7] != result_sign());
            end

            alu_ops.OP_A_ROR_B: begin 
                c_buf_c = {!_flag_c_in, a, 1'b0};
                for (count = 0; count < b; count++) begin
                    c_buf_c = c_buf_c >> 1;
                    set_ctop(c_bot()); // move the carry-out bit to the return value position
                end
                _overflow = !(a[7] != result_sign());
            end


            alu_ops.OP_A_OR_B: begin
                set_result(a | b);
                _overflow = !(a[7] != result_sign());
            end
            alu_ops.OP_A_AND_B: begin
                set_result(a & b);
                _overflow = !(a[7] != result_sign());
            end
            default: begin
                c_buf_c = 10'bxzxzxzxzxz;
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!! UNHANDLED alu_op=%5b : SpecifiedOp:%-s EffectiveOp=%-s", $time, alu_op, 
                        alu_ops.aluopName(alu_op),
                        alu_ops.aluopName(alu_op_effective)
                        );
            end

        endcase
    end

endmodule: alu

`endif
