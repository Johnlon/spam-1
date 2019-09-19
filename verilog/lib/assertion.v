// assertion macro used in tests - is there a built for this??
`define equals(actual, expected, msg) \
if (actual === expected) begin \
  if (1==2) $display("passed :  %d == %d : %s", actual, expected, msg); \
end \
else \
begin  \
  $display("failed : got '%d' when expecting '%d' : %s", actual, expected, msg); 	\
end
 // $finish; \
    
