module test (c, ip, value, var1);
  input c;
  input ip;
  inout value;
  output reg var1;

  assign value = (c) ? 1'b1 : 1'bz;

  always @(*)
    var1 = value;

endmodule