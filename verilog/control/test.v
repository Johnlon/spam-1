
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    reg [60*8:0] label;
    `define DISPLAY(x) label=x; $display("\n>>>> %-s", label);

    logic clk;
    logic _mr;

    logic [7:0] hi_rom;

    wire _addrmode_register, _addrmode_pc, _addrmode_direct;

    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;

    wire [4:0] aluop;

    wire [2:0] _addr_mode = {_addrmode_pc, _addrmode_register, _addrmode_direct};

	control_selector #(.LOG(1)) ctrl( .clk, ._mr, .hi_rom, ._addrmode_pc, ._addrmode_register, ._addrmode_direct, .rbus_dev, .lbus_dev, .targ_dev, .aluop);
    

    initial begin

        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        $monitor ("%9t ", $time,  "clk=%1b _mr=%2b hi_rom=%08b", clk, _mr, hi_rom, 
                " addr(prd=%1b%1b%1b)", _addrmode_pc, _addrmode_register, _addrmode_direct, 
                " rbus=%04b lbus=%04b targ=%05b aluop=%05b", rbus_dev, lbus_dev, targ_dev, aluop,
                " %s", label
                );

        `endif
    end

    integer count;
    integer p1count=3;
    integer p2count=4;

    initial begin
        localparam T=100;   // clock cycle
        localparam SMALL_DELAY=20; // come gate delay

        `DISPLAY("init");
        hi_rom <= 8'b00000000;
        _mr <= 0;
        clk <= 0;

        `DISPLAY("_mr so no clocking = stay in PC mode")
        #T
        hi_rom <= 8'b00000000;
        count = 10*2;
        while (count -- > 0) begin
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b011);

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b011);
        end
        
        `DISPLAY("_mr released = still in PC mode after settle")
        _mr <= 1;
         #T
         `Equals( _addr_mode, 3'b011);

        `DISPLAY("stay in PC mode for 3 clocks")
        count = 0;
        while (count++ < p1count) begin
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b011);

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b011);
        end
        

        `DISPLAY("stay in REG mode for 4 clocks = - op[7]=0 so address mode = REGISTER")
        hi_rom <= 8'b00000000;
        count = 0;
        while (count++ < p2count) begin
            clk <= 1;
            #T
            `Equals( _addr_mode, 3'b101);

            clk <= 0;
            #T
            `Equals( _addr_mode, 3'b101);
        end

        `DISPLAY("stay in REG mode for 4 clocks = - op[7]=0 so address mode = REGISTER")
        hi_rom <= 8'b00000000;
        clk <= 1;
        #T
        `Equals( _addr_mode, 3'b111);
        clk <= 0;
        #T
        `Equals( _addr_mode, 3'b101);

        `DISPLAY("if op[7]=1 address mode = IMMEDIATE")
        hi_rom <= 8'b10000000;
        #SMALL_DELAY
        `Equals( _addr_mode, 3'b110);

        `DISPLAY("next clock - EXECUTE - addressing should stay stable during exec")
        hi_rom <= 8'b10000000;
        clk <= 1;
        #T
        `Equals( _addr_mode, 3'b110);
        clk <= 0;
        #T
        `Equals( _addr_mode, 3'b110);

        
/*
        parameter pad6      = 6'b000000;
        parameter pad5      = 5'b00000;
        parameter pad4      = 4'b0000;
        
        // all routes to belect
        parameter [2:0] op_DEV_eq_ROM_sel = 0;
        parameter [2:0] op_DEV_eq_RAM_sel = 1;
        parameter [2:0] op_DEV_eq_RAMZP_sel = 2;
        parameter [2:0] op_DEV_eq_UART_sel = 3;
        parameter [2:0] op_NONREG_eq_OPREGY_sel = 4;
        parameter [2:0] op_REGX_eq_ALU_sel = 5;
        parameter [2:0] op_RAMZP_eq_REG_sel = 6;
        parameter [2:0] op_RAMZP_eq_UART_sel = 7;

        // because MSB
        parameter [4:0] idx_RAM_sel      = 0;
        parameter [4:0] idx_MARLO_sel    = 1;
        parameter [4:0] idx_MARHI_sel    = 2;
        parameter [4:0] idx_UART_sel     = 3;
        parameter [4:0] idx_PCHITMP_sel  = 4;
        parameter [4:0] idx_PCLO_sel     = 5;
        parameter [4:0] idx_PC_sel       = 6;
        parameter [4:0] idx_JMPO_sel     = 7;

        parameter [4:0] idx_JMPZ_sel     = 8;
        parameter [4:0] idx_JMPC_sel     = 9;
        parameter [4:0] idx_JMPDI_sel    = 10;
        parameter [4:0] idx_JMPDO_sel    = 11;
        parameter [4:0] idx_JMPEQ_sel    = 12;
        parameter [4:0] idx_JMPNE_sel    = 13;
        parameter [4:0] idx_JMPGT_sel    = 14;
        parameter [4:0] idx_JMPLT_sel    = 15;

        parameter [4:0] idx_REGA_sel     = 16;
        parameter [4:0] idx_REGP_sel     = 31;

*/
        // all devices to select
/*
        parameter [4:0] dev_RAM_sel      = rol(idx_RAM_sel);
        parameter [4:0] dev_MARLO_sel    = rol(idx_MARLO_sel);
        parameter [4:0] dev_MARHI_sel    = rol(idx_MARHI_sel);
        parameter [4:0] dev_UART_sel     = rol(idx_UART_sel);
        parameter [4:0] dev_PCHITMP_sel  = rol(idx_PCHITMP_sel);
        parameter [4:0] dev_PCLO_sel     = rol(idx_PCLO_sel);
        parameter [4:0] dev_PC_sel       = rol(idx_PC_sel);
        parameter [4:0] dev_JMPO_sel     = rol(idx_JMPO_sel);

        parameter [4:0] dev_JMPZ_sel     = rol(idx_JMPZ_sel);
        parameter [4:0] dev_JMPC_sel     = rol(idx_JMPC_sel);
        parameter [4:0] dev_JMPDI_sel    = rol(idx_JMPDI_sel);
        parameter [4:0] dev_JMPDO_sel    = rol(idx_JMPDO_sel);
        parameter [4:0] dev_JMPEQ_sel    = rol(idx_JMPEQ_sel);
        parameter [4:0] dev_JMPNE_sel    = rol(idx_JMPNE_sel);
        parameter [4:0] dev_JMPGT_sel    = rol(idx_JMPGT_sel);
        parameter [4:0] dev_JMPLT_sel    = rol(idx_JMPLT_sel);

        parameter [4:0] dev_REGA_sel     = rol(idx_REGA_sel);
        parameter [4:0] dev_REGP_sel     = rol(idx_REGP_sel);
 */       
        
        $display("testing select");
    // ===========================================================================

//`include "./generated_tests.v"


end

endmodule : test
