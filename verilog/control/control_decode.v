`ifndef V_CONTROL
`define V_CONTROL

`include "../74138/hct74138.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/100ps

module control_decode (
    input [4:0] device_in,
    input _flag_z, _flag_c, _flag_o, _flag_eq, _flag_ne, _flag_gt, _flag_lt,
    input _uart_in_ready, _uart_out_ready,

    // decoded
    output _ram_in, _marlo_in, _marhi_in, _uart_in, 
    output _pchitmp_in, // load hi tmp reg
    output _pclo_in, // load lo pc reg only
    output _pc_in, // load high (from tmp)
    
    output _reg_in

);
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

    wire reg_in = device_in[4];
    assign _reg_in = ! device_in[4];

    wire [7:0] _decodedDevLo;
    hct74138 decoderLoDev(.Enable3(1'b1), .Enable2_bar(reg_in), .Enable1_bar(device_in[3]), .A(device_in[2:0]), .Y(_decodedDevLo));
    
    wire [7:0] _decodedDevHi;
    hct74138 decoderHiDev(.Enable3(device_in[3]), .Enable2_bar(reg_in), .Enable1_bar(1'b0), .A(device_in[2:0]), .Y(_decodedDevHi));

    assign  _ram_in = _decodedDevLo[idx_RAM_sel];
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
            " device_in=%05b" , device_in,
            " devHi:Lo=%08b" , _decodedDevHi,
            ",%08b" , _decodedDevLo,
            " _ram_in=%1b", _ram_in, 
            " _reg_in=%1b", _reg_in, 
            " _marlo_in=%1b", _marlo_in,
            " _marhi_in=%1b", _marhi_in,
            " _uart_in =%1b", _uart_in ,
            " _pchitmp_in=%1b", _pchitmp_in,
            " _pclo_in=%1b", _pclo_in,
            " _pc_in=%1b", _pc_in
           );

    endmodule : control_decode
    // verilator lint_on ASSIGNDLY

`endif
