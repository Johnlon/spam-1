// SNIPPED TO INCLUDE IN BODY OF MODULE
// adds this label to dumpfile and logging

reg [20*8:0] label;
`define DISPLAY(x) label=x; $display(x);
