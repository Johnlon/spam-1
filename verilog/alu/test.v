`include "./alu.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns


module test();

	logic [7:0] a;
	logic [7:0] b;
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
        .a,
        .b,
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

/*
        $display ("");
        
        $monitor ("%9t", $time, " MON: a=%8b b=%8b  op=%6b  result=%8b   _flag_c_in=%b _flags (_c=%b _z=%1b _n=%1b _o=%1b _eq=%1b _ne=%1b _gt=%1b _lt=%b)", 
            a,
            b,
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
        */
        
        `endif
    end

    wire [7:0] C=1;
    wire [7:0] Z=2;
    wire [7:0] N=4;
    wire [7:0] O=8;
    wire [7:0] EQ=16;
    wire [7:0] NE=32;
    wire [7:0] GT=64;
    wire [7:0] LT=128;

    function flagCheck(string flagName, [0:0] _flagValue, [7:0] expectation, bitSelector, string expectationStr);
        reg bitExpected;
        bitExpected = (expectation & bitSelector) == 0;
        flagCheck = (_flagValue == bitExpected);
        if (!flagCheck) $display("FLAG %2s wrong : got=%1b expected=%1b     for test case '%s'", flagName, _flagValue, bitExpected, expectationStr); 
    endfunction

    `define FCMP(EXPECTATION,FLAGNAME,flagname) flagCheck("FLAGNAME", _flag_``flagname``, (EXPECTATION), FLAGNAME, "EXPECTATION")

    `define FLAGS(X) \
        if (!(`FCMP(X, C, c) & `FCMP(X, N, n) & `FCMP(X, Z, z) & `FCMP(X, O, o) & `FCMP(X, EQ, eq) & `FCMP(X, NE, ne) & `FCMP(X, GT, gt) & `FCMP(X, LT, lt))) \
        begin \
            $display("flag error at ", `__LINE__);  \
        `ifndef verilator \
            $finish_and_return(1);  \
        `endif \
        end

    localparam PropDelay=1000;

    function [7:0] to8([7:0] i);
        to8 = i;
    endfunction

    task PD;
        #PropDelay $display("");
    endtask

    initial begin

        ////////////////////////////////////////////////////////////// A_PLUS_B

        // -86 + -127 is the same as 
        assign _flag_c_in=1;
        assign a = 8'b10101010; // -86 = 170 unsigned
        assign b = 8'b10000001; // -127 = 129 unsigned
        assign alu_op = alu_ops.OP_A_PLUS_B;

        PD;
        `Equals(o, 8'b00101011); // +43 so this is signed overflow but also carry because 170+129=42 carry 1
        `FLAGS(C | O | NE | GT)

        // UNSIGNED & TWOS COMP
        // 1 UN - 5 TC   = -4 TC = 252 UN
        // 1 UN + 251 UN = -4 TC = 252 UN
        assign a = 1;  
        assign b = -5; // same as 251 unsigned
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 8'(-4)); // 1+-5=-4   
        `Equals(o, 252); // but also 1+251 unsigned = 252
        `FLAGS(N | NE | LT) // LT because 1 < -5 when considered as unsigned 8 bit

        // -5 +3 = -2 
        assign _flag_c_in=1;
        assign a = 251; // -5
        assign b = 3;   // +3
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 8'b11111110); // -2
        `FLAGS(N|NE|GT)

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 3);
        `FLAGS(EQ) 

        assign a = 1;
        assign b = -1; 
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_PLUS_B; 
        PD;
        `Equals(o, 0); // signed 1 + -1 = 0,  unsigned 1+255=0 carry 1
        `FLAGS(C|Z|NE|LT) 

        ////////////////////////////////////////////////////////////// A_MINUS_B
        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 0);
        `FLAGS(Z|EQ)

        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'b11111110); // -2 = 254 unsigned
        `Equals(o, -8'd2); // also can write a negative twos complement like this
        `FLAGS(C|N|NE|LT)  // 1-3 = -2   but also  1 - 254 unsigned = 255 borrow 1

        assign a = 1;
        assign b = -3; // same as 254
        assign _flag_c_in = 1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'b00000100); // 1 - -3 = +4    but also 1-254 = 255 borrow 1
        `Equals(o, 8'd4); // also can write a negative twos complement like this
        `FLAGS(C|NE|LT)  // O set and C set - bug FIXME

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 0; // carry an extra -1 into the subtraction pushing it past 0
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 255);
        `Equals(o, -8'd1);
        `FLAGS(C|N|EQ)

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'b11111111); // 1-1-1c=-1
        `FLAGS(C|N|EQ) 

        // -255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 1'b1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'h01); // 0 - -1 = +1   , but in unsigned this is 0-255 = 1 borrow 1
        `FLAGS(C|NE|LT) 

        assign a = 1;
        assign b = 1; 
        assign _flag_c_in = 1'b1;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'h00);
        `FLAGS(Z|EQ) 

        assign a = 1;
        assign b = 1; 
        assign _flag_c_in = 1'b0;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'hff); // 1-1-1=-1   = 255UNSIGNED
        `FLAGS(C|N|EQ) 

        ////////////////////////////////////////////////////////////// MINUS_B
        assign a = 130; // ONLY RELEVANT TO COMPARATORS
        assign b = 255; // 255 looks the same as -1 so the negation will be +1  , whereas in unsigned 0-255=1 borrow 1
        assign _flag_c_in = 'x; // not relevant
        assign alu_op = alu_ops.OP_MINUS_B;
        PD;
        `Equals(o, 8'h01); // negate -1 = +1    
        `FLAGS(C|NE|LT) 


        ////////////////////////////////////////////////////////////// A_TIMES_B_HI
        // TIMES
        assign a = 8'hff;
        assign b = 8'h00;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        PD;
        `Equals(o, 8'h00);
        `FLAGS(Z|NE|GT)  

        assign a = 8'h0F;
        assign b = 8'h0F;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI; // 0fx0f=e1
        PD;
        `Equals(o, 8'b00000000); // 0f*0f=00e1
        `Equals(_flag_c, 1'b1);
        `FLAGS(EQ|Z) 

        assign a = 8'hFF;
        assign b = 8'hFF;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        PD;
        `Equals(o, 8'hFE);
        `FLAGS(N|EQ) 

        assign a = 8'hF0;
        assign b = 8'h10;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_HI;
        PD;
        `Equals(o, 8'h0F);
        `FLAGS(NE|GT) 

        ////////////////////////////////////////////////////////////// A_TIMES_B_HI
        assign a = 8'hff;
        assign b = 8'h00;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_LO;
        PD;
        `Equals(o, 8'h00);
        `FLAGS(Z|NE|GT)  

        assign a = 8'h0f;
        assign b = 8'h0f;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_LO;
        PD;
        `Equals(o, 8'he1);
        `FLAGS(N|EQ)  

        assign a = 8'hFF;
        assign b = 8'hFF;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_TIMES_B_LO;
        PD;
        `Equals(o, 8'h01);
        `FLAGS(C|EQ)  // carry here indicates that the upper byte is non-zero

        ////////////////////////////////////////////////////////////// A_AND_B
        assign a = 8'b11010101; // LOGICAL VALUE
        assign b = 8'b10000000; // LOGICAL VALUE
        assign alu_op = alu_ops.OP_A_AND_B;
        PD;
        `Equals(o, 8'b10000000);
        `FLAGS(N|NE|GT) // neg and ov get set but since this isn't arith then interpet with that in mind

        ////////////////////////////////////////////////////////////// A
        assign a = 8'b11010101; // PASS THRU
        assign b = 8'b10000000; // PASS THRU
        assign alu_op = alu_ops.OP_A;
        PD;
        `Equals(o, 8'b11010101);
        `FLAGS(N|NE|GT) // neg and ov get set but since this isn't arith then interpet with that in mind

        PD;
        $display("---");
        $display("done");
        

    end
endmodule : test
