//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control_decode.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps
`default_nettype none


module test();

        logic [4:0] device_in;

	logic _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt;
        logic _uart_in_ready, _uart_out_ready;

        logic _ram_in;
        logic _marlo_in;
        logic _marhi_in;
        logic _uart_in;

        logic _pchitmp_in; // load hi tno
        logic _pclo_in; // load lo only (local jmp)
        logic _pc_in; // load hi

        logic _reg_in;

        integer counter; // first JMP flag

	control_decode ctrl(
            .device_in,
            ._flag_z, ._flag_c, ._flag_o, ._flag_eq, ._flag_ne, ._flag_gt, ._flag_lt,
            ._uart_in_ready, ._uart_out_ready,

            ._ram_in, ._marlo_in, ._marhi_in, ._uart_in,
            ._pchitmp_in, ._pclo_in, ._pc_in,

            ._reg_in
	);
    

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  
            device_in, 
            _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
            _uart_in_ready, _uart_out_ready,

            _ram_in, _marlo_in, _marhi_in, _uart_in,
            _pchitmp_in, _pclo_in, _pc_in,
            _reg_in
        );

/*
        $monitor ($time, "  %05b  %3b %3b %3b %3b %3b %3b %3b  %3b %3b  %5b %5b %5b %5b   %8b %6b %5b  %5b", 
            device_in, 
            _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
            _uart_in_ready, _uart_out_ready,

            _ram_in, _marlo_in, _marhi_in, _uart_in,
            _pchitmp_in, _pclo_in, _pc_in,
            
            _reg_in
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

    // ===========================================================================
        device_in=5'b00000;
	#101
        `Equals( _ram_in, F);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);

        device_in=5'b00001;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, F);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);
            
        device_in=5'b00010;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, F);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);

        device_in=5'b00011;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, F);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);
            
        device_in=5'b00100;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, F);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);

        device_in=5'b00101;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, F);
	`Equals( _pc_in, T);
	`Equals( _reg_in, T);
            
        device_in=5'b00110;
	#101
        `Equals( _ram_in, T);
	`Equals( _marlo_in, T);
	`Equals( _marhi_in, T);
	`Equals( _uart_in, T);
	`Equals( _pchitmp_in, T);
	`Equals( _pclo_in, T);
	`Equals( _pc_in, F);
	`Equals( _reg_in, T);

        // conditional JMP tests
`define RESET_FLAGS \
        _flag_o=1; \
	_flag_z=1; \
        _flag_c=1; \
        _uart_in_ready=1; \
        _uart_out_ready=1; \
        _flag_eq=1; \
        _flag_ne=1; \
        _flag_gt=1; \
        _flag_lt=1;
            
        // JMP's should all be disabled as no flags set
        for (counter=7; counter < 16; counter++) begin
            device_in=counter;
            `RESET_FLAGS;
            
            #101
            `Equals( _ram_in, T);
            `Equals( _marlo_in, T);
            `Equals( _marhi_in, T);
            `Equals( _uart_in, T);
            `Equals( _pchitmp_in, T);
            `Equals( _pclo_in, T);
            `Equals( _pc_in, T);
            `Equals( _reg_in, T);
        end

    `define PC_IN_SET \
        `Equals( _ram_in, T); \
	`Equals( _marlo_in, T); \
	`Equals( _marhi_in, T); \
	`Equals( _uart_in, T); \
	`Equals( _pchitmp_in, T); \
	`Equals( _pclo_in, T); \
	`Equals( _pc_in, F); \
	`Equals( _reg_in, T);

        // conditional JMP tests
        device_in=7;
        `RESET_FLAGS;
        _flag_o=0;
	#101
	`PC_IN_SET;
            
        // conditional JMP tests
        device_in=8;
        `RESET_FLAGS;
        _flag_z=0;
	#101
	`PC_IN_SET;

        // conditional JMP tests
        device_in=9;
        `RESET_FLAGS;
        _flag_c=0;
	#101
	`PC_IN_SET;

        // conditional JMP tests
        device_in=10;
        `RESET_FLAGS;
        _uart_in_ready=0;
	#101
	`PC_IN_SET;
            
        // conditional JMP tests
        device_in=11;
        `RESET_FLAGS;
        _uart_out_ready=0;
	#101
	`PC_IN_SET;
            
        // conditional JMP tests
        device_in=12;
        `RESET_FLAGS;
        _flag_eq=0;
	#101
	`PC_IN_SET;
            
        // conditional JMP tests
        device_in=13;
        `RESET_FLAGS;
        _flag_ne=0;
	#101
	`PC_IN_SET;

        // conditional JMP tests
        device_in=14;
        `RESET_FLAGS;
        _flag_gt=0;
	#101
	`PC_IN_SET;

        // conditional JMP tests
        device_in=15;
        `RESET_FLAGS;
        _flag_lt=0;
	#101
	`PC_IN_SET;


        // REGISTER TESTS 
        for (counter=16; counter < 32; counter++) begin
            device_in=counter;
            
            #101
            `Equals( _ram_in, T);
            `Equals( _marlo_in, T);
            `Equals( _marhi_in, T);
            `Equals( _uart_in, T);
            `Equals( _pchitmp_in, T);
            `Equals( _pclo_in, T);
            `Equals( _pc_in, T);
            `Equals( _reg_in, F);

        end


    end

endmodule : test
