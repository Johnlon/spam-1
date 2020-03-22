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
	logic _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt;
        logic _uart_in_ready, _uart_out_ready;

        logic _rom_out, _ram_out, _alu_out, _uart_out;
            
        logic _ram_in;
        logic _marlo_in;
        logic _marhi_in;
        logic _uart_in;

        logic _pchitmp_in; // load hi tno
        logic _pclo_in; // load lo only (local jmp)
        logic _pc_in; // load hi

        logic _reg_in;
        logic force_alu_op_to_passx;
        logic force_x_val_to_zero;
	logic _ram_zp;


	control ctrl(
            .hi_rom, 
            ._flag_z, ._flag_c, ._flag_o, ._flag_eq, ._flag_ne, ._flag_gt, ._flag_lt,
            ._uart_in_ready, ._uart_out_ready,

            ._rom_out, ._ram_out,	._alu_out, ._uart_out,
            ._ram_in, ._marlo_in, ._marhi_in, ._uart_in,
            ._pchitmp_in, ._pclo_in, ._pc_in,

            ._reg_in,
            .force_alu_op_to_passx,
            .force_x_val_to_zero,

            ._ram_zp
	);
    

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
            hi_rom, 
            _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
            _uart_in_ready, _uart_out_ready,

            _rom_out, _ram_out, _alu_out, _uart_out,
            _ram_in, _marlo_in, _marhi_in, _uart_in,
            _pchitmp_in, _pclo_in, _pc_in,
            
            _reg_in,
            force_x_val_to_zero,
            force_alu_op_to_passx,
            
            _ram_zp
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
            _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
            _uart_in_ready, _uart_out_ready,

            _rom_out, _ram_out, _alu_out, _uart_out,
            _ram_in, _marlo_in, _marhi_in, _uart_in,
            _pchitmp_in, _pclo_in, _pc_in,
            
            _reg_in,
            force_x_val_to_zero,
            force_alu_op_to_passx,
            
            _ram_zp
        );
 */       
        `endif
    end

    initial begin
        
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
        `equals(_rom_out, F, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_reg_in, T, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
        
	hi_rom={op_DEV_eq_ROM_sel, dev_MARLO_sel};
	#101
        `equals(_rom_out, F, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in sel");
        `equals(_marlo_in, F, "_marlo_in sel");
        `equals(_reg_in, T, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
        
    hi_rom={op_DEV_eq_ROM_sel, dev_REGA_sel};
	#101
        `equals(_rom_out, F, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");

    hi_rom={op_DEV_eq_ROM_sel, dev_REGP_sel};
	#101
        `equals(_rom_out, F, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");

    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAM_sel, dev_RAM_sel}; // ILLEGAL - CANT READ AND WRITE RAM
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_MARLO_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_marlo_in, F, "_marlo_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
    
    hi_rom={op_DEV_eq_RAM_sel, dev_REGA_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
        
    hi_rom={op_DEV_eq_RAM_sel, dev_REGP_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
        
    // ===========================================================================
	
    hi_rom={op_DEV_eq_RAMZP_sel, dev_RAM_sel}; // ILLEGAL - CAN'T READ AND WRITE RAM
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel - ILLEGAL");  // !! RAM_IN is disabled
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, F, "_ram_zp not sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_MARLO_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_marlo_in, F, "_marlo_in sel");
        `equals(_ram_zp, F, "_ram_zp not sel");
    
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGA_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, F, "_ram_zp not sel");
        
    hi_rom={op_DEV_eq_RAMZP_sel, dev_REGP_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, F, "ram_out sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(_ram_zp, F, "_ram_zp not sel");
            
    // ===========================================================================
    
    hi_rom={op_RAMZP_eq_REG_sel, dev_RAM_sel};
	#101
        // illegal cos can't read and write RAM - REGX_ADDR=device[3:0] for RAM sel is same as REGA sel     
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, F,   "_ram_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
    
    hi_rom={op_RAMZP_eq_REG_sel, dev_MARLO_sel};
	#101
        // illegal cos can't read MARLO - REGX_ADDR=device[3:0] for MARLO sel is same as REGB sel     
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, F,   "_ram_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGA_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    hi_rom={op_RAMZP_eq_REG_sel, dev_REGP_sel};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(force_alu_op_to_passx, T, "force_alu_op_to_passx sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero not sel");
        
    // ===========================================================================
    
    hi_rom={op_NONREG_eq_OPREGY_sel, dev_MARLO_sel[4:1], 1'b1};
	#101
        `equals(_rom_out, T, "rom_out not sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_uart_out, T, "uart_out not sel");
        `equals(_ram_in, T,   "_ram_in not sel");
        `equals(_marlo_in, F,   "_marlo_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        `equals(_ram_zp, T, "_ram_zp not sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, T, "force_x_val_to_zero sel");

    // ===========================================================================
    
    hi_rom={op_REGX_eq_ALU_sel, dev_REGA_sel[4:1], 1'b1};
	#101
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero sel");
 
    hi_rom={op_REGX_eq_ALU_sel, dev_REGP_sel[4:1], 1'b1};
	#101
        `equals(_alu_out, F, "_alu_out sel");
        `equals(_reg_in, F, "_reg_in sel");
        `equals(force_alu_op_to_passx, F, "force_alu_op_to_passx not sel");
        `equals(force_x_val_to_zero, F, "force_x_val_to_zero sel");
 
    // ===========================================================================
	
    hi_rom={op_DEV_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(_rom_out, T, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, F, "uart_out not sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        
    hi_rom={op_DEV_eq_UART_sel, dev_MARLO_sel};
	#101
        `equals(_rom_out, T, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, F, "uart_out not sel");
        `equals(_ram_in, T, "_ram_in sel");
        `equals(_marlo_in, F, "_marlo_in sel");
        `equals(_reg_in, T, "_reg_in not sel");
        
    hi_rom={op_DEV_eq_UART_sel, dev_REGP_sel};
	#101
        `equals(_rom_out, T, "rom_out sel");
        `equals(_ram_out, T, "ram_out not sel");
        `equals(_alu_out, T, "_alu_out not sel");
        `equals(_uart_out, F, "uart_out sel");
        `equals(_ram_in, T, "_ram_in not sel");
        `equals(_marlo_in, T, "_marlo_in not sel");
        `equals(_reg_in, F, "_reg_in sel");
        
    // ===========================================================================
	
    hi_rom={op_RAMZP_eq_UART_sel, dev_RAM_sel};
	#101
        `equals(_uart_out, F, "uart_out sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(_reg_in, T, "_reg_in not sel");
        
    hi_rom={op_RAMZP_eq_UART_sel, 5'b11111};
	#101
        `equals(_uart_out, F, "uart_out sel");
        `equals(_ram_in, F, "_ram_in sel");
        `equals(_ram_zp, F, "_ram_zp sel");
        `equals(_reg_in, T, "_reg_in not sel");
        
    // JUMP TESTS ===========================================================================
	
    hi_rom={op_DEV_eq_ROM_sel, dev_REGA_sel};  // just to see PC flags not set
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
        

    hi_rom={op_DEV_eq_ROM_sel, dev_PCHITMP_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, F);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
        
    hi_rom={op_DEV_eq_ROM_sel, dev_PCLO_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, F);
        `Equals(_pc_in, T);

    hi_rom={op_DEV_eq_ROM_sel, dev_PC_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);

    //---------------------------

    _flag_o=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPO_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
        
    _flag_o=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPO_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
        
    //---------------------------
    
    _flag_z=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPZ_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_z=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPZ_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);

    //---------------------------
    
    _flag_c=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPC_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_c=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPC_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
    
    //---------------------------
    
    _uart_in_ready=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDI_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _uart_in_ready=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDI_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
    
    //---------------------------
    
    _uart_out_ready=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDO_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _uart_out_ready=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPDO_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);

    //---------------------------
    
    _flag_eq=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPEQ_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_eq=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPEQ_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
     
    //---------------------------
    
    _flag_ne=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPNE_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_ne=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPNE_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
            
    //---------------------------
    
    _flag_gt=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPGT_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_gt=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPGT_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
            
    //---------------------------
    
    _flag_lt=F;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPLT_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, F);
    
    _flag_lt=T;
    hi_rom={op_DEV_eq_ROM_sel, dev_JMPLT_sel};
	#101
        `Equals(_rom_out, F);
        `Equals(_pchitmp_in, T);
        `Equals(_pclo_in, T);
        `Equals(_pc_in, T);
            
	end
endmodule : test
