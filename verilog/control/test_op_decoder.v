
`include "./control.v"
`include "../lib/assertion.v"
`include "../lib/display.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns


module test();

    display d();

    logic [7:0] data_hi, data_mid, data_lo;

    wire [3:0] lbus_dev;
    wire [3:0] rbus_dev;
    wire [4:0] targ_dev;
    wire [4:0] alu_op;

	op_decoder #(.LOG(1)) decoder( .data_hi, .data_mid, .data_lo, .rbus_dev, .lbus_dev, .targ_dev, .alu_op);

    localparam T=100;   // clock cycle
    localparam SETTLE_TOLERANCE=20;

    initial begin
        `ifndef verilator
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
        `endif
    end

    
    initial begin

        d.display("0: OP_dev_eq_xy_alu");
        
        {data_hi, data_mid, data_lo} = {3'd0, control.TDEV_uart, 3'bxxx, control.DEV_marlo, control.DEV_marhi, alu_func.ALUOP_ZERO}; // op, devt, x3, regl, regr, alu

        #T
        `Equals( targ_dev, control.TDEV_uart)
        `Equals( lbus_dev, control.DEV_marlo)
        `Equals( rbus_dev, control.DEV_marhi)
        `Equals( alu_op, alu_func.ALUOP_ZERO)


        d.display("1: OP_dev_eq_const8");
        
        {data_hi, data_mid, data_lo} = {3'd1, control.TDEV_marlo, 8'bx, 8'b10101010}; // op, devt, x8, const8

        #T
        `Equals( targ_dev, control.TDEV_marlo)
        `Equals( lbus_dev, 4'bxxxx)  // passed thru but irrelevant for this operation as [15:0] go to the address bus
        `Equals( rbus_dev, control.DEV_instreg)
        `Equals( alu_op, alu_func.ALUOP_PASSR)

        d.display("4: OP_dev_eq_rom_direct");
        
        {data_hi, data_mid, data_lo} = {3'd4, control.TDEV_marlo, 16'b010xzxz100011100}; // op, devt, addr16

        #T
        `Equals( targ_dev, control.TDEV_marlo) 
        `Equals( lbus_dev, 4'bxzxz)  // passed thru but irrelevant for this operation as [15:0] go to the address bus
        `Equals( rbus_dev, control.DEV_rom)
        `Equals( alu_op, alu_func.ALUOP_PASSR)

        d.display("5: OP_dev_eq_ram_direct");
        
        {data_hi, data_mid, data_lo} = {3'd5, control.TDEV_marlo, 16'b010xzxz100011100}; // op, devt, addr16

        #T
        `Equals( targ_dev, control.TDEV_marlo) 
        `Equals( lbus_dev, 4'bxzxz) // passed thru but irrelevant for this operation as [15:0] go to the address bus
        `Equals( rbus_dev, control.DEV_ram)
        `Equals( alu_op, alu_func.ALUOP_PASSR)

        d.display("6: OP_ram_direct_eq_dev");
        
        {data_hi, data_mid, data_lo} = {3'd6, 1'b0, control.DEV_marlo, 16'b010xzxz100011100}; // op, devt, addr16

        #T
        `Equals( targ_dev, control.TDEV_ram) 
        `Equals( lbus_dev, control.DEV_marlo) // passed thru but irrelevant for this operation as [15:0] go to the address bus 
        `Equals( rbus_dev, 4'bzzzz)  // because the control logic at present doesn't set it at all in this case - but if we use tri0 then this will be 0
        `Equals( alu_op, alu_func.ALUOP_PASSL)
        
        d.display("test end");

    end

endmodule : test
