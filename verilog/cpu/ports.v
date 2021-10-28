
`ifndef V_PORTS
`define V_PORTS

`timescale 1ns/1ns

package ports;
 
    localparam  PORT_RD_RANDOM = 0;
    localparam  PORT_RD_GAMEPAD1 = 1;
    localparam  PORT_RD_GAMEPAD2 = 2;
    localparam  PORT_RD_PARALLEL = 7;

    localparam  PORT_WR_PARALLEL = 7;

endpackage

`endif
