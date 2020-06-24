

// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
//`include "../control/controller.v"
`include "../cpu/wide_controller.v"
//`include "../control/controller.v"
`include "../phaser/phaser.v"
`include "../registerFile/syncRegisterFile.v"
`include "../pc/pc.v"
`include "../lib/assertion.v"
`include "../74245/hct74245.v"
`include "../74573/hct74573.v"
`include "../7474/hct7474.v"
`include "../74139/hct74139.v"
`include "../74377/hct74377.v"
`include "../rom/rom.v"
`include "../ram/ram.v"
`include "../alu/alu.v"
`include "../alu/alu_func.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

`define SEMICOLON ;
`define COMMA ,

module test();

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    `include "../lib/display_snippet.v"
    
    tri [15:0] address_bus;
    logic [15:0] prev_address_bus;
    logic [7:0] prev_alu_result_bus;

    tri [7:0] rbus, lbus, alu_result_bus;
    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;
    wire [4:0] alu_op;


    wire phaseFetch, phaseDecode, phaseExec, _phaseFetch, _phaseExec;
    wire [2:0] phase = {phaseFetch, phaseDecode, phaseExec};

    // CLOCK ===================================================================================
    localparam T=1000;

    logic clk;
    //always begin
    //   #CLOCK_INTERVAL clk = !clk;
    //end

    wire #8 _clk = ! clk; // GATE + PD
    
    // RESET CIRCUIT ===================================================================================
    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html

    logic _RESET_SWITCH;
    wire #9 RESET_SWITCH = ! _RESET_SWITCH;

    wire mrPC, _mrPC;
    wire mrPH, _mrPH;

    // syncs the reset with the clock.
    // mrPH forces phase to 000 so clock to PC will be low.
    // at this point _mrPC will also be low so when the clock releases _mrPH then 
    // there will be a phase transition to 100 which the PC will see as a clock pulse that resets the PC   
    hct7474 #(.BLOCKS(1), .LOG(0)) resetPH(
          ._SD(1'b1),
          ._RD(_RESET_SWITCH),
          .D(1'b1),
          .CP(clk),
          .Q(_mrPH),
          ._Q(mrPH)
        );

    hct7474 #(.BLOCKS(1), .LOG(0)) resetPCFF(
          ._SD(1'b1),
          ._RD(_mrPH), // reset released after clock on resetPH
          .D(1'b1),
          .CP(phaseFetch),
          .Q(_mrPC),
          ._Q(mrPC)
        );


    // CLOCK PHASING ===================================================================================

    wire [9:0] seq;
    `define SEQ(x) (10'd2 ** (x-1))

    // releasing reset allows phaser to go from 000 to 100 whilst _mrPC is low which resets the PC
    phaser #(.LOG(0)) ph(.clk, .mr(mrPH), .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec, ._phaseExec);

    // CONTROL ===========================================================================================
    wire _addrmode_register, _addrmode_pc, _addrmode_direct;
    //wire [7:0] instruction_hi, instruction_mid, instruction_lo;
    wire [7:0] direct_address_hi, direct_address_lo;
    wire [7:0] direct8;
    wire [7:0] immed8;

    // selection wires
    `define WIRE_LDEV_SEL(DNAME) wire _ldev_``DNAME``
    `define WIRE_RDEV_SEL(DNAME) wire _rdev_``DNAME``
    `define WIRE_TDEV_SEL(DNAME) wire _``DNAME``_in

    `CONTROL_WIRES(WIRE, `SEMICOLON);

    `define BIND_LDEV_SEL(DNAME) ._ldev_``DNAME``
    `define BIND_RDEV_SEL(DNAME) ._rdev_``DNAME``
    `define BIND_TDEV_SEL(DNAME) ._``DNAME``_in

    wide_controller ctrl(
        ._mr(_mrPC),
        .phaseFetch, .phaseDecode, .phaseExec, ._phaseFetch, ._phaseExec,
        .address_bus,

        ._addrmode_register, ._addrmode_pc, ._addrmode_direct,
        `CONTROL_WIRES(BIND, `COMMA),
        .direct_address_hi, .direct_address_lo,
        .direct8,
        .immed8,
        .alu_op,
        .rbus_dev, .lbus_dev, .targ_dev // for regfile
    );

    // PROGRAM COUNTER ======================================================================================

    wire [7:0] PCHI, PCLO; // output of PC
    
    // PC reset is sync with +ve edge of clock
    pc #(.LOG(0))  PC (
        .clk(phaseFetch),
        ._MR(_mrPC),
        ._pc_in(_pc_in),
        ._pclo_in(_pclo_in),
        ._pchitmp_in(_pchitmp_in),
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    hct74245ab pchi_addrbushi_buf(.A(PCHI), .B(address_bus[15:8]), .nOE(_addrmode_pc));
    hct74245ab pclo_addrbuslo_buf(.A(PCLO), .B(address_bus[7:0]), .nOE(_addrmode_pc));

    // ROM =============================================================================================

    
    // ROM OUT to RBUS when direct rom addressing is being used
    hct74245ab rom_rbus_buf(.A(direct8), .B(rbus), .nOE(_rdev_rom));

    // ROM OUT TO RBUS VIA IR is immediate addressing of that operand, and we can be simultaneously register (MAR) addressing the RAM
    hct74245ab rom_instreg_rbus_buf(.A(immed8), .B(rbus), .nOE(_rdev_instreg));

    hct74245ab rom_addrbuslo_buf(.A(direct_address_lo), .B(address_bus[7:0]), .nOE(_addrmode_direct)); // optional - needed for direct addressing
    hct74245ab rom_addrbushi_buf(.A(direct_address_hi), .B(address_bus[15:8]), .nOE(_addrmode_direct)); // optional - needed for direct addressing

    // RAM =============================================================================================

    wire #(8) _gated_ram_in = _phaseExec | _ram_in;
    ram #(.AWIDTH(16)) ram64(._WE(_gated_ram_in), ._OE(1'b0), .A(address_bus));
    
    hct74245ab ram_alubus_buf(.A(alu_result_bus), .B(ram64.D), .nOE(_ram_in));
    hct74245ab ram_rbus_buf(.A(ram64.D), .B(rbus), .nOE(_rdev_ram));

    // MAR =============================================================================================
    hct74377 #(.LOG(0)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(0)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));

    hct74245ab marlo_lbus_buf(.A(MARLO.Q), .B(lbus), .nOE(_ldev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marlo_rbus_buf(.A(MARLO.Q), .B(rbus), .nOE(_rdev_marlo)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab marhi_lbus_buf(.A(MARHI.Q), .B(lbus), .nOE(_ldev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register
    hct74245ab marhi_rbus_buf(.A(MARHI.Q), .B(rbus), .nOE(_rdev_marhi)); // optional - needed for marlo arith so MAR appears as a GP register

    hct74245ab marhi_addrbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register));
    hct74245ab marlo_addrbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register));

    // ALU ==============================================================================================
    wire _flag_c_out, _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out;
    wire _flag_c, _flag_z, _flag_n, _flag_o, _flag_gt, _flag_lt, _flag_eq, _flag_ne;

	alu #(.LOG(0)) Alu(
        .o(alu_result_bus), 
        .x(lbus),
        .y(rbus),
        .alu_op(alu_op),
        ._flag_c_in(_flag_c),
        ._flag_c(_flag_c_out),
        ._flag_z(_flag_z_out),
        ._flag_o(_flag_o_out),
        ._flag_n(_flag_n_out),
        ._flag_gt(_flag_gt_out),
        ._flag_lt(_flag_lt_out),
        ._flag_eq(_flag_eq_out),
        ._flag_ne(_flag_ne_out)
    );

    hct74574 #(.LOG(1)) flags_czonGLEN( .D({_flag_c_out , _flag_z_out, _flag_o_out, _flag_n_out, _flag_gt_out, _flag_lt_out, _flag_eq_out, _flag_ne_out}),
                                       .Q({_flag_c, _flag_z, _flag_n, _flag_o, _flag_gt, _flag_lt, _flag_eq, _flag_ne}),
                                        .CLK(phaseExec), ._OE(1'b0)); 

    // REGISTER FILE =====================================================================================
    // INTERESTING THAT THE SELECTION LOGIC DOESN'T CONSIDER REGD - THIS SIMPLIFIED VALUE DOMAIN CONSIDERING ONLY THE FOUR ACTIVE LOW STATES NEEDS JUST THIS SIMPLE LOGIC FOR THE ADDRESSING
    wire #(8) _gated_regfile_in = _phaseExec | (_rega_in & _regb_in & _regc_in & _regd_in);
    wire #(8) _regfile_rdL_en = _ldev_rega &_ldev_regb &_ldev_regc &_ldev_regd ;
    wire #(8) _regfile_rdR_en = _rdev_rega &_rdev_regb &_rdev_regc &_rdev_regd ;
    wire [1:0] regfile_rdL_addr = lbus_dev[1:0];
    wire [1:0] regfile_rdR_addr = rbus_dev[1:0];
    wire [1:0] regfile_wr_addr = targ_dev[1:0];

    if (0) always @* $display("regfile gated in=", _gated_regfile_in, " wr addr  ", regfile_wr_addr, " in : a=%b b=%b c=%b d=%b " , _rega_in , _regb_in , _regc_in , _regd_in);
    if (0) always @* $display("regfile lbus out=", _regfile_rdL_en, " rd addr  ", regfile_rdL_addr, " in : a=%b b=%b c=%b d=%b " , _ldev_rega , _ldev_regb , _ldev_regc , _ldev_regd);
    if (0) always @* $display("regfile rbus out=", _regfile_rdR_en, " rd addr  ", regfile_rdR_addr, " in : a=%b b=%b c=%b d=%b " , _rdev_rega , _rdev_regb , _rdev_regc , _rdev_regd);


    syncRegisterFile #(.LOG(0)) regFile(
        .clk,
        ._wr_en(_gated_regfile_in),
        .wr_addr(regfile_wr_addr),
        .wr_data(alu_result_bus),
        
        ._rdL_en(_regfile_rdL_en),
        .rdL_addr(regfile_rdL_addr),
        .rdL_data(lbus),
        
        ._rdR_en(_regfile_rdR_en),
        .rdR_addr(regfile_rdR_addr),
        .rdR_data(rbus)
    );
    

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    `define RAM(A) ram64.Mem[A]
    `define DATA(D) {40'bz, D} /* padded to rom width with z */

    `define DUMP_ROM(ADDR)    $display ("%9t ", $time,  "PROGRAM  ", " rom=%08b:%08b:%08b:%08b:%08b:%08b",  ctrl.rom_6.Mem[ADDR], ctrl.rom_5.Mem[ADDR], ctrl.rom_4.Mem[ADDR], ctrl.rom_3.Mem[ADDR], ctrl.rom_2.Mem[ADDR], ctrl.rom_1.Mem[ADDR]);
//    `define DUMP_ROM(ADDR)    

    localparam MAX_INST_LEN=100;
    localparam MAX_PC=100;
    typedef reg[MAX_INST_LEN:0][7:0] string_bits ;
    string_bits CODE [MAX_PC];

    // SETUP ROM
    task INIT_ROM;
    begin

        // CODE SEGMENT
        `DEV_EQ_ROM_DIRECT(0, marlo, 'hffaa);
        `DUMP_ROM(0)

        // dev_eq_const8 tdev=00011(MARHI), const8=0           
        `DEV_EQ_IMMED8(1, marhi, 0);                  // MARHI=const 0      implies ALUOP=R
        `DUMP_ROM(1)

        // dev_eq_xy_alu tdev=00010(MARLO) ldev=0010(MARLO) rdev=0010(MARLO) alu=00101(5=A+1)
        `DEV_EQ_XY_ALU(2, marlo, marlo, marlo, A_PLUS_1); 

        // dev_eq_const8 tdev=00000(RAM[MAR]), const8=0x22           
        `DEV_EQ_IMMED8(3, ram, 'h22);

        // dev_eq_ram_direct tdev=00010(MARLO), address=ffaa     
        `DEV_EQ_RAM_DIRECT(4, marlo, 'h0043);

        // ram_direct_eq_dev tdev=00001(RAM), rdev=MARLO  address=abcd     
        //`ROM(5)= { 8'b110_00010, 16'habcd };                // RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R
        `RAM_DIRECT_EQ_DEV(5, 'habcd, marlo);

        // write RAM into regb
        `DEV_EQ_RAM_DIRECT(6, regb, 'h0043);

        // write regb into RAM
        `RAM_DIRECT_EQ_DEV(7, 'hdcba, regb);

        // test all registers read write
        `DEV_EQ_IMMED8(8, rega, 1);
        `DEV_EQ_IMMED8(9, regb, 2);
        `DEV_EQ_IMMED8(10, regc, 3);
        `DEV_EQ_IMMED8(11, regd, 4);
        `RAM_DIRECT_EQ_DEV(12, 'h0001, rega);
        `RAM_DIRECT_EQ_DEV(13, 'h0002, regb);
        `RAM_DIRECT_EQ_DEV(14, 'h0003, regc);
        `RAM_DIRECT_EQ_DEV(15, 'h0004, regd);

        // test all registers on L and R channel into ALU
        `DEV_EQ_XY_ALU(16, marlo, rega, rom, A);  // rom is a noop here
        `DEV_EQ_XY_ALU(17, marhi, rom, rega, B);  // rom is a noop here
        `DEV_EQ_XY_ALU(18, marlo, regb, rom, A);  // rom is a noop here
        `DEV_EQ_XY_ALU(19, marhi, rom, regb, B);  // rom is a noop here
        `DEV_EQ_XY_ALU(20, marlo, regc, rom, A);  // rom is a noop here
        `DEV_EQ_XY_ALU(21, marhi, rom, regc, B);  // rom is a noop here
        `DEV_EQ_XY_ALU(22, marlo, regd, rom, A);  // rom is a noop here
        `DEV_EQ_XY_ALU(23, marhi, rom, regd, B);  // rom is a noop here

        // DATA SEGMENT - ONLY LOWER 8 BITS ACCESSIBLE AT THE MOMENT AS ITS AN 8 BITS OF DATA CPU
        // initialise rom[ffaa] = 0x42
        //`ROM(16'hffaa) = { 8'b0, 8'b0, 8'h42 }; 
        `ROM('hffaa) = `DATA(8'h42);

    end
    endtask : INIT_ROM

    wire [2:0] _addrmode = {_addrmode_pc, _addrmode_register, _addrmode_direct}; 

    `define LOG_LDEV_SEL(DNAME) " _ldev_``DNAME``=%1b", _ldev_``DNAME``
    `define LOG_RDEV_SEL(DNAME) " _rdev_``DNAME``=%1b", _rdev_``DNAME``
    `define LOG_TDEV_SEL(DNAME) " _``DNAME``_in=%1b",  _``DNAME``_in


    // TESTS
    initial begin
        `define EXECUTE_CYCLE(N) for (count =0; count < N*(phaseFetchLen+phaseDecodeLen+phaseExecLen); count++) begin #TCLK CLK_UP; #TCLK CLK_DN; end

        localparam TCLK=1000;   // clock cycle
        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        CLK_DN;

        #TCLK
        `Equals( phase, control.PHASE_NONE)

        `Equals( seq, `SEQ(1))
        `Equals( _addrmode, 3'b1xx)
        //HACK`Equals( _addrmode, 3'b111)

        `Equals(PCHI, 8'bx)
        `Equals(PCLO, 8'bx)

        //HACK`Equals(address_bus, 16'bz); // noone providing address
        `Equals(address_bus, 16'bx); // noone providing address

        #TCLK
        `DISPLAY("_mrPC=0  - so clocking is ineffective = stay in PC addressing mode")
        `Equals( _mrPC, 0);

        for (count =0; count < 3; count++) begin
            count++; 
            #TCLK
            CLK_UP; //CLK_UP;
            #TCLK
            CLK_DN;
        end
        #TCLK
        `Equals(PCHI, 8'bx)
        `Equals(PCLO, 8'bx)
        

        `DISPLAY("_RESET_SWITCH released : still in PC addressing mode after settle and PC=0")
        _RESET_SWITCH <= 1;
        `Equals( _mrPC, 0);
        `Equals( phase, control.PHASE_NONE)

        #TCLK

        `DISPLAY("instruction 1 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals( _mrPC, 1'b1); // +clock due to phaseFetch on SR plus the release of the reset on the SR
            `Equals(PCHI, 8'b0) 
            `Equals(PCLO, 8'b0)
            `Equals(address_bus, 16'h0000);
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 1 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_DIR);
            `Equals(address_bus, 16'hffaa); // FROM ROM[15:0] 
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 1 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_DIR);
            `Equals(address_bus, 16'hffaa); // FROM ROM[15:0] 
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseExecLen));
        end

        // operation result 
        `Equals(MARLO.Q, 8'h42)
        `Equals(MARHI.Q, 8'hxx)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("instruction 2 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(address_bus, 16'h0001); // FROM PC
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 2 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 2 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end

        // operation result 
        `Equals(MARLO.Q, 8'h42)
        `Equals(MARHI.Q, 8'h00)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("instruction 3 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(address_bus, 16'd2); // FROM PC
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 3 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            `Equals(address_bus, 16'h0042); // FROM MAR
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 3 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC) 
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            //`Equals(address_bus, 16'h0000); // FROM MAR - NOT MATERIAL TO THE TEST BUT A SIDE EFFECT OF SETTING MAR=0000
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end
        
        `Equals(MARLO.Q, 8'h43)
        `Equals(MARHI.Q, 8'h00)

        `DISPLAY("instruction 4 - RAM[MAR=0x0043]=0x22 ")
        // fetch/decode
        for (count =0; count < 1* (phaseFetchLen+phaseDecodeLen); count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        `Equals(`RAM(16'h0000), 8'hxx); // Should still be XX as we've not entered EXECUTE yet

        // exec
        for (count =0; count < phaseExecLen; count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        `Equals(`RAM(16'h0043), 8'h22);

        `DISPLAY("instruction 5 - MARLO=RAM[MAR=0x0043]=0x22")
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'h22)
        `Equals(MARHI.Q, 8'h00)

        `DISPLAY("instruction 6 - RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R")
        `EXECUTE_CYCLE(1)
        `Equals(`RAM(16'habcd), 8'h22);


        `DISPLAY("instruction 7 - DEV_EQ_RAM_DIRECT(regb, 'habcd) write to Register File");
        `EXECUTE_CYCLE(1)
        `Equals( regFile.get(1), 8'h22);

        `DISPLAY("instruction 8 - RAM_DIRECT_EQ_DEV('hdcba, regb) read from Register File");
        `EXECUTE_CYCLE(1)
        `Equals(`RAM(16'hdcba), 8'h22);

        `DISPLAY("instruction 9 to 16 - REGA=1 / B=2 / C=3 / E=4 round trip const to reg to ram");
        `EXECUTE_CYCLE(8)
        `Equals( regFile.get(0), 8'h1);
        `Equals( regFile.get(1), 8'h2);
        `Equals( regFile.get(2), 8'h3);
        `Equals( regFile.get(3), 8'h4);
        `Equals(`RAM(1), 1);
        `Equals(`RAM(2), 2);
        `Equals(`RAM(3), 3);
        `Equals(`RAM(4), 4);

        `DISPLAY("instruction - REGA ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd1)
        `Equals(MARHI.Q, 8'd0)
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd1)
        `Equals(MARHI.Q, 8'd1)

        `DISPLAY("instruction - REGB ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd2)
        `Equals(MARHI.Q, 8'd1)
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd2)
        `Equals(MARHI.Q, 8'd2)

        `DISPLAY("instruction - REGC ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd3)
        `Equals(MARHI.Q, 8'd2)
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd3)
        `Equals(MARHI.Q, 8'd3)

        `DISPLAY("instruction - REGD ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd4)
        `Equals(MARHI.Q, 8'd3)
        `EXECUTE_CYCLE(1)
        `Equals(MARLO.Q, 8'd4)
        `Equals(MARHI.Q, 8'd4)
/*
*/

//`include "./generated_tests.v"
/*
        #TCLK
        count=100;
        while (count -- > 0) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
            $display("PC %2x:%2x !!!!!!!!!!!!!!!!!!!!!!!! CLK COUNT REMAINING=%-d", PCHI, PCLO, count);
        end
*/

        // consume any remaining code
        // while (1==1) begin
        //     #TCLK
        //     CLK_UP;
        //     #TCLK
        //     CLK_DN;
        // end

        $display("END OF TEST");
        $finish();

    end

    integer count;
    integer phaseFetchLen=1;
    integer phaseDecodeLen=1;
    integer phaseExecLen=1;
    integer pcval;


    assign pcval={PCHI, PCLO};
    string_bits currentCode; // create field so it can appear in dump file


    always @(PCHI or PCLO) begin
        $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {PCHI, PCLO});
        currentCode = string_bits'(CODE[pcval]); // assign outside 'always' doesn't work so do here instead
    end

    task DUMP;
            $display ("%9t ", $time,  "DUMP  ",
                 ": CODE: %-s", currentCode,
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 ": %-s", label
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 " phase=%-6s", control.fPhase(phaseFetch, phaseDecode, phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " seq=%-2d", $clog2(seq)+1);
            $display ("%9t ", $time,  "DUMP  ",
//                 " instruction=%08b:%08b:%08b", ctrl.instruction_hi, ctrl.instruction_mid, ctrl.instruction_lo);
                 " instruction=%08b:%08b:%08b:%08b:%08b:%08b", ctrl.instruction_6, ctrl.instruction_5, ctrl.instruction_4, ctrl.instruction_3, ctrl.instruction_2, ctrl.instruction_1);
            $display ("%9t ", $time,  "DUMP  ",
                " op=%d(%-s)", ctrl.op_ctrl, control.opName(ctrl.op_ctrl),
                 " FDE=%1b%1b%1b(%-s)", phaseFetch, phaseDecode, phaseExec, control.fPhase(phaseFetch, phaseDecode, phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " _amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                 " (%03b)", {_addrmode_pc, _addrmode_register, _addrmode_direct},
                 " addrbus=0x%4x", address_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 //" rom=%08b:%08b:%08b", ctrl.rom_hi.D, ctrl.rom_mid.D, ctrl.rom_lo.D);
                 " rom=%08b:%08b:%08b:%08b:%08b:%08b",  ctrl.rom_6.D, ctrl.rom_5.D, ctrl.rom_4.D, ctrl.rom_3.D, ctrl.rom_2.D, ctrl.rom_1.D);
            $display ("%9t ", $time,  "DUMP  ",
                 " direct8=%08b", direct8,
                 " immed8=%08b", immed8);
            $display ("%9t ", $time,  "DUMP  ",
                 " ram=%08b", ram64.D);
            $display ("%9t ", $time,  "DUMP  ",
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " alu_op=%5b(%s)", alu_op, alu_func.aluopName(alu_op)
            );            
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
            $display ("%9t ", $time,  "DUMP  ",
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " MAR=%8b:%8b (0x%2x:%2x)", MARHI.Q, MARLO.Q, MARHI.Q, MARLO.Q);
            $display ("%9t ", $time,  "DUMP  ",
                 " PC=%02h:%02h", PCHI, PCLO);
            $display("%9t", $time, " DUMP:",
                 "  REGA:%08b", regFile.get(0),
                 "  REGB:%08b", regFile.get(1),
                 "  REGC:%08b", regFile.get(2),
                 "  REGD:%08b", regFile.get(3)
                 );
    endtask 

    task CLK_UP; 
    begin
        $display("\n%9t", $time, " CLK  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"); 
        clk = 1;
    end
    endtask

    task CLK_DN; 
    begin
        $display("\n%9t", $time, " END OF CLOCK STATE %d", clk); 
        // op_decode.DUMP();
        // addr_decode.DUMP();
        DUMP;
        $display("\n%9t", $time, " CLK  -----------------------------------------------------------------------"); 
        clk = 0;
    end
    endtask

    if (0) always @* begin
        $display ("%9t ", $time,  "MON     ",
                 "rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                 " seq=%-2d", $clog2(seq)+1,
                 " _amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                 " addrbus=0x%4x", address_bus,
                 " FDE=%-6s (%1b%1b%1b)", control.fPhase(phaseFetch, phaseDecode, phaseExec), phaseFetch, phaseDecode, phaseExec,
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
                 " rdev=%04b ldev=%04b targ=%05b alu_op=%05b (%1s)", rbus_dev, lbus_dev, targ_dev, alu_op, alu_func.aluopName(alu_op),
                 " tsel=%32b ", tsel,
                 " PC=%02h:%02h", PCHI, PCLO,
                 "     : %1s", label
                 );
    end

    always @* 
        if (_RESET_SWITCH)  
            $display("\n%9t RESET SWITCH RELEASE   _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
        else      
            $display("\n%9t RESET SWITCH SET       _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 

    always @* 
        if (_mrPC)  
            $display("\n%9t PC RESET RELEASE   _mrPC=%1b  ======================================================================\n", $time, _mrPC); 
        else      
            $display("\n%9t PC RESET SET       _mrPC=%1b  ======================================================================\n", $time, _mrPC); 


    
    if (0) always @(*) begin
        $display("%9t", $time, " PHASE CHANGE: FDE=%-s  %1b%1b%1b seq=%10b", control.fPhase(phaseFetch, phaseDecode, phaseExec), 
                                                        phaseFetch, phaseDecode, phaseExec, seq); 
    end

    if (0) always @(*) begin
        $display("%9t", $time, " control._AMODE: PRI=%-s  %1b%1b%1b seq=%10b", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct), 
                                                        _addrmode_pc, _addrmode_register, _addrmode_direct, seq); 
    end

    integer instCount = 0;
    always @(posedge phaseFetch) begin
        instCount ++;
        $display("%9t", $time, " PHASE: FETCH  INTRUCTION#=%-d", instCount); 
    end

    always @(posedge phaseDecode) begin
        $display("%9t", $time, " PHASE: DECODE"); 
    end

    always @(posedge phaseExec) begin
        $display("%9t", $time, " PHASE: EXECUTE"); 
    end

    if (0) always @* 
        $display ("%9t ", $time,  "ADDRESSING      _amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct), " addrbus=0x%4x", address_bus);

    if (0) always @* 
        $display ("%9t ", $time,  "ROM      rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                " _amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                " addrbus=0x%4x", address_bus);
        
    if (0) always @* 
        $display ("%9t ", $time,  "RAM     ram=%08b", ram64.D,
                " _amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                " addrbus=0x%4x", address_bus,
                " _ram_in=%1b _gated_ram_in=%1b", _ram_in, _gated_ram_in,
                );
        
    if (0) always @* 
        $display("%9t ... seq=%-2d  %8b................", $time, $clog2(seq)+1, seq); 
        
/*
    if (0) always @* 
        $display("%9t ", $time, "ROMBUFFS rom_addrbuslo_buf=0x%-2x", rom_addrbuslo_buf.B, 
            " rom_addrbus_hi_buf=0x%-2x", rom_addrbushi_buf.B,
            " instruction_hi=%8b", instruction_hi,
            " _oe=%1b(_addrmode_direct)", _addrmode_direct
            ); 
*/

                
    if (0) always @* 
        $display("%9t ", $time, "DEVICE-SEL ", 
                    "rdev=%04b ldev=%04b targ=%05b alu_op=%05b ", rbus_dev, lbus_dev, targ_dev, alu_op
        ); 

    if (0) always @* 
        $display("%9t ", $time, "MAR  %02x:%02x    _marhi_in=%b _marlo_in=%b", MARHI.Q, MARLO.Q, _marhi_in, _marlo_in);

    if (0) always @* 
        $display("%9t ", $time, "tsel=%032b  lsel=%016b rsel=%016b", tsel, lsel, rsel);

    if (0) always @* 
        $display("%9t ", $time, "ALU BUS ",
            " rbus=0x%-2x", rbus, 
            " lbus=0x%-2x", lbus,
            " alu_result_bus=%-2x", alu_result_bus
            ); 
        
        
    // constraints

    always @(*) begin
        if (phaseDecode & ctrl.instruction_6 === 'x) begin
           $display("instruction_6", ctrl.instruction_6); 
        //if (phaseDecode & ctrl.instruction_hi === 'x) begin
         //   $display("instruction_6", ctrl.instruction_hi); 
            DUMP;
            $display("END OF PROGRAM - PROGRAM BYTE = XX "); 
            $finish();
        end
    end

    // constraints
    always @* begin
        // expect address and data to remain stable while ram write enabled
        if (!_gated_ram_in) begin
            if (prev_address_bus != address_bus) begin
                $display("\n\n%9t ", $time, " ADDRESS CHANGED WHILE GATED RAM WRITE ENABLED");
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
            end
            if (prev_alu_result_bus != alu_result_bus) begin
                $display("\n\n%9t ", $time, " DATA CHANGED WHILE GATED RAM WRITE ENABLED");
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
            end
        end
        prev_address_bus = address_bus;
        prev_alu_result_bus = alu_result_bus;
    end

    always @* begin
        // permits a situation where the control lines conflict.
        // this is ok as long as they settle quickly and are settled before exec phase.
        if (_RESET_SWITCH & phaseDecode) begin
            if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_direct === 1'bx) begin
                $display("\n\n%9t ", $time, " ERROR ILLEGAL INDETERMINATE ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b", _addrmode_pc , _addrmode_register , _addrmode_direct );
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
                //#SETTLE_TOLERANCE
                // only one may be low at a time
                //if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_direct === 1'bx) begin
                //    DUMP;
                //    $display("\n\n%9t ", $time, " ABORT");
                //    $finish();
                //end
            end
            if (_addrmode_pc + _addrmode_register + _addrmode_direct < 2) begin
                $display("\n\n%9t ", $time, " ERROR CONFLICTING ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b sAddrMode=%-s", _addrmode_pc , _addrmode_register , _addrmode_direct,
                                            control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct));
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
                //#SETTLE_TOLERANCE
                //if (_addrmode_pc + _addrmode_register + _addrmode_direct < 2) begin
                //    DUMP;
                //    $display("\n\n%9t ", $time, " ABORT");
                //    $finish();
                //end
            end
        end
    end

    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        // $monitor ("%9t ", $time,  "TEST     ",
        //         //"rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
        //         //" seq=%-2d", nSeq,
        //         // " _amode=%-3s", sAddrMode,
        //         // " addrbus=0x%4x", address_bus,
        //         " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
        //         " rdev=%04b ldev=%04b targ=%05b alu_op=%05b ", rbus_dev, lbus_dev, targ_dev, alu_op,
        //         " PC=%02h:%02h", PCHI, PCLO,
        //         //"      %-s", label
        //         );

        `endif
    end

endmodule : test
