// assertion macro used in tests - is there a built for this??
`define equals(actual, expected, msg) \
if (actual === expected) begin \
  if (1==2) $display("%d passed:  %d == %d - %s", `__LINE__,actual, expected, msg); \
end \
else \
begin  \
  $display("%d failed: '%d' is not '%d' - %s", `__LINE__,actual, expected, msg); 	\
end
  //$finish; 

`define assertEquals(actual, expected_value) \
if (actual === expected_value) begin \
  if (1==2) $display("Passed %-d %d == %d", `__LINE__,actual, expected_value); \
end \
else \
begin  \
  $display("Failed @ %-4d : expected '%d'", `__LINE__, expected_value); 	\
  $display("              : but got  '%d'", actual); 	\
end
  //$finish; \


