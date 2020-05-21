
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control.v"
`include "../pc/pc.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

   `include "../lib/display_snippet.v"

    logic clk;
    logic _mr;
    
    wire #8 _clk = ! clk; // GATE + PD

    wire phaseFetch, _phaseFetch;
    logic [7:0] hi_rom;

    wire [7:0]  alu_result_bus;

    tri0 [15:0] address_bus;
    wire _addrmode_register, _addrmode_pc, _addrmode_direct;

    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;

    wire [4:0] aluop;

    wire [2:0] _addr_mode = {_addrmode_pc, _addrmode_register, _addrmode_direct}; 
    wire [9:0] seq;

	control #(.LOG(1)) ctrl( .clk, ._mr, .hi_rom, .seq, .phaseFetch, ._phaseFetch, ._addrmode_pc, ._addrmode_register, ._addrmode_direct, .rbus_dev, .lbus_dev, .targ_dev, .aluop);

    wire _pclo_in=1;
    wire _pc_in=1;
    wire _pchitmp_in=1;
    wire [7:0] PCHI, PCLO; // output of PC

    localparam T=25;
    localparam SETTLE=50;

    pc #(.LOG(1))  PC (
        .clk(_mr & phaseFetch),
        ._clk(_phaseFetch),
        ._MR(_mr),
        ._pc_in(_pc_in),
        ._pclo_in(_pclo_in),
        ._pchitmp_in(_pchitmp_in),
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    
    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $monitor ("%9t ", $time,  "TEST clk=%1b _mr=%2b hi_rom=%08b", clk, _mr, hi_rom, 
                " seq=%10b", seq,
                " addr(prd=%1b%1b%1b)", _addrmode_pc, _addrmode_register, _addrmode_direct, 
                " rbus=%04b lbus=%04b targ=%05b aluop=%05b", rbus_dev, lbus_dev, targ_dev, aluop,
                " PC %02h:%02h", PCHI, PCLO,
                " %s", label
                );

        `endif
    end

    integer count;
    integer p1count=3;
    integer p2count=5;

    initial begin
        localparam T=100;   // clock cycle
        localparam SMALL_DELAY=20; // come gate delay

        `DISPLAY("init")
        hi_rom <= 8'b00000000;
        _mr <= 0;
        clk <= 0;

        #T
        `Equals( phaseFetch, 1'b0) // seq=1 isn't in fetch

        `Equals( seq, 10'b1)
        `Equals( _addr_mode, 3'b111)

        `Equals(PCHI, 8'b0)
        `Equals(PCLO, 8'b0)

        #T
        `DISPLAY("_mr no clocking is ineffective = stay in NONE addressing mode")
        hi_rom <= 8'b00000000;
        count = 0;
        while (count++ < 3) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b111)

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b111)
        end

        `Equals(PCHI, 8'b0)
        `Equals(PCLO, 8'b0)
        

        `DISPLAY("_mr released : still in NONE addressing mode after settle and PC=0")
        _mr <= 1;
        #T
        `Equals( _addr_mode, 3'b111);
        `Equals(PCHI, 8'b0)
        `Equals(PCLO, 8'b0)


        `DISPLAY("stay in PC addressing mode for 3 clocks with PC=0")
        count = 0;
        while (count++ < p1count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b011)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b0)

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b011)
        end


        `DISPLAY("enter in REG addressing mode for 6 clocks - Note op[7]=0 so address mode = REGISTER")
        hi_rom <= 8'b00000000;
        count = 0;
        while (count++ < p2count) begin
            $display("count ", count);
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b101)

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b101)
        end


        `DISPLAY("while in REG addressing mode if op[7]=1 then address mode = IMMEDIATE")
        hi_rom <= 8'b10000000;
        #SMALL_DELAY
        `Equals( _addr_mode, 3'b110)


        `DISPLAY("enter clk10 NOOP mode")
        clk <= 1;
        #T
        `Equals( _addr_mode, 3'b111)
        `Equals( seq, 10'b1000000000)
        clk <= 0;
        #T

        `DISPLAY("return to clk0 NOOP state")
        clk <= 1;
        #T
        `Equals( seq, 10'b0000000001)
        `Equals( _addr_mode, 3'b111)
        clk <= 0;
        #T

        `DISPLAY("return to PC state")
        clk <= 1;
        #T
        `Equals( seq, 10'b0000000010)
        `Equals( _addr_mode, 3'b011)
        clk <= 0;
        #T

        `DISPLAY("PC=1")
        `Equals(PCHI, 1'h0)
        `Equals(PCLO, 1'h1)

        #T
        $display("testing end");
    // ===========================================================================

//`include "./generated_tests.v"


end

endmodule : test
