// http://www.cs.hiroshima-u.ac.jp/~nakano/wiki/wiki.cgi?page=ram%2Ev

module ram(
 input _OE, _WE,
 input [AWIDTH-1:0] addr,
 inout [DWIDTH-1:0] d
 );

 parameter DWIDTH=8,AWIDTH=8,WORDS=4096;

reg [DWIDTH-1:0] mem [WORDS-1:0];
logic [7:0] dout;

assign d = dout;

// always @*
//     $monitor("RAM : _OE=%1b, _WE=%1b, addr=%8b, d=%8b, m0=%8b, m1=%8b, m2=%8b",_OE, _WE, addr, d, mem[0], mem[1], mem[2]);

 // take we low then high to write - effectivey latching on the +ve.
 // in real ram write is typically active for the whole _WE phase 
//  always @(negedge _WE)
//    begin
//      //assign mem[addr] = _WE? mem[addr]: d; 
//       dout = 8'bzzzzzzzz;
//    end
// always @(posedge _WE)
//    begin
//      //assign mem[addr] = _WE? mem[addr]: d; 
//       mem[addr] <= d;
//    end



always @(_WE or d or addr)
   begin
     if (!_WE) begin
        dout = 8'bzzzzzzzz;
        mem[addr] <= d;
    end
   end



// always @(negedge _OE or addr)
always @(_OE or addr)
   begin
     // inactive when _WE is active
      if (!_OE && _WE) dout = mem[addr];
      // if ( _WE) dout = mem[addr];
   end

 integer i;
 initial begin
    for(i=0;i<WORDS;i=i+1)
       mem[i]=0;
 end

endmodule
