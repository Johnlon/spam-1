
`include "./control.v"
`include "./control_decoding.v"
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
    wire [4:0] aluop;

	op_decoder #(.LOG(0)) decoder( .data_hi, .data_mid, .data_lo, .rbus_dev, .lbus_dev, .targ_dev, .aluop);

    localparam T=100;   // clock cycle
    localparam SETTLE_TOLERANCE=20;

    initial begin
        `ifndef verilator
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);
        `endif
    end

    initial begin

        d.display("OP_dev_eq_xy_alu");
        
        {data_hi, data_mid, data_lo} = {3'd0, 5'b10101, 3'bxxx, 4'b1101, 4'b1110,  5'b01100}; // op, devt, x3, regl, regr, alu

        #T
        `Equals( targ_dev, 5'b10101)
        `Equals( lbus_dev, 4'b1101)
        `Equals( rbus_dev, 4'b1110)
        `Equals( aluop, 5'b01100)


        d.display("OP_dev_eq_const8");
        
        {data_hi, data_mid, data_lo} = {3'd1, 5'b10101, 8'bx, 8'b10101010}; // op, devt, x8, const8

        #T
        `Equals( targ_dev, 5'b10101)
        `Equals( lbus_dev, 4'bxxxx) // not used
        `Equals( rbus_dev, control_params.DEV_rom)
        `Equals( aluop, alu_func.ALUOP_PASSR)


        d.display("OP_dev_eq_rom_immed");
        
        {data_hi, data_mid, data_lo} = {3'd4, 5'b10101, 16'b0101001100011100}; // op, devt, const16

        #T
        `Equals( targ_dev, 5'b10101) 
        `Equals( lbus_dev, 4'b1001) // ignored
        `Equals( rbus_dev, control_params.DEV_rom)
        `Equals( aluop, alu_func.ALUOP_PASSR)

        $display("testing end");
    end

endmodule : test
