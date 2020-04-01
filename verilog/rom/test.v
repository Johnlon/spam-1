// verilator lint_off STMTDLY

`include "rom.v"
`include "../lib/assertion.v"

`timescale 1ns/100ps

module test();

 logic _OE, _CS;
 logic [3:0] A;
 tri [7:0] D;
 
rom #(.AWIDTH(4), .DEPTH(16)) r1(._CS, ._OE, .A, .D);

 initial begin
    _OE=1'b1;
    _CS=1'b0;
    #100
    $display("TEST: _OE=%1b, _CS=%1b, A=%8b, d=%8b - output disabled",_OE, _CS, A, D);
     `Equals(D, 8'bzzzzzzzz);

    assign A=4'b0010;
    _OE=1'b0;
    _CS=1'b0;
    #100
    $display("TEST: _OE=%1b, _CS=%1b, A=%8b, d=%8b - setup write 2 @ 2",_OE, _CS, A, D);
     `Equals(D, 8'b00000010);
 
    assign A=4'b1111;
    _OE=1'b0;
    _CS=1'b0;
    #100
    $display("TEST: _OE=%1b, _CS=%1b, A=%8b, d=%8b - setup write 2 @ 2",_OE, _CS, A, D);
     `Equals(D, 8'b00001111);
 
 end

endmodule
