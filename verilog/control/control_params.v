
`ifndef V_CONTROL_PARAMS
`define V_CONTROL_PARAMS

`timescale 1ns/1ns

module control_params;

// ops
localparam [2:0] OP_dev_eq_xy_alu =0;
localparam [2:0] OP_dev_eq_const8 =1;
localparam [2:0] OP_dev_eq_const16 =2;
localparam [2:0] OP_3_unused =3;
localparam [2:0] OP_dev_eq_rom_immed =4;
localparam [2:0] OP_dev_eq_ram_immed =5;
localparam [2:0] OP_ram_immed_eq_dev =6;
localparam [2:0] OP_7_unused =7;

// sources
localparam [3:0] DEV_ram = 0;
localparam [3:0] DEV_rom = 1;
localparam [3:0] DEV_marlo = 2;
localparam [3:0] DEV_marhi = 3;

// targets
localparam [4:0] TDEV_ram = {1'b0, DEV_ram};
localparam [4:0] TDEV_rom = {1'b0, DEV_rom};
localparam [4:0] TDEV_marlo = {1'b0, DEV_marlo};
localparam [4:0] TDEV_marhi = {1'b0, DEV_marhi};

endmodule: control_params

`endif