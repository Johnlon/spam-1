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

    input _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
    input _uart_in_ready, _uart_out_ready,

    output _rom_out, _ram_out, _alu_out, _uart_out,
    
    output _ram_in, _marlo_in, _marhi_in, _uart_in, 
    output _pchitmp_in, // load hi tmp reg
    output _pclo_in, // load lo pc reg only
    output _pc_in, // load high (from tmp)
    
    output _reg_in,

    output force_alu_op_to_passx,
    output force_x_val_to_zero,

    output _ram_zp
);

    wire [2:0] operation_sel = hi_rom[7:5];
    wire [7:0] _decodedOp;
    hct74138 opDecoder(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(operation_sel), .Y(_decodedOp));
    
    assign  _rom_out = _decodedOp[0]; // diode logic?
    assign  _ram_out = _decodedOp[1] && _decodedOp[2]; // diodes? 
    assign  _alu_out = _decodedOp[3] && _decodedOp[4] && _decodedOp[5];
    assign  _uart_out = _decodedOp[6] && _decodedOp[7];

    // _ram_zp will turn off the ram address buffers letting HiAddr pull down to 0 and will turn on ROM->MARLO for the lo addr
    assign _ram_zp = _decodedOp[2] && _decodedOp[3] && _decodedOp[7];
    
    assign force_x_val_to_zero = !_decodedOp[4];  // +ve logic needed - EXTRA GATE
    assign force_alu_op_to_passx = !_decodedOp[3]; // +ve logic needed - EXTRA GATE
    
    wire _is_non_reg = _decodedOp[4];
    wire [4:0] device_sel = {hi_rom[0] && _is_non_reg, hi_rom[4:1]}; // pull down top bit if this instruction applies to non-reg as that bit is used by ALU

    wire device_is_reg = device_sel[4];

    wire [7:0] _decodedDevLo;
    hct74138 decoderLoDev(.Enable3(1'b1), .Enable2_bar(device_is_reg), .Enable1_bar(device_sel[3]), .A(device_sel[2:0]), .Y(_decodedDevLo));
    
    wire [7:0] _decodedDevHi;
    hct74138 decoderHiDev(.Enable3(device_sel[3]), .Enable2_bar(device_is_reg), .Enable1_bar(1'b0), .A(device_sel[2:0]), .Y(_decodedDevHi));
    
    assign _reg_in = ! (
        (!_rom_out && device_is_reg) || 
        (!_ram_out && device_is_reg) ||
        (!_alu_out && _ram_zp && device_is_reg) || 
        (!_decodedOp[5]) ||
        (!_decodedOp[6] && device_is_reg)
    );
    
    assign  _ram_in = (_decodedDevLo[0] && _decodedOp[3] && _decodedOp[7]) || ! _ram_out ;  // combine with _ram_out prevents ram_in and doing ram_out
    assign  _marlo_in = _decodedDevLo[1];
    assign  _marhi_in= _decodedDevLo[2];
    assign  _uart_in= _decodedDevLo[3];

    wire _jmpo_in, _jmpz_in, _jmpc_in, _jmpdi_in, _jmpdo_in, _jmpeq_in, _jmpne_in, _jmpgt_in, _jmplt_in;

    assign  _jmpo_in= _decodedDevLo[7] || _flag_o;
    
    assign  _jmpz_in= _decodedDevHi[0] || _flag_z;
    assign  _jmpc_in= _decodedDevHi[1] || _flag_c;
    assign  _jmpdi_in= _decodedDevHi[2] || _uart_in_ready;
    assign  _jmpdo_in= _decodedDevHi[3] || _uart_out_ready;
    assign  _jmpeq_in= _decodedDevHi[4] || _flag_eq;
    assign  _jmpne_in= _decodedDevHi[5] || _flag_ne;
    assign  _jmpgt_in= _decodedDevHi[6] || _flag_gt;
    assign  _jmplt_in= _decodedDevHi[7] || _flag_lt;

    assign  _pchitmp_in= _decodedDevLo[4];
    assign  _pclo_in= _decodedDevLo[5];
    assign  _pc_in= _decodedDevLo[6] && _jmpo_in && _jmpz_in && _jmpc_in && _jmpdi_in && _jmpdo_in && _jmpeq_in && _jmpne_in && _jmpgt_in && _jmplt_in;
    
    
    // always @ * 
    //     $display(
    //      " hi=%08b", hi_rom, 
    //         " op_sel=%03b ", operation_sel, " dev_sel=%05b ", device_sel, 
    //         " => devHi=%08b" , _decodedDevHi," devLo=%08b" , _decodedDevLo,
    //         " _force_x_to_zero=%1b", force_x_val_to_zero, 
    //         " force_alu_op_to_passx=%1b", force_alu_op_to_passx, 
    //         " zp=%1b", _ram_zp, " isreg=%1b", device_is_reg);

endmodule : control
// verilator lint_on ASSIGNDLY

`endif
