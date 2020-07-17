// verilator lint_off STMTDLY

`include "ram.v"
`include "../lib/assertion.v"
`timescale 1ns/1ns

module test();

 logic _OE, _WE;
 logic [7:0] A;
 tri [7:0] D;
 
 logic [7:0] Vd;
 assign D=Vd;

 localparam PD=1000;

  ram #(.AWIDTH(8), .DEPTH(16), .LOG(1)) r1(._OE, ._WE, .A, .D);

  always @*
      $display("%9t", $time, " MON : _OE=%1b, _WE=%1b, A=%8b, D=%8b - setup write 1 @ 1",_OE, _WE, A, D);

 initial begin

    $display("TEST: INITIAL STATE");
    #PD

    $display("TEST: write 1 at 1");
    assign A=8'b00000001;
    Vd=8'b00000001;
    _OE=1'b1;
    _WE=1'b0;
    #PD
    _WE=1'b1;
    #PD
 
    $display("TEST: write 2 at 2");
    assign A=8'b00000010;
    Vd=8'b00000010;
    _OE=1'b1;
    _WE=1'b0;
    #PD
    _WE=1'b1;
    #PD
 
    $display("TEST: NO READ OR WRITE");
    Vd=8'bzzzzzzzz;
    #PD
    `Equals(D, 8'bzzzzzzzz);

    $display("TEST: READ at 2");
    assign A=8'b00000010;
    _OE=1'b0;
    #PD
    `Equals(D, 8'b00000010);
 
    $display("TEST: Slide READ at 1");
    assign A=8'b00000001;
    #PD
    `Equals(D, 8'b00000001);
 
    $display("TEST: Slide READ at 0 is not defined");
    assign A=8'b00000000;
    #PD
    `Equals(D, 8'bxzxzxzxz);

    $display("TEST: READ at 3");
    Vd=8'bz;
    _WE=1'b1;
    _OE=1'b0;
    assign A=8'b00000011;
    #PD
    `Equals(D, 8'bxzxzxzxz);
 
    $display("TEST: WRITE AT 3 with _OE override");
    Vd=8'b00000011;
    A=8'b00000011;
    _WE=1'b0;
    _OE=1'b0;
    #PD

    $display("TEST: READ BACK AT 3 and expect bus conflict");
    Vd=8'b11111111;
    _WE=1'b1;
    _OE=1'b0;
    #PD
    `Equals(D, 8'bxxxxxx11); // conflicting for 6 lines
 
    $display("TEST: READ BACK AT 3");
    Vd=8'bz;
    _WE=1'b1;
    _OE=1'b0;
    #PD
    `Equals(D, 8'b11); // conflicting for 6 lines
 
 
 end

endmodule
