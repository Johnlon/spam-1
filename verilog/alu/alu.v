`include "../74245/hct74245.v"
`timescale 1ns/100ps

// need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
// use a sip5 10k resistor pull down to get 0. 
// else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
//assign reg_x_addr = device_sel[3:0]; // top bit of device sel ignored
    

module alu(
    output [7:0] o,
    output flag_cout,
    input  [7:0] x,
    input  [7:0] y,
    input  [4:0] alu_op,
    input  force_alu_op_to_passx,
    input  force_x_val_to_zero,
    input  flag_cin
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


    wire [7:0] xout;

    wire [7:0] alu_op_out;
    wire [4:0] alu_op_effective;
    assign alu_op_effective = alu_op_out[4:0];

    logic [7:0] ALU_Result;
    assign o =ALU_Result;

    logic [8:0] tmp;
    assign flag_cout = tmp[8];
    
    // always @(*) 
    //     $display("alu force_alu_op_to_passx=%1b", force_alu_op_to_passx, 
    //     " force_x_val_to_zero=%1b", force_x_val_to_zero, 
    //     " op=%-5b ", alu_op,
    //     // " xin=%-8b ", xin,
    //     " x=%-8b ", x,
    //     " y=%-8b ", y,
    //     " o=%-8b ", o
    //     );


    // always @(*) 
    //     $display("alu  : x=%-8b y=%-8b o=%-8b xin=%8b xout=%8b forcepassx=%1b aluopin=%b opeff=%d", x,y,o,xin,xout, force_alu_op_to_passx, aluopin, alu_op_effective);
    localparam AtoB=1'b1;

    wire [7:0] xin = x;
    hct74245 bufX(.A(xin), .B(xout), .dir(AtoB), .nOE(force_x_val_to_zero)); 
    pulldown pullXToZero[7:0](xout);

    wire [7:0] alu_op_in = {3'b0, alu_op};
    hct74245 bufOp(.A(alu_op_in), .B(alu_op_out), .dir(AtoB), .nOE(force_alu_op_to_passx)); 
    pulldown pullOpToZero[7:0](alu_op_out);

    
    //https://www.fpga4student.com/2017/06/Verilog-code-for-ALU.html
    wire [7:0] cin8 = {7'b0, flag_cin};
    
    always @* begin
        case (alu_op_effective)
            OP_A: 
                ALU_Result = xout;
            OP_A_OR_B: 
                ALU_Result = xout | y;
            OP_A_AND_B: 
                ALU_Result = xout & y;
            OP_A_PLUS_B: begin 
                ALU_Result = xout + y + cin8;
                tmp = {1'b0,xout} + {1'b0,y} + cin8;
            end
            OP_A_MINUS_B: begin
                ALU_Result = xout - y - cin8;
                tmp = {1'b0,xout} - {1'b0,y} - cin8;
            end
            default: ALU_Result = 8'bxxxxxxxx;
        endcase
    end

endmodule: alu