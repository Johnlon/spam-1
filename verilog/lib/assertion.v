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


`define Equals(ACTUAL, expected) \
if (ACTUAL === expected) begin \
  if (1==2) $display("%d passed:  %b == %b - ACTUAL", `__LINE__, ACTUAL, expected); \
end \
else \
begin  \
  $display("%d FAILED: '%b' is not '%b' - ACTUAL", `__LINE__, ACTUAL, expected); 	\
  `FAIL \
end
  
