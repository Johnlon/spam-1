/* verilator lint_off ASSIGNDLY */
/* verilator lint_off STMTDLY */


`include "../lib/assertion.v"
`include "./hct74151.v"
`timescale 1ns/1ns

module test();

    wire Y, _Y;
    logic [2:0] S;
    logic [7:0] I;
    logic _E;

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
    end
     
    `include "../lib/display_snippet.v"

    hct74151 #(.LOG(1)) demux(
        .Y, ._Y,
        ._E,
        .S,
        .I);


    initial begin
      
        `DISPLAY("disabled");
        _E=1;
        #100
        `Equals(Y, 1'b0);
        `Equals(_Y, 1'b1);
                
        `DISPLAY("enabled s0 i0");
        _E=0;
        S=0;
        I=8'b00000000;
        #100
        `Equals(Y, 1'b0);
        `Equals(_Y, 1'b1);
                
        `DISPLAY("enabled s0 i1");
        _E=0;
        S=0;
        I=8'b00000001;
        #100
        `Equals(Y, 1'b1);
        `Equals(_Y, 1'b0);

        `DISPLAY("enabled s1 i2 - Y will glitch low because S is faster than I");
        _E=0;
        S=1;
        I=8'b00000010;
        #100
        `Equals(Y, 1'b1);
        `Equals(_Y, 1'b0);
                
                
        `DISPLAY("disabled");
        _E=1;
        S=0;
        I=8'b00000000;
        #100
        `Equals(Y, 1'b0);
        `Equals(_Y, 1'b1);
                

        #1000
        `DISPLAY("done");
        $finish;
    end

endmodule : test

