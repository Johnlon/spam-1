// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off UNOPTFLAT


`include "../lib/assertion.v"
`include "./hct74245.v"
`timescale 1ns/1ns

module tb();

    tri [7:0] A_muxX;
    tri [7:0] B_mux;

    logic [7:0] Vb;
    logic [7:0] Va;
  
    logic dir;
    logic nOEX;
    logic nOEY=1;

    assign B_mux=Vb;
    assign A_muxX=Va;
    wire [7:0] A_muxY = 8'b10101010;


    // Only used in Mux test
    hct74245 #(.LOG(1), .NAME("buf245MuxX")) buf245MuxX(.A(A_muxX), .B(B_mux), .dir,       .nOE(nOEX));
    hct74245 #(.LOG(0), .NAME("buf245MuxX")) buf245MuxY(.A(A_muxY), .B(B_mux), .dir(1'b1), .nOE(nOEY));

    // for pull down int test
    tri [7:0] B_pulldown;
    hct74245ab #(.LOG(0), .NAME("buf245pd")) buf245pd(.A(A_muxY), .B(B_pulldown), .nOE(1'b1));
    pulldown pd[7:0](B_pulldown);

    tri0 [7:0] B_tri0;
    hct74245ab #(.LOG(0), .NAME("buf245tri")) buf245tri(.A(A_muxY), .B(B_tri0), .nOE(1'b1));
    hct74245ab #(.LOG(0), .NAME("buf245triNL")) buf245triNL(.A(A_muxY), .B(B_tri0), .nOE(1'b1));

    always @*
        $display("%9t", $time, " => dir=%1b", dir, " nOEX=%1b", nOEX, " Va=%8b", Va, " Vb=%8b ", Vb, " A_muxX=%8b ", A_muxX," B_mux=%8b ", B_mux);
     
    time timer;

    initial begin
      $display("test initial values");

      Va='x;
      Vb='x;
      dir = 1; // a->b
      nOEX = 1;
      #2 // not enough time to stabilise
      `equals(A_muxX, 8'bxxxxxxxx, "OE disable");
      `equals(B_mux , 8'bxxxxxxxx, "OE disable");
      ////////////////////////////////

      Va=8'bzzzzzzzz;
      Vb=8'bzzzzzzzz;

      #100      
      $display("test output disabled");
      dir = 1; // a->b
      nOEX = 1;
      #30
      `equals(A_muxX, 8'bzzzzzzzz, "OE disable A->B");
      `equals(B_mux , 8'bzzzzzzzz, "OE disable A->B");

      #30
      `equals(A_muxX, 8'bzzzzzzzz, "OE disable A->B");
      `equals(B_mux , 8'bzzzzzzzz, "OE disable A->B");

      ////////////////////////////////
      #100      
      $display("test that when OE is disabled that other drivers can assert");
      
      nOEX = 1;
      Va=8'b11111111;
      Vb=8'b10xz10xz;
      #31
       `equals(A_muxX, 8'b11111111, "OE disable");
       `equals(B_mux , 8'b10xz10xz, "OE disable");

      ////////////////////////////////
      
      #100      
      $display("check timing for enable output");
      dir = 1; // a->b
      nOEX = 1;
      Va=8'b11111111;
      Vb=8'bzzzzzzzz; // avoid contention

      #50 // settle
      $display($time, "start");
      nOEX = 0; // REENABLE

      // check that the timings in the chip actually work
      `ASSERT_TOOK(B_mux === 8'b11111111, buf245MuxX.PD_OE)
      
      
      #100      
      // necessary to setup A as Z before next timing test
      $display("switch dir and value while enabled");
      Va=8'bzzzzzzzz; // avoid contention
      #100
      nOEX = 0;
      Vb=8'b10101011;
      dir = 1; // a-b
      #100
      Vb=8'b10101010;
      dir = 0; // b->a

      `ASSERT_TOOK(A_muxX === 8'b10101010, buf245MuxX.PD_DIR)

      
      #100      
      $display("switch value while enabled");
      timer=$time;
      dir = 0; // b->a
      nOEX = 0;
      #100
      Va=8'bzzzzzzzz; // avoid contention
      Vb=8'b00000000;

      `ASSERT_TOOK( A_muxX === 8'b00000000, buf245MuxX.PD_TRANS)
      
      ////////////////////////////////
      
      #100      
      $display($time, "switch dir while enabled");
      nOEX = 0;
      Va=8'b00000000;
      Vb=8'bzzzzzzzz;
      #100

      dir = 1; // a->b

      `ASSERT_TOOK( B_mux === 8'b00000000, buf245MuxX.PD_DIR)
      
      #100      
      $display("switch to value while enabled");
      dir = 1; // a->b
      nOEX = 0;
      #100 
      Va=8'b11111111;
      Vb=8'bzzzzzzzz;
      
      `ASSERT_TOOK( B_mux === 8'b11111111, buf245MuxX.PD_TRANS)

      ////////////////////////////////
      
      #100      
      $display("conflict tests a->b 0 - output already asserted by other device");
      dir = 1; // a->b
      nOEX = 0;
      #100
      Va=8'b00000000;
      Vb=8'b1111111z;
      `ASSERT_TOOK( B_mux === 8'bxxxxxxx0, buf245MuxX.PD_TRANS)

      #100      
      $display("conflict tests a->b 1 - output already asserted by other device");
      dir = 1; // a->b
      nOEX = 0;
      #100
      Va=8'b11111111;
      Vb=8'b0000000z;
      `ASSERT_TOOK( B_mux === 8'bxxxxxxx1, buf245MuxX.PD_TRANS)

      #100
      $display("conflict tests b->a 1 - output already asserted by other device");
      dir = 0; // b->a
      nOEX = 0;
      #100
      Va=8'b0000000z;
      Vb=8'b11111111;
      `equals(A_muxX, 8'bxxxxxxx1, "OE B->A 1 conflicted's");

      #100
      $display("mux tests - switching to A->B with no driver");
      Vb=8'bzzzzzzzz;
      Va=8'b10101010;
      nOEX = 1;
      nOEY = 1;
      dir = 1; // a->b
      #0
      `equals(B_mux, 8'bzzzzzzzz, "no driver's");
      
      #100
      $display("muxed out - X driving");

      nOEX = 0;
      nOEY = 1;
      #30
       `equals(B_mux , 8'b10101010, "OE A->B X driving");
       

    $display("muxed out - Y driving");

      nOEX = 1;
      nOEY = 0;
      #30
      `equals(B_mux , 8'b10101010, "OE A->B Y driving");
    
    $display("pull down integration test");
      #60
      `equals(B_pulldown , 8'b00000000, "pulldown");
    
    $display("tri0 integration test");
      #60
      `equals(B_tri0 , 8'b0, "pulldown");

    end

endmodule : tb

