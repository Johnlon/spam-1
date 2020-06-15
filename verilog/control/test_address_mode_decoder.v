
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

    wire _addrmode_register, _addrmode_pc, _addrmode_direct;

    memory_address_mode_decoder #(.LOG(0)) amode_decoder( .ctrl, 
                                .phaseFetch, .phaseDecode, .phaseExec, ._phaseFetch, 
                                ._addrmode_pc, ._addrmode_register, ._addrmode_direct);

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
                "     addrmode PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_direct,
                " amode=%-3s", `DECODE_ADDRMODES,
                "   : %-s", d.label
                );

   
    // constraints
    always @* begin
        if ($time > T) begin
         // only one may be high at a time
         if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_direct === 1'bx) begin
            $display("%9t ", $time, "!!!! WARNING INDETERMINATE AMODE  PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_direct);
            #SETTLE_TOLERANCE
             if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_direct === 1'bx) begin
                $display("INDETERMINATE STATE PERSISTED -- ABORT");
                 $finish();
            end
            else
                $display("%9t ", $time, "!!!! RESOLVED INDETERMINATE AMODE  PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_direct);

         end
         if ( _addrmode_register + _addrmode_pc + _addrmode_direct < 2) begin
            $display("%9t ", $time, "!!!! WARNING CONFLICTING AMODE  PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_direct);
            #SETTLE_TOLERANCE
             if ( _addrmode_register + _addrmode_pc + _addrmode_direct < 2) begin
                $display("CONFLICT PERSISTED -- ABORT");
                 $finish();
             end
            else
                $display("%9t ", $time, "!!!! RESOLVED CONFLICTING AMODE PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_direct);
         end
         end
    end

   
    wire [2:0] _addr_mode = {_addrmode_pc, _addrmode_register, _addrmode_direct}; 
    
    integer count;

    initial begin

        d.display("fetch forces addrmode PC");
        for (count = 0; count < 8; count++) begin
            d.display("fetch forces addrmode PC");
            $display("fetch forces addrmode PC, op=%d", count);
            ctrl = count;
            phaseFetch = 1;
            phaseDecode = 0;
            phaseExec = 0;
            #T
            `Equals( _addr_mode, 3'b011)
        end

        for (count = 0; count < 2; count ++)
        begin
            if (count == 0) begin
                d.display("decode tests");
                $display("decode tests, op=%d", count);
                phaseFetch = 0;
                phaseDecode = 1;
                phaseExec = 0;
            end 
            else 
            begin
                d.display("exec tests");
                $display("exec tests, op=%d", count);
                phaseFetch = 0;
                phaseDecode = 0;
                phaseExec = 1;
            end
            
            d.display("op 0=reg");
            ctrl = 0;
            #T
            `Equals( _addr_mode, 3'b101)

            d.display("op 1=reg");
            ctrl = 1;
            #T
            `Equals( _addr_mode, 3'b101)

            d.display("op 2=reg");
            ctrl = 2;
            #T
            `Equals( _addr_mode, 3'b101)

            d.display("op 3=not defined yet");
            ctrl = 3;
            #T

            d.display("op 4=dir");
            ctrl = 4;
            #T
            `Equals( _addr_mode, 3'b110)

            d.display("op 5=dir");
            ctrl = 5;
            #T
            `Equals( _addr_mode, 3'b110)

            d.display("op 6=dir");
            ctrl = 6;
            #T
            `Equals( _addr_mode, 3'b110)

            d.display("op 7=not defined yet");
            ctrl = 7;
            
        end


        $display("testing end");
    // ===========================================================================

//`include "./generated_tests.v"


    end

endmodule : test
