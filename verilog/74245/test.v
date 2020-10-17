// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off UNOPTFLAT


`include "../lib/assertion.v"
`include "./hct74245.v"
`timescale 1ns/1ns

module tb();

      tri [7:0]A;
      tri [7:0]B;

      logic [7:0] Vb;
      logic [7:0] Va;
      //logic [7:0] Vb=8'b00000000;
      //logic [7:0] Va=8'b11111111;

      logic dir;
      logic nOEX;
      logic nOEY=1;

      assign B=Vb;
      assign A=Va;

      hct74245 #(.LOG(1), .NAME("BUFX")) buf245X(.A, .B, .dir, .nOE(nOEX));

        // Only used in Mux test
      wire [7:0] Ay = 8'b10101010;
      hct74245 #(.LOG(1), .NAME("BUFY")) buf245Y(.A(Ay), .B, .dir(1'b1), .nOE(nOEY));

    // for pull down int test
    tri [7:0] B_pulldown;
    hct74245ab #(.LOG(1), .NAME("BUFZ")) buf245pd(.A(Ay), .B(B_pulldown), .nOE(1'b1));
    pulldown pd[7:0](B_pulldown);

    tri0 [7:0] B_tri0;
    hct74245ab #(.LOG(1), .NAME("BUFZ")) buf245tri(.A(Ay), .B(B_tri0), .nOE(1'b1));
    hct74245ab #(.LOG(1), .NAME("BUFZ")) buf245triNL(.A(Ay), .B(B_tri0), .nOE(1'b1));

    always @*
        $display($time, " => dir=%1b", dir, " nOEX=%1b", nOEX, " Va=%8b", Va, " Vb=%8b ", Vb, " A=%8b ", A," B=%8b ", B);
     
    time timer;

    initial begin
      
      Va='x;
      Vb='x;
      dir = 1; // a->b
      nOEX = 1;
      #2 // not enough time to stabilise
      `equals(A , 8'bxxxxxxxx, "OE disable");
      `equals(B , 8'bxxxxxxxx, "OE disable");
      ////////////////////////////////

      Va=8'bzzzzzzzz;
      Vb=8'bzzzzzzzz;

      dir = 1; // a->b
      nOEX = 1;
      #30
      `equals(A , 8'bzzzzzzzz, "OE disable A->B");
      `equals(B , 8'bzzzzzzzz, "OE disable A->B");

      dir = 0; // b->a
      nOEX = 1;
      #30
      `equals(A , 8'bzzzzzzzz, "OE disable A->B");
      `equals(B , 8'bzzzzzzzz, "OE disable A->B");

      ////////////////////////////////

      Va=8'b11111111;
      Vb=8'b11111111;
      #31
       `equals(A , 8'b11111111, "OE disable - 1");
       `equals(B , 8'b11111111, "OE disable - 1");

      Va=8'b00000000;
      Vb=8'b00000000;
      #30
       `equals(A , 8'b00000000, "OE disable - 0");
       `equals(B , 8'b00000000, "OE disable - 0");
      
      ////////////////////////////////

      $display("disable output , set dir a->b");
      dir = 1; // b->a
      nOEX = 1;
      Va=8'b11111111;
      Vb=8'bzzzzzzzz;
      #50 // settle
      timer=$time;
      nOEX = 0;

      `WAIT(B === 8'b11111111);

      if ($time - timer < 16) 
        $display("TOO QUICK - EXPECTED 16ns - TOOK %-d", ($time - timer));
        else
        $display("TOOK %-d", ($time - timer));
        //$finish;

      #50

      $display("enable output");
      nOEX = 0;
      #30
       `Equals(A , 8'b11111111);
       `Equals(B , 8'b11111111);

      $display("switch dir while enabled");
      dir = 0; // b->a
      nOEX = 0;
      Va=8'bzzzzzzzz;
      Vb=8'b11111111;
      #30
       `equals(A , 8'b11111111, "OE B->A 1's");
       `equals(B , 8'b11111111, "OE B->A 1's");
      
      $display("switch to 0's while enabled");
      dir = 0; // b->a
      nOEX = 0;
      Va=8'bzzzzzzzz;
      Vb=8'b00000000;
      #30
       `equals(A , 8'b00000000, "OE B->A 0's");
       `equals(B , 8'b00000000, "OE B->A 0's");

      ////////////////////////////////

      $display("switch dir while enabled");
      dir = 1; // a->b
      nOEX = 0;
      Va=8'b00000000;
      Vb=8'bzzzzzzzz;
      #30
       `equals(A , 8'b00000000, "OE A->B 0's");
       `equals(B , 8'b00000000, "OE A->B 0's");
      
      $display("switch to 1's while enabled");
      dir = 1; // a->b
      nOEX = 0;
      Va=8'b11111111;
      Vb=8'bzzzzzzzz;
      #30
       `equals(A , 8'b11111111, "OE A->B 1's");
       `equals(B , 8'b11111111, "OE A->B 1's");

      ////////////////////////////////

    $display("conflict tests - output already asserted by other device");

      dir = 1; // a->b
      nOEX = 0;
      Va=8'b00000000;
      Vb=8'b1111111z;
      #30
       `equals(A , 8'b00000000, "OE A->B 0's");
       `equals(B , 8'bxxxxxxx0, "OE A->B 1 conflicted's");
      
      #30
      
      dir = 1; // a->b
      nOEX = 0;
      Va=8'b11111111;
      Vb=8'b0000000z;
      #17
       `equals(A , 8'b11111111, "OE A->B 1's");
       `equals(B , 8'bxxxxxxx1, "OE A->B 0 conflicted's");
      
      ////////////////////////////////
$display("-------------------------");
      dir = 0; // b-a
      nOEX = 0;
      Va=8'b1111111z;
      Vb=8'b00000000;
      #33 // 2*16+1 << problem with 74245 delays - they double up if changing DIR and Val at same time
$display("-------------------------");
       `equals(A , 8'bxxxxxxx0, "OE B->A 1 conflicted's");
       `equals(B , 8'b00000000, "OE B->A 0's");
      
      dir = 0; // b->a
      nOEX = 0;
      Va=8'b0000000z;
      Vb=8'b11111111;
      #17 
$display("-------------------------");
       `equals(A , 8'bxxxxxxx1, "OE B->A 1 conflicted's");
       `equals(B , 8'b11111111, "OE B->A 0's");
      
    $display("mux tests - switching to A->B with no driver");
      Va=8'b10101010;
      Vb=8'bzzzzzzzz;
      dir = 1; // a->b
      nOEX = 1;
      nOEY = 1;
      #30
      `equals(B , 8'bzzzzzzzz, "OE A->B X driving");

    $display("muxed out - X driving");

      nOEX = 0;
      nOEY = 1;
      #30
       `equals(B , 8'b10101010, "OE A->B X driving");

    $display("muxed out - Y driving");

      nOEX = 1;
      nOEY = 0;
      #30
      `equals(B , 8'b10101010, "OE A->B Y driving");
    
    $display("pull down integration test");
      #60
      `equals(B_pulldown , 8'b00000000, "pulldown");
    
    $display("tri0 integration test");
      #60
      `equals(B_tri0 , 8'b0, "pulldown");

    end

endmodule : tb

