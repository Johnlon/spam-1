`timescale 1ns/1ns
`include "../lib/assertion.v"
`include "./sr.v"


module test();
    logic s,r;
    wire q, _q;

    sr sr1(.r, .s, .q, ._q);

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );
        $monitor("%9t", $time, " s=", s, " r=", r, " q=", q, " _q=", _q);

        $display("s=r=0");
        s <= 0;
        r <= 0;
        #100
        `Equals({q, _q}, 2'bxx)

        $display("s=r=1");
        s <= 1;
        r <= 1;
        #100
        `Equals({q, _q}, 2'b00)

        $display("hold after s=r=1 - will oscillate if there are nonzero propdelays");
        s <= 0;
        r <= 0;
        #100
        // CANNOT MAKE MAKE VALID ASSERTION

        $display("set");
        s <= 1;
        r <= 0;
        #100
        `Equals({q, _q}, 2'b10)

        $display("hold");
        s <= 0;
        r <= 0;
        #100

        $display("reset");
        s <= 0;
        r <= 1;
        #100
        `Equals({q, _q}, 2'b01)

        $display("hold");
        s <= 0;
        r <= 0;
        #100
        `Equals({q, _q}, 2'b01)

        $display("set");
        s <= 1;
        r <= 0;
        #100
        `Equals({q, _q}, 2'b10)

        $display("hold");
        s <= 0;
        r <= 0;
        #100
        `Equals({q, _q}, 2'b10)

        $display("reset");
        s <= 0;
        r <= 1;
        #100
        `Equals({q, _q}, 2'b01)

        $display("hold");
        s <= 0;
        r <= 0;
        #100
        `Equals({q, _q}, 2'b01)

        $display("both high");
        s <= 1;
        r <= 1;
        #100
        `Equals({q, _q}, 2'b00)

        #100;
        $finish; 

    end

endmodule
