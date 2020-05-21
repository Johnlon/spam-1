// SNIPPED TO INCLUDE IN BODY OF MODULE
// adds this label to dumpfile and logging

/* verilator lint_off UNUSED */
reg [20*8:0] label;

`ifndef verilator
    `define DISPLAY(x) label=x; \
            $display(x);
`else 
    `define DISPLAY(x) label=x; 
`endif
/* verilator lint_on UNUSED */
