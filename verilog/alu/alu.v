`ifndef  V_ALU
`define  V_ALU


`include "../74245/hct74245.v"
`timescale 1ns/100ps

// need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
// use a sip5 10k resistor pull down to get 0. 
// else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
//assign reg_x_addr = device_sel[3:0]; // top bit of device sel ignored
    

module alu #(parameter LOG=0) (
    output [7:0] o,
    output _flag_cout,
    output _flag_z,
    input  [7:0] x,
    input  [7:0] y,
    input  [4:0] alu_op,
    input  force_alu_op_to_passx,
    input  force_x_val_to_zero,
    input  _flag_cin,
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

    localparam OP_A_TIMES_B_HI=16;
    localparam OP_A_TIMES_B_LO=17;

    localparam OP_A_AND_B=25;
    localparam OP_A_OR_B=26;
    reg [8*8:0] OP_NAME;

    assign OP_OUT=OP_NAME;



    logic [7:0] ALU_Result;
    logic [15:0] TimesResult;
    assign o = ALU_Result;

    logic [8:0] tmp = 0;
    assign _flag_cout = ! tmp[8];
    assign _flag_z = o != 0;
//     always @(*) 
 //        $display("alu  : x=%-8b y=%-8b o=%-8b xin=%8b xout=%8b forcepassx=%1b aluopin=%b opeff=%d", x,y,o,xin,xout, force_alu_op_to_passx, aluopin, alu_op_effective);

    localparam AtoB=1'b1;

    //tri [7:0] xout;
    tri0 [7:0] xout;
    wire [7:0] xin = x;
    hct74245 #(.NAME("F_X_to_0")) bufX(.A(xin), .B(xout), .dir(AtoB), .nOE(force_x_val_to_zero)); 
    //pulldown pullXToZero[7:0](xout);

    //wire [7:0] alu_op_out;
    tri0 [7:0] alu_op_out;
    wire [7:0] alu_op_in = {3'b0, alu_op};
    wire [4:0] alu_op_effective;
    assign alu_op_effective = alu_op_out[4:0];

    hct74245 #(.NAME("F_OP_PASSX")) bufOp(.A(alu_op_in), .B(alu_op_out), .dir(AtoB), .nOE(force_alu_op_to_passx)); 
    //pulldown pullOpToZero[7:0](alu_op_out);
    
    wire [7:0] cin8 = {7'b0, !_flag_cin};

    if (LOG) always @(*) 
         $display("%9t ALU", $time,
         " aluop=(%d) %-s ", alu_op, OP_NAME, 
         " result=%08b(%3d) ", o, o,
         " x=%08b(%3d) ", xout, xout,
         " y=%08b(%3d) ", y, y,
         " xin=%08b(%3d) ", x, x,
         "  passx=%1b", force_alu_op_to_passx, 
         "  x_to_0=%1b", force_x_val_to_zero, 
         "   _cin=%1b ", _flag_cin,
         " _cout=%1b ", _flag_cout,
         );


    always @* begin
        case (alu_op_effective)
            OP_A: begin
                OP_NAME = "A";
                ALU_Result = xout;
                tmp=0;
            end
            OP_B: begin
                OP_NAME = "B";
                ALU_Result = y;
                tmp=0;
            end
            OP_0: begin
                OP_NAME = "0";
                ALU_Result = 0;
                tmp=0;
            end
            OP_MINUS_A: begin
                OP_NAME = "-A";
                ALU_Result = -xout;
                tmp = -{1'b0,xout};
            end
            OP_MINUS_B: begin
                OP_NAME = "-B";
                ALU_Result = -y;
                tmp = -{1'b0,y};
            end
            OP_A_PLUS_1: begin
                OP_NAME = "A+1";
                ALU_Result = xout+1;
                tmp = {1'b0,xout}+1;
            end
            OP_B_PLUS_1: begin
                OP_NAME = "B+1";
                ALU_Result = y+1;
                tmp = {1'b0,y}+1;
            end
            OP_A_MINUS_1: begin
                OP_NAME = "A-1";
                ALU_Result = xout-1;
                tmp = {1'b0,xout}-1;
            end
            OP_B_MINUS_1: begin
                OP_NAME = "B-1";
                ALU_Result = y-1;
                tmp = {1'b0,y}-1;
            end
            OP_A_OR_B: begin
                OP_NAME = "OR";
                ALU_Result = xout | y;
                tmp=0;
            end
            OP_A_AND_B: begin
                OP_NAME = "AND";
                ALU_Result = xout & y;
                tmp=0;
            end
            OP_A_PLUS_B: begin 
                OP_NAME = "PLUS";
                ALU_Result = xout + y + cin8;
                tmp = {1'b0,xout} + {1'b0,y} + cin8;
            end
            OP_A_MINUS_B: begin
                OP_NAME = "MINUS";
                ALU_Result = xout - y - cin8;
                tmp = {1'b0,xout} - {1'b0,y} - cin8;
            end

            OP_A_TIMES_B_HI: begin
                OP_NAME = "*HI";
                TimesResult = (xout * y);
                ALU_Result = TimesResult[15:8];
                tmp=0;
            end

            OP_A_TIMES_B_LO: begin
                OP_NAME = "*LO";
                TimesResult = (xout * y);
                ALU_Result = TimesResult[7:0];
                tmp=0;
            end

            default: begin
                //ALU_Result = 8'b11111111;
                ALU_Result = 8'bxzxzxzxz;
                $sformat(OP_NAME,"? %02x ?",alu_op_effective);
                $display("%9t !!!!!!!!!!!!!!!!!!!!!!!!!!!! RANDOM ALU OUT !!!!!!!!!!!!!!!!!!!!!!", $time);
            end


        endcase
    end

endmodule: alu

`endif
