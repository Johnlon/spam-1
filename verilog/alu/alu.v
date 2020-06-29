/// FIXME NEED TO USE CARRY IN CONSISTENTLY ON ARITH AND ROTATES (SHIFTS??)

`ifndef  V_ALU
`define  V_ALU


`include "../74245/hct74245.v"
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

module alu #(parameter LOG=0) (
    output [7:0] o,
    output _flag_c,
    output _flag_z,
    output _flag_n,
    output _flag_o,
    output _flag_gt,
    output _flag_lt,
    output _flag_eq,
    output _flag_ne,
    input  [7:0] x,
    input  [7:0] y,
    input  [4:0] alu_op,
    input  _flag_c_in,
    output [8*8:0] OP_OUT
);
// A           | B-1               | A*B (high bits)   | A ROR B       |
// | B           | __A+B+Cin (0)__   | A*B (low bits)    | A AND B       |
// | 0           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B arithmetic | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B logical    | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

    
    reg [8*8:0] OP_NAME;

    assign OP_OUT=OP_NAME;



    logic [7:0] ALU_Result;
    logic [15:0] TimesResult;
    assign o = ALU_Result;

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
    logic [8:0] tmp = 0;
    assign _flag_c = ! tmp[8];
    assign _flag_n = !ALU_Result[7]; // top bit set indicates negative in signed arith
    assign _flag_z = !(o == 8'b0);
    assign _flag_o = (x[7] == y[7]) & (x[7] != o[7]); // fixme
    assign _flag_eq = !(x == y);    
    assign _flag_ne = !(x != y);    
    // unsigned magnitude comparison of the input values.
    // if the bytes are eg two complement signed then this will produce incorrect results.
    // if this is the case then use a subtract operation instead
    assign _flag_gt = !(x > y);
    assign _flag_lt = !(x < y);

    localparam AtoB=1'b1;

    wire [7:0] cin8 = {7'b0, !_flag_c_in};

    if (LOG) always @(*) 
         $display("%9t ALU", $time,
         " aluop=(%1d) '%1s' ", alu_op, OP_NAME, // %1s causes string to lose trailing space
         " result=%08b(%3d) ", o, o,
         " x=%08b(%3d) ", x, x,
         " y=%08b(%3d) ", y, y,
         " _cin=%1b ", _flag_c_in,
         " _cout=%1b ", _flag_c,
         );


    always @* begin
        case (alu_op)
            alu_ops.OP_A: begin
                OP_NAME = "A";
                ALU_Result = x;
                tmp=0;
            end
            alu_ops.OP_B: begin
                OP_NAME = "B";
                ALU_Result = y;
                tmp=0;
            end
            alu_ops.OP_0: begin
                OP_NAME = "0";
                ALU_Result = 0;
                tmp=0;
            end
            alu_ops.OP_MINUS_A: begin
                OP_NAME = "-A";
                ALU_Result = -x;
                tmp = -{1'b0,x};
            end
            alu_ops.OP_MINUS_B: begin
                OP_NAME = "-B";
                ALU_Result = -y;
                tmp = -{1'b0,y};
            end
            alu_ops.OP_A_PLUS_1: begin
                OP_NAME = "A+1";
                ALU_Result = x+1;
                tmp = {1'b0,x}+1;
            end
            alu_ops.OP_B_PLUS_1: begin
                OP_NAME = "B+1";
                ALU_Result = y+1;
                tmp = {1'b0,y}+1;
            end
            alu_ops.OP_A_MINUS_1: begin
                OP_NAME = "A-1";
                ALU_Result = x-1;
                tmp = {1'b0,x}-1;
            end
            alu_ops.OP_B_MINUS_1: begin
                OP_NAME = "B-1";
                ALU_Result = y-1;
                tmp = {1'b0,y}-1;
            end
            alu_ops.OP_A_OR_B: begin
                OP_NAME = "OR";
                ALU_Result = x | y;
                tmp=0;
            end
            alu_ops.OP_A_AND_B: begin
                OP_NAME = "AND";
                ALU_Result = x & y;
                tmp=0;
            end
            alu_ops.OP_A_PLUS_B: begin 
                OP_NAME = "PLUS";
                ALU_Result = x + y + cin8;
                tmp = {1'b0,x} + {1'b0,y} + cin8;
            end
            alu_ops.OP_A_MINUS_B: begin
                OP_NAME = "MINUS";
                ALU_Result = x - y - cin8;
                tmp = {1'b0,x} - {1'b0,y} - cin8;
            end

            alu_ops.OP_A_TIMES_B_HI: begin
                OP_NAME = "*HI";
                TimesResult = (x * y);
                ALU_Result = TimesResult[15:8];
                tmp=0;
            end

            alu_ops.OP_A_TIMES_B_LO: begin
                OP_NAME = "*LO";
                TimesResult = (x * y);
                ALU_Result = TimesResult[7:0];
                tmp=0;
            end

            default: begin
                //ALU_Result = 8'b11111111;
                ALU_Result = 8'bxzxzxzxz;
                $sformat(OP_NAME,"? %02x ?",alu_op);
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!!", $time);
            end


        endcase
    end

endmodule: alu

`endif
