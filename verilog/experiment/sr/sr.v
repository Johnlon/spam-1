`timescale 1ns/1ns
`include "../lib/assertion.v"


// going straight from 11 to 00 will cause oscillation
module SR_latch_gate_as (input R, input S, output Q, output _Q);
   assign  #(5) Q = ! (R | _Q);
   assign  #(5)_Q = ! (S | Q);
endmodule 

module SR_latch_gate_T (input R, input S, output Q, output _Q);
    nor #(5) (Q, R, _Q);
    nor #(5) (_Q, S, Q);
endmodule 

// runs with no propagation delay and doesn't oscillate but drops to q/_q=01 due to simulation
module SR_latch_gate (input R, input S, output Q, output _Q);
    nor (_Q, S, Q);
    nor (Q, R, _Q);
endmodule 

module test();
   logic s,r;
   wire q, _q;

//  SR_latch_gate_as sr1(.R(r), .S(s), .Q(q), ._Q(_q));
  SR_latch_gate_T sr1(.R(r), .S(s), .Q(q), ._Q(_q));

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
