
`include "testChained.v"
`include "testAsPer74163.v"

`timescale 1ns/1ns

module test();

// tests will run concurrently
testAsPer74163 tbAsPer74163();
testChained tbChained();

endmodule
