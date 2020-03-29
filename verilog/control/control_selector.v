`ifndef V_CONTROL
`define V_CONTROL


`include "../74138/hct74138.v"
`include "../74139/hct74139.v"
`include "../74245/hct74245.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps

module control_selector (
    input [7:0] hi_rom,

    output _rom_out, _ram_out, _alu_out, _uart_out,

    output force_alu_op_to_passx,
    output force_x_val_to_zero,
    output _ram_zp,

    output [4:0] device_in
);

    // constants
    parameter [2:0] op_DEV_eq_ROM_sel = 0;
    parameter [2:0] op_DEV_eq_RAM_sel = 1;
    parameter [2:0] op_DEV_eq_RAMZP_sel = 2;
    parameter [2:0] op_DEV_eq_UART_sel = 3;
    parameter [2:0] op_NONREG_eq_OPREGY_sel = 4;
    parameter [2:0] op_REGX_eq_ALU_sel = 5;
    parameter [2:0] op_RAMZP_eq_REG_sel = 6;
    parameter [2:0] op_RAMZP_eq_UART_sel = 7;

    // wiring
    // dual and     5 = 1x7408 quad dual and and use one of the 7411s
    // triple and   2 =	1x7411 triple triple input, use one gate as dual
    // not          4 =	1x7404 inverter
    // or           1 =	1x7432 quad or
    // or 17 diodes and 3 not and the 74245 can be replaced by 10 diodes in an AND cfg
    // plus             1x74245 buf
    //                  1x74138 decoder


    wire [2:0] operation_sel = hi_rom[7:5];
    wire [7:0] _decodedOp;
    hct74138 opDecoder(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(operation_sel), .Y(_decodedOp));
    
    // BUS ACCESS
    assign  _rom_out = _decodedOp[op_DEV_eq_ROM_sel];
    assign  _ram_out = _decodedOp[op_DEV_eq_RAM_sel] && _decodedOp[op_DEV_eq_RAMZP_sel];
    assign  _alu_out = _decodedOp[op_NONREG_eq_OPREGY_sel] && _decodedOp[op_REGX_eq_ALU_sel] && _decodedOp[op_RAMZP_eq_REG_sel];
    assign  _uart_out = _decodedOp[op_DEV_eq_UART_sel] && _decodedOp[op_RAMZP_eq_UART_sel];

    // _ram_zp will turn off the ram address buffers letting HiAddr pull down to 0 and will turn on ROM->MARLO for the lo addr
    assign _ram_zp = _decodedOp[op_DEV_eq_RAMZP_sel] && _decodedOp[op_RAMZP_eq_REG_sel] && _decodedOp[op_RAMZP_eq_UART_sel];
    
    assign force_x_val_to_zero = !_decodedOp[op_NONREG_eq_OPREGY_sel];  // +ve logic needed - EXTRA GATE
    assign force_alu_op_to_passx = !_decodedOp[op_RAMZP_eq_REG_sel]; // +ve logic needed - EXTRA GATE
    
    // write device - sometimes bit 0 is a device bit and sometimes ALU op bit
    wire _ram_write_override = _decodedOp[op_RAMZP_eq_REG_sel] && _decodedOp[op_RAMZP_eq_UART_sel];
    wire _is_non_reg_override = _decodedOp[op_NONREG_eq_OPREGY_sel] && _ram_write_override;
    wire _is_reg_override = _decodedOp[op_REGX_eq_ALU_sel];
    wire implied_dev_top_bit = hi_rom[0];

    wire reg_in = (implied_dev_top_bit && _is_non_reg_override) || !_is_reg_override; // EXTRA GATE

    wire [4:0] device_sel_pre = {reg_in, hi_rom[4:1]}; // pull down top bit if this instruction applies to non-reg as that bit is used by ALU

    // apply ram write device override
    wire [7:0] device_sel_in = {3'b0, device_sel_pre};
    wire [7:0] device_sel_out;

    hct74245 bufDeviceSel(.A(device_sel_in), .B(device_sel_out), .dir(1'b1), .nOE( !_ram_write_override ));  // EXTRA GATE
    pulldown pullDeviceToZero[7:0](device_sel_out);

    // return
    assign device_in = device_sel_out[4:0];

    always @ * 
         $display("%5d", $time,
          " hi=%08b", hi_rom, 
            // " op_sel=%03b ", operation_sel, " dev_sel=%05b ", device_sel, 
            " _rom_out=%1b, _ram_out=%1b, _alu_out=%1b, _uart_out=%1b", _rom_out, _ram_out, _alu_out, _uart_out,
            " device_in=%5b", device_in, 
            " force_x_0=%1b", force_x_val_to_zero, 
            " force_alu_passx=%1b", force_alu_op_to_passx, 
            " _ram_zp=%1b", _ram_zp, 
            //" _is_non_reg_override=%1b",_is_non_reg_override,
            //" _is_reg_override=%1b", _is_reg_override, 
            //" implied_dev_top_bit=%1b", implied_dev_top_bit
);

endmodule : control_selector
// verilator lint_on ASSIGNDLY

`endif
