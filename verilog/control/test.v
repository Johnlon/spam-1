//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps
`default_nettype none


module test();

function [4:0] rol(input [4:0] x);
    logic [4:0] rol;
    rol = {x[3:0], x[4]};
endfunction

`define EXPECT_STATE_BIT(bitno, name, expected, msg) \
        begin \
            localparam tmpwire = expected; \
            if (1==2) $display("expected %d got %d  => %d ", tmpwire[bitno], state[bitno], tmpwire[bitno] !== state[bitno]);  \
            if (tmpwire[bitno] !== state[bitno]) begin \
                $display("%4d failed: '%b'\n      is not '%b' @ [bitno] %s = %1b but wanted %b - %s", \
                    `__LINE__ ,state, expected, name, state[bitno], tmpwire[bitno], msg); \
            end \
        end

`define EXPECT_STATE(expected, msg) \
        if (expected === state) begin \
            if (1==2) $display("ok"); \
        end \
        else \
        begin \
            `EXPECT_STATE_BIT(34, "rom_out_en", expected, msg); \
            `EXPECT_STATE_BIT(32, "ram_out_en", expected, msg); \
            `EXPECT_STATE_BIT(33, "alu_out_en", expected, msg); \
            `EXPECT_STATE_BIT(31, "uart_out_en", expected, msg); \
            \
            `EXPECT_STATE_BIT(30, "ram_in_n", expected, msg); \
            `EXPECT_STATE_BIT(29, "marlo_in_n", expected, msg); \
            `EXPECT_STATE_BIT(28, "marhi_in_n", expected, msg); \
            `EXPECT_STATE_BIT(27, "uart_in_n", expected, msg); \
            `EXPECT_STATE_BIT(26, "pchitmp_in_n", expected, msg); \
            `EXPECT_STATE_BIT(25, "pclo_in_n", expected, msg); \
            `EXPECT_STATE_BIT(24, "jmp_in_n", expected, msg); \
            `EXPECT_STATE_BIT(23, "jmpo_in_n", expected, msg); \
            \
            `EXPECT_STATE_BIT(22, "jmpz_in_n", expected, msg); \
            `EXPECT_STATE_BIT(21, "jmpc_in_n", expected, msg); \
            `EXPECT_STATE_BIT(20, "jmpdi_in_n", expected, msg); \
            `EXPECT_STATE_BIT(19, "jmpdo_in_n", expected, msg); \
            `EXPECT_STATE_BIT(18, "jmpeq_in_n", expected, msg); \
            `EXPECT_STATE_BIT(17, "jmpne_in_n", expected, msg); \
            `EXPECT_STATE_BIT(16, "jmpgt_in_n", expected, msg); \
            `EXPECT_STATE_BIT(15, "jmplt_in_n", expected, msg); \
            \
            `EXPECT_STATE_BIT(14, "reg_in", expected, msg); \
            `EXPECT_STATE_BIT(13:10, "reg_x_addr", expected, msg); \
            `EXPECT_STATE_BIT(4:2, "alu_op", expected, msg); \
            `EXPECT_STATE_BIT(1, "force_x_val_to_zero_n", expected, msg); \
            `EXPECT_STATE_BIT(0, "ram_zp_n", expected, msg); \
        end

`define TEST_STATE(expected, msg) \
        begin \
            #101  \
            `EXPECT_STATE(expected, msg); \
        end

	logic [7:0] hi_rom, lo_rom;
	
    logic rom_out_n, ram_out_n, alu_out_n, uart_out_n;
	
    logic ram_in_n;
    logic marlo_in_n;
    logic marhi_in_n;
    logic uart_in_n;
    logic pchitmp_in_n;
    logic pclo_in_n;
    logic jmp_in_n;
    logic jmpo_in_n;

    logic jmpz_in_n;
    logic jmpc_in_n;
    logic jmpdi_in_n;
    logic jmpdo_in_n;
    logic jmpeq_in_n;
    logic jmpne_in_n;
    logic jmpgt_in_n;
    logic jmplt_in_n;

    logic reg_in_n;
    logic [3:0] reg_x_addr;
    logic [3:0] reg_y_addr;
    logic [4:0] alu_op;
    logic force_x_val_to_zero_n;
	logic ram_zp_n;


    wire [34:0] state = {
        rom_out_n,
        ram_out_n,
        alu_out_n,
        uart_out_n,

        ram_in_n,
        marlo_in_n,
        marhi_in_n,
        uart_in_n,
        pchitmp_in_n,
        pclo_in_n,
        jmp_in_n,
        jmpo_in_n,

        jmpz_in_n,
        jmpc_in_n,
        jmpdi_in_n,
        jmpdo_in_n,
        jmpeq_in_n,
        jmpne_in_n,
        jmpgt_in_n,
        jmplt_in_n,

        reg_in_n,
        reg_x_addr,
        reg_y_addr,
        alu_op,
        force_x_val_to_zero_n,

        ram_zp_n
    };

	control ctrl(
	.hi_rom(hi_rom), 
	.lo_rom(lo_rom), 
   
    .rom_out_n,
	.ram_out_n,
	.alu_out_n,
	.uart_out_n,
    
    .ram_in_n,
    .marlo_in_n,
    .marhi_in_n,
    .uart_in_n,
    .pchitmp_in_n,
    .pclo_in_n,
    .jmp_in_n,
    .jmpo_in_n,
    .jmpz_in_n,
    .jmpc_in_n,
    .jmpdi_in_n,
    .jmpdo_in_n,
    .jmpeq_in_n,
    .jmpne_in_n,
    .jmpgt_in_n,
    .jmplt_in_n,

    .reg_in_n,
    .reg_x_addr,
    .reg_y_addr,
    .alu_op,
    .force_x_val_to_zero_n,

    .ram_zp_n
	);
    
    
    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  hi_rom, lo_rom, 
                        rom_out_n,
                        ram_out_n,
                        alu_out_n,
                        uart_out_n,
                        ram_in_n,
                        marlo_in_n,
                        marhi_in_n,
                        uart_in_n,
                        pchitmp_in_n,
                        pclo_in_n,
                        jmp_in_n,
                        jmpo_in_n,
                       
                        reg_x_addr,
                        reg_y_addr,
                        alu_op,
                        force_x_val_to_zero_n,
                        ram_zp_n);
        `endif

        $display ("");
        $display ($time, "   %8s %8s %3s %3s %3s %4s %3s %5s %5s %4s %7s %4s %4s %4s %4s %7s %7s %3s", 
                                                "hi", "lo","rom","ram","alu","uart",
                                                "ram", "marlo", "marhi", "uart", "pchitmp", "pclo", "jmp",
                                                "reg","reg","alu", "force_x", "ram");
        
        $display ($time, "   %8s %8s %3s %3s %3s %4s %3s %5s %5s %4s %7s %4s %4s %4s %4s %4s %7s %3s", 
                                                "rom", "rom","out","out","out","out",
                                                "in", "in", "in", "in", "in", "in", 
                                                "in","x","y","op","zero", "zp");
        //$monitor
        $display ($time, "   %8b %8b %3b %3b %3b %4b %3b %5b %5b %4b %7b %4b %4b %4b %4b %4b %3b", 
                                hi_rom, lo_rom, 
                                rom_out_n, ram_out_n, alu_out_n, uart_out_n,
                                ram_in_n,
                                marlo_in_n,
                                marhi_in_n,
                                uart_in_n,
                                pchitmp_in_n,
                                pclo_in_n,
                                jmp_in_n,
                                reg_x_addr,
                                reg_y_addr,
                                alu_op,
                                force_x_val_to_zero_n,
                                ram_zp_n);
    end

    initial begin
        
        logic [7:0] bZedByte       = 8'bz;
        logic undefined      = 1'bx;

        parameter T      = 1'b1;
        parameter F      = 1'b0;

        parameter pad6      = 6'b000000;
        parameter pad5      = 5'b00000;
        parameter pad4      = 4'b0000;
        

        // all routes to select
        parameter [2:0] op_DEV_eq_ROM_sel = 0;
        parameter [2:0] op_DEV_eq_RAM_sel = 1;
        parameter [2:0] op_DEV_eq_RAMZP_sel = 2;
        parameter [2:0] op_RAMZP_eq_REG_sel = 3;
        parameter [2:0] op_NONREG_eq_OPREGY_sel = 4;
        parameter [2:0] op_REGX_eq_ALU_sel = 5;
        parameter [2:0] op_DEV_eq_UART_sel = 6;
        parameter [2:0] op_RAMZP_eq_UART_sel = 7;

        // because MSB

        // all devices to select
        parameter [4:0] dev_RAM_sel      = rol(0);
        parameter [4:0] dev_MARLO_sel    = rol(1);
        parameter [4:0] dev_MARHI_sel    = rol(2);
        parameter [4:0] dev_UART_sel     = rol(3);
        parameter [4:0] dev_PCHITMP_sel  = rol(4);
        parameter [4:0] dev_PCLO_sel     = rol(5);
        parameter [4:0] dev_JMP_sel      = rol(6);
        parameter [4:0] dev_JMPO_sel     = rol(7);

        parameter [4:0] dev_JMPZ_sel     = rol(8);
        parameter [4:0] dev_JMPC_sel     = rol(9);
        parameter [4:0] dev_JMPDI_sel    = rol(10);
        parameter [4:0] dev_JMPDO_sel    = rol(11);
        parameter [4:0] dev_JMPEQ_sel    = rol(12);
        parameter [4:0] dev_JMPNE_sel    = rol(13);
        parameter [4:0] dev_JMPGT_sel    = rol(14);
        parameter [4:0] dev_JMPLT_sel    = rol(15);

        parameter [4:0] dev_REGA_sel     = rol(16);
        parameter [4:0] dev_REGP_sel     = rol(31);
        
        
        parameter [4:0] ALU_ZERO_VAL     = 0;
        parameter [4:0] ALU_PASSX        = 1;
        
        parameter zp_off_sel = 1'b1;
        parameter zp_on_sel = 1'b0;
        

	lo_rom=bZedByte;

    // ===========================================================================
	hi_rom={op_DEV_eq_ROM_sel, dev_RAM_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
        
	hi_rom={op_DEV_eq_ROM_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0001, "regb sel");
        
    hi_rom={op_DEV_eq_ROM_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");

    hi_rom={op_DEV_eq_ROM_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b1111, "regp sel");

    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAM_sel, dev_RAM_sel}; // ILLEGAL - CANT READ AND WRITE RAM
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "reg sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0001, "regb sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
        
    hi_rom={op_DEV_eq_RAM_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b1111, "regp sel");
        
    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAMZP_sel, dev_RAM_sel}; // ILLEGAL - CAN'T READ AND WRITE RAM
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0001, "regb sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
        
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
        `equals(reg_x_addr, 4'b1111, "regp sel");
            
    // ===========================================================================
    
    lo_rom=8'bxxxxxxxx;

    hi_rom={op_RAMZP_eq_REG_sel, dev_RAM_sel};
	#101
        // illegal cos can't read and write RAM - REGX_ADDR=device[3:0] for RAM sel is same as REGA sel     
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F,   "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_x_addr, 4'b0000, "regx addr");
        `equals(alu_op, ALU_PASSX, "passx sel");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n not sel");
    
    hi_rom={op_RAMZP_eq_REG_sel, dev_MARLO_sel};
	#101
        // illegal cos can't read MARLO - REGX_ADDR=device[3:0] for MARLO sel is same as REGB sel     
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F,   "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_x_addr, 4'b0001, "regb sel");
        `equals(alu_op, ALU_PASSX, "passx sel");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
        `equals(alu_op, ALU_PASSX, "passx sel");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_x_addr, 4'b1111, "regp sel");
        `equals(alu_op, ALU_PASSX, "passx sel");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n not sel");
        
    // ===========================================================================
    
    lo_rom={4'b0101, 4'bxxxx};
    hi_rom={op_NONREG_eq_OPREGY_sel, dev_MARLO_sel[4:1], 1'b1};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T,   "ram_in_n not sel");
        `equals(marlo_in_n, F,   "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(alu_op, 5'b10101, "alu op");
        `equals(force_x_val_to_zero_n, F, "force_x_val_to_zero_n sel");

    // as above but with top bit of ALU op as zero
    lo_rom={4'b0101, 4'bxxxx};
    hi_rom={op_NONREG_eq_OPREGY_sel, dev_MARLO_sel[4:1], 1'b0};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T,   "ram_in_n not sel");
        `equals(marlo_in_n, F,   "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(alu_op, 5'b00101, "alu op");
        `equals(force_x_val_to_zero_n, F, "force_x_val_to_zero_n sel");

    // as above but with bottom of the ALU op tweaked
    lo_rom={4'b1111, 4'bxxxx};
    hi_rom={op_NONREG_eq_OPREGY_sel, dev_MARLO_sel[4:1], 1'b0};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T,   "ram_in_n not sel");
        `equals(marlo_in_n, F,   "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(alu_op, 5'b01111, "alu op");
        `equals(force_x_val_to_zero_n, F, "force_x_val_to_zero_n sel");
    // ===========================================================================
    
    lo_rom={4'b0101, 4'bxxxx};
    hi_rom={op_REGX_eq_ALU_sel, dev_REGA_sel[4:1], 1'b1};
	#101
        `equals(alu_out_n, F, "alu_out sel");
        `equals(reg_in_n, F, "reg_in_n not sel");
        `equals(alu_op, 5'b10101, "alu op");
        `equals(reg_x_addr, 4'b0000, "regx addr");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n sel");
 
    // as above but with top bit of ALU op as zero
    lo_rom={4'b0101, 4'bxxxx};
    hi_rom={op_REGX_eq_ALU_sel, dev_REGA_sel[4:1], 1'b0};
	#101
        `equals(alu_out_n, F, "alu_out sel");
        `equals(reg_in_n, F, "reg_in_n not sel");
        `equals(alu_op, 5'b00101, "alu op");
        `equals(reg_x_addr, 4'b0000, "regx addr");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n sel");

    // as above but with bottom of the ALU op tweaked
    lo_rom={4'b1111, 4'bxxxx};
    hi_rom={op_REGX_eq_ALU_sel, dev_REGA_sel[4:1], 1'b0};
	#101
        `equals(alu_out_n, F, "alu_out sel");
        `equals(reg_in_n, F, "reg_in_n not sel");
        `equals(alu_op, 5'b01111, "alu op");
        `equals(reg_x_addr, 4'b0000, "regx addr");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n sel");
        
    // as above but with regx tweaked
    lo_rom={4'b1111, 4'bxxxx};
    hi_rom={op_REGX_eq_ALU_sel, dev_REGP_sel[4:1], 1'b0};
	#101
        `equals(alu_out_n, F, "alu_out sel");
        `equals(reg_in_n, F, "reg_in_n not sel");
        `equals(alu_op, 5'b01111, "alu op");
        `equals(reg_x_addr, 4'b1111, "regx addr");
        `equals(force_x_val_to_zero_n, T, "force_x_val_to_zero_n sel");

    // ===========================================================================
	
    lo_rom={4'bxxxx, 4'bxxxx};
    hi_rom={op_DEV_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, F, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(reg_x_addr, 4'b0000, "rega sel");
        
    lo_rom={4'bxxxx, 4'bxxxx};
    hi_rom={op_DEV_eq_UART_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, F, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n sel");
        `equals(marlo_in_n, F, "ram_marlo_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(reg_x_addr, 4'b0001, "rega sel");
        
    lo_rom={4'bxxxx, 4'bxxxx};
    hi_rom={op_DEV_eq_UART_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out not sel");
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(marlo_in_n, T, "ram_marlo_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(reg_x_addr, 4'b1111, "rega sel");
        
    // ===========================================================================
	
    lo_rom={4'bxxxx, 4'bxxxx};
    hi_rom={op_RAMZP_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
    lo_rom={4'bxxxx, 4'bxxxx};
    hi_rom={op_RAMZP_eq_UART_sel, 5'b11111};
	#101
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
	end
endmodule : test
