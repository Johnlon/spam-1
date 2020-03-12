
`include "../74138/hct74138.v"
`include "../74139/hct74139.v"

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
    output [3:0] reg_x_addr,
    output [3:0] reg_y_addr,
    output [3:0] alu_op,
    output force_x_zero_n,
    
    output ram_zp_n
);

    wire [4:0] device = hi_rom[4:0];

    wire [3:0] busDecode;
    /* verilator lint_off PINMISSING */
    hct74139 busDemux(._Ea(1'b0), .Aa(hi_rom[7:6]), ._Ya(busDecode), .Ab(2'b0), ._Eb(1'b0)); 
    /* verilator lint_on PINMISSING */

    assign  rom_out_n = busDecode[0];
    assign  ram_out_n = busDecode[1];
    assign  alu_out_n = busDecode[2];
    assign  uart_out_n = busDecode[3];

    // ram_zp_n will turn off the ram address buffers letting HiAddr pull down to 0 and will turn on ROM->MARLO for the lo addr
    assign ram_zp_n = hi_rom[5];
    
    /* verilator lint_off UNUSED */
    wire [3:0] decodeAlu;
    /* verilator lint_on UNUSED */

    wire device_is_reg = device[4];

    /* verilator lint_off PINMISSING */
    // decode device bits when ALU is active - high when ALU inactive
    hct74139 aluCtrlDecoder(._Ea(alu_out_n), .Aa({ram_zp_n, device_is_reg}), ._Ya(decodeAlu), .Ab(2'b0), ._Eb(1'b0)); 
    /* verilator lint_on PINMISSING */
    
    assign force_x_zero_n = decodeAlu[2]; // low when ALU active and device is not a register and ZP is not active 
    
    // !!!! IF ZP IS ENABLED THEN FORCE ALUOP TO 1 
    wire force_ram_write_n = decodeAlu[0] && decodeAlu[1]; // ? diode logic to pull down a 10k resistor

    wire [7:0] decodeLoDev;
    hct74138 decoderLoDev(.Enable3(1'b1), .Enable2_bar(device_is_reg), .Enable1_bar(device[3]), .A(device[2:0]), .Y(decodeLoDev));
    
    wire [7:0] decodeHiDev;
    hct74138 decoderHiDev(.Enable3(device[3]), .Enable2_bar(device_is_reg), .Enable1_bar(1'b0), .A(device[2:0]), .Y(decodeHiDev));
    
    assign reg_in_n = ! ((!rom_out_n && device_is_reg) || (!ram_out_n && device_is_reg) || (!alu_out_n && ram_zp_n && device_is_reg));
    
    // multiplexer 
    always @ * 
        $display("LOW DEV " , decodeLoDev[0], " FORCE_RAMW ", force_ram_write_n, 
        " / ALU0 ", decodeAlu[0] , " ALU1 ", decodeAlu[1],
        " ALU2 ", decodeAlu[2] , " ALU3 ", decodeAlu[3],
        " / ZP ", ram_zp_n, " ISREG ", device_is_reg);

    assign  ram_in_n = decodeLoDev[0] && force_ram_write_n;
    assign  marlo_in_n = decodeLoDev[1];
    assign  marhi_in_n= decodeLoDev[2];
    assign  uart_in_n= decodeLoDev[3];
    assign  pchitmp_in_n= decodeLoDev[4];
    assign  pclo_in_n= decodeLoDev[5];
    assign  jmp_in_n= decodeLoDev[6];
    assign  jmpo_in_n= decodeLoDev[7];

    assign  jmpz_in_n= decodeHiDev[0];
    assign  jmpc_in_n= decodeHiDev[1];
    assign  jmpdi_in_n= decodeHiDev[2];
    assign  jmpdo_in_n= decodeHiDev[3];
    assign  jmpeq_in_n= decodeHiDev[4];
    assign  jmpne_in_n= decodeHiDev[5];
    assign  jmpgt_in_n= decodeHiDev[6];
    assign  jmplt_in_n= decodeHiDev[7];

    // need to be able to mux the device[3:0] with 74HC243 quad bus tranceiver has OE & /OE for outputs control and 
    // use a sip5 10k resistor pull down to get 0. 
    // else use mux use 74241 (2x4 with hi or low en) or 74244 (2x4 with low en) 
    assign reg_x_addr = hi_rom[3:0];
    assign reg_y_addr = 4'b1111;
    assign alu_op = 4'b1111;

endmodule : control
// verilator lint_on ASSIGNDLY
