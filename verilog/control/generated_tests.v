// 
// op_DEV_eq_ROM_sel _ram_in
hi_rom=8'b00000000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_DEV_eq_ROM_sel _rega_in
hi_rom=8'b00000001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_DEV_eq_ROM_sel _marlo_in
hi_rom=8'b00000010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_DEV_eq_ROM_sel _regb_in
hi_rom=8'b00000011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_DEV_eq_ROM_sel _marhi_in
hi_rom=8'b00000100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_DEV_eq_ROM_sel _regc_in
hi_rom=8'b00000101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10010); // expect _regc_in
// 
// op_DEV_eq_ROM_sel _uart_in
hi_rom=8'b00000110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_DEV_eq_ROM_sel _regd_in
hi_rom=8'b00000111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_DEV_eq_ROM_sel _pchitmp_in
hi_rom=8'b00001000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_DEV_eq_ROM_sel _rege_in
hi_rom=8'b00001001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_DEV_eq_ROM_sel _pclo_in
hi_rom=8'b00001010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_DEV_eq_ROM_sel _regf_in
hi_rom=8'b00001011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_DEV_eq_ROM_sel _pc_in
hi_rom=8'b00001100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_DEV_eq_ROM_sel _regg_in
hi_rom=8'b00001101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_DEV_eq_ROM_sel _jmpo_in
hi_rom=8'b00001110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_DEV_eq_ROM_sel _regh_in
hi_rom=8'b00001111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_DEV_eq_ROM_sel _jmpz_in
hi_rom=8'b00010000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_DEV_eq_ROM_sel _regi_in
hi_rom=8'b00010001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_DEV_eq_ROM_sel _jmpc_in
hi_rom=8'b00010010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_DEV_eq_ROM_sel _regj_in
hi_rom=8'b00010011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_DEV_eq_ROM_sel _jmpdi_in
hi_rom=8'b00010100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_DEV_eq_ROM_sel _regk_in
hi_rom=8'b00010101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_DEV_eq_ROM_sel _jmpdo_in
hi_rom=8'b00010110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_DEV_eq_ROM_sel _regl_in
hi_rom=8'b00010111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_DEV_eq_ROM_sel _jmpeq_in
hi_rom=8'b00011000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_DEV_eq_ROM_sel _regm_in
hi_rom=8'b00011001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_DEV_eq_ROM_sel _jmpne_in
hi_rom=8'b00011010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_DEV_eq_ROM_sel _regn_in
hi_rom=8'b00011011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_DEV_eq_ROM_sel _jmpgt_in
hi_rom=8'b00011100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_DEV_eq_ROM_sel _rego_in
hi_rom=8'b00011101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_DEV_eq_ROM_sel _jmplt_in
hi_rom=8'b00011110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_DEV_eq_ROM_sel _regp_in
hi_rom=8'b00011111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, F); //  expected
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11111); // expect _regp_in
// // => RAM[MAR]=0 - _WE wins & bus is Z pulled down to 0
// op_DEV_eq_RAM_sel _ram_in
hi_rom=8'b00100000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_DEV_eq_RAM_sel _rega_in
hi_rom=8'b00100001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_DEV_eq_RAM_sel _marlo_in
hi_rom=8'b00100010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_DEV_eq_RAM_sel _regb_in
hi_rom=8'b00100011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_DEV_eq_RAM_sel _marhi_in
hi_rom=8'b00100100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_DEV_eq_RAM_sel _regc_in
hi_rom=8'b00100101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10010); // expect _regc_in
// 
// op_DEV_eq_RAM_sel _uart_in
hi_rom=8'b00100110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_DEV_eq_RAM_sel _regd_in
hi_rom=8'b00100111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_DEV_eq_RAM_sel _pchitmp_in
hi_rom=8'b00101000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_DEV_eq_RAM_sel _rege_in
hi_rom=8'b00101001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_DEV_eq_RAM_sel _pclo_in
hi_rom=8'b00101010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_DEV_eq_RAM_sel _regf_in
hi_rom=8'b00101011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_DEV_eq_RAM_sel _pc_in
hi_rom=8'b00101100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_DEV_eq_RAM_sel _regg_in
hi_rom=8'b00101101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_DEV_eq_RAM_sel _jmpo_in
hi_rom=8'b00101110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_DEV_eq_RAM_sel _regh_in
hi_rom=8'b00101111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_DEV_eq_RAM_sel _jmpz_in
hi_rom=8'b00110000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_DEV_eq_RAM_sel _regi_in
hi_rom=8'b00110001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_DEV_eq_RAM_sel _jmpc_in
hi_rom=8'b00110010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_DEV_eq_RAM_sel _regj_in
hi_rom=8'b00110011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_DEV_eq_RAM_sel _jmpdi_in
hi_rom=8'b00110100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_DEV_eq_RAM_sel _regk_in
hi_rom=8'b00110101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_DEV_eq_RAM_sel _jmpdo_in
hi_rom=8'b00110110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_DEV_eq_RAM_sel _regl_in
hi_rom=8'b00110111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_DEV_eq_RAM_sel _jmpeq_in
hi_rom=8'b00111000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_DEV_eq_RAM_sel _regm_in
hi_rom=8'b00111001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_DEV_eq_RAM_sel _jmpne_in
hi_rom=8'b00111010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_DEV_eq_RAM_sel _regn_in
hi_rom=8'b00111011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_DEV_eq_RAM_sel _jmpgt_in
hi_rom=8'b00111100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_DEV_eq_RAM_sel _rego_in
hi_rom=8'b00111101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_DEV_eq_RAM_sel _jmplt_in
hi_rom=8'b00111110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_DEV_eq_RAM_sel _regp_in
hi_rom=8'b00111111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11111); // expect _regp_in
// // => RAM[ZP]=0 - _WE wins & bus is Z pulled down to 0
// op_DEV_eq_RAMZP_sel _ram_in
hi_rom=8'b01000000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_DEV_eq_RAMZP_sel _rega_in
hi_rom=8'b01000001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_DEV_eq_RAMZP_sel _marlo_in
hi_rom=8'b01000010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_DEV_eq_RAMZP_sel _regb_in
hi_rom=8'b01000011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_DEV_eq_RAMZP_sel _marhi_in
hi_rom=8'b01000100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_DEV_eq_RAMZP_sel _regc_in
hi_rom=8'b01000101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10010); // expect _regc_in
// 
// op_DEV_eq_RAMZP_sel _uart_in
hi_rom=8'b01000110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_DEV_eq_RAMZP_sel _regd_in
hi_rom=8'b01000111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_DEV_eq_RAMZP_sel _pchitmp_in
hi_rom=8'b01001000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_DEV_eq_RAMZP_sel _rege_in
hi_rom=8'b01001001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_DEV_eq_RAMZP_sel _pclo_in
hi_rom=8'b01001010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_DEV_eq_RAMZP_sel _regf_in
hi_rom=8'b01001011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_DEV_eq_RAMZP_sel _pc_in
hi_rom=8'b01001100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_DEV_eq_RAMZP_sel _regg_in
hi_rom=8'b01001101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_DEV_eq_RAMZP_sel _jmpo_in
hi_rom=8'b01001110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_DEV_eq_RAMZP_sel _regh_in
hi_rom=8'b01001111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_DEV_eq_RAMZP_sel _jmpz_in
hi_rom=8'b01010000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_DEV_eq_RAMZP_sel _regi_in
hi_rom=8'b01010001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_DEV_eq_RAMZP_sel _jmpc_in
hi_rom=8'b01010010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_DEV_eq_RAMZP_sel _regj_in
hi_rom=8'b01010011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_DEV_eq_RAMZP_sel _jmpdi_in
hi_rom=8'b01010100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_DEV_eq_RAMZP_sel _regk_in
hi_rom=8'b01010101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_DEV_eq_RAMZP_sel _jmpdo_in
hi_rom=8'b01010110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_DEV_eq_RAMZP_sel _regl_in
hi_rom=8'b01010111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_DEV_eq_RAMZP_sel _jmpeq_in
hi_rom=8'b01011000;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_DEV_eq_RAMZP_sel _regm_in
hi_rom=8'b01011001;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_DEV_eq_RAMZP_sel _jmpne_in
hi_rom=8'b01011010;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_DEV_eq_RAMZP_sel _regn_in
hi_rom=8'b01011011;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_DEV_eq_RAMZP_sel _jmpgt_in
hi_rom=8'b01011100;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_DEV_eq_RAMZP_sel _rego_in
hi_rom=8'b01011101;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_DEV_eq_RAMZP_sel _jmplt_in
hi_rom=8'b01011110;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_DEV_eq_RAMZP_sel _regp_in
hi_rom=8'b01011111;
#101
`Equals(_ram_out, F); //  expected
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, T);
`Equals(device_in, 5'b11111); // expect _regp_in
// 
// op_DEV_eq_UART_sel _ram_in
hi_rom=8'b01100000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_DEV_eq_UART_sel _rega_in
hi_rom=8'b01100001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_DEV_eq_UART_sel _marlo_in
hi_rom=8'b01100010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_DEV_eq_UART_sel _regb_in
hi_rom=8'b01100011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_DEV_eq_UART_sel _marhi_in
hi_rom=8'b01100100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_DEV_eq_UART_sel _regc_in
hi_rom=8'b01100101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10010); // expect _regc_in
// // => UART= UNKNOWN 
// op_DEV_eq_UART_sel _uart_in
hi_rom=8'b01100110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_DEV_eq_UART_sel _regd_in
hi_rom=8'b01100111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_DEV_eq_UART_sel _pchitmp_in
hi_rom=8'b01101000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_DEV_eq_UART_sel _rege_in
hi_rom=8'b01101001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_DEV_eq_UART_sel _pclo_in
hi_rom=8'b01101010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_DEV_eq_UART_sel _regf_in
hi_rom=8'b01101011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_DEV_eq_UART_sel _pc_in
hi_rom=8'b01101100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_DEV_eq_UART_sel _regg_in
hi_rom=8'b01101101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_DEV_eq_UART_sel _jmpo_in
hi_rom=8'b01101110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_DEV_eq_UART_sel _regh_in
hi_rom=8'b01101111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_DEV_eq_UART_sel _jmpz_in
hi_rom=8'b01110000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_DEV_eq_UART_sel _regi_in
hi_rom=8'b01110001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_DEV_eq_UART_sel _jmpc_in
hi_rom=8'b01110010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_DEV_eq_UART_sel _regj_in
hi_rom=8'b01110011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_DEV_eq_UART_sel _jmpdi_in
hi_rom=8'b01110100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_DEV_eq_UART_sel _regk_in
hi_rom=8'b01110101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_DEV_eq_UART_sel _jmpdo_in
hi_rom=8'b01110110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_DEV_eq_UART_sel _regl_in
hi_rom=8'b01110111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_DEV_eq_UART_sel _jmpeq_in
hi_rom=8'b01111000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_DEV_eq_UART_sel _regm_in
hi_rom=8'b01111001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_DEV_eq_UART_sel _jmpne_in
hi_rom=8'b01111010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_DEV_eq_UART_sel _regn_in
hi_rom=8'b01111011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_DEV_eq_UART_sel _jmpgt_in
hi_rom=8'b01111100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_DEV_eq_UART_sel _rego_in
hi_rom=8'b01111101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_DEV_eq_UART_sel _jmplt_in
hi_rom=8'b01111110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_DEV_eq_UART_sel _regp_in
hi_rom=8'b01111111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b11111); // expect _regp_in
// 
// op_NONREG_eq_OPREGY_sel _ram_in
hi_rom=8'b10000000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_NONREG_eq_OPREGY_sel _ram_in
hi_rom=8'b10000001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_NONREG_eq_OPREGY_sel _marlo_in
hi_rom=8'b10000010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_NONREG_eq_OPREGY_sel _marlo_in
hi_rom=8'b10000011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00001); // expect _marlo_in
// 
// op_NONREG_eq_OPREGY_sel _marhi_in
hi_rom=8'b10000100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_NONREG_eq_OPREGY_sel _marhi_in
hi_rom=8'b10000101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00010); // expect _marhi_in
// 
// op_NONREG_eq_OPREGY_sel _uart_in
hi_rom=8'b10000110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_NONREG_eq_OPREGY_sel _uart_in
hi_rom=8'b10000111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00011); // expect _uart_in
// 
// op_NONREG_eq_OPREGY_sel _pchitmp_in
hi_rom=8'b10001000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_NONREG_eq_OPREGY_sel _pchitmp_in
hi_rom=8'b10001001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00100); // expect _pchitmp_in
// 
// op_NONREG_eq_OPREGY_sel _pclo_in
hi_rom=8'b10001010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_NONREG_eq_OPREGY_sel _pclo_in
hi_rom=8'b10001011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00101); // expect _pclo_in
// 
// op_NONREG_eq_OPREGY_sel _pc_in
hi_rom=8'b10001100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_NONREG_eq_OPREGY_sel _pc_in
hi_rom=8'b10001101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00110); // expect _pc_in
// 
// op_NONREG_eq_OPREGY_sel _jmpo_in
hi_rom=8'b10001110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_NONREG_eq_OPREGY_sel _jmpo_in
hi_rom=8'b10001111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00111); // expect _jmpo_in
// 
// op_NONREG_eq_OPREGY_sel _jmpz_in
hi_rom=8'b10010000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_NONREG_eq_OPREGY_sel _jmpz_in
hi_rom=8'b10010001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01000); // expect _jmpz_in
// 
// op_NONREG_eq_OPREGY_sel _jmpc_in
hi_rom=8'b10010010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_NONREG_eq_OPREGY_sel _jmpc_in
hi_rom=8'b10010011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01001); // expect _jmpc_in
// 
// op_NONREG_eq_OPREGY_sel _jmpdi_in
hi_rom=8'b10010100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_NONREG_eq_OPREGY_sel _jmpdi_in
hi_rom=8'b10010101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01010); // expect _jmpdi_in
// 
// op_NONREG_eq_OPREGY_sel _jmpdo_in
hi_rom=8'b10010110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_NONREG_eq_OPREGY_sel _jmpdo_in
hi_rom=8'b10010111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01011); // expect _jmpdo_in
// 
// op_NONREG_eq_OPREGY_sel _jmpeq_in
hi_rom=8'b10011000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_NONREG_eq_OPREGY_sel _jmpeq_in
hi_rom=8'b10011001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01100); // expect _jmpeq_in
// 
// op_NONREG_eq_OPREGY_sel _jmpne_in
hi_rom=8'b10011010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_NONREG_eq_OPREGY_sel _jmpne_in
hi_rom=8'b10011011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01101); // expect _jmpne_in
// 
// op_NONREG_eq_OPREGY_sel _jmpgt_in
hi_rom=8'b10011100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_NONREG_eq_OPREGY_sel _jmpgt_in
hi_rom=8'b10011101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01110); // expect _jmpgt_in
// 
// op_NONREG_eq_OPREGY_sel _jmplt_in
hi_rom=8'b10011110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_NONREG_eq_OPREGY_sel _jmplt_in
hi_rom=8'b10011111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b01111); // expect _jmplt_in
// 
// op_REGX_eq_ALU_sel _rega_in
hi_rom=8'b10100000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_REGX_eq_ALU_sel _rega_in
hi_rom=8'b10100001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10000); // expect _rega_in
// 
// op_REGX_eq_ALU_sel _regb_in
hi_rom=8'b10100010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_REGX_eq_ALU_sel _regb_in
hi_rom=8'b10100011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10001); // expect _regb_in
// 
// op_REGX_eq_ALU_sel _regc_in
hi_rom=8'b10100100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10010); // expect _regc_in
// 
// op_REGX_eq_ALU_sel _regc_in
hi_rom=8'b10100101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10010); // expect _regc_in
// 
// op_REGX_eq_ALU_sel _regd_in
hi_rom=8'b10100110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_REGX_eq_ALU_sel _regd_in
hi_rom=8'b10100111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10011); // expect _regd_in
// 
// op_REGX_eq_ALU_sel _rege_in
hi_rom=8'b10101000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_REGX_eq_ALU_sel _rege_in
hi_rom=8'b10101001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10100); // expect _rege_in
// 
// op_REGX_eq_ALU_sel _regf_in
hi_rom=8'b10101010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_REGX_eq_ALU_sel _regf_in
hi_rom=8'b10101011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10101); // expect _regf_in
// 
// op_REGX_eq_ALU_sel _regg_in
hi_rom=8'b10101100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_REGX_eq_ALU_sel _regg_in
hi_rom=8'b10101101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10110); // expect _regg_in
// 
// op_REGX_eq_ALU_sel _regh_in
hi_rom=8'b10101110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_REGX_eq_ALU_sel _regh_in
hi_rom=8'b10101111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b10111); // expect _regh_in
// 
// op_REGX_eq_ALU_sel _regi_in
hi_rom=8'b10110000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_REGX_eq_ALU_sel _regi_in
hi_rom=8'b10110001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11000); // expect _regi_in
// 
// op_REGX_eq_ALU_sel _regj_in
hi_rom=8'b10110010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_REGX_eq_ALU_sel _regj_in
hi_rom=8'b10110011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11001); // expect _regj_in
// 
// op_REGX_eq_ALU_sel _regk_in
hi_rom=8'b10110100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_REGX_eq_ALU_sel _regk_in
hi_rom=8'b10110101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11010); // expect _regk_in
// 
// op_REGX_eq_ALU_sel _regl_in
hi_rom=8'b10110110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_REGX_eq_ALU_sel _regl_in
hi_rom=8'b10110111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11011); // expect _regl_in
// 
// op_REGX_eq_ALU_sel _regm_in
hi_rom=8'b10111000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_REGX_eq_ALU_sel _regm_in
hi_rom=8'b10111001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11100); // expect _regm_in
// 
// op_REGX_eq_ALU_sel _regn_in
hi_rom=8'b10111010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_REGX_eq_ALU_sel _regn_in
hi_rom=8'b10111011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11101); // expect _regn_in
// 
// op_REGX_eq_ALU_sel _rego_in
hi_rom=8'b10111100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_REGX_eq_ALU_sel _rego_in
hi_rom=8'b10111101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11110); // expect _rego_in
// 
// op_REGX_eq_ALU_sel _regp_in
hi_rom=8'b10111110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11111); // expect _regp_in
// 
// op_REGX_eq_ALU_sel _regp_in
hi_rom=8'b10111111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b11111); // expect _regp_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11000111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11001111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11010111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_REG_sel _ram_in
hi_rom=8'b11011111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, F); //  expected
`Equals(_uart_out, T);
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11100111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11101111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11110111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111000;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111001;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111010;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111011;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111100;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111101;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111110;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
// 
// op_RAMZP_eq_UART_sel _ram_in
hi_rom=8'b11111111;
#101
`Equals(_ram_out, T);
`Equals(_rom_out, T);
`Equals(_alu_out, T);
`Equals(_uart_out, F); //  expected
`Equals(device_in, 5'b00000); // expect _ram_in
