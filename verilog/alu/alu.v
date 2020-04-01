`ifndef  V_ALU
`define  V_ALU


`include "../74245/hct74245.v"
`timescale 1ns/100ps

// need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
// use a sip5 10k resistor pull down to get 0. 
// else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
//assign reg_x_addr = device_sel[3:0]; // top bit of device sel ignored
    

module alu(
    output [7:0] o,
    output _flag_cout,
    input  [7:0] x,
    input  [7:0] y,
    input  [4:0] alu_op,
    input  force_alu_op_to_passx,
    input  force_x_val_to_zero,
    input  _flag_cin
);
// A           | B-1               | A*B (high bits)   | A ROR B       |
// | B           | __A+B+Cin (0)__   | A*B (low bits)    | A AND B       |
// | 0           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B arithmetic | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B logical    | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

    
    localparam OP_A=0;
    localparam OP_B=1;
    localparam OP_0=2;
    localparam OP_MINUS_A=3;
    localparam OP_MINUS_B=4;
    localparam OP_A_PLUS_1=5;
    localparam OP_B_PLUS_1=6;
    localparam OP_A_MINUS_1=7;
    localparam OP_B_MINUS_1=8;
    localparam OP_A_PLUS_B=9;
    localparam OP_A_MINUS_B=10;
    localparam OP_B_MINUS_A=11;
    localparam OP_A_AND_B=25;
    localparam OP_A_OR_B=26;
    localparam OP_A_TIMES_B_HI=16;
    localparam OP_A_TIMES_B_LO=17;
    reg [8*12:0] sOp;


    wire [7:0] xout;

    wire [7:0] alu_op_out;
    wire [4:0] alu_op_effective;
    assign alu_op_effective = alu_op_out[4:0];

    logic [7:0] ALU_Result;
    logic [15:0] TimesResult;
    assign o = ALU_Result;

    logic [8:0] tmp = 0;
    assign _flag_cout = ! tmp[8];
    
    if (1) always @(*) 
         $display("%6d ALU", $time,
         " aluop=%05b(%-s) ", alu_op, sOp,
         " x=%08b(%3d) ", x, x,
         " y=%08b(%3d) ", y, y,
         " o=%08b(%3d) ", o, o,
         " force_passx=%1b", force_alu_op_to_passx, 
         " force_x_to_0=%1b", force_x_val_to_zero, 
         " _cin=%1b ", _flag_cin,
         " _cout=%1b ", _flag_cout,
         );

//     always @(*) 
 //        $display("alu  : x=%-8b y=%-8b o=%-8b xin=%8b xout=%8b forcepassx=%1b aluopin=%b opeff=%d", x,y,o,xin,xout, force_alu_op_to_passx, aluopin, alu_op_effective);

    localparam AtoB=1'b1;

    wire [7:0] xin = x;
    hct74245 #(.NAME("F_X_to_0")) bufX(.A(xin), .B(xout), .dir(AtoB), .nOE(force_x_val_to_zero)); 
    pulldown pullXToZero[7:0](xout);

    wire [7:0] alu_op_in = {3'b0, alu_op};
    hct74245 #(.NAME("F_OP_PASSX")) bufOp(.A(alu_op_in), .B(alu_op_out), .dir(AtoB), .nOE(force_alu_op_to_passx)); 
    pulldown pullOpToZero[7:0](alu_op_out);

    
    wire [7:0] cin8 = {7'b0, !_flag_cin};
    
    always @* begin
        case (alu_op_effective)
            OP_A: begin
                sOp = "A";
                ALU_Result = xout;
                tmp=0;
            end
            OP_A_OR_B: begin
                sOp = "A_OR_B";
                ALU_Result = xout | y;
                tmp=0;
            end
            OP_A_AND_B: begin
                sOp = "A_AND_B";
                ALU_Result = xout & y;
                tmp=0;
            end
            OP_A_PLUS_B: begin 
                sOp = "A_PLUS_B";
                ALU_Result = xout + y + cin8;
                tmp = {1'b0,xout} + {1'b0,y} + cin8;
            end
            OP_A_MINUS_B: begin
                sOp = "A_MINUS_B";
                ALU_Result = xout - y - cin8;
                tmp = {1'b0,xout} - {1'b0,y} - cin8;
            end

            OP_A_TIMES_B_HI: begin
                sOp = "A_TIMES_B_HI";
                TimesResult = (xout * y);
                ALU_Result = TimesResult[15:8];
                tmp=0;
            end

            OP_A_TIMES_B_LO: begin
                sOp = "A_TIMES_B_LO";
                TimesResult = (xout * y);
                ALU_Result = TimesResult[7:0];
                tmp=0;
            end

            default: ALU_Result = 8'bxxxxxxxx;
        endcase
    end

endmodule: alu

`endif
