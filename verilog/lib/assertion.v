// assertion macro used in tests - is there a built for this??
`define equals(actual, expected, msg) \
if (actual === expected) begin \
  if (1==2) $display("%d passed:  %b == %b - %s", `__LINE__,actual, expected, msg); \
end \
else \
begin  \
  $display("%d failed: '%b' is not '%b' - %s", `__LINE__,actual, expected, msg); 	\
end
  //$finish; 

`define assertEquals(actual, expected_value) \
if (actual === expected_value) begin \
  if (1==2) $display("Passed %-d %b == %b", `__LINE__,actual, expected_value); \
end \
else \
begin  \
  $display("Failed @ %-4d : expected '%b'", `__LINE__, expected_value); 	\
  $display("              : but got  '%b'", actual); 	\
end
  //$finish; \


