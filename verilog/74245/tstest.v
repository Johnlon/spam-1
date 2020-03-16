`include "tsbuf.v"
//`timescale 1ns/100ps
//`default_nettype none

module tribuf(out, in, control);
input in;
input control;
inout out;

assign #2 out = control? in: 1'bz;

endmodule

module tstest();

    tri [3:0] bus;
    logic [3:0] a;
    logic [3:0] a1;
    logic c;
    logic c1;

    tribuf tb1[3:0] (
      .out(bus), .in(a), .control(c)
    );
  
    tribuf tb2[3:0] (
      .out(bus), .in(a1), .control(c1)
    );

    pullup p[3:0](bus);

  initial begin
  

    #1
      $display(bus);
  
    c=1;
    c1=0;
    a=4'b1010;
    a1=4'b0011;
    #4
      $display(bus);
  
    c=0;
    c1=1;
    #4
      $display(bus);
    
    c=0;
    c1=0;
    bus=4'b1000;
    #4
      $display(bus);
  
    c=0;
    c1=0;
    a=4;
    #4
      $display(bus);
  
  
  end
endmodule : tstest

