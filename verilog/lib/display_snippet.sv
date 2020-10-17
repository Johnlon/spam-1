// SNIPPED TO INCLUDE IN BODY OF MODULE
// adds this label to dumpfile and logging

/* verilator lint_off UNUSED */
reg [80:0][7:0] label;
//string label;

`ifndef verilator
    `define DISPLAY(x) label=x; \
            $display("\n", x);
`else 
    `define DISPLAY(x) label=x; 
`endif
/* verilator lint_on UNUSED */
