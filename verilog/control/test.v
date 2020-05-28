
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control.v"
`include "../pc/pc.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

   `include "../lib/display_snippet.v"
   `include "decoding.v"
    `DECODE_PHASE
    `DECODE_ADDRMODE

    logic clk;
    logic _mr;

    function [3*8-1:0] sAddrMode(); begin
         sAddrMode = !_addrmode_pc ? "pc" : !_addrmode_register?  "reg" : !_addrmode_immediate? "imm": "---";
        end
    endfunction
    
    wire #8 _clk = ! clk; // GATE + PD

    logic phaseFetch, phaseDecode, phaseExec, _phaseFetch;
    logic [2:0] ctrl;

    wire _addrmode_register, _addrmode_pc, _addrmode_immediate;

    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;

    wire [4:0] aluop;

    wire [2:0] _addr_mode = {_addrmode_pc, _addrmode_register, _addrmode_immediate}; 

	control #(.LOG(0)) ctrl_logic( .clk, ._mr, .ctrl, 
                                .phaseFetch, .phaseDecode, .phaseExec, ._phaseFetch, 
                                ._addrmode_pc, ._addrmode_register, ._addrmode_immediate, 
                                .rbus_dev, .lbus_dev, .targ_dev, .aluop);
    wire _pclo_in=1;
    wire _pc_in=1;
    wire _pchitmp_in=1;

    localparam T=100;   // clock cycle
    localparam SETTLE_TOLERANCE=20;

    assign #(50) _phaseFetch = ! phaseFetch;
    

    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);


        `endif
    end

    `define DUMP \
        $display ("%9t ", $time,  "TEST clk=%1b _mr=%1b ctrl=%3b", clk, _mr, ctrl, \
                " phaseFetch=%b, phaseDecode=%b, phaseExec=%b,    _phaseFetch=%b", phaseFetch, phaseDecode, phaseExec, _phaseFetch,\
                "     addrmode PRI %b/%b/%b", _addrmode_pc, _addrmode_register, _addrmode_immediate,\
                " amode=%-3s", sAddrMode(),\
                "   : %-s", label\
                );

    always @* `DUMP

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


    initial begin

        `DISPLAY("fetch sets addrmode PC")
        ctrl <= 3'b1xx;
        phaseFetch <= 1;
        phaseDecode <= 0;
        phaseExec <= 0;
        #T
        //`Equals( _addr_mode, 3'b0xx)

        `DISPLAY("fetch sets addrmode PC")
        ctrl <= 3'b0xx;
        phaseFetch <= 1;
        phaseDecode <= 0;
        phaseExec <= 0;
        #T
        `Equals( _addr_mode, 3'b011)

        `DISPLAY("decode sets addrmode IMM or REG depending on top bit of rom")
        ctrl <= 3'b1xx;
        phaseFetch <= 0;
        phaseDecode <= 1;
        phaseExec <= 0;
        #T
        `Equals( _addr_mode, 3'b110)

        `DISPLAY("decode sets addrmode IMM or REG depending on top bit of rom")
        ctrl <= 3'b0xx;
        phaseFetch <= 0;
        phaseDecode <= 1;
        phaseExec <= 0;
        #T
        `Equals( _addr_mode, 3'b101)

        `DISPLAY("exec sets addrmode IMM or REG depending on top bit of rom")
        ctrl <= 3'b1xx;
        phaseFetch <= 0;
        phaseDecode <= 0;
        phaseExec <= 1;
        #T
        `Equals( _addr_mode, 3'b110)

        `DISPLAY("exec sets addrmode IMM or REG depending on top bit of rom")
        ctrl <= 3'b0xx;
        phaseFetch <= 0;
        phaseDecode <= 0;
        phaseExec <= 1;
        #T
        `Equals( _addr_mode, 3'b101)

        `DISPLAY("fetch sets addrmode PC")
        ctrl <= 3'b0xx;
        phaseFetch <= 1;
        phaseDecode <= 0;
        phaseExec <= 0;
        #T
        `Equals( _addr_mode, 3'b011)


        $display("testing end");
    // ===========================================================================

//`include "./generated_tests.v"


    end

endmodule : test
