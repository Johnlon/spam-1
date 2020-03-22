
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
    task clkpulse;
      begin
      #50
      CP= 1'b0;
      #50
      CP= 1'b1;
      end
    endtask

    logic CP=1'b0;
    logic _POWER_ON_RESET=1'b0;
    logic _MR = _POWER_ON_RESET;
    logic _PCLOin = 1'b1;
    logic _PCHIin = 1'b1;
    logic _PCHITMPin = 1'b1;
    logic [7:0] D;
    wire [7:0] PCHI, PCLO;


    pc PC (
      .CP(CP),
      ._MR(_MR),
      ._PCHIin(_PCHIin),
      ._PCLOin(_PCLOin),
      ._PCHITMPin(_PCHITMPin),
      .D(D),

      .PCLO(PCLO),
      .PCHI(PCHI)
    );

    
    logic _rom_out;
    logic [14:0] rom_address;
    tri [7:0] rom_hi_data;
    tri [7:0] rom_lo_data;

    rom #(.AWIDTH(15), .Filename("hi.rom")) rom_hi(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_hi_data));
    rom #(.AWIDTH(15), .Filename("lo.rom")) rom_lo(._CS(1'b0), ._OE(_rom_out), .Address(rom_address), .Data(rom_lo_data));


    always @(_MR)
        $display("%10d: _MR=%1b", $time, _MR);

    always @(*)
        $display("%10d: CP=%1b PC=%8b,%8b", $time, CP, PCHI, PCLO);
        
    always begin
        #100 CP = ! CP; 
    end
    
    initial #200 _MR=1'b1;
    initial #1000  $finish; 

endmodule

