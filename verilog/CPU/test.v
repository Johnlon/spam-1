
`include "../lib/assertion.v"
`include "cpu.v"

`timescale 1ns/100ps
`default_nettype none

`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../control/control.v"
`include "../alu/alu.v"
`include "../pc/pc.v"
`include "../74163/hct74163.v"


module test();
    
    logic _rom_out;
    logic [14:0] rom_address;
    tri [7:0] rom_hi_data;
    tri [7:0] rom_lo_data;


    assert(1!=2);
    rom #(.AWIDTH(15), .Filename("hi.rom")) rom_hi(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_hi_data));
    rom #(.AWIDTH(15), .Filename("lo.rom")) rom_lo(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_lo_data));

        

endmodule

