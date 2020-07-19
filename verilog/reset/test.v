`include "../pc/pc.v"
`include "../lib/assertion.v"
`include "reset.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns


// "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
module test(
);

    parameter LOG=0;
    parameter GAP=100;

    logic _RESET_SWITCH;
    logic clk;
    
    wire _mr;
    wire phase_clk;
    wire _reset_pc;


    reset RESET(
        ._RESET_SWITCH,
        .clk,
        ._mr,
        .phase_clk,
        ._reset_pc
    );

    ///////////////////// PROGRAM COUNTER

    wire [7:0] pc_din = 8'haa;
    wire [7:0] PCHI, PCLO; // output of PC
    wire [15:0] pc = {PCHI, PCLO}; 

    logic _do_jmp = 1;
    logic _pclo_in = 1;
    logic _pchitmp_in = 1;
    
    // PC reset is sync with +ve edge of clock
    pc #(.LOG(0))  PC (
        .clk(phase_clk),
        ._MR(_reset_pc),
        ._pc_in(_do_jmp),  // load both
        ._pclo_in(_pclo_in), // load lo
        ._pchitmp_in(_pchitmp_in), // load tmp
        .D(pc_din),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );


    integer count;

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        clk=1;
        #GAP
        `Equals(_mr, 'x);

        _RESET_SWITCH = 1;
        `Equals(pc, 'x);

        
        $display("if reset is NOT pressed then a +ve clock unsets resetff");
        clk=1;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 'x);
        `Equals(phase_clk, 'x);
        `Equals(_reset_pc, 'x);

        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 'x);
        `Equals(phase_clk, 1'b0); // 0 because of AND with clk
        `Equals(_reset_pc, 'x);

        clk=1;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b1);    // flip to 1 because of +ve edge
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, 'x);

        $display("while resetff clear then phase tracks clock");
        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0); // clk tracking
        `Equals(_reset_pc, 'x);
        
        clk=1;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b1); // clk tracking
        `Equals(_reset_pc, '1);
        
        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0); // clk tracking
        `Equals(_reset_pc, '1);
        
        
        $display("reset button asynchronously clears resetff._mr=0 and clears the phaseff");
        // during high phase
        clk=1;
        #(GAP/2)
        `Equals(pc, 'x);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, '1);

        // _mr and phase_clk are async reset
        _RESET_SWITCH=0;
        #(GAP/2) // async PD
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        $display("reset _mr=0 and and phaseff stay reset while clocking");
        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        clk=1;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        clk=1; 
        #(GAP/2)
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        $display("release RESET - mid cycle clock high");
        _RESET_SWITCH=1;
        #(GAP/2)
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        $display("release RESET - reset held during low cycle");
        clk=0;
        #GAP
        `Equals(pc, 'x);
        `Equals(_mr, 1'b0);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);

        $display("release RESET - reset released on +ve clk");
        clk=1;
        #25
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, 1'b0);
        `Equals(pc, 'x);

        $display("_mr is released and clock has gone high so a small delay later phase_clk responds to the +ve edge");
        #10 
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, '0);

        $display("_reset_pc is low and phase_clk goes high which loads PC=0");
        #(GAP-(25+10)) // _reset_pc stays low waiting for clk
        `Equals(_reset_pc, '0);
        `Equals(pc, 16'd0);

        $display("clk -ve edge , PC stays as 0");
        clk=0;
        #GAP
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '0);
        `Equals(pc, 16'd0);

        $display("clk +ve edge , _reset_pc is cleared");
        clk=1;
        #GAP
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, '1); // +clk clears _reset_pc
        `Equals(pc, 16'd0);
        
        $display("_reset_pc is cleared so next cycle increments the PC");
        clk=0;
        #GAP
        `Equals(pc, 16'd0);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '1);

        clk=1;
        #GAP
        `Equals(pc, 16'd1);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, '1);
        
        clk=0;
        #GAP
        `Equals(pc, 16'd1);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b0);
        `Equals(_reset_pc, '1);
        
        clk=1;
        #GAP
        `Equals(pc, 16'd2);
        `Equals(_mr, 1'b1);
        `Equals(phase_clk, 1'b1);
        `Equals(_reset_pc, '1);
        

        $display("free run reset");
        _RESET_SWITCH=0;
        clk=0;
        #(GAP/2)
        $display("release RESET - mid cycle clock low");
        _RESET_SWITCH=1;
        #(GAP/2)

        clk=1;
        #GAP

        count=4;
        while (count-->0) begin
            clk=0;
            #GAP
            clk=1;
            #GAP
            clk=1;
        end

        clk=0;
        #GAP
        clk=1;
        #GAP
        clk=1;
       
        `Equals(pc, 16'd4);
    end

    always @* $display("%9t ", $time, "      _mr %1b  ", _mr);
    always @* $display("%9t ", $time, "phase_clk %1b  ", phase_clk);
    always @* $display("%9t ", $time, "_reset_pc %1b  ", _reset_pc);
    always @* $display("%9t ", $time, "       pc %04h ", pc);

endmodule 
