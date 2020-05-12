

/* verilator lint_off ASSIGNDLY */
/* verilator lint_off STMTDLY */


`include "../lib/assertion.v"
`include "./hct74139.v"
`timescale 1ns/1ns

module tb();

reg _Ea=1'bx;
reg _Eb;
reg [1:0] Aa;
reg [1:0] Ab;
wire [3:0] _Ya;
wire [3:0] _Yb;

    always @*
        $display($time, " TEST>   _Ea=%1b", _Ea, " Aa=%2b", Aa, " _Ya=%4b", _Ya);
     
    integer timer;

    hct74139 #(.LOG(1)) demux(
                    ._Ea,
                    ._Eb,
                    .Aa,
                    .Ab,
                    ._Ya,
                    ._Yb
                    );

    initial begin
      

        $display("initial");
        `Equals(_Ya, 4'bx)

        $display("setting address=0");
        Aa <= 0;
        #400
        `Equals(_Ya, 4'bx)

        $display("disable");
        _Ea <= 1;
        #40
        `Equals(_Ya, 4'b1111)

        $display("enable");
        _Ea <= 0;
        #40
        `Equals(_Ya, 4'b1110)

        $display("address=1");
        Aa <= 1;
        #400
        `Equals(_Ya, 4'b1101)

        $display("address=2");
        Aa <= 2;
        #400
        `Equals(_Ya, 4'b1011)

        $display("address=3");
        Aa <= 3;
        #400
        `Equals(_Ya, 4'b0111)

        $display("disable");
        _Ea <= 1;
        #40
        `Equals(_Ya, 4'b1111)
        
        $display("enable output");
        timer=$time;
        _Ea <= 0; // b->a
        wait(_Ya === 4'b0111);
        if ($time - timer < 10) 
            $display("TOO QUICK - EXPECTED 16ns - TOOK %-d", ($time - timer));
        else
            $display("TOOK %-d", ($time - timer));
        
        #50

        $display("change address");
        timer=$time;
        Aa <= 0; // b->a
        wait(_Ya === 4'b1110);
        if ($time - timer < 24) 
            $display("TOO QUICK - EXPECTED 16ns - TOOK %-d", ($time - timer));
        else
            $display("TOOK %-d", ($time - timer));

      #50

        $display("done");
        $finish;
    end

endmodule : tb

