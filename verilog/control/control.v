
`include "../74138/hct74138.v"
`include "../74139/hct74139.v"
`include "../74245/hct74245.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY


`timescale 1ns/100ps
module control (
    input [7:0] hi_rom,
//
//    input flag_z,
//    input flag_c,
//    input flag_o,
//    input flag_eq,
//    input flag_ne,
//    input flag_gt,
//    input flag_lt,
//
//    input uart_in_ready,
//    input uart_out_ready,
//
    output rom_out_n,
    output ram_out_n,
    output alu_out_n,
    output uart_out_n,
    
    output ram_in_n,
    output marlo_in_n,
    output marhi_in_n,
    output uart_in_n,
    output pchitmp_in_n,
    output pclo_in_n,
    output jmp_in_n,
    output jmpo_in_n,
    
    output jmpz_in_n,
    output jmpc_in_n,
    output jmpdi_in_n,
    output jmpdo_in_n,
    output jmpeq_in_n,
    output jmpne_in_n,
    output jmpgt_in_n,
    output jmplt_in_n,

    output reg_in_n,
    output force_alu_op_to_passx,
    output force_x_val_to_zero_n,

    output ram_zp_n
);

    wire [2:0] operation_sel = hi_rom[7:5];
    wire [7:0] _decodedOp;
    hct74138 opDecoder(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(operation_sel), .Y(_decodedOp));
    
    assign  rom_out_n = _decodedOp[0]; // diode logic?
    assign  ram_out_n = _decodedOp[1] && _decodedOp[2]; // diodes? 
    assign  alu_out_n = _decodedOp[3] && _decodedOp[4] && _decodedOp[5];
    assign  uart_out_n = _decodedOp[6] && _decodedOp[7];

    // ram_zp_n will turn off the ram address buffers letting HiAddr pull down to 0 and will turn on ROM->MARLO for the lo addr
    assign ram_zp_n = _decodedOp[2] && _decodedOp[3] && _decodedOp[7];
    
    assign force_x_val_to_zero_n = _decodedOp[4];  
    assign force_alu_op_to_passx = !_decodedOp[3]; // +vs logic needed - EXTRA GATE
    
    wire is_non_reg_n = _decodedOp[4];
    wire [4:0] device_sel = {hi_rom[0] && is_non_reg_n, hi_rom[4:1]}; // pull down top bit if this instruction applies to non-reg as that bit is used by ALU

    wire device_is_reg = device_sel[4];

    wire [7:0] _decodedDevLo;
    hct74138 decoderLoDev(.Enable3(1'b1), .Enable2_bar(device_is_reg), .Enable1_bar(device_sel[3]), .A(device_sel[2:0]), .Y(_decodedDevLo));
    
    wire [7:0] _decodedDevHi;
    hct74138 decoderHiDev(.Enable3(device_sel[3]), .Enable2_bar(device_is_reg), .Enable1_bar(1'b0), .A(device_sel[2:0]), .Y(_decodedDevHi));
    
    assign reg_in_n = ! (
        (!rom_out_n && device_is_reg) || 
        (!ram_out_n && device_is_reg) ||
        (!alu_out_n && ram_zp_n && device_is_reg) || 
        (!_decodedOp[5]) ||
        (!_decodedOp[6] && device_is_reg)
    );
    
    assign  ram_in_n = (_decodedDevLo[0] && _decodedOp[3] && _decodedOp[7]) || ! ram_out_n ;  // combine with ram_out_n prevents ram_in and doing ram_out
    assign  marlo_in_n = _decodedDevLo[1];
    assign  marhi_in_n= _decodedDevLo[2];
    assign  uart_in_n= _decodedDevLo[3];
    assign  pchitmp_in_n= _decodedDevLo[4];
    assign  pclo_in_n= _decodedDevLo[5];
    assign  jmp_in_n= _decodedDevLo[6];
    assign  jmpo_in_n= _decodedDevLo[7];

    assign  jmpz_in_n= _decodedDevHi[0];
    assign  jmpc_in_n= _decodedDevHi[1];
    assign  jmpdi_in_n= _decodedDevHi[2];
    assign  jmpdo_in_n= _decodedDevHi[3];
    assign  jmpeq_in_n= _decodedDevHi[4];
    assign  jmpne_in_n= _decodedDevHi[5];
    assign  jmpgt_in_n= _decodedDevHi[6];
    assign  jmplt_in_n= _decodedDevHi[7];

    // logging 
    always @ * 
        $display(
         " hi %08b", hi_rom, 
            " op_sel %03b ", operation_sel, " dev_sel %05b ", device_sel, 
            " => devHi %08b" , _decodedDevHi," devLo %08b" , _decodedDevLo,
            " force_x_to_zero_n %1b", force_x_val_to_zero_n, 
            " force_alu_op_to_passx %1b", force_alu_op_to_passx, 
            " zp %1b", ram_zp_n, " isreg %1b", device_is_reg);

endmodule : control
// verilator lint_on ASSIGNDLY