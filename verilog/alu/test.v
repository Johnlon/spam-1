`include "./alu.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns


module test();

	wire  [7:0] o;
	logic [7:0] x;
	logic [7:0] y;
	logic [4:0] alu_op;
        logic force_alu_op_to_passx;
        logic force_x_val_to_zero;
        logic _flag_cin;
        logic _flag_cout;
        logic _flag_n;
        logic _flag_z;
	
	alu #(.LOG(1)) Alu(
        .o, 
        .x,
        .y,
        .alu_op,
        .force_alu_op_to_passx,
        .force_x_val_to_zero,
        ._flag_cin,
        ._flag_cout,
        ._flag_z
    );

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
            o, 
            x,
            y,
            alu_op,
            force_alu_op_to_passx,
            force_x_val_to_zero
        );

        $display ("");
        $display ($time, "  %8s %8s  %6s  %8s   %5s %5s  %3s %4s %4s", 
            "",
            "",
            "",
            "",
            "force",
            "force",
            "flag",
            "flag",
            "flag"
         );
        $display ($time, "  %8s %8s  %6s  %8s   %5s %5s  %3s %4s %4s", 
            "x",
            "y",
            "alu_op",
            "result",
            "passx",
            "xzero",
            "cin",
            "cout",
            "z"
         );
        
        $monitor ($time, "  %8b %8b  %6b  %8b   %5b %5b  %3b %4b %4b", 
            x,
            y,
            alu_op,
            o, 
            force_alu_op_to_passx,
            force_x_val_to_zero,
            _flag_cin,
            _flag_cout,
            _flag_z
        );
        
        `endif
    end

    initial begin
        // localparam OP_PASSX=0;
        // localparam OP_OR=1;
        // localparam OP_AND=2;
        // localparam OP_PLUS=3;
        // localparam OP_MINUS=4;

        assign force_alu_op_to_passx = 1'b0;
        assign force_x_val_to_zero = 1'b0;

        //
        assign _flag_cin=1;
        assign x = 8'b10101010;
        assign y = 8'b10000001;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 8'b00101011);
        `Equals(_flag_cout, 0);


        assign _flag_cin=1;
        assign x = 254;
        assign y = 3;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 8'b00000001);
        `Equals(_flag_cout, 0);

        assign x = 8'b11010101;
        assign y = 8'b10000000;
        assign alu_op = Alu.OP_A_AND_B;
        #100
        `Equals(o, 8'b10000000);
        `Equals(_flag_cout, 1);

        assign x = 8'b11010101;
        assign y = 8'b10000000;
        assign alu_op = Alu.OP_A;
        #100
        `Equals(o, 8'b11010101);
        `Equals(_flag_cout, 1);

        assign force_alu_op_to_passx = 1'b0;
        assign force_x_val_to_zero = 1'b0;
        #100
        assign x = 1;
        assign y = 1;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 2);
        `Equals(_flag_cout, 1);
        
        assign x = 1;
        assign y = 1;
        assign _flag_cin = 0;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 3);
        `Equals(_flag_cout, 1);

        $display("no carryin and 1-1=0 and Z flag");
        assign x = 1;
        assign y = 1;
        assign _flag_cin = 1;
        assign alu_op = Alu.OP_A_MINUS_B;
        #100
        `Equals(o, 0);
        `Equals(_flag_cout, 1);
        `Equals(_flag_z, 0);

        $display("carryin and 1-1=255 sets C flag");
        assign x = 1;
        assign y = 1;
        assign _flag_cin = 0; // carry an extra -1 into the subtraction pushing it past 0
        assign alu_op = Alu.OP_A_MINUS_B;
        #100
        `Equals(o, 255);
        `Equals(_flag_cout, 1'b0);
        `Equals(_flag_z, 1'b1);

        assign x = 1;
        assign y = 1;
        assign _flag_cin = 0;
        assign alu_op = Alu.OP_A_MINUS_B;
        #100
        `Equals(o, 8'b11111111);
        `Equals(_flag_cout, 0);

        // TIMES
        assign x = 8'h0F;
        assign y = 8'h0F;
        assign _flag_cin = 1'bx;
        assign alu_op = Alu.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'b00000000);
        `Equals(_flag_cout, 1'b1);

        assign x = 8'hF0;
        assign y = 8'h10;
        assign _flag_cin = 1'bx;
        assign alu_op = Alu.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'h0F);
        `Equals(_flag_cout, 1'b1);

        assign x = 8'hFF;
        assign y = 8'hFF;
        assign _flag_cin = 1'bx;
        assign alu_op = Alu.OP_A_TIMES_B_HI;
        #100
        `Equals(o, 8'hFE);
        `Equals(_flag_cout, 1'b1);

        assign x = 8'hFF;
        assign y = 8'hFF;
        assign _flag_cin = 1'bx;
        assign alu_op = Alu.OP_A_TIMES_B_LO;
        #100
        `Equals(o, 8'h01);
        `Equals(_flag_cout, 1'b1);



        #100



        $display("---");
        

    end
endmodule : test
