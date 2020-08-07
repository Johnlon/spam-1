`include "../lib/assertion.v"
`include "counterReg.v"

`timescale 1ns/1ns

// similar to 74163 test but 8 bit counterReg
module testAsPer74163();
logic CP;
logic _MR;
logic CEP;
logic CET;
logic _PE;
logic [7:0] D;
logic [7:0] Q;

wire TC;

always @(*)  begin
    $strobe("%m %9t TEST : CP=%1b _MR=%1b CEP=%1b CET=%1b _PE=%1b D=%8b Q=%8b ", $time, CP, _MR, CEP, CET, _PE, D, Q);
end

// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
counterReg DUT
(
  .CP(CP),
  ._MR(_MR),
  .CEP(CEP),
  .CET(CET),
  ._PE(_PE),
  .D(D),

  .Q(Q),
  .TC(TC)
);

`define CLOCK_PULSE  \
  #300 \
  CP= 1'b0; \
  #300 \
  CP= 1'b1; \
  #300 \
  CP= 1'b0; \
  #300

initial begin
 // $strobe("%m %9t TEST : CP=%1b _MR=%1b CEP=%1b CET=%1b _PE=%1b D=%8b Q=%8b ", $time, CP, _MR, CEP, CET, _PE, D, Q);
  // Set up time printing format to nanoseconds with no decimal precision digits
  // $timeformat [ ( units_number , precision_number , suffix_string , minimum_field_width ) ] ;
  $timeformat(-9, 0, " ns", 10);
  $display("%m %9t: initial state", $time);

  CP = 1'b0;
  CEP = 1'b0;
  CET = 1'b0;
  _PE = 1'b1;
  D = 8'b11111111;

  // SYNC RESET
  _MR = 1'b0;
  #100;
  `Equals(Q, 8'bxxxxxxxx);

  // Clock the reset state into the latch
  $display("%m %9t: clock pulse", $time);
  `CLOCK_PULSE
  `Equals(Q, 8'b00000000);

  // Release reset state
  _MR = 1'b1;
  #100;

  // Load zero as initial counter state
  $display("%m %9t: Load 1 as initial value", $time);
  `define LOADED_VALUE 8'b01011010
  D = `LOADED_VALUE;
  #10
  _PE = 1'b0;

  `CLOCK_PULSE 

  #100
  _PE = 1'b1;
  #100;

  `Equals(Q, `LOADED_VALUE);
  
  
  $display("%m %9t: Regular count test", $time);
  CEP = 1'b1;
  CET = 1'b1;
  $display("COUNT %8b", Q);
  for (int i = 0; i < 255; i++)
  begin
    //$display("i=", i);
    `Equals(Q, 8'(`LOADED_VALUE + i)); 

    #300
    CP= 1'b1;
    #300
    
    CP= 1'b0;
  end

  `define VALUE 8'(`LOADED_VALUE + 255)
  `Equals(Q, `VALUE);

  //////////
  $display("%m %9t", $time, " both CEP and CET have to be High to count");
  CEP = 1'b0;
  CET = 1'b1;
  #50;

  // toggle clock
  CP= 1'b1;
  #300
  CP= 1'b0;
  #300
  $display("%m %9t", $time, " NO COUNT %8b", Q);
  `Equals(Q, `VALUE);

  //////////
  $display("%m %9t", $time, " both CEP and CET have to be High to count");
  CEP = 1'b1;
  CET = 1'b0; // other way round
  #50;

  // toggle clock
  CP= 1'b1;
  #300
  CP= 1'b0;
  #300
  $display("%m %9t", $time, " NO COUNT %8b", Q);
  `Equals(Q, `VALUE);

  // MR sync with clock test
  // MR is synced with clock reset on new +ve edge of clock
  $display("%m %9t", $time, " _MR test = _MR low");
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b0;
  #300
  `Equals(Q, `VALUE); // no reset cos no +ve clock
  
  $display("%m %9t", $time, " _MR test = _MR low and CP low");
  CP= 1'b0;
  #300
  `Equals(Q, `VALUE); // still no reset cos no +ve clock
  
  $display("%m %9t", $time, " _MR test = _MR low and CP high");
  CP= 1'b1;
  #300
  `Equals(Q, 8'b00000000); // now resets
  

  // PE sync with clock test
  // MR is synced with clock reset on new +ve edge of clock
  $display("%m %9t", $time, " _PE test = _MR hi and CP high");
  CP= 1'b1;
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b1;
  #300
  `Equals(Q, 8'b00000000); // still 0

  $display("%m %9t", $time, " _PE test = _PE low but no load yet as no +ve CP");
  D=8'b11111110;
  _PE=1'b0;
  #300
  `Equals(Q, 8'b00000000); // no load cos no +ve clock
  
  $display("%m %9t", $time, " _PE test = _PE low but no load yet as no +ve CP");
  CP= 1'b0;
  #300
  `Equals(Q, 8'b00000000); // still no load cos no +ve clock
  
  $display("%m %9t", $time, " _PE test = _PE low expect load as +ve CP");
  CP= 1'b1;
  #300
  `Equals(Q, 8'b11111110); // now loads
  
  // test overflow
  // The TC output is HIGH when CET is HIGH and the counter is at terminal count (HHHH);

  $display("%m %9t", $time, " overflow test");
  `Equals(Q, 8'b11111110); // initial state
  `Equals(TC, 1'b0); // not carry
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b1;
  _PE= 1'b1;
  #300
  `Equals(TC, 1'b0); // not carry
  `Equals(Q, 8'b11111110); // not counted
  
  CP= 1'b0;
  #300
  `Equals(TC, 1'b0); // not carry
  `Equals(Q, 8'b11111110); // not counted
  
  CP= 1'b1;
  #300
  `Equals(TC, 1'b1); // now carry
  `Equals(Q, 8'b11111111); // now loads
  
  CET=1'b0;
  #300
  `Equals(TC, 1'b0); // now no carry
  `Equals(Q, 8'b11111111); // now loads
  `Equals(Q, 8'b11111111); // now loads
  
end

endmodule
