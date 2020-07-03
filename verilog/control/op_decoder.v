
`ifndef V_CONTROL_OP_DECODER
`define V_CONTROL_OP_DECODER

`include "../74138/hct74138.v"
`include "../74245/hct74245.v"
`include "../alu/alu_func.v"



`timescale 1ns/1ns

module op_decoder #(parameter LOG=1) 
(
    input [7:0] data_hi, data_mid, data_lo,

    output tri [3:0] rbus_dev,
    output tri [3:0] lbus_dev,
    output tri [4:0] targ_dev,
    output tri [4:0] alu_op
);

    wire [23:0] rom_data = {data_hi, data_mid, data_lo};
    wire [2:0] ctrl = rom_data[23:21];

    hct74138 op_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(ctrl));

    wire _op_dev_eq_xy_alu;
    wire _op_dev_eq_const8;
    wire _op_dev_eq_const16;
    wire _op_3_unused;
    wire _op_dev_eq_rom_direct;
    wire _op_dev_eq_ram_direct;
    wire _op_ram_direct_eq_dev;
    wire _op_7_unused;
    assign {
        _op_7_unused,
        _op_ram_direct_eq_dev,
        _op_dev_eq_ram_direct,
        _op_dev_eq_rom_direct,
        _op_3_unused,
        _op_dev_eq_const16,
        _op_dev_eq_const8,
        _op_dev_eq_xy_alu
    } = op_demux.Y;


     // target device sel
    tri [7:0] targ_dev_out; 
    wire #(10) op_ram_direct_eq_dev = ! _op_ram_direct_eq_dev;  // NOT GATE
    hct74245ab tdev_from_instruction(.A({3'bz, rom_data[20:16]}), .B(targ_dev_out), .nOE(op_ram_direct_eq_dev)); // ie when NOT a ram direct then user the 20:16
    hct74245ab tdev_eq_ram(.A({3'b0, control.TDEV_ram}), .B(targ_dev_out), .nOE(_op_ram_direct_eq_dev)); // only op_ram_direct_eq_dev has targ forced to RAM
    assign targ_dev = targ_dev_out[4:0];

    // l device sel 
    tri [7:0] lbus_dev_out;
    hct74245ab ldev_from_instruction(.A({4'b0, rom_data[12:9]}), .B(lbus_dev_out), .nOE(op_ram_direct_eq_dev));
    hct74245ab ldev_from_instruction_ramdirect_eq_dev(.A({4'b0, rom_data[19:16]}), .B(lbus_dev_out), .nOE(_op_ram_direct_eq_dev));
    assign lbus_dev = lbus_dev_out[3:0];

    // r device sel
    tri [7:0] rbus_dev_out;
    wire _force_source_rom = _op_dev_eq_rom_direct; // JUST A WIRE
    wire #(10) _force_source_instreg = _op_dev_eq_const8 &  _op_dev_eq_const16; // 2 INPUT AND GATE
    hct74245ab rdev_from_instruction_for_aluop(.A({4'b0, rom_data[8:5]}), .B(rbus_dev_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab rdev_eq_ram(.A({4'b0, control.DEV_ram}), .B(rbus_dev_out), .nOE(_op_dev_eq_ram_direct));
    hct74245ab rdev_eq_rom(.A({4'b0, control.DEV_rom}), .B(rbus_dev_out), .nOE(_force_source_rom));
    hct74245ab rdev_eq_instreg(.A({4'b0, control.DEV_instreg}), .B(rbus_dev_out), .nOE(_force_source_instreg));
    assign rbus_dev = rbus_dev_out[3:0]; 

    // aluop
    tri [7:0] alu_op_out;
    wire #(10) _force_passb = _force_source_rom & _op_dev_eq_ram_direct & _force_source_instreg; // source ram or rom or ireg means passb : 3 INPUT AND GATE
    hct74245ab aluopfrom_instruction(.A({3'b0, rom_data[4:0]}), .B(alu_op_out), .nOE(_op_dev_eq_xy_alu));
    hct74245ab aluop_eq_passa(.A({3'b0, alu_func.ALUOP_PASSA}), .B(alu_op_out), .nOE(_op_ram_direct_eq_dev));
    hct74245ab aluop_eq_passb(.A({3'b0, alu_func.ALUOP_PASSB}), .B(alu_op_out), .nOE(_force_passb));
    assign alu_op = alu_op_out[4:0];

    if (LOG)    
    always @ *  begin
         $display("%9t OP_DECODER", $time,
                " data=%8b:%8b:%8b", data_hi, data_mid, data_lo,
                " ctrl=%3b (%s)\t ", ctrl, control.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y,
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " alu_op=%5b(%s)", alu_op, alu_func.aluopName(alu_op)
            );
    end

    task DUMP; begin
        $display("%9t OP_DECODER", $time,
                " data=%8b:%8b:%8b", data_hi, data_mid, data_lo,
                " ctrl=%3b (%s)", ctrl, control.opName(ctrl),
                " _decodedOp=%8b", op_demux.Y
            );
        $display("%9t OP_DECODER", $time,
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " alu_op=%5b(%s)", alu_op, alu_func.aluopName(alu_op)
            );
    end 
    endtask

endmodule : op_decoder
`endif
