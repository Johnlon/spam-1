// verilator lint_off STMTDLY

`include "ram.v"

module test();

 logic _OE, _WE;
 logic [7:0] addr;
 tri [7:0] d;
 
 logic [7:0] Vd;
 assign d=Vd;

  ram r1(._OE, ._WE, .addr, .d);

 initial begin
    assign addr=8'b00000001;
    Vd=8'b00000001;
    _OE=1'b1;
    _WE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - setup write 1 @ 1",_OE, _WE, addr, d);
 
    _WE=1'b1;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - commit write",_OE, _WE, addr, d);
 
    assign addr=8'b00000010;
    Vd=8'b00000010;
    _OE=1'b1;
    _WE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - setup write 2 @ 2",_OE, _WE, addr, d);
 
    _WE=1'b1;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - commit write",_OE, _WE, addr, d);
 
    Vd=8'bzzzzzzzz;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - nothing asserted",_OE, _WE, addr, d);
 
    _OE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - read back 2",_OE, _WE, addr, d);
 
    assign addr=8'b00000001;
    // _OE=1'b1;
    // #100
    // // _OE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - read back 1 ",_OE, _WE, addr, d);
 
    assign addr=8'b00000000;
    // _OE=1'b1;
    // #100
    // _OE=1'b0;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - read back 0 ",_OE, _WE, addr, d);
 
    assign addr=8'b00000001;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - read back 1",_OE, _WE, addr, d);
 
    assign addr=8'b00000010;
    #100
    $display("TEST: _OE=%1b, _WE=%1b, addr=%8b, d=%8b - read back 2",_OE, _WE, addr, d);
 end

endmodule
