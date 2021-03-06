// FIXME MAKE ALL THE tri WIRES tri0
// verilator lint_off PINMISSING
// verilator lint_off MULTITOP


`ifndef V_CONTROLLER_WIDE
`define V_CONTROLLER_WIDE

`include "../74151/hct74151.v"
`include "../74139/hct74139.v"
`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../lib/cast.v"
`include "../rom/rom.v"
`include "../alu/alu.v"
`include "control_lines.v"

`timescale 1ns/1ns


module controller(
    input [15:0] pc,
    input [7:0] _flags_czonGLEN,
    input _flag_di, _flag_do,

    output _addrmode_register,
    inout tri [15:0] address_bus,
    inout tri [7:0] bbus,


    // selection wires
`define OUT_ADEV_SEL(DNAME) output _adev_``DNAME``
`define OUT_BDEV_SEL(DNAME) output _bdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

    `CONTROL_WIRES(OUT, `COMMA),
    output [7:0] direct_address_lo, direct_address_hi,
    output [7:0] immed8,
    output [4:0] alu_op,
    output [2:0] bbus_dev, abus_dev,
    output [3:0] targ_dev,
    output _set_flags
);

    wire _addrmode_direct;

    // ROM IMMEDIATE TO BBUS AND ROM DIRECT ADDRESSING TO ADDRESSBUS OUTPUT 

    hct74245 rom_bbus_buf(.A(immed8), .B(bbus), .nOE(_bdev_immed), .dir(1'b1));  // DONE
    hct74245 rom_addbbuslo_buf(.A(direct_address_lo), .B(address_bus[7:0]),  .nOE(_addrmode_direct), .dir(1'b1));  // DONE
    hct74245 rom_addbbushi_buf(.A(direct_address_hi), .B(address_bus[15:8]), .nOE(_addrmode_direct), .dir(1'b1)); // DONE
     
    // ROMS
    rom #(.AWIDTH(16)) rom_6(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_5(._CS(1'b0), ._OE(1'b0), .A(pc));
    rom #(.AWIDTH(16)) rom_4(._CS(1'b0), ._OE(1'b0), .A(pc)); 
    rom #(.AWIDTH(16)) rom_3(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_2(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_1(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
   
    wire [7:0] instruction_6 = rom_6.D; // aliases
    wire [7:0] instruction_5 = rom_5.D;
    wire [7:0] instruction_4 = rom_4.D;
    wire [7:0] instruction_3 = rom_3.D;
    wire [7:0] instruction_2 = rom_2.D;
    wire [7:0] instruction_1 = rom_1.D;

    wire [47:0] instruction = {rom_6.D, rom_5.D, rom_4.D, rom_3.D, rom_2.D, rom_1.D};


    // instruction decompose
    assign immed8            = instruction_1; // DONE
    assign direct_address_lo = instruction_2; // DONE
    assign direct_address_hi = instruction_3; // DONR
    wire amode_bit           = instruction_4[0]; // DONE
    wire _set_flags_bit      = instruction_4[4]; // DONE
    wire [3:0] condition     ={instruction_5[0],instruction_4[7:5]}; // DONE
    assign bbus_dev          = instruction_5[3:1]; // DONE
    assign abus_dev          = instruction_5[6:4]; // DONE
    assign targ_dev          ={instruction_6[2:0],instruction_5[7]}; // DONE
    assign alu_op            ={instruction_6[7:3]}; // DONE 

    wire _do_exec;

    // only set flags if executing instruction 
    or #(9) ic7432_a(_set_flags, _set_flags_bit, _do_exec); // DONE

    // addressing mode
    assign _addrmode_register = amode_bit; // low = reg  // DONE
    nand #(9) ic7400_a(_addrmode_direct, amode_bit, amode_bit);  // INVERT - DONE

    // device decoders
    hct74138 bbus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(bbus_dev[2:0])); // DONE
    hct74138 abus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(abus_dev[2:0])); // DONE

    hct74138 targ_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(_do_exec), .Enable1_bar(targ_dev[3]), .A(targ_dev[2:0])); // DONE
    hct74138 targ_dev_16_demux(.Enable3(targ_dev[3]), .Enable2_bar(_do_exec), .Enable1_bar(1'b0),        .A(targ_dev[2:0])); // DONE

    // control lines for device selection
    wire [7:0] lsel = {abus_dev_08_demux.Y};
    wire [7:0] rsel = {bbus_dev_08_demux.Y};
    wire [15:0] tsel = {targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    
    `define HOOKUP_ADEV_SEL(DNAME) assign _adev_``DNAME`` = lsel[ADEV_``DNAME``]
    `define HOOKUP_BDEV_SEL(DNAME) assign _bdev_``DNAME`` = rsel[BDEV_``DNAME``]
    `define HOOKUP_TDEV_SEL(DNAME) assign _``DNAME``_in = tsel[TDEV_``DNAME``]
    
    `CONTROL_WIRES(HOOKUP, `SEMICOLON);


    wire [7:0] _flags_hi = {
            5'b0,
            _flag_do, //DO
            _flag_di, // DI
            _flags_czonGLEN[0]  // LT
            };

    wire [7:0] _flags_lo = {
            _flags_czonGLEN[1], // EQ
            _flags_czonGLEN[2], // LT
            _flags_czonGLEN[3], // GT
            _flags_czonGLEN[4], // n
            _flags_czonGLEN[5], // o
            _flags_czonGLEN[6], // z
            _flags_czonGLEN[7], // Carry
            1'b0}; // Always

    // organise two 8-to-1 multiplexers as a 16-1 multiulexer
    wire conditionTopBit = condition[3]; // DONE
    wire _conditionTopBit;
    nand #(8) ic7400_b(_conditionTopBit, conditionTopBit, conditionTopBit); // as inverter - DONE

    hct74151 #(.LOG(0)) do_exec_lo(._E(conditionTopBit),  .S(condition[2:0]), .I(_flags_lo)); // DONE
    hct74151 #(.LOG(0)) do_exec_hi(._E(_conditionTopBit), .S(condition[2:0]), .I(_flags_hi)); // DONE

    nand #(9) ic7400_c(_do_exec, do_exec_lo._Y, do_exec_hi._Y);  // DONE

    always @* begin
        if (_halt_in == 0) begin
            $display("----------------------------- HALTED %d ---------------------------", immed8);
            $finish();
        end
    end


endmodule


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
