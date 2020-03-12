//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "./control.v"
`include "../lib/assertion.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps
`default_nettype none


module test();
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
            `EXPECT_STATE_BIT(9:6, "reg_y_addr", expected, msg); \
            `EXPECT_STATE_BIT(4:2, "alu_op", expected, msg); \
            `EXPECT_STATE_BIT(1, "force_x_zero_n", expected, msg); \
            `EXPECT_STATE_BIT(0, "ram_zp_n", expected, msg); \
        end

`define TEST_STATE(expected, msg) \
        begin \
            #101  \
            `EXPECT_STATE(expected, msg); \
        end

	logic [7:0] hiRom, loRom;
	
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
    logic [3:0] alu_op;
    logic force_x_zero_n;
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
        force_x_zero_n,

        ram_zp_n
    };

	control ctrl(
	.hi_rom(hiRom), 
   
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
    .force_x_zero_n,

    .ram_zp_n
	);
    
    
    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0,  hiRom, loRom, 
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
                        force_x_zero_n,
                        ram_zp_n);
        `endif

        $display ("");
        $display ($time, "   %8s %8s %3s %3s %3s %4s %3s %5s %5s %4s %7s %4s %4s %4s %4s %4s %3s", 
                                                "hi", "lo","rom","ram","alu","uart",
                                                "ram", "marlo", "marhi", "uart", "pchitmp", "pclo", "jmp",
                                                "reg","reg","alu", "force_x", "ram");
        
        $display ($time, "   %8s %8s %3s %3s %3s %4s %3s %5s %5s %4s %7s %4s %4s %4s %4s %4s %3s", 
                                                "rom", "rom","out","out","out","out",
                                                "in", "in", "in", "in", "in", "in", 
                                                "in","x","y","op","zero", "zp");
        //$monitor
        $display ($time, "   %8b %8b %3b %3b %3b %4b %3b %5b %5b %4b %7b %4b %4b %4b %4b %4b %3b", 
                                hiRom, loRom, 
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
                                force_x_zero_n,
                                ram_zp_n);
    end

    initial begin
        parameter aa     = 8'b10101010;
        
        parameter T      = 1'b1;
        parameter F      = 1'b0;

        parameter pad6      = 6'b000000;
        
        parameter bus_ROM_sel      = 2'b00;
        parameter bus_RAM_sel      = 2'b01;
        parameter bus_ALU_sel      = 2'b10;
        parameter bus_UART_sel     = 2'b11;
        
        parameter dev_RAM_IN_sel      = 5'b00000;
        parameter dev_MARLO_IN_sel    = 5'b00001;
        parameter dev_MARHI_IN_sel    = 5'b00010;
        parameter dev_UART_IN_sel     = 5'b00011;
        parameter dev_PCHITMP_IN_sel  = 5'b00100;
        parameter dev_PCLO_IN_sel     = 5'b00101;
        parameter dev_JMP_IN_sel      = 5'b00110;
        parameter dev_REGA_IN_sel     = 5'b10000;
        parameter dev_REGP_IN_sel     = 5'b11111;
        
        logic [7:0] bZedByte       = 8'bz;
        logic undefined      = 1'bx;

        parameter latched       = 1'b0;
        parameter transparent   = 1'b1;

        parameter disabled = 1'b1;
        parameter enabled = 1'b0;
        
        parameter zp_off_sel = 1'b1;
        parameter zp_on_sel = 1'b0;
        

    #30 
        `equals(rom_out_n , undefined, "initial");
        `equals(ram_out_n , undefined, "initial");

	loRom=bZedByte;

	hiRom={bus_ROM_sel, pad6};
	#101
        `equals(rom_out_n , 0, "rom_out _selected");
        `equals(ram_out_n , 1, "ram_out not sel");
        `equals(alu_out_n , 1, "alu_out not sel");
        `equals(uart_out_n , 1, "uart_out not sel");

	hiRom={bus_RAM_sel, pad6};
	#101
        `equals(rom_out_n , 1, "rom_out not sel");
        `equals(ram_out_n , 0, "rom_out selected");
        `equals(alu_out_n , 1, "alu_out not sel");
        `equals(uart_out_n , 1, "uart_out not sel");

	hiRom={bus_ALU_sel, pad6};
	#101
	    `equals(rom_out_n , 1, "rom_out not sel");
        `equals(ram_out_n , 1, "rom_out selected");
        `equals(alu_out_n , 0, "alu_out not sel");
        `equals(uart_out_n , 1, "uart_out not sel");

	hiRom={bus_UART_sel, pad6};
	#101
	    `equals(rom_out_n , 1, "rom_out not sel");
        `equals(ram_out_n , 1, "rom_out selected");
        `equals(alu_out_n , 1, "alu_out not sel");
        `equals(uart_out_n , 0, "uart_out not sel");

	hiRom={bus_ROM_sel,zp_off_sel,dev_RAM_IN_sel};
	#101
	    `equals(rom_out_n , 0, "rom_out not sel");
	    `equals(ram_zp_n, 1, "zp disabled");

	hiRom={bus_ROM_sel,zp_on_sel,dev_RAM_IN_sel};
	#101
	    `equals(ram_zp_n, 0, "zp enabled");

    $display("=== ROM OUT");
    hiRom = {bus_ROM_sel,zp_off_sel,dev_RAM_IN_sel};
    `TEST_STATE(35'b01110111111111111111100001111111111, "ram");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_MARLO_IN_sel};
    `TEST_STATE(35'b01111011111111111111100011111111111, "marlo");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_MARHI_IN_sel};
    `TEST_STATE(35'b01111101111111111111100101111111111, "marhi");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_UART_IN_sel};
    `TEST_STATE(35'b01111110111111111111100111111111111, "uart");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_PCHITMP_IN_sel};
    `TEST_STATE(35'b01111111011111111111101001111111111, "pctmphi");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_PCLO_IN_sel};
    `TEST_STATE(35'b01111111101111111111101011111111111, "pclo");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_JMP_IN_sel};
    `TEST_STATE(35'b01111111110111111111101101111111111, "jmp");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_REGA_IN_sel};
    `TEST_STATE(35'b01111111111111111111000001111111111, "rega");

    hiRom = {bus_ROM_sel,zp_off_sel,dev_REGP_IN_sel};
    `TEST_STATE(35'b01111111111111111111011111111111111, "regp");

    $display("=== RAM OUT");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_RAM_IN_sel};
    `TEST_STATE(35'b10110111111111111111100001111111111, "ram");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_MARLO_IN_sel};
    `TEST_STATE(35'b10111011111111111111100011111111111, "marlo");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_MARHI_IN_sel};
    `TEST_STATE(35'b10111101111111111111100101111111111, "marhi");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_UART_IN_sel};
    `TEST_STATE(35'b10111110111111111111100111111111111, "uart");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_PCHITMP_IN_sel};
    `TEST_STATE(35'b10111111011111111111101001111111111, "pctmphi");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_PCLO_IN_sel};
    `TEST_STATE(35'b10111111101111111111101011111111111, "pclo");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_REGA_IN_sel};
    `TEST_STATE(35'b10111111111111111111000001111111111, "rega");

    hiRom = {bus_RAM_sel,zp_off_sel,dev_REGP_IN_sel};
    `TEST_STATE(35'b10111111111111111111011111111111111, "regp");

    $display("=== ALU OUT");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_RAM_IN_sel};
    `TEST_STATE(35'b11010111111111111111100001111111101, "ram");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_MARLO_IN_sel};
    `TEST_STATE(35'b11011011111111111111100011111111101, "marlo");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_MARHI_IN_sel};
    `TEST_STATE(35'b11011101111111111111100101111111101, "marhi");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_UART_IN_sel};
    `TEST_STATE(35'b11011110111111111111100111111111101, "marhi");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_PCHITMP_IN_sel};
    `TEST_STATE(35'b11011111011111111111101001111111101, "pctmphi");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_PCLO_IN_sel};
    `TEST_STATE(35'b11011111101111111111101011111111101, "pclo");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_REGA_IN_sel};
    `TEST_STATE(35'b11011111111111111111000001111111111, "rega");

    hiRom = {bus_ALU_sel,zp_off_sel,dev_REGP_IN_sel};
    `TEST_STATE(35'b11011111111111111111011111111111111, "regp");

    $display("=== UART OUT");

    hiRom = {bus_UART_sel,zp_off_sel,dev_RAM_IN_sel};
    `TEST_STATE(35'b11100111111111111111100001111111101, "ram");

    hiRom = {bus_UART_sel,zp_off_sel,dev_MARLO_IN_sel};
    `TEST_STATE(35'b11101011111111111111100011111111101, "marlo");

    hiRom = {bus_UART_sel,zp_off_sel,dev_MARHI_IN_sel};
    `TEST_STATE(35'b11101101111111111111100101111111101, "marhi");

    hiRom = {bus_UART_sel,zp_off_sel,dev_UART_IN_sel};
    `TEST_STATE(35'b11101110111111111111100111111111101, "marhi");

    hiRom = {bus_UART_sel,zp_off_sel,dev_PCHITMP_IN_sel};
    `TEST_STATE(35'b11101111011111111111101001111111101, "pctmphi");

    hiRom = {bus_UART_sel,zp_off_sel,dev_PCLO_IN_sel};
    `TEST_STATE(35'b11101111101111111111101011111111101, "pclo");

    hiRom = {bus_UART_sel,zp_off_sel,dev_REGA_IN_sel};
    `TEST_STATE(35'b11101111111111111111000001111111111, "rega");

    hiRom = {bus_UART_sel,zp_off_sel,dev_REGP_IN_sel};
    `TEST_STATE(35'b11101111111111111111011111111111111, "regp");

	end
endmodule : test
