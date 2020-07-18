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

    parameter [DWIDTH-1:0] UNDEF = {(DWIDTH/4){4'bxzzx}};
    parameter [DWIDTH-1:0] HIZ = {DWIDTH{1'bz}};

    reg [DWIDTH-1:0] Mem [DEPTH-1:0];

    localparam t_a_a = 70;
    localparam t_dis_w = 25;
    localparam t_en_w = 0;
    localparam t_dis_g = 25;
    localparam t_en_g = 0;
    wire #(t_en_w, t_dis_w) _delayedWE = _WE;
    wire #(t_dis_g, t_en_g) _delayedOE = _OE;
    wire [AWIDTH-1:0] delayedA;
    assign #(t_a_a) delayedA = A;

    //assign D=!_WE? HIZ: _OE? HIZ: Mem[A];
    assign D=!_delayedWE? HIZ: _delayedOE ? HIZ: Mem[delayedA];

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

  if (LOG) begin
    always @(*) begin
        // NOTE: 6116 and 62256 RAM datasheet says _WE overrides _OE but I want to detect this unexpected situation
        if (!_WE && !_OE) begin
           $display("%9t", $time, " ALERT - RAM _OE and _WE simultaneously - WRITE WINS - RAM[0x%04x]=%08b", A, D );
        end
    end
  end

  if (LOG)
    always @(Mem[A]) begin
        if (!_WE) $display("%9t ", $time, "RAM : UPDATE - RAM[0x%04x]=%08b     Mem[A]=%02h", A, D , Mem[A]);
  end

  always @(delayedA)
     $display("%9t ", $time, "RAM : DETAIL _OE=%1b _delayedOE=%1b _WE=%1b _delayedWE=%1b A=%04x delayedA=%04h D=%8b m0=%8b m1=%8b m2=%8b", 
                    _OE, _delayedOE, _WE, _delayedWE, A, delayedA, D, Mem[0], Mem[1], Mem[2]);

  always @(_WE or _OE or D or A or delayedA)
  begin
     if (!_WE) begin
        Mem[A] = D;
        if (LOG) $display("%9t ", $time, "RAM : WRITE - RAM[0x%04x]=%08b     Mem[A]=%02h", A, D , Mem[A]);
     end
     else if (!_OE) begin
        if (LOG) $display("%9t ", $time, "RAM : READ - D=%08b  A=%04h  delayed : %08d=RAM[0x%04x]", D, A, Mem[delayedA], delayedA);

        if (LOG && Mem[delayedA] === UNDEF) begin
           $display("%9t", $time, " RAM ALERT - READING UNINITIALISED VALUE AT RAM[0x%04x]=%08b A=%04h", delayedA, Mem[delayedA], A );
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
