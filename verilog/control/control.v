`ifndef V_CONTROL
`define V_CONTROL


`include "../74138/hct74138.v"
`include "../74139/hct74139.v"
`include "../74245/hct74245.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps

module control (
    input [7:0] hi_rom,

    output _rom_out, _ram_out, _alu_out, _uart_out,

    output force_alu_op_to_passx,
    output force_x_val_to_zero,
    output _ram_zp,
    
    // decoded

    input _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
    input _uart_in_ready, _uart_out_ready,

    output _ram_in, _marlo_in, _marhi_in, _uart_in, 
    output _pchitmp_in, // load hi tmp reg
    output _pclo_in, // load lo pc reg only
    output _pc_in, // load high (from tmp)
    
    output _reg_in

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

    // bank 0
    parameter [4:0] idx_RAM_sel      = 0;
    parameter [4:0] idx_MARLO_sel    = 1;
    parameter [4:0] idx_MARHI_sel    = 2;
    parameter [4:0] idx_UART_sel     = 3;
    parameter [4:0] idx_PCHITMP_sel  = 4;
    parameter [4:0] idx_PCLO_sel     = 5;
    parameter [4:0] idx_PC_sel       = 6;
    parameter [4:0] idx_JMPO_sel     = 7;

    // bank 1
    parameter [4:0] idx_JMPZ_sel     = 0;
    parameter [4:0] idx_JMPC_sel     = 1;
    parameter [4:0] idx_JMPDI_sel    = 2;
    parameter [4:0] idx_JMPDO_sel    = 3;
    parameter [4:0] idx_JMPEQ_sel    = 4;
    parameter [4:0] idx_JMPNE_sel    = 5;
    parameter [4:0] idx_JMPGT_sel    = 6;
    parameter [4:0] idx_JMPLT_sel    = 7;

    // bank 2-3
    parameter [4:0] idx_REGA_sel     = 16;
    parameter [4:0] idx_REGP_sel     = 31;

    // wiring

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
    
    // write device
    wire _ram_write_override = _decodedOp[op_RAMZP_eq_REG_sel] && _decodedOp[op_RAMZP_eq_UART_sel];
    wire _is_non_reg_override = _decodedOp[op_NONREG_eq_OPREGY_sel] && _ram_write_override;
    wire _is_reg_override = _decodedOp[op_REGX_eq_ALU_sel];
    wire implied_dev_top_bit = hi_rom[0];

    wire reg_in = (implied_dev_top_bit && _is_non_reg_override) || !_is_reg_override;

    wire [4:0] device_sel_pre = {reg_in, hi_rom[4:1]}; // pull down top bit if this instruction applies to non-reg as that bit is used by ALU


    wire [7:0] device_sel_in = {3'b0, device_sel_pre};
    wire [7:0] device_sel_out;
    wire [4:0] device_sel = device_sel_out[4:0]; 

    // DO I NEED THIS
    //hct74245 bufDeviceSel(.A(device_sel_in), .B(device_sel_out), .dir(1'b1), .nOE( ! _ram_write_override )); 
    hct74245 bufDeviceSel(.A(device_sel_in), .B(device_sel_out), .dir(1'b1), .nOE( 1'b0 )); 
    pulldown pullDeviceToZero[7:0](device_sel_out);

    /////////////////////
    // everything else is device decode and jump logic
    /////////////////////

    assign _reg_in = ! device_sel_out[4];

    wire [7:0] _decodedDevLo;
    hct74138 decoderLoDev(.Enable3(1'b1), .Enable2_bar(reg_in), .Enable1_bar(device_sel[3]), .A(device_sel[2:0]), .Y(_decodedDevLo));
    
    wire [7:0] _decodedDevHi;
    hct74138 decoderHiDev(.Enable3(device_sel[3]), .Enable2_bar(reg_in), .Enable1_bar(1'b0), .A(device_sel[2:0]), .Y(_decodedDevHi));

    assign  _ram_in = (_decodedDevLo[idx_RAM_sel] && _decodedOp[op_RAMZP_eq_REG_sel] && _decodedOp[op_RAMZP_eq_UART_sel]) || ! _ram_out ; // combine with _ram_out prevents ram_in and doing ram_out

    assign  _marlo_in = _decodedDevLo[idx_MARLO_sel];
    assign  _marhi_in= _decodedDevLo[idx_MARHI_sel];
    assign  _uart_in= _decodedDevLo[idx_UART_sel];

    wire _jmpo_in, _jmpz_in, _jmpc_in, _jmpdi_in, _jmpdo_in, _jmpeq_in, _jmpne_in, _jmpgt_in, _jmplt_in;

    assign  _jmpo_in= _decodedDevLo[idx_JMPO_sel] || _flag_o;
    
    assign  _jmpz_in= _decodedDevHi[idx_JMPZ_sel] || _flag_z;
    assign  _jmpc_in= _decodedDevHi[idx_JMPC_sel] || _flag_c;
    assign  _jmpdi_in= _decodedDevHi[idx_JMPDI_sel] || _uart_in_ready;
    assign  _jmpdo_in= _decodedDevHi[idx_JMPDO_sel] || _uart_out_ready;
    assign  _jmpeq_in= _decodedDevHi[idx_JMPEQ_sel] || _flag_eq;
    assign  _jmpne_in= _decodedDevHi[idx_JMPNE_sel] || _flag_ne;
    assign  _jmpgt_in= _decodedDevHi[idx_JMPGT_sel] || _flag_gt;
    assign  _jmplt_in= _decodedDevHi[idx_JMPLT_sel] || _flag_lt;

    assign  _pchitmp_in= _decodedDevLo[idx_PCHITMP_sel];
    assign  _pclo_in= _decodedDevLo[idx_PCLO_sel];
    assign  _pc_in= _decodedDevLo[idx_PC_sel] && _jmpo_in && _jmpz_in && _jmpc_in && _jmpdi_in && _jmpdo_in && _jmpeq_in && _jmpne_in && _jmpgt_in && _jmplt_in;
    
    
     always @ * 
         $display("%5d", $time,
          " hi=%08b", hi_rom, 
            // " op_sel=%03b ", operation_sel, " dev_sel=%05b ", device_sel, 
            " devHi=%08b" , _decodedDevHi,
            " devLo=%08b" , _decodedDevLo,
            // " _force_x_0=%1b", force_x_val_to_zero, 
            // " force_alu_passx=%1b", force_alu_op_to_passx, 
            " _ram_in=%1b", _ram_in, 
            " ram_write_ov=%1b", _ram_write_override, 
            " reg_in=%1b", reg_in, 
            //" dsin=%5b", device_sel_in[4:0] , 
            " dev_write=%5b", device_sel_out[4:0] , 
            " zp=%1b", _ram_zp, 
            //" _is_non_reg_override=%1b",_is_non_reg_override,
            //" _is_reg_override=%1b", _is_reg_override, 
            //" implied_dev_top_bit=%1b", implied_dev_top_bit
);

endmodule : control
// verilator lint_on ASSIGNDLY

`endif
