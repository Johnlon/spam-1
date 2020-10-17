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
    logic system_clk;
    
    wire _mrPos, _mrNeg, clk;

    reset RESET(
        ._RESET_SWITCH,
        .system_clk,
        .clk,
        ._mrPos,
        ._mrNeg
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
        .clk(clk),
        ._MR(_mrNeg),
        ._long_jump(_do_jmp),  // load both
        ._local_jump(_pclo_in), // load lo
        ._pchitmp_in(_pchitmp_in), // load tmp
        .D(pc_din),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );


    integer count;

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        system_clk=1;
        #GAP
        `Equals(_mrPos, 'x);

        _RESET_SWITCH = 1;
        `Equals(pc, 'x);

        $display("if reset is NOT pressed then a +ve clock unsets resetff");
        system_clk=1;
        #GAP
        `Equals(_mrPos, 'x);
        `Equals(pc, 'x);

        system_clk=0;
        #GAP
        `Equals(_mrPos, 'x);
        `Equals(pc, 'x);

        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);    // flip to 1 because of +ve edge
        `Equals(pc, 'x);

        $display("while resetff clear then phase tracks clock");
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 'x);
        
        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 'x);
        
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 'x);
        
        
        $display("reset button asynchronously sets  _mrPos=0 ");
        // during high phase
        system_clk=1;
        #(GAP/2)
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 'x);

        // _mrPos async reset
        _RESET_SWITCH=0;
        #(GAP/2) // async PD
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        $display("_mrPos stays 0 while _RESET held, but PC resets to 0 on first +ve clock");
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        $display("PC stays 0 while _mrPos=0");
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        system_clk=1; 
        #(GAP/2)
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        $display("release RESET - mid cycle clock high");
        _RESET_SWITCH=1;
        #(GAP/2)
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        $display("released RESET - _mrPos remains held during low cycle");
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b0);
        `Equals(pc, 'x);

        $display("released RESET - _mrPos released on +ve clk, PC remains 0");
        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, '0);

        $display("clk -ve edge , PC stays as 0");
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd0);

        $display("clk +ve edge , PC increments");
        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd1);
        
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd1);

        $display("clk +ve edge , PC increments");
        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd2);
        
        system_clk=0;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd2);
        
        $display("clk +ve edge , PC increments");
        system_clk=1;
        #GAP
        `Equals(_mrPos, 1'b1);
        `Equals(pc, 16'd3);
        

        $display("reset again");
        _RESET_SWITCH=0;
        system_clk=0;
        #(GAP/2)
        $display("release RESET mid cycle clock low");
        _RESET_SWITCH=1;
        #(GAP/2)

        system_clk=1;
        #GAP

        $display("free run clock");
        for (count=4; count>0; count--) begin
            system_clk=0;
            #GAP
            system_clk=1;
            #GAP
            system_clk=1;
        end

        system_clk=0;
        #GAP
        system_clk=1;
        #GAP
        system_clk=1;
       
        `Equals(pc, 16'd5);
    end

    always @* $display("%9t ", $time, "      _mrPos %1b  ", _mrPos);
    always @* $display("%9t ", $time, "       pc %04h ", pc);

endmodule 
