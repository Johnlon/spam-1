`ifndef RAM_V
`define RAM_V

`timescale 1ns/1ns

// verilator lint_off UNOPTFLAT
module ram(_OE, _WE, A, D);

    input _OE, _WE;
    input [AWIDTH-1:0] A;
    inout [DWIDTH-1:0] D;

    parameter DWIDTH=8,AWIDTH=16, DEPTH= 1 << AWIDTH;
    parameter LOG=0;

    parameter [DWIDTH-1:0] UNDEF = {(DWIDTH/2){2'bxz}};
    parameter [DWIDTH-1:0] HIZ = {DWIDTH{1'bz}};

    reg [DWIDTH-1:0] Mem [DEPTH-1:0];

    assign D=!_WE? HIZ: _OE? HIZ: Mem[A];

/*
  if (LOG) begin
    always @(negedge _WE) begin
        if (!_OE) $display("%9t ", $time, "RAM : END READ _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
        $display("%9t ", $time, "RAM : BEGIN WRITE _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
    end
    always @(posedge _WE) begin
        $display("%9t ", $time, "RAM : END WRITE   _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
        if (!_OE) $display("%9t ", $time, "RAM : BEGIN READ _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
    end

    always @(negedge _OE) begin
        if (_WE) $display("%9t ", $time, "RAM : BEGIN READ _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
    end

    always @(posedge _OE) begin
        if (_WE) $display("%9t ", $time, "RAM : END READ   _OE=%1b _WE=%1b A=%04x D=%8b m0=%8b m1=%8b m2=%8b", _OE, _WE, A, D, Mem[0], Mem[1], Mem[2]);
    end
end
*/

  if (LOG) begin
    always @(*) begin
        // NOTE: 6116 and 62256 RAM datasheet says _WE overrides _OE but I want to detect this unexpected situation
        if (!_WE && !_OE) begin
           $display("%9t", $time, " ALERT - RAM _OE and _WE simultaneously - WRITE WINS - RAM[0x%04x]=%08b", A, D );
        end
    end
  end

  always @(_WE or _OE or D or A)
  begin
     if (!_WE) begin
        Mem[A] = D;
        if (LOG) $display("%9t ", $time, "RAM : WRITE - RAM[0x%04x]=%08b     Mem[A]=%02h", A, D , Mem[A]);
     end
     else if (!_OE) begin
        //dout = Mem[A];
        if (LOG) $display("%9t ", $time, "RAM : READ - %08b=RAM[0x%04x]", D, A );

        if (LOG && Mem[A] === UNDEF) begin
           $display("%9t", $time, " RAM ALERT - READING UNINITIALISED VALUE AT RAM[0x%04x]=%08b", A, Mem[A] );
        end
     end
  end

  integer i;
  initial begin
    for(i=0;i<DEPTH;i=i+1)
       Mem[i]=UNDEF;
  end

endmodule

`endif
