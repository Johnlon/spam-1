`ifndef RAM_V
`define RAM_V

`timescale 1ns/100ps

// verilator lint_off UNOPTFLAT
module ram(_OE, _WE, A, D);

parameter DWIDTH=8,AWIDTH=16, DEPTH= 1 << AWIDTH;

input _OE, _WE;
input [AWIDTH-1:0] A;
inout [DWIDTH-1:0] D;
 
reg [DWIDTH-1:0] mem [DEPTH-1:0];
logic [7:0] dout;

assign D = dout;

// always @*
//     $monitor("RAM : _OE=%1b, _WE=%1b, addr=%8b, d=%8b, m0=%8b, m1=%8b, m2=%8b",_OE, _WE, addr, d, mem[0], mem[1], mem[2]);

  always @(_WE or _OE)
   begin
    // NOTE: 6116 and 62256 RAM datasheet says _WE overrides _OE but I want to detect this unexpected situation
     if (!_WE && !_OE) begin
       $display("ALERT - RAM cannot be _OE and _WE simultaneously - WRITE WINS - RAM[0x%04x]=0x%02x", A, D );
       // $finish;
     end
   end

  always @(_WE or D or A)
   begin
     if (!_WE) begin
        dout = {DWIDTH{1'bz}};
        mem[A] = D;
     end
   end

  always @(_OE or A)
   begin
      if (!_OE && _WE) 
        dout = mem[A];

        //if (dout == 8'bxxxxxxxx) dout=99; // MARKER VALUE FOR UNASSIGNED - TEMPORARY
   end

  integer i;
  initial begin
    for(i=0;i<DEPTH;i=i+1)
       mem[i]={DWIDTH{1'bx}};
  end

endmodule

`endif
