// http://www.cs.hiroshima-u.ac.jp/~nakano/wiki/wiki.cgi?page=ram%2Ev

module ram(clk, load, addr, d, q);
 parameter DWIDTH=16,AWIDTH=12,WORDS=4096;

 input clk,load;
 input [AWIDTH-1:0] addr;
 input [DWIDTH-1:0] d;
 output [DWIDTH-1:0] q;
 reg [DWIDTH-1:0] q;
 reg [DWIDTH-1:0] mem [WORDS-1:0];

 always @(posedge clk)
   begin
     if(load) mem[addr] <= d;
     q <= mem[addr];
   end

 integer i;
 initial begin
    for(i=0;i<WORDS;i=i+1)
       mem[i]=0;
       // ここにメモリの初期化（mem[12'h001]=16'h1234;など）を書く．
 end

endmodule