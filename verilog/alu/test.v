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
        if (!flagCheck) $display("FLAG %2s wrong : got=%1b expected=%1b     for test case '%s'      a=%8b(%d) b%8b (%d) o=%8b (%d)", flagName, _flagValue, bitExpected, expectationStr, a,a,b,b,o,o); 
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

    integer count;
    logic [7:0] bcount;

    initial begin

/*
        ////////////////////////////////////////////////////////////// 0
        assign a = 1;
        assign b = 2;
        assign _flag_c_in=1;
        assign alu_op = alu_ops.OP_0;
        PD;
        `Equals(o, 8'b0); 
        `FLAGS(NE | Z | LT)

        ////////////////////////////////////////////////////////////// A
        assign a = 1;
        assign b = 2;
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_A;
        PD;
        `Equals(o, 8'b1); 
        `FLAGS(NE | LT)

        assign a = 10;
        assign b = 20;
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_A;
        PD;
        `Equals(o, 10); 
        `FLAGS(NE | LT)

        ////////////////////////////////////////////////////////////// B
        assign a = 1;
        assign b = 2;
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_B;
        PD;
        `Equals(o, 2); 
        `FLAGS(NE | LT)

        assign a = 10;
        assign b = 20;
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_B;
        PD;
        `Equals(o, 20); 
        `FLAGS(NE | LT)

        ////////////////////////////////////////////////////////////// NEGATE

        for (bcount=0; bcount<255; bcount++) begin
            assign a = bcount; 
            assign b = 0;
            assign _flag_c_in = 'x; // not relevant
            assign alu_op = alu_ops.OP_NEGATE_A;
            PD;
            `Equals(o, 8'((~bcount)+1)); // perform 2s comp negation
            `FLAGS( (bcount == 0?Z:0)  |
                    (bcount > 0 & bcount <= 128? N:0)  |
                    (bcount == 0?EQ:0) |
                    (bcount != 0?NE:0)  |
                    (bcount > 0?GT:0)
                ) 
        end

        for (bcount=0; bcount<255; bcount++) begin
            assign a = 0; 
            assign b = bcount; 
            assign _flag_c_in = 'x; // not relevant
            assign alu_op = alu_ops.OP_NEGATE_B;
            PD;
            `Equals(o, 8'((~bcount)+1)); // perform 2s comp negation
            `FLAGS( (bcount == 0?Z:0)  |
                    (bcount > 0 & bcount <= 128? N:0)  |
                    (bcount == 0?EQ:0) |
                    (bcount != 0?NE:0)  |
                    (bcount > 0?LT:0)
                ) 
        end



        ////////////////////////////////////////////////////////////// A_MINUS_1

        for (bcount=0; bcount<255; bcount++) begin
            assign a = bcount; 
            assign b = 0;
            assign _flag_c_in = 'x; // not relevant
            assign alu_op = alu_ops.OP_A_MINUS_1;
            PD;
            $display(8'(bcount)); 
            `Equals(o, 8'(bcount-1)); 
            `FLAGS( 
                    (bcount == 0?C:0)  |
                    (bcount == 1?Z:0)  |
                    ((bcount == 0 | bcount > (127+1)) ?N:0)  |  
                    (bcount == 0?EQ:0) |
                    (bcount != 0?NE:0)  |
                    (bcount > 0?GT:0)
                ) 
        end

        ////////////////////////////////////////////////////////////// B_MINUS_1

        for (bcount=0; bcount<255; bcount++) begin
            assign a = 0; 
            assign b = bcount;
            assign _flag_c_in = 'x; // not relevant
            assign alu_op = alu_ops.OP_B_MINUS_1;
            PD;
            `Equals(o, 8'(bcount-1)); 
            `FLAGS( 
                    (bcount == 0?C:0)  |
                    (bcount == 1?Z:0)  |
                    ((bcount == 0 | bcount > (127+1)) ?N:0)  |  
                    (bcount == 0?EQ:0) |
                    (bcount != 0?NE:0)  |
                    (bcount > 0?LT:0)
                ) 
        end

*/
        ////////////////////////////////////////////////////////////// A_PLUS_B

        // -86 + -127 is the same as 
        assign a = 8'b10101010; // -86 = 170 unsigned
        assign b = 8'b10000001; // -127 = 129 unsigned
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 8'b00101011); // +43 so this is signed overflow but also carry because 170+129=42 carry 1
        `FLAGS(C | O | NE | GT)

        // UNSIGNED & TWOS COMP
        // 1 UN - 5 TC   = -4 TC = 252 UN
        // 1 UN + 251 UN = -4 TC = 252 UN
        assign a = 1;  
        assign b = -5; // same as 251 unsigned
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 8'(-4)); // 1+-5=-4   
        `Equals(o, 252); // but also 1+251 unsigned = 252
        `FLAGS(N | NE | LT) // LT because 1 < -5 when considered as unsigned 8 bit

        // -5 +3 = -2 
        assign a = 251; // -5
        assign b = 3;   // +3
        assign _flag_c_in='x;
        assign alu_op = alu_ops.OP_A_PLUS_B;
        PD;
        `Equals(o, 8'b11111110); // -2
        `FLAGS(N|NE|GT)

        assign a = 1;
        assign b = -1; 
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_PLUS_B; 
        PD;
        `Equals(o, 0); // signed 1 + -1 = 0,  unsigned 1+255=0 carry 1
        `FLAGS(C|Z|NE|LT) 

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_PLUS_B; // carry ignored
        PD;
        `Equals(o, 8'd2);
        `FLAGS(EQ) 

        ////////////////////////////////////////////////////////////// A_MINUS_B

        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'b11111110); // -2 = 254 unsigned
        `Equals(o, -8'd2); // also can write a negative twos complement like this
        `FLAGS(C|N|NE|LT)  // 1-3 = -2   but also  1 - 254 unsigned = 255 borrow 1

        assign a = 1;
        assign b = -3; // same as 254
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'b00000100); // 1 - -3 = +4    but also 1-254 = 255 borrow 1
        `Equals(o, 8'd4); // also can write a negative twos complement like this
        `FLAGS(C|NE|LT)  // O set and C set - bug FIXME

        // -255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'h01); // 0 - -1 = +1   , but in unsigned this is 0-255 = 1 borrow 1
        `FLAGS(C|NE|LT) 

        assign a = 1;
        assign b = 1; 
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B;
        PD;
        `Equals(o, 8'h00);
        `FLAGS(Z|EQ) 

        ////////////////////////////////////////////////////////////// B_MINUS_A
        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_B_MINUS_A;
        PD;
        `Equals(o, 0);
        `FLAGS(Z|EQ)

        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_B_MINUS_A;
        PD;
        `Equals(o, 8'd2);
        `FLAGS(NE|LT)  

        assign a = 1;
        assign b = -3; // same as 253
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_B_MINUS_A;
        PD;
        `Equals(o, 8'd252); // -3 -1 = -4    but also 253 - 1 = 252
        `Equals(o, -8'd4); // also can write a negative twos complement like this
        `FLAGS(N|NE|LT)   // BUG !!!!!!! O IS BEING SET

        // 255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_B_MINUS_A;
        PD;
        `Equals(o, 255); //  -1 - 1 = -1 
        `FLAGS(N|NE|LT) 


        ////////////////////////////////////////////////////////// A_MINUS_B_SIGNEDMAG 
        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B_SIGNEDMAG;
        PD;
        `Equals(o, 8'b11111110); // -2 = 254 unsigned
        `Equals(o, -8'd2); // also can write a negative twos complement like this
        `FLAGS(C|N|NE|LT)  // 1-3 = -2   but also  1 - 254 unsigned = 255 borrow 1

        assign a = 1;
        assign b = -3; // same as 254
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B_SIGNEDMAG;
        PD;
        `Equals(o, 8'b00000100); // 1 - -3 = +4    but also 1-254 = 255 borrow 1
        `Equals(o, 8'd4); // also can write a negative twos complement like this
        `FLAGS(C|NE|GT)  // O set and C set - bug FIXME

        // -255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B_SIGNEDMAG;
        PD;
        `Equals(o, 8'h01); // 0 - -1 = +1   , but in unsigned this is 0-255 = 1 borrow 1
        `FLAGS(C|NE|GT) 

        assign a = 1;
        assign b = 1; 
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_MINUS_B_SIGNEDMAG;
        PD;
        `Equals(o, 8'h00);
        `FLAGS(Z|EQ) 


        ////////////////////////////////////////////////////////////// A_PLUS_B_PLUS_C

        // -86 + -127 is the same as 
        assign a = 8'b10101010; // -86 = 170 unsigned
        assign b = 8'b10000001; // -127 = 129 unsigned
        assign _flag_c_in=0;
        assign alu_op = alu_ops.OP_A_PLUS_B_PLUS_C;
        PD;
        `Equals(o, 8'b00101100); // +43 so this is signed overflow but also carry because 170+129=42 carry 1
        `FLAGS(C | O | NE | GT)

        // UNSIGNED & TWOS COMP
        // 1 UN - 5 TC   = -4 TC = 252 UN
        // 1 UN + 251 UN = -4 TC = 252 UN
        assign a = 1;  
        assign b = -5; // same as 251 unsigned
        assign _flag_c_in=0;
        assign alu_op = alu_ops.OP_A_PLUS_B_PLUS_C;
        PD;
        `Equals(o, 8'(-3)); // 1+-5=-4   
        `Equals(o, 8'(253)); // but also 1+251 unsigned = 252
        `FLAGS(N | NE | LT) // LT because 1 < -5 when considered as unsigned 8 bit

        // -5 +3 = -2 
        assign a = 251; // -5
        assign b = 3;   // +3
        assign _flag_c_in=0;
        assign alu_op = alu_ops.OP_A_PLUS_B_PLUS_C;
        PD;
        `Equals(o, 8'b11111111); // -2
        `FLAGS(N|NE|GT)

        assign a = 1;
        assign b = -1; 
        assign _flag_c_in =0;
        assign alu_op = alu_ops.OP_A_PLUS_B_PLUS_C; 
        PD;
        `Equals(o, 1); // signed 1 + -1 + 1 = 0,  unsigned 1+255+1=1 carry 1
        `FLAGS(C|NE|LT) 

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 1; // will be promoted to low bank of A+B
        assign alu_op = alu_ops.OP_A_PLUS_B_PLUS_C; // carry consumed
        PD;
        `Equals(o, 8'd2);
        `FLAGS(EQ) 


        ////////////////////////////////////////////////////////////// A_MINUS_B_PLUS_C

        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B_MINUS_C;
        PD;
        `Equals(o, 8'b11111101); // -2 = 254 unsigned
        `Equals(o, -8'd3); // also can write a negative twos complement like this
        `FLAGS(C|N|NE|LT)  // 1-3 = -2   but also  1 - 254 unsigned = 255 borrow 1

        assign a = 1;
        assign b = -3; // same as 254
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B_MINUS_C;
        PD;
        `Equals(o, 8'd3); //  ((1 - (-3)) - 1) = 3
        `FLAGS(C|NE|LT)  // O set and C set - bug FIXME


        // -255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B_MINUS_C;
        PD;
        `Equals(o, 8'h00); // ((0 - (-1)) -1) = 0   , but in unsigned this is (0-255)= = 1 borrow 1
        `FLAGS(C|Z|NE|LT)

        assign a = 1;
        assign b = 1; 
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_A_MINUS_B_MINUS_C;
        PD;
        `Equals(o, 8'hff);
        `FLAGS(C|N|EQ) 

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 1; 
        assign alu_op = alu_ops.OP_A_MINUS_B_MINUS_C; 
        PD;
        `Equals(o, -8'd0);
        `FLAGS(Z|EQ)

        ////////////////////////////////////////////////////////////// B_MINUS_A_MINUS_C
        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_B_MINUS_A_MINUS_C;
        PD;
        `Equals(o, 8'd255);
        `FLAGS(C|N|EQ)

        assign a = 1;
        assign b = 3;
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_B_MINUS_A_MINUS_C;
        PD;
        `Equals(o, 8'd1);
        `FLAGS(NE|LT)  

        assign a = 1;
        assign b = -3; // same as 253
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_B_MINUS_A_MINUS_C;
        PD;
        `Equals(o, 8'd251); // (-3 -1 = -4) -1c = -5    but also 253 - 1 -1 = 251
        `Equals(o, -8'd5); 
        `FLAGS(N|NE|LT)   // BUG !!!!!!! O IS BEING SET

        // 255=0-255  is (9'b100000001) too big for 8 bits so overflow
        assign a = 0;
        assign b = 255; // 255 unsigned = but this is -1 in twos complement
        assign _flag_c_in = 0;
        assign alu_op = alu_ops.OP_B_MINUS_A_MINUS_C;
        PD;
        `Equals(o, 8'd254); //  (-1 - 0) -1  = -2 
        `FLAGS(N|NE|LT) 

        assign a = 1;
        assign b = 1;
        assign _flag_c_in = 1; 
        assign alu_op = alu_ops.OP_B_MINUS_A_MINUS_C;
        PD;
        `Equals(o, 8'd0);
        `FLAGS(Z|EQ)


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

        ////////////////////////////////////////////////////////////// A_TIMES_B_LO
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


        ////////////////////////////////////////////////////////////// A_DIV_B
        assign a = 8'haa;
        assign b = 8'h02;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_DIV_B;
        PD;
        `Equals(o, 8'h55);
        `FLAGS(NE|GT)  // carry here indicates that the upper byte is non-zero

        assign a = 8'haa;
        assign b = 8'h00;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_DIV_B;
        PD;
        `Equals(o, 8'bx);
        `FLAGS(C|O|NE|GT)  // carry here indicates that the upper byte is non-zero


        ////////////////////////////////////////////////////////////// A_MOD_B
        assign a = 8'd7;
        assign b = 8'd2;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_MOD_B;
        PD;
        `Equals(o, 8'd1);
        `FLAGS(NE|GT)  // carry here indicates that the upper byte is non-zero

        assign a = 8'haa;
        assign b = 8'h00;
        assign _flag_c_in = 1'bx;
        assign alu_op = alu_ops.OP_A_MOD_B;
        PD;
        `Equals(o, 8'bx);
        `FLAGS(C|O|NE|GT)  // carry here indicates that the upper byte is non-zero

        ////////////////////////////////////////////////////////////// A_LSL_B
        assign a = 8'b10000010;
        assign b = 0;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_LSL_B;
        PD;
        `Equals(o, 8'b10000010);
        `FLAGS(N|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b00000100);
        `FLAGS(C|NE|GT)  

        assign b = 0;
        PD;
        `Equals(o, 8'b10000010);
        `FLAGS(N|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b00000100);
        `FLAGS(C|NE|GT)  

        assign b = 8;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(Z|NE|GT)  

        assign b = 9;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(Z|NE|GT)  

        assign b = 10;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(Z|NE|GT)  

        ////////////////////////////////////////////////////////////// A_LSR_B

        assign a = 8'b10000001;
        assign b = 0;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_LSR_B;
        PD;
        `Equals(o, 8'b10000001);
        `FLAGS(N|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b01000000);
        `FLAGS(C|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b01000000);
        `FLAGS(C|NE|GT)  

        assign b = 8;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(C|Z|NE|GT)  

        assign b = 9;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(Z|NE|GT)  

        assign b = 10;
        PD;
        `Equals(o, 8'b00000000);
        `FLAGS(Z|NE|GT)  

        ////////////////////////////////////////////////////////////// A_ASR_B

        assign a = 8'b10000001;
        assign b = 0;
        assign _flag_c_in = 'x;
        assign alu_op = alu_ops.OP_A_ASR_B;
        PD;
        `Equals(o, 8'b10000001);
        `FLAGS(N|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b11000000);
        `FLAGS(C|N|NE|GT)  

        assign b = 1;
        PD;
        `Equals(o, 8'b11000000);
        `FLAGS(C|N|NE|GT)  

        assign b = 6;
        PD;
        `Equals(o, 8'b11111110);
        `FLAGS(N|NE|GT)  

        assign b = 7;
        PD;
        `Equals(o, 8'b11111111);
        `FLAGS(N|NE|GT)  

        assign b = 8;
        PD;
        `Equals(o, 8'b11111111);
        `FLAGS(C|N|NE|GT)  

        assign b = 9;
        PD;
        `Equals(o, 8'b11111111);
        `FLAGS(C|N|NE|GT)  

        assign b = 10;
        PD;
        `Equals(o, 8'b11111111);
        `FLAGS(C|N|NE|GT)  

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
