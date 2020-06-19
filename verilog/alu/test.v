`include "./alu.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns


module test();

	logic [7:0] x;
	logic [7:0] y;
	logic [4:0] alu_op;
    logic _flag_c_in;
	wire  [7:0] o;

    wire _flag_c;
    wire _flag_n;
    wire _flag_z;
    wire _flag_o;
    wire _flag_gt;
    wire _flag_lt;
    wire _flag_eq;
    wire _flag_ne;
	
	alu #(.LOG(1)) Alu(
        .o, 
        .x,
        .y,
        .alu_op,
        ._flag_c_in,
        ._flag_c,
        ._flag_z,
        ._flag_n,
        ._flag_o,
        ._flag_gt,
        ._flag_lt,
        ._flag_eq,
        ._flag_ne
    );

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $display ("");
        
        $monitor ($time, "MON: x=%8b y=%8b  op=%6b  result=%8b   _flag_c_in=%b _flag_c=%b _flag_z=%b", 
            x,
            y,
            alu_op,
            o, 
            _flag_c_in,
            _flag_c,
            _flag_z,
            _flag_n,
            _flag_o,
            _flag_gt,
            _flag_lt,
            _flag_ne,
            _flag_eq
        );
        
        `endif
    end

    initial begin

        assign _flag_c_in=1;
        assign x = 8'b10101010; 
        assign y = 8'b10000001;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        #100
        `Equals(o, 8'b00101011);
        `Equals(_flag_c, 1'b0);
        `Equals(_flag_z, 1'b1);
        `Equals(_flag_o, 1'b1);
        `Equals(_flag_n, 1'b1);
        `Equals(_flag_gt, 1'b0); // unsigned magnitude
        `Equals(_flag_lt, 1'b1);
        `Equals(_flag_ne, 1'b0);
        `Equals(_flag_eq, 1'b1);


        assign _flag_c_in=1;
        assign x = 254;
        assign y = 3;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        #100
        `Equals(o, 8'b00000001);
        `Equals(_flag_c, 1'b0);

        assign x = 8'b11010101;
        assign y = 8'b10000000;
        assign alu_op = alu_ops.OP_A_AND_B;
        #100
        `Equals(o, 8'b10000000);
        `Equals(_flag_c, 1'b1);

        assign x = 8'b11010101;
        assign y = 8'b10000000;
        assign alu_op = alu_ops.OP_A;
        #100
        `Equals(o, 8'b11010101);
        `Equals(_flag_c, 1'b1);

        assign x = 1;
        assign y = 1;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        #100
        `Equals(o, 3);
        `Equals(_flag_c, 1'b1);

        $display("no carryin and 1-1=0 set Z flag");
        assign x = 1;
        assign y = 1;
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        #100
        `Equals(o, 0);
        `Equals(_flag_c, 1'b1);
        `Equals(_flag_z, 1'b0);

        $display("no carryin and 1-3=-2 in twos complement set N+C flag");
        assign x = 1;
        assign y = 3;
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        #100
        `Equals(o, 8'b11111110); // literal binary two complement of -2
        `Equals(o, -8'd2); // also can write a negative twos complement like this
        `Equals(_flag_c, 1'b0);
        `Equals(_flag_z, 1'b1);
        `Equals(_flag_n, 1'b0);
        `Equals(_flag_o, 1'b1);


        $display("carryin and 1-1=-1 twos comp (ie 255) and sets C flag");
        assign x = 1;
        assign y = 1;
        assign _flag_c_in = 0; // carry an extra -1 into the subtraction pushing it past 0
        assign alu_op = alu_ops.OP_A_MINUS_B;
        #100
        `Equals(o, 255);
        `Equals(_flag_c, 1'b0);
        `Equals(_flag_z, 1'b1);

        assign x = 1;
        assign y = 1;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        #100
        `Equals(o, 8'b11111111);
        `Equals(_flag_c, 0);

        // TIMES
        assign x = 8'h0F;
        assign y = 8'h0F;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'b00000000);
        `Equals(_flag_c, 1'b1);

        assign x = 8'hF0;
        assign y = 8'h10;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'h0F);
        `Equals(_flag_c, 1'b1);

        assign x = 8'hFF;
        assign y = 8'hFF;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'hFE);
        `Equals(_flag_c, 1'b1);

        assign x = 8'hFF;
        assign y = 8'hFF;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_LO;
        #100
        `Equals(o, 8'h01);
        `Equals(_flag_c, 1'b1);


        #100
        $display("---");
        $display("done");
        

    end
endmodule : test
