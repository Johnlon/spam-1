`include "../lib/assertion.v"

`include "hct74245.v"
`timescale 1ns/100ps
//`default_nettype none

module tb();

      tri [7:0]A;
      tri [7:0]B;

      logic [7:0] Vb=8'b00000000;
      logic [7:0] Va=8'b11111111;

      logic dir;
      logic nOE;

      assign B=Vb;
      assign A=Va;

      hct74245 buf245(.A, .B, .dir, .nOE);
    
    initial begin


      Va=8'bxxxxxxxx;
      Vb=8'bxxxxxxxx;
      dir <= 1; // a->b
      nOE <= 1;
      #30
      `equals(A , 8'bxxxxxxxx, "OE disable");
      `equals(B , 8'bxxxxxxxx, "OE disable");
      #30

      Va=8'bzzzzzzzz;
      Vb=8'bzzzzzzzz;
      dir <= 1; // a->b
      nOE <= 1;
      #30
      `equals(A , 8'bzzzzzzzz, "OE disable");
      `equals(B , 8'bzzzzzzzz, "OE disable");
      #30

      dir <= 0; // b->a
      nOE <= 1;
      #30
      `equals(A , 8'bzzzzzzzz, "OE disable");
      `equals(B , 8'bzzzzzzzz, "OE disable");
      #30

      Va=8'b11111111;
      Vb=8'b11111111;
      #30
       `equals(A , 8'b11111111, "OE disable - 1");
       `equals(B , 8'b11111111, "OE disable - 1");
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30

      Va=8'b00000000;
      Vb=8'b00000000;
      #30
       `equals(A , 8'b00000000, "OE disable - 0");
       `equals(B , 8'b00000000, "OE disable - 0");
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30

      dir <= 0; // b->a
      nOE <= 0;
      Va=8'bzzzzzzzz;
      Vb=8'b11111111;
      #30
       `equals(A , 8'b11111111, "OE B->A 1's");
       `equals(B , 8'b11111111, "OE B->A 1's");
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      
      dir <= 0; // b->a
      nOE <= 0;
      Va=8'bzzzzzzzz;
      Vb=8'b00000000;
      #30
       `equals(A , 8'b00000000, "OE B->A 0's");
       `equals(B , 8'b00000000, "OE B->A 0's");
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      dir <= 1; // a->b
      nOE <= 0;
      Va=8'b00000000;
      Vb=8'bzzzzzzzz;
      #30
       `equals(A , 8'b00000000, "OE A->B 0's");
       `equals(B , 8'b00000000, "OE A->B 0's");

      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      dir <= 1; // a->b
      nOE <= 0;
      Va=8'b11111111;
      Vb=8'bzzzzzzzz;
      #30
       `equals(A , 8'b11111111, "OE A->B 1's");
       `equals(B , 8'b11111111, "OE A->B 1's");

      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      ////////////////////////////////

      dir <= 1; // a->b
      nOE <= 0;
      Va=8'b00000000;
      Vb=8'b11111111;
      #30
       `equals(A , 8'b00000000, "OE A->B 0's");
       `equals(B , 8'bxxxxxxxx, "OE A->B 1 conflicted's");
      
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      dir <= 1; // a->b
      nOE <= 0;
      Va=8'b11111111;
      Vb=8'b00000000;
      #30
       `equals(A , 8'b11111111, "OE A->B 1's");
       `equals(B , 8'bxxxxxxxx, "OE A->B 0 conflicted's");
      
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      ////////////////////////////////
      dir <= 0; // b-a
      nOE <= 0;
      Va=8'b11111111;
      Vb=8'b00000000;
      #30
       `equals(A , 8'bxxxxxxxx, "OE B->A 1 conflicted's");
       `equals(B , 8'b00000000, "OE B->A 0's");
      
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      #30
      
      dir <= 0; // b->a
      nOE <= 0;
      Va=8'b00000000;
      Vb=8'b11111111;
      #30
       `equals(A , 8'bxxxxxxxx, "OE B->A 1 conflicted's");
       `equals(B , 8'b11111111, "OE B->A 0's");
      
      $display($time, "1 dir ", dir, " nOE ", nOE, " A %8b ", A," B %8b ", B);
      
      
      
    end

endmodule : tb

