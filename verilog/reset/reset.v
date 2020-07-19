// see also ../../docs/reset_timings_waveform.json
`include "../7474/hct7474.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

// "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
module reset(
    input _RESET_SWITCH,
    input clk,
    
    output _mr,
    output phase_clk,
    output _reset_pc
);
    parameter LOG=0;

    hct7474 #(.BLOCKS(1), .LOG(0)) resetff(
          ._SD(1'b1),
          ._RD(_RESET_SWITCH),
          .D(1'b1),
          .CP(clk),
          .Q(_mr)
          //._Q(_mr)
        );


    hct7474 #(.BLOCKS(1), .LOG(0)) pcresetff(
          ._SD(1'b1),
          ._RD(_mr),
          .D(1'd1),
          .CP(~clk),
          .Q(_reset_pc),
          ._Q()
        );

    assign #(10) phase_clk = clk & _mr;

endmodule 
