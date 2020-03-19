`include "./alu.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps
`default_nettype none


module test();

	wire  [7:0] o;
	logic [7:0] x;
	logic [7:0] y;
	logic [4:0] alu_op;
    logic force_alu_op_to_passx;
    logic force_x_val_to_zero;
    logic flag_cin;
    logic flag_cout;
    logic flag_n;
    logic flag_z;
	
	alu Alu(
        .o, 
        .x,
        .y,
        .alu_op,
        .force_alu_op_to_passx,
        .force_x_val_to_zero,
        .flag_cin,
        .flag_cout

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
        $display ($time, "  %8s %8s  %6s  %8s   %5s %5s  %3s %4s", 
            "",
            "",
            "",
            "",
            "force",
            "force",
            "cin",
            "cout"
         );
        $display ($time, "  %8s %8s  %6s  %8s   %5s %5s  %3s %4s", 
            "x",
            "y",
            "alu_op",
            "result",
            "passx",
            "xzero",
            "cin",
            "cout"
         );
        
        $monitor ($time, "  %8b %8b  %6b  %8b   %5b %5b  %3b %4b", 
            x,
            y,
            alu_op,
            o, 
            force_alu_op_to_passx,
            force_x_val_to_zero,
            flag_cin,
            flag_cout
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
        assign flag_cin=0;
        
        assign x = 8'b10101010;
        assign y = 8'b10000001;
        assign alu_op = Alu.OP_A_PLUS_B;
        
        #100
        `Equals(o, 8'b00101011);
        assign alu_op = Alu.OP_A_AND_B;

        #100
        `Equals(o, 8'b10000000);
        assign alu_op = Alu.OP_A;

        #100
        `Equals(o, 8'b10101010);

        assign force_alu_op_to_passx = 1'b0;
        assign force_x_val_to_zero = 1'b0;
        
        #100
        assign x = 1;
        assign y = 1;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 2);
        
        assign x = 1;
        assign y = 1;
        assign flag_cin = 1;
        assign alu_op = Alu.OP_A_PLUS_B;
        #100
        `Equals(o, 3);

        assign x = 1;
        assign y = 1;
        assign flag_cin = 0;
        assign alu_op = Alu.OP_A_MINUS_B;
        #100
        `Equals(o, 0);

        assign x = 1;
        assign y = 1;
        assign flag_cin = 1;
        assign alu_op = Alu.OP_A_MINUS_B;
        #100
        `Equals(o, 8'b11111111);

        #100


        $display("---");
        

    end
endmodule : test
