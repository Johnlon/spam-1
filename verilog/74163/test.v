`include "../lib/assertion.v"
`include "hct74163.v"

module counter_74163_tb();
logic CP;
logic _MR;
logic CEP;
logic CET;
logic _PE;
logic [3:0] D;
logic [3:0] Q;

wire TC;

// naming from https://www.ti.com/lit/ds/symlink/sn74f163a.pdf
hct74163 DUT
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

  always @(*)
    $display("Q=%4b", Q);

initial begin
  // Set up time printing format to nanoseconds with no decimal precision digits
  // $timeformat [ ( units_number , precision_number , suffix_string , minimum_field_width ) ] ;
  $timeformat(-9, 0, " ns", 3);

  CP = 1'b0;
  CEP = 1'b0;
  CET = 1'b0;
  _PE = 1'b1;
  D = 4'b1111;
  _MR = 1'b0;

  #100;

  // Release reset state
  _MR = 1'b1;
  #100;

  `Equals(Q, 4'b0000);

  // Load zero as initial counter state
  $display("[%t]: Load 0 as initial value", $time);
  D = 4'b0000;
  #10
  _PE = 1'b0;
  #100
  _PE = 1'b1;
  #100;

  `Equals(Q, 4'b0000);
  
  
  $display("[%t]: Regular count test", $time);
  CEP = 1'b1;
  CET = 1'b1;
  $display("COUNT ", Q);
  for (int i = 0; i < 10; i++)
  begin
    #300
    CP= 1'b1;
    #300
    CP= 1'b0;
    $display("COUNT ", Q);
  end

  `Equals(Q, 4'b1010);

  //////////
  // both CEP and CET have to be High to count
  CEP = 1'b0;
  CET = 1'b1;
  #50;

  // toggle clock
  CP= 1'b1;
  #300
  CP= 1'b0;
  #300
  $display("NO COUNT ", Q);
  `Equals(Q, 4'b1010);

  //////////
  // both CEP and CET have to be High to count
  CEP = 1'b1;
  CET = 1'b0; // other way round
  #50;

  // toggle clock
  CP= 1'b1;
  #300
  CP= 1'b0;
  #300
  $display("NO COUNT ", Q);
  `Equals(Q, 4'b1010);

  // MR sync with clock test
  // MR is synced with clock reset on new +ve edge of clock
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b0;
  #300
  `Equals(Q, 4'b1010); // no reset cos no +ve clock
  
  CP= 1'b0;
  #300
  `Equals(Q, 4'b1010); // still no reset cos no +ve clock
  
  CP= 1'b1;
  #300
  `Equals(Q, 4'b0000); // now resets
  

  // PE sync with clock test
  // MR is synced with clock reset on new +ve edge of clock
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b1;
  #300
  `Equals(Q, 4'b0000); // still 0

  D=4'b1110;
  _PE=1'b0;
  #300
  `Equals(Q, 4'b0000); // no load cos no +ve clock
  
  CP= 1'b0;
  #300
  `Equals(Q, 4'b0000); // still no load cos no +ve clock
  
  CP= 1'b1;
  #300
  `Equals(Q, 4'b1110); // now loads
  
  // test overflow
  // The TC output is HIGH when CET is HIGH and the counter is at terminal count (HHHH);

  `Equals(Q, 4'b1110); // initial state
  `Equals(TC, 1'b0); // not carry
  CEP = 1'b1;
  CET = 1'b1; 
  _MR= 1'b1;
  _PE= 1'b1;
  #300
  `Equals(TC, 1'b0); // not carry
  `Equals(Q, 4'b1110); // not counted
  
  CP= 1'b0;
  #300
  `Equals(TC, 1'b0); // not carry
  `Equals(Q, 4'b1110); // not counted
  
  CP= 1'b1;
  #300
  `Equals(TC, 1'b1); // now carry
  `Equals(Q, 4'b1111); // now loads
  
  CET=1'b0;
  #300
  `Equals(TC, 1'b0); // now no carry
  `Equals(Q, 4'b1111); // now loads
  `Equals(Q, 4'b1111); // now loads
  
end

endmodule
