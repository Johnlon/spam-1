// assertion macro used in tests - is there a built for this??
`ifndef verilator
`define FAIL $finish_and_return(1); 
`else
`define FAIL $finish; 
`endif



`define equals(actual, expected, msg) \
if (actual === expected) begin \
  if (1==2) $display("%d passed:  %b == %b - %s", `__LINE__,actual, expected, msg); \
end \
else \
begin  \
  $display("%d FAILED: '%b' is not '%b' - %s", `__LINE__,actual, expected, msg); 	\
  `FAIL \
end


`define assertEquals(actual, expected_value) \
if (actual === expected_value) begin \
  if (1==2) $display("Passed %-d %b == %b", `__LINE__,actual, expected_value); \
end \
else \
begin  \
  $display("FAILED @ %-4d : expected '%b'", `__LINE__, expected_value); 	\
  $display("              : but got  '%b'", actual); 	\
  `FAIL \
end

`ifdef verilator
  `define Equals(ACTUAL, EXPECTED)  // PASS
`else
`define Equals(ACTUAL, EXPECTED) \
if (ACTUAL === EXPECTED) begin \
  if (0) $display("%9t ", $time, " Line:%-5d PASSED: '%b' == '%b'    : ACTUAL != EXPECTED", `__LINE__, ACTUAL, EXPECTED); \
end \
else \
begin  \
  $display("%9t ", $time, " Line:%-5d FAILED: actual '%b' != '%b' expected,   (dec %1d != %1d)  : ACTUAL != EXPECTED", `__LINE__, ACTUAL, EXPECTED, ACTUAL, EXPECTED); 	\
  `FAIL \
end

`endif


