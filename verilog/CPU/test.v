
`include "../lib/assertion.v"
`include "cpu.v"

`default_nettype none

`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../control/control.v"
`include "../alu/alu.v"
`include "../pc/pc.v"

`timescale 1ns/1ns

module test();
    task clkpulse;
      begin
      #50
      CP= 1'b0;
      #50
      CP= 1'b1;
      end
    endtask

    logic CP=1'b0;
    tri [7:0] data_bus;

    // PROGRAM COUNTER 
    logic _POWER_ON_RESET=1'b0;
    logic _MR;
    logic _PCLOin = 1'b1;
    logic _PCHIin = 1'b1;
    logic _PCHITMPin = 1'b1;
    wire [7:0] PCHI, PCLO;

    // ROM IO
    logic [14:0] rom_address;
    tri [7:0] rom_hi_data;
    tri [7:0] rom_lo_data;

    // BUS CONTROL
    logic _rom_out = 1'b0;


    // COMPONENTS

    pc PC (
      .CP(CP),
      ._MR(_MR),
      ._PCHIin(_PCHIin),
      ._PCLOin(_PCLOin),
      ._PCHITMPin(_PCHITMPin),
      .D(data_bus),

      .PCLO(PCLO),
      .PCHI(PCHI)
    );

    
    rom #(.AWIDTH(15), .Filename("hi.rom")) rom_hi(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_hi_data));
    rom #(.AWIDTH(15), .Filename("lo.rom")) rom_lo(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_lo_data));

    hct74245 bufROMBUS(.A(rom_lo_data), .B(data_bus), .dir(1'b1), .nOE(_rom_out));
    hct74245 bufROMZP(.A(rom_lo_data), .B(data_bus), .dir(1'b1), .nOE(_rom_out));


    assign rom_address = { PCHI[6:0], PCLO };

    // THREADS ...
    always @(_MR)
        $display("%10d: _MR=%1b", $time, _MR);

    always @(*)
        $display("%10d: CP=%1b PC=%4x ROM=%8b,%8b BUS=%2x", $time, CP, rom_address, rom_hi_data, rom_lo_data, data_bus);
        
    always begin
        #100 CP = ! CP; 
    end
    
    initial _MR = _POWER_ON_RESET;
    initial #200 _MR=1'b1;
    initial #1000  $finish; 

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test );
    end


endmodule

