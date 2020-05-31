
`include "./control.v"
`include "../lib/assertion.v"
`include "../lib/display.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    display d();

    logic phaseFetch, phaseDecode, phaseExec, _phaseFetch;
    logic [2:0] ctrl;

    wire _addrmode_register, _addrmode_pc, _addrmode_immediate;

    address_mode_decoder #(.LOG(0)) amode_decoder( .ctrl, 
                                .phaseFetch, .phaseDecode, .phaseExec, ._phaseFetch, 
                                ._addrmode_pc, ._addrmode_register, ._addrmode_immediate);

    localparam T=100;   // clock cycle
    localparam SETTLE_TOLERANCE=20;

    assign #(50) _phaseFetch = ! phaseFetch;
    

    initial begin
        `ifndef verilator
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
        `endif
    end

    always @* 
            $display("%9t ", $time,
                "TEST ctrl=%3b", ctrl, 
                " phaseFetch=%b, phaseDecode=%b, phaseExec=%b,    _phaseFetch=%b", phaseFetch, phaseDecode, phaseExec, _phaseFetch,
                "     addrmode PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_immediate,
                " amode=%-3s", `DECODE_ADDRMODES,
                "   : %-s", d.label
                );

   
    // constraints
    always @* begin
        if ($time > T) begin
         // only one may be high at a time
         if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_immediate === 1'bx) begin
            $display("%9t ", $time, "!!!! ERROR INDETERMINATE AMODE  PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_immediate);
            #SETTLE_TOLERANCE
             if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_immediate === 1'bx) begin
                $display("-- ABORT");
                 $finish();
            end
         end
         if ( _addrmode_register + _addrmode_pc + _addrmode_immediate < 2) begin
            $display("%9t ", $time, "!!!! ERROR CONFLICTING AMODE  PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_immediate);
            #SETTLE_TOLERANCE
             if ( _addrmode_register + _addrmode_pc + _addrmode_immediate < 2) begin
                $display("-- ABORT");
                 $finish();
             end
         end
         end
    end

   
    wire [2:0] _addr_mode = {_addrmode_pc, _addrmode_register, _addrmode_immediate}; 
    
    initial begin

        d.display("fetch forces addrmode PC");
        ctrl = 3'b1xx;
        phaseFetch = 1;
        phaseDecode = 0;
        phaseExec = 0;
        #T
        `Equals( _addr_mode, 3'b011)

        d.display("fetch forces addrmode PC");
        ctrl = 3'b0xx;
        phaseFetch = 1;
        phaseDecode = 0;
        phaseExec = 0;
        #T
        `Equals( _addr_mode, 3'b011)

        d.display("decode sets addrmode IMM or REG depending on top bit of rom");
        ctrl = 3'b1xx;
        phaseFetch = 0;
        phaseDecode = 1;
        phaseExec = 0;
        #T
        `Equals( _addr_mode, 3'b110)

        d.display("decode sets addrmode IMM or REG depending on top bit of rom");
        ctrl = 3'b0xx;
        phaseFetch = 0;
        phaseDecode = 1;
        phaseExec = 0;
        #T
        `Equals( _addr_mode, 3'b101)

        d.display("exec sets addrmode IMM or REG depending on top bit of rom");
        ctrl = 3'b1xx;
        phaseFetch = 0;
        phaseDecode = 0;
        phaseExec = 1;
        #T
        `Equals( _addr_mode, 3'b110)

        d.display("exec sets addrmode IMM or REG depending on top bit of rom");
        ctrl = 3'b0xx;
        phaseFetch = 0;
        phaseDecode = 0;
        phaseExec = 1;
        #T
        `Equals( _addr_mode, 3'b101)

        d.display("fetch sets addrmode PC");
        ctrl = 3'b0xx;
        phaseFetch = 1;
        phaseDecode = 0;
        phaseExec = 0;
        #T
        `Equals( _addr_mode, 3'b011)


        $display("testing end");
    // ===========================================================================

//`include "./generated_tests.v"


    end

endmodule : test
