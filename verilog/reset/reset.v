// see also ../../docs/reset_timings_waveform.json
`include "../7474/hct7474.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

// "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
module reset(
    input _RESET_SWITCH,
    input system_clk,
    
    output _mrPos,  // clears on the 1st positive edge after _RESET_SWITH is released
    output _mrNeg, // clears later - on the 1st negative edge after the positive edge that cleared _mr
    output clk   // gated clock - clock stops in low state during reset
);
    parameter LOG=0;

    hct7474 #(.BLOCKS(1), .LOG(0)) resetff(
          ._SD(1'b1),
          ._RD(_RESET_SWITCH),
          .D(1'b1),
          .CP(system_clk),
          .Q(_mrPos)
          //._Q(_mr)
        );


    // _reset_pc is same as MR however it clears on the neg edge so that the PC can reset on the previous +ve edge
    wire #(8) _system_clk = !system_clk;

    hct7474 #(.BLOCKS(1), .LOG(0)) pcresetff(
          ._SD(1'b1),
          ._RD(_mrPos), // wont set back to H until after _mrPos clears
          .D(1'd1),
          .CP(_system_clk), // resets on the -ve edge after _mrPos is released
          .Q(_mrNeg),
          ._Q()
        );

    assign #(10) clk = system_clk & _mrPos; // AND GATE

endmodule 
