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

	logic [7:0] hi_rom;
	logic flag_z_n, flag_c_n, flag_o_n, flag_eq_n, flag_ne_n, flag_gt_n, flag_lt_n;
    logic uart_in_ready_n, uart_out_ready_n;

    logic rom_out_n, ram_out_n, alu_out_n, uart_out_n;
	
    logic ram_in_n;
    logic marlo_in_n;
    logic marhi_in_n;
    logic uart_in_n;

    logic pchitmp_in_n; // load hi tno
    logic pclo_in_n; // load lo only (local jmp)
    logic pc_in_n; // load hi
    
    logic reg_in_n;
    logic force_alu_op_to_passx;
    logic force_x_val_to_zero;
	logic ram_zp_n;


	control ctrl(
        .hi_rom, 
        .flag_z_n, .flag_c_n, .flag_o_n, .flag_eq_n, .flag_ne_n, .flag_gt_n, .flag_lt_n,
        .uart_in_ready_n, .uart_out_ready_n,

        .rom_out_n, .ram_out_n,	.alu_out_n, .uart_out_n,
        .ram_in_n, .marlo_in_n, .marhi_in_n, .uart_in_n,
        .pchitmp_in_n, .pclo_in_n, .pc_in_n,

        .reg_in_n,
        .force_alu_op_to_passx,
        .force_x_val_to_zero,

        .ram_zp_n
	);
    

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
            hi_rom, 
            flag_z_n, flag_c_n, flag_o_n, flag_eq_n, flag_ne_n, flag_gt_n, flag_lt_n,
            uart_in_ready_n, uart_out_ready_n,

            rom_out_n, ram_out_n, alu_out_n, uart_out_n,
            ram_in_n, marlo_in_n, marhi_in_n, uart_in_n,
            pchitmp_in_n, pclo_in_n, pc_in_n,
            
            reg_in_n,
            force_x_val_to_zero,
            force_alu_op_to_passx,
            
            ram_zp_n
        );

/*
        $display ("");
        $display ($time, "  %8s  %3s %3s %3s %3s %3s %3s %3s  %3s %3s  %5s %5s %5s %5s   %5s %5s %5s %5s  %8s %6s %5s  %5s %14s %12s %7s", 
                                                "hi",
                                                "nZ", "nC", "nO", "nEQ", "nNE", "nGT", "nLT",
                                                "nDI", "nDO", 
                                                "nRom","nRam","nAlu","nUart",
                                                "nRam", "nMarlo", "nMarhi", "nUart", 
                                                "nPchitmp", "nPclo", "nPc",
                                                "nRegin",
                                                "nForceXvalTo0", 
                                                "forceAluToA", 
                                                "nRamZp");
        $monitor ($time, "  %08b  %3b %3b %3b %3b %3b %3b %3b  %3b %3b  %5b %5b %5b %5b   %5b %5b %5b %5b  %8b %6b %5b  %5b %14b %12b %7b", 
            hi_rom, 
            flag_z_n, flag_c_n, flag_o_n, flag_eq_n, flag_ne_n, flag_gt_n, flag_lt_n,
            uart_in_ready_n, uart_out_ready_n,

            rom_out_n, ram_out_n, alu_out_n, uart_out_n,
            ram_in_n, marlo_in_n, marhi_in_n, uart_in_n,
            pchitmp_in_n, pclo_in_n, pc_in_n,
            
            reg_in_n,
            force_x_val_to_zero,
            force_alu_op_to_passx,
            
            ram_zp_n
        );
  */      
        
        `endif
    end

    initial begin
        
        logic [7:0] bZedByte       = 8'bz;
        logic undefined      = 1'bx;

        parameter T      = 1'b1;
        parameter F      = 1'b0;

        parameter pad6      = 6'b000000;
        parameter pad5      = 5'b00000;
        parameter pad4      = 4'b0000;
        

        // all routeb to belect
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
        parameter [4:0] dev_PC_sel       = rol(6);
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
        parameter [4:0] ALU_PASSX        = 0;
        
        parameter zp_off_sel = 1'b1;
        parameter zp_on_sel = 1'b0;
        

    // ===========================================================================
	hi_rom={op_DEV_eq_ROM_sel, dev_RAM_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        
	hi_rom={op_DEV_eq_ROM_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        
    hi_rom={op_DEV_eq_ROM_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");

    hi_rom={op_DEV_eq_ROM_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, F, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");

    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAM_sel, dev_RAM_sel}; // ILLEGAL - CANT READ AND WRITE RAM
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        
    hi_rom={op_DEV_eq_RAM_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        
    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAMZP_sel, dev_RAM_sel}; // ILLEGAL - CAN'T READ AND WRITE RAM
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(marlo_in_n, F, "marlo_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
        
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, F, "ram_out sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n not sel");
            
    // ===========================================================================
    
    hi_rom={op_RAMZP_eq_REG_sel, dev_RAM_sel};
	#101
        // illegal cos can't read and write RAM - REGX_ADDR=device[3:0] for RAM sel is same as REGA sel     
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F,   "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
    
    hi_rom={op_RAMZP_eq_REG_sel, dev_MARLO_sel};
	#101
        // illegal cos can't read MARLO - REGX_ADDR=device[3:0] for MARLO sel is same as REGB sel     
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F,   "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGA_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    // ===========================================================================
    
    hi_rom={op_NONREG_eq_OPREGY_sel, dev_MARLO_sel[4:1], 1'b1};
	#101
        `equals(rom_out_n, T, "rom_out not sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(uart_out_n, T, "uart_out not sel");
        `equals(ram_in_n, T,   "ram_in_n not sel");
        `equals(marlo_in_n, F,   "marlo_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        `equals(ram_zp_n, T, "ram_zp_n not sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, T, "force_x_val_to_zero sel");

    // ===========================================================================
    
    hi_rom={op_REGX_eq_ALU_sel, dev_REGA_sel[4:1], 1'b1};
	#101
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero sel");
 
    hi_rom={op_REGX_eq_ALU_sel, dev_REGP_sel[4:1], 1'b1};
	#101
        `equals(alu_out_n, F, "alu_out_n sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero sel");
 
    // ===========================================================================
	
    hi_rom={op_DEV_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, F, "uart_out not sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
    hi_rom={op_DEV_eq_UART_sel, dev_MARLO_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, F, "uart_out not sel");
        `equals(ram_in_n, T, "ram_in_n sel");
        `equals(marlo_in_n, F, "ram_marlo_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
    hi_rom={op_DEV_eq_UART_sel, dev_REGP_sel};
	#101
        `equals(rom_out_n, T, "rom_out sel");
        `equals(ram_out_n, T, "ram_out not sel");
        `equals(alu_out_n, T, "alu_out_n not sel");
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, T, "ram_in_n not sel");
        `equals(marlo_in_n, T, "ram_marlo_n not sel");
        `equals(reg_in_n, F, "reg_in_n sel");
        
    // ===========================================================================
	
    hi_rom={op_RAMZP_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
    hi_rom={op_RAMZP_eq_UART_sel, 5'b11111};
	#101
        `equals(uart_out_n, F, "uart_out sel");
        `equals(ram_in_n, F, "ram_in_n sel");
        `equals(ram_zp_n, F, "ram_zp_n sel");
        `equals(reg_in_n, T, "reg_in_n not sel");
        
    // JUMP TESTS ===========================================================================
	
    hi_rom={op_DEV_eq_ROM_sel, dev_REGA_sel};  // just to see PC flags not set
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
        

    hi_rom={op_DEV_eq_ROM_sel, dev_PCHITMP_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, F);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
        
    hi_rom={op_DEV_eq_ROM_sel, dev_PCLO_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, F);
        `Equals(pc_in_n, T);

    hi_rom={op_DEV_eq_ROM_sel, dev_PC_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);

    //---------------------------

    flag_o_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPO_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
        
    flag_o_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPO_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
        
    //---------------------------
    
    flag_z_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPZ_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_z_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPZ_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);

    //---------------------------
    
    flag_c_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPC_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_c_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPC_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
    
    //---------------------------
    
    uart_in_ready_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDI_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    uart_in_ready_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDI_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
    
    //---------------------------
    
    uart_out_ready_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDO_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    uart_out_ready_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDO_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);

    //---------------------------
    
    flag_eq_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPEQ_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_eq_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPEQ_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
     
    //---------------------------
    
    flag_ne_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPNE_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_ne_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPNE_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
            
    //---------------------------
    
    flag_gt_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPGT_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_gt_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPGT_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
            
    //---------------------------
    
    flag_lt_n=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPLT_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, F);
    
    flag_lt_n=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPLT_sel};
	#101
        `Equals(rom_out_n, F);
        `Equals(pchitmp_in_n, T);
        `Equals(pclo_in_n, T);
        `Equals(pc_in_n, T);
            
	end
endmodule : test
