/* verilator lint_off ASSIGNDLY */

// EG USE M27C322 21bit address x16 data

// TODO TODO TODO - generate rom images for Warren

/// FIXME NEED A BINARY TO BCD OPERATION AS THIS IS HARD - https://www.nandland.com/vhdl/modules/double-dabble.html
//  replace A-1 and A+1 with 

// FIXME - include those ALU Div with Borrow Remainder functions?

/// EG USING ROM 28C512


/// MANY OPS NOT REQUIRED IF R CAN BE IMMEDIATE eg "A+1" is same as "A+immediate 1" as long as both treat carry the same
/// HMMMM .. But can't do "B+immediate 1" unless instreg is available on A bus too.

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
// | 0           | B-1               | A*B (low bits)    | A ROR B       |
// | A           | __A+B+Cin (0)__   | A*B (high bits)   | A AND B       |
// | B           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B signedmag     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B logical    | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B arithmetic | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |


`include "../74138/hct74138.v"


// See also http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt#:~:text=The%20ALU%20doesn't%20know,after%20the%20math%20is%20done.

`ifndef  V_ALU
`define  V_ALU

`timescale 1ns/1ns


`define toALUOP(OPNAME) alu_ops::OP_``OPNAME``

package alu_ops;

    localparam [4:0] OP_0=0; // Needed for RAM=0
    localparam [4:0] OP_A=1; 
    localparam [4:0] OP_B=2;
    localparam [4:0] OP_NEGATE_A=3;  
    localparam [4:0] OP_NEGATE_B=4;  
    localparam [4:0] OP_BCD_DIV=5; // Divide binary value A by 10 using B as a carry in remainder (=A+(B*256)/10), if B>9 then remainder was illegal and result is 0 and overflow is set 
    localparam [4:0] OP_BCD_MOD=6; // Mode binary value A by 10 using B as a carry in remainder (=A+(B*256)%10), if B>9 then remainder was illegal and result is 0 and overflow is set

    localparam [4:0] OP_B_PLUS_1=7; // needed for X=RAM+1  & doesn't carry in ---- CONSIDER RAM ON BUS A!!!!
    localparam [4:0] OP_B_MINUS_1=8; // needed for X=RAM-1 (ROM-1 isn't convincing) , no carry in ---- CONSIDER RAM ON BUS A!!!!
    localparam [4:0] OP_A_PLUS_B=9;
    localparam [4:0] OP_A_MINUS_B=10;
    localparam [4:0] OP_B_MINUS_A=11;
    localparam [4:0] OP_A_MINUS_B_SIGNEDMAG=12;
    localparam [4:0] OP_A_PLUS_B_PLUS_C=13;
    localparam [4:0] OP_A_MINUS_B_MINUS_C=14;
    localparam [4:0] OP_B_MINUS_A_MINUS_C=15;

    localparam [4:0] OP_A_TIMES_B_LO=16;
    localparam [4:0] OP_A_TIMES_B_HI=17;
    localparam [4:0] OP_A_DIV_B=18; // fix? doesn't use carry remainer in 
    localparam [4:0] OP_A_MOD_B=19; // fix? doesn't use carry remainer in
    localparam [4:0] OP_A_LSL_B=20;
    localparam [4:0] OP_A_LSR_B=21; // logical shift right - simple bit wise
    localparam [4:0] OP_A_ASR_B=22; // arith shift right - preserves top bit and fills with top bit as shift right   ie same as "CMP #80/ROR A" on 6502
    localparam [4:0] OP_A_ROL_B=23; // https://www.masswerk.at/6502/6502_instruction_set.html#ROL

    localparam [4:0] OP_A_ROR_B=24; // https://www.masswerk.at/6502/6502_instruction_set.html#ROR
    localparam [4:0] OP_A_AND_B=25;
    localparam [4:0] OP_A_OR_B=26;  
    localparam [4:0] OP_A_XOR_B=27; // NB XOR can can also synthesise NOT A by setting B to 0xff 
    localparam [4:0] OP_A_NAND_B=28;  
    localparam [4:0] OP_NOT_B=29;  // if NOT_A is need then use A XOR 0xff
    localparam [4:0] OP_A_PLUS_B_BCD=30;
    localparam [4:0] OP_A_MINUS_B_BCD=31;


    // returning a bitset is needed when using strobe to print
    typedef reg[13*8:1] OpName;
    function OpName aluopNameR; input [4:0] opcode;
        OpName ret;
        begin
            case(opcode)
                 0 : aluopNameR =    "0";
                 1 : aluopNameR =    "A";
                 2 : aluopNameR =    "B";
                 3 : aluopNameR =    "-A";
                 4 : aluopNameR =    "-B";
                 5 : aluopNameR =    "BCD DIV";
                 6 : aluopNameR =    "BCD MOD";
                 7 : aluopNameR =    "B+1";

                 8 : aluopNameR =    "B-1";
                 9 : aluopNameR =    "A+B";   // CarryIn not considered
                10 : aluopNameR =    "A-B";   // CarryIn not considered
                11 : aluopNameR =    "B-A";   // CarryIn not considered
                12 : aluopNameR =    "A-B signedmag"; // CarryIn not considered
                13 : aluopNameR =    "A+B+C"; // If CarryIn=N then this op is automatically updated to A+B
                14 : aluopNameR =    "A-B-C"; // If CarryIn=N then this op is automatically updated to A-B
                15 : aluopNameR =    "B-A-C"; // If CarryIn=N then this op is automatically updated to B-A

                16 : aluopNameR =    "A*B LO";
                17 : aluopNameR =    "A*B HI";
                18 : aluopNameR =    "A/B";
                19 : aluopNameR =    "A%B";
                20 : aluopNameR =    "A LSL B";
                21 : aluopNameR =    "A LSR B";
                22 : aluopNameR =    "A ASR B" ;
                23 : aluopNameR =    "A ROL B";

                24 : aluopNameR =    "A ROR B";
                25 : aluopNameR =    "A AND B";
                26 : aluopNameR =    "A OR B";
                27 : aluopNameR =    "A XOR B"; 
                28 : aluopNameR =    "A NAND B";
                29 : aluopNameR =    "NOT B";
                30 : aluopNameR =    "A+B BCD";
                31 : aluopNameR =    "A-B BCD";
                default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    aluopNameR = ret;
                end
            endcase
        end
    endfunction
    
    function string aluopName; input [4:0] opcode;
        string ret;
        begin
            $sformat(ret,"%-s",aluopNameR(opcode));
            aluopName = ret;
        end
    endfunction

endpackage



/* 
    If Inputs to arithmentic are two's complement then value will be 7 bits plus sign and Overflow is relevant, but ignore Carry

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
    import alu_ops::*;

    //////////////////////////////////////////////////////
    // CARRY-IN LOGIC - EXTERNAL LOGIC
    //////////////////////////////////////////////////////

    wire #(8) flag_c_in = !_flag_c_in; // inverter

    wire D,E,C,B,A;
    assign E=alu_op[4];
    assign D=alu_op[3];
    assign C=alu_op[2];
    assign B=alu_op[1];
    assign A=alu_op[0];

    wire [7:0] _decoded;
    hct74138 decoder
    (
      .Enable1_bar(E),
      .Enable2_bar(1'b0),
      .Enable3(D),
      .A({C,B,A}),
      .Y(_decoded)
    );

    wire #(8) _use_cin = _decoded[7] & _decoded[6] & _decoded[5]; // AND GATE - BUT USE TRIPLE 3 INPUT NAND AS WE NEED A NOT ON THE _flag_c_in ABOVE

    wire effective_bit3 = _use_cin ? alu_op[2]: flag_c_in; // multiplexer
        
    wire [4:0] alu_op_effective = {alu_op[4:3], effective_bit3, alu_op[1:0]};

    logic signed [9:0] c_buf_c;

    //////////////////////////////////////////////////////
    // ROM PROGRAMMING
    // ROM PROGRAMMING
    // ROM PROGRAMMING
    //////////////////////////////////////////////////////

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

    logic _overflow;
    logic _force_neg;

    assign #(PD) o = c_buf_c[8:1];
    assign #(PD) _flag_c = !c_buf_c[9];
    assign #(PD) _flag_n = _force_neg & (!c_buf_c[8]); // top bit set indicates negative in signed arith
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

    wire [7:0] cin8 = {7'b0, flag_c_in};

    logic [15:0] TimesResult;

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

    //////////////////////////////////////////////////////
    // ROM PROGRAMMING
    //////////////////////////////////////////////////////

    always @* begin

        c_buf_c = 1'bx; // use x to ensure this isn't relied upon unless expicitely set
        _overflow = 1'bx; // use x to ensure this isn't relied upon unless expicitely set
        _force_neg = 1'bx; // use x to ensure this isn't relied upon unless expicitely set
        unsigned_magnitude=1; // select whether a given op will use signed or unsigned arithmetic

        case (alu_op_effective)
            OP_0: begin // not needed anymore cos immed allows 0 value into ALU
                set_result(0);
            end
            OP_A: begin // this is not the same as "A+0 immediate" because + takes carry into account and what we want is PASSA so maybe call it PASSA?
                set_result(a);
            end
            OP_B: begin // this is not the same as "B+0 immediate" because + takes carry into account and what we want is PASSB so maybe call it PASSB?
                set_result(b);
            end
            OP_NEGATE_A: begin  // eg switches -1 to 255 and 255 to -1
                set_result(-a); 
                _overflow = !(a==8'b10000000); // indicates the argument cannot be converted
            end
            OP_NEGATE_B: begin 
                set_result(-b);
                _overflow = !(b==8'b10000000); // indicates the argument cannot be converted
            end
            OP_BCD_DIV: begin 
                TimesResult = b<10 ? (a + (b*256))/10: 0;

                set_result9(8'(TimesResult));

                _overflow = b < 10;
            end
            OP_B_PLUS_1: begin 
                // UNLIKE B_PLUS_A this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                // FIXME - not needed?  CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                set_result9(b + 1);
                _overflow = _addOv(b[7], 1'b0, result_sign());
            end
            OP_BCD_MOD: begin 
                TimesResult = b<10 ? (a + (b*256))%10: 0;

                set_result9(8'(TimesResult));

                _overflow = b < 10;
            end

            ///// 8 ...
            OP_B_MINUS_1: begin 
                // UNLIKE B_MINUS_A this sets carry but doesn't consume it 
                // - useful for low byte of a counter where we always want CLC first  
                //FIXME - not needed? FIXME CAN BE DONE USING "LOWER" A_+_B OP IN MULTIPLEXED "ALU[4]|CIN" APPROACH
                set_result9(b - 1);
                _overflow = _subOv(b[7], 1'b0, result_sign());
            end

            // low bank is when CIN=0 or these ops were directly selected
            OP_A_PLUS_B: begin  
                set_result9(a + b);
                _overflow = _addOv(a[7], b[7], o[7]);
            end
            OP_A_MINUS_B: begin 
                set_result9(a - b);
                _overflow = _subOv(a[7], b[7], o[7]);
            end
            OP_B_MINUS_A: begin 
                set_result9(b - a);
                _overflow = _subOv(b[7], a[7], o[7]);
            end

            OP_A_MINUS_B_SIGNEDMAG: begin 
                set_result9(a - b);
                unsigned_magnitude=0;
                _overflow = _subOv(a[7], b[7], o[7]);
            end

            OP_A_PLUS_B_PLUS_C: begin  // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((a + b) + 1); 
                _overflow = _addOv(a[7], b[7], o[7]);
            end
            OP_A_MINUS_B_MINUS_C: begin // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((a - b) - 1); 
                _overflow = _subOv(a[7],b[7],o[7]);
            end
            OP_B_MINUS_A_MINUS_C: begin // OP ONLY USED WHEN CARRY IS ACTIVE
                set_result9((b - a) - 1); 
                _overflow = _subOv(b[7],a[7],o[7]);
            end

            // 24 .............................................................
            OP_A_TIMES_B_HI: begin 
                TimesResult = (a * b);
                set_result(TimesResult[15:8]);
            end

            OP_A_TIMES_B_LO: begin 
                TimesResult = (a * b);
                set_result9( {(TimesResult[15:8] > 0), TimesResult[7:0] });
            end

            OP_A_DIV_B: begin 
                set_result( a/ b );
                if (b == 0) begin
                    // div/0
                    // result will be 'x
                    _overflow=0; // force overflow - when div/0
                    set_ctop(1); // force carry
                end
            end

            OP_A_MOD_B: begin 
                set_result( a % b );
                if (b == 0) begin
                    // div/0
                    // result will be 'x
                    _overflow=0; // force overflow - when div/0
                    set_ctop(1); // force carry
                end
            end

            OP_A_LSL_B: begin  // C <- A <- 0
                c_buf_c = {a, 1'b0};
                c_buf_c = c_buf_c << b;
                _overflow = !(a[7] != result_sign()); // sign bit change - not must use but hey ho
            end

            OP_A_LSR_B: begin // 0 -> A -> C
                c_buf_c = {a, 1'b0};
                c_buf_c = c_buf_c >> b;
                set_ctop(c_bot()); // move the carry-out bit to the return value position
                _overflow = !(a[7] != result_sign()); // sign bit change - not must use but hey ho
            end

            OP_A_ASR_B: begin // Sxxxxxxx -> SSxxxxxx  C=last carry out right
                c_buf_c = {a[7], a, 1'b0}; // extend the sign bit left
                c_buf_c = c_buf_c >>> b;
                set_ctop(c_bot()); // move the carry-out bit to the return value position
                _overflow = !(a[7] != result_sign()); // sign bit change can't happen unless this code is flawed
            end

            OP_A_ROL_B: begin // C <- A <- C
                c_buf_c = {a, flag_c_in};
                for (count = 0; count < b; count++) begin
                    c_buf_c = c_buf_c << 1;
                    set_cbot(c_top());
                end
                _overflow = !(a[7] != result_sign()); // sign bit change - not much use but hey ho
            end

            OP_A_ROR_B: begin // C -> A -> C
                c_buf_c = {flag_c_in, a, 1'b0};
                for (count = 0; count < b; count++) begin
                    c_buf_c = c_buf_c >> 1;
                    set_ctop(c_bot()); // move the carry-out bit to the return value position
                end
                _overflow = !(a[7] != result_sign()); // mnot much use
            end


            OP_A_OR_B: begin
                set_result(a | b);
            end
            OP_A_AND_B: begin
                set_result(a & b);
            end
            OP_A_XOR_B: begin
                set_result(a ^ b);
            end
            OP_A_NAND_B: begin
                set_result(~(a & b));
            end
            OP_NOT_B: begin
                set_result(~b);
            end
    

            OP_A_PLUS_B_BCD: begin

                `define P_A (((a >>4)*10) + (a & 8'h0f))
                `define P_B (((b >>4)*10) + (b & 8'h0f))
                `define P_SUM (`P_A + `P_B + cin8)

                `define P_CARRY 8'(`P_SUM/100)
                `define P_REMAIN 8'(`P_SUM % 100)

                `define P_TOP 8'(`P_REMAIN/10)
                `define P_BOT 8'(`P_REMAIN%10)

                set_result9({`P_CARRY, ((`P_TOP<<4) | `P_BOT) });
            end

            OP_A_MINUS_B_BCD: begin
                _force_neg = !(b>a);

                `define S_A 8'(((a >>4)*10) + (a & 8'h0f))
                `define S_B 8'(((b >>4)*10) + (b & 8'h0f))
                `define S_SUM 8'((`S_A + (100-`S_B)) + (100-cin8))

                `define S_CARRY 8'(`S_SUM/100)
                `define S_REMAIN 8'(`S_SUM % 100)

                `define S_TOP 8'(`S_REMAIN/10)
                `define S_BOT 8'(`S_REMAIN%10)

                set_result9({`S_CARRY, ((`S_TOP<<4) | `S_BOT) });
            end

            default: begin
                c_buf_c = 10'bxzxzxzxzxz;
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!! UNHANDLED alu_op=%5b : SpecifiedOp:%-s EffectiveOp=%-s", $time, alu_op, 
                        aluopName(alu_op),
                        aluopName(alu_op_effective)
                        );
            end

        endcase
    end


    if (LOG) 
    always @(*) 
        $display("%9t ALU", $time,
        " aluop=%-1s (op:%d)", aluopName(alu_op), alu_op, // %1s causes string to lose trailing space
        " (%-1s)", aluopName(alu_op_effective), // %1s causes string to lose trailing space
        "  ",
        " a=%08b (u%-3d/s%-4d/h%-02h) ", a, a, signed_a, a,
        " b=%08b (u%-3d/s%-4d/h%-02h) ", b, b, signed_b, b,
        " _c_in=%1b ", _flag_c_in,
        "  ",
        " out=%08b (u%-3d/s%-4d/h%-02h) ", o, o, signed_o, o,
        //" _c=%1b",  _flag_c,
        //" _z=%1b",  _flag_z,
        //" _n=%1b",  _flag_n,
        //" _o=%1b",  _flag_o,
        //" _gt=%1b", _flag_gt,
        //" _lt=%1b", _flag_lt,
        //" _eq=%1b", _flag_eq,
        //" _ne=%1b", _flag_ne,
        " _c%1b",  _flag_c,
        " _z%1b",  _flag_z,
        " _n%1b",  _flag_n,
        " _o%1b",  _flag_o,
        " _gt%1b", _flag_gt,
        " _lt%1b", _flag_lt,
        " _eq%1b", _flag_eq,
        " _ne%1b", _flag_ne,
        " ",
        " unsigned_magnitude=%b ", unsigned_magnitude
         );

endmodule: alu

`endif
