
// FIXME MAKE ALL THE tri WIRES tri0
// verilator lint_off PINMISSING
// verilator lint_off MULTITOP

/*
TODO - use an unused istruction bit to invert the condition so "do if DO ready" becomes "do if DO not ready"

take the do_exec through a XOR gate to optionally invert the do_exec signal

use spare gates: (a or b) and (a nand b)  = a XOR b
https://en.wikipedia.org/wiki/XOR_gate see diagram three mxed gates

better logic ...

    loop:
        pchitmp = loop
        pc = loop !DO
        uart = X

existing logic ...

    loop:
        pchitmp = prt
        pc = prt DO
        pchitmp = loop
        pc = loop
    prt:
        uart = X



*/


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
    output [4:0] alu_op,
    output [2:0] bbus_dev, abus_dev,
    output [3:0] targ_dev,
    output _set_flags
);

    //----------------------------------------------------------------------------------
    // ROM wiring

    rom #(.AWIDTH(16)) rom_6(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_5(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_4(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_3(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_2(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
    rom #(.AWIDTH(16)) rom_1(._CS(1'b0), ._OE(1'b0), .A(pc)); // DONE
   
    wire [7:0] instruction_6 = rom_6.D; // aliases
    wire [7:0] instruction_5 = rom_5.D;
    wire [7:0] instruction_4 = rom_4.D;
    wire [7:0] instruction_3 = rom_3.D;
    wire [7:0] instruction_2 = rom_2.D;
    wire [7:0] instruction_1 = rom_1.D;

    // bussed for logging
    wire [47:0] instruction = {rom_6.D, rom_5.D, rom_4.D, rom_3.D, rom_2.D, rom_1.D};


    // instruction decompose
    wire [7:0] immed8            = instruction_1; // DONE
    wire [7:0] direct_address_lo = instruction_2; // DONE
    wire [7:0] direct_address_hi = instruction_3; // DONR
    wire amode_bit               = instruction_4[0]; // DONE
    wire [1:0] unused_bits       = instruction_4[2:1]; // DONE
    wire condition_invert_bit    = instruction_4[3];  // +ve logic as it makes the hardware easier using the existing components
    wire _set_flags_bit          = instruction_4[4]; // DONE
    wire [2:0] conditionBot      = instruction_4[7:5]; // DONE
    wire conditionTopBit         = instruction_5[0]; // DONE
    assign bbus_dev              = instruction_5[3:1]; // DONE
    assign abus_dev              = instruction_5[6:4]; // DONE
    assign targ_dev              ={instruction_6[2:0],instruction_5[7]}; // DONE
    assign alu_op                ={instruction_6[7:3]}; // DONE 

    wire [3:0] condition        = { conditionTopBit, conditionBot};

    //----------------------------------------------------------------------------------
    // condition logic

    // bus the flags
    wire [7:0] _flags_hi = {
            5'b0,
            _flag_do, //DO
            _flag_di, // DI
            _flags_czonGLEN[0]  // NE
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


    // need inverse of this signal so select the other mux
    wire _conditionTopBit;
    nand #(8) ic7400_c(_conditionTopBit, conditionTopBit, conditionTopBit); // as inverter - DONE

    // organises two 8-to-1 multiplexers as a 16-1 multiulexer
    hct74151 #(.LOG(0)) condition_mux_lo(._E(conditionTopBit),  .S(conditionBot), .I(_flags_lo)); // DONE
    hct74151 #(.LOG(0)) condition_mux_hi(._E(_conditionTopBit), .S(conditionBot), .I(_flags_hi)); // DONE

    // We are using _Y so the _Y will go high if the selected flag input is low (ie flat is set).
    // Also, at any moment one of the two mux's is disabled and its _Y will be high.
    // So the result state is always determined by whether active mux.
    // If the selected flag is set (ie low) then both mux's will be emitting a high and therefore the result will be a low.
    // On the other hand if the selected flag is unset (high) then the active mux will be emitting a low and the nand will return a high
    wire _condition_met; // set to low when the execution condition is met
    nand #(9) ic7400_d(_condition_met, condition_mux_lo._Y, condition_mux_hi._Y);  // DONE


    //----------------------------------------------------------------------------------
    // execution control logic

    wire _do_exec, do_exec, set_flags_bit;

    // The following three lines are the same as an OR as shown below however by using XOR (inverter) and Nand I can avoid the need for an OR gate chip and avoid an IC
    //   or #(9) ic7432_a(_set_flags, _set_flags_bit, _do_exec); 
    // enable flag update when both _set_flags_bit is active low and _do_exec is active low
    xor #(9) ic7486_b(do_exec, _do_exec, 1'b1); // use xor as inverter
    xor #(9) ic7486_d(set_flags_bit, _set_flags_bit, 1'b1); // use xor as inverter
    nand #(9) ic7400_b(_set_flags, set_flags_bit, do_exec); // use nand

    // When condition_invert_bit is active high then the conditional exec logic is reversed.
    // eg when "invert" is inactive "DO" means execute only if DO flag is set, 
    // but when "invert" is active "DO" means execute only if DO flag is not set.
    // This new feature means the NE output of the ALU is redundant as the same can be achieved using 
    // using EQ but with the condition invert enabled.
    xor #(9) ic7486_c(_do_exec, _condition_met, condition_invert_bit); // use xor as conditional inverter 

    //----------------------------------------------------------------------------------
    // address mode logic

    // when amode_bit is low this signals as enable REG mode active low
    assign _addrmode_register = amode_bit; // low = reg  // DONE

    // when amode_bit is high then this signals as enable DIR mode active low
    wire _addrmode_direct; 
    nand #(9) ic7400_a(_addrmode_direct, amode_bit, amode_bit);  // INVERT - DONE

    //----------------------------------------------------------------------------------
    // alu input device selection

    // device decoders
    hct74138 bbus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(bbus_dev[2:0])); // DONE
    hct74138 abus_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(abus_dev[2:0])); // DONE

    hct74138 targ_dev_08_demux(.Enable3(1'b1),        .Enable2_bar(_do_exec), .Enable1_bar(targ_dev[3]), .A(targ_dev[2:0])); // DONE
    hct74138 targ_dev_16_demux(.Enable3(targ_dev[3]), .Enable2_bar(_do_exec), .Enable1_bar(1'b0),        .A(targ_dev[2:0])); // DONE
    
    //----------------------------------------------------------------------------------
    // hookup the imediate values and direct address lines to the bus output buffers

    hct74245 rom_bbus_buf(.A(immed8), .B(bbus), .nOE(_bdev_immed), .dir(1'b1));  // DONE
    hct74245 rom_addbbuslo_buf(.A(direct_address_lo), .B(address_bus[7:0]),  .nOE(_addrmode_direct), .dir(1'b1));  // DONE
    hct74245 rom_addbbushi_buf(.A(direct_address_hi), .B(address_bus[15:8]), .nOE(_addrmode_direct), .dir(1'b1)); // DONE
    
    //----------------------------------------------------------------------------------
    // hookup all the signals from the decoders to the output wires

    // control lines for device selection
    wire [7:0] adev_sel = {abus_dev_08_demux.Y};
    wire [7:0] bdev_sel = {bbus_dev_08_demux.Y};
    wire [15:0] tdev_sel = {targ_dev_16_demux.Y, targ_dev_08_demux.Y};

    // define the functions to hookup the lines
    `define HOOKUP_ADEV_SEL(DNAME) assign _adev_``DNAME`` = adev_sel[ADEV_``DNAME``]
    `define HOOKUP_BDEV_SEL(DNAME) assign _bdev_``DNAME`` = bdev_sel[BDEV_``DNAME``]
    `define HOOKUP_TDEV_SEL(DNAME) assign _``DNAME``_in   = tdev_sel[TDEV_``DNAME``]
    
    // apply the functions to the lines
    `CONTROL_WIRES(HOOKUP, `SEMICOLON);


endmodule


`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
