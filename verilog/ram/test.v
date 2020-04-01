// verilator lint_off STMTDLY

`include "ram.v"
`include "../lib/assertion.v"

module test();

 logic _OE, _WE;
 logic [7:0] A;
 tri [7:0] D;
 
 logic [7:0] Vd;
 assign D=Vd;

ram #(.AWIDTH(8), .DEPTH(16)) r1(._OE, ._WE, .A, .D);

 initial begin
    assign A=8'b00000001;
    Vd=8'b00000001;
    _OE=1'b1;
    _WE=1'b0;
    #100
    _WE=1'b1;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - setup write 1 @ 1",_OE, _WE, A, D);
 
    assign A=8'b00000010;
    Vd=8'b00000010;
    _OE=1'b1;
    _WE=1'b0;
    #100
    _WE=1'b1;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - setup write 2 @ 2",_OE, _WE, A, D);
 
    Vd=8'bzzzzzzzz;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - nothing asserted",_OE, _WE, A, D);
    `Equals(D, 8'bzzzzzzzz);

    assign A=8'b00000010;
    _OE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - read back 2",_OE, _WE, A, D);
    `Equals(D, 8'b00000010);
 
    assign A=8'b00000001;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - read back 1 ",_OE, _WE, A, D);
    `Equals(D, 8'b00000001);
 
    assign A=8'b00000000;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, A=%8b, D=%8b - read back 0 ",_OE, _WE, A, D);
    `Equals(D, 8'bxxxxxxxx);
 
    // this will trip an exception trap in the RAM - but I can't test that - uncomment 
    // _OE=1'b0;
    // _WE=1'b0;
    // #100
    // $display("TEST: _OE=%1b, _WE=%1b, A=%8b, d=%8b - cannot enable write and output at same time",_OE, _WE, addr, d);
    // `Equals(d, 8'bzzzzzzzz);
 
 end

endmodule
