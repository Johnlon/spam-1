// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCITON CONTAINS THE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "../control/control.v"
`include "../phaser/phaser.v"
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

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    `include "../lib/display_snippet.v"
    
    tri [15:0] address_bus;
    logic [15:0] prev_address_bus;
    logic [7:0] prev_alu_result_bus;

    tri [7:0] rbus, lbus, alu_result_bus;
    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;
    wire [4:0] aluop;

    wire _addrmode_register, _addrmode_pc, _addrmode_direct;
    wire [2:0] _addrmode = {_addrmode_pc, _addrmode_register, _addrmode_direct}; 

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
   
    wire [7:0] instruction_hi, instruction_mid, instruction_lo;

    // instruction reg buffer
    hct74573 rom_hi_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_hi.D),
         .Q(instruction_hi) 
    );

    hct74573 rom_mid_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_mid.D),
         .Q(instruction_mid) 
    );

    hct74573 rom_lo_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_lo.D),
         .Q(instruction_lo) 
    );

    op_decoder #(.LOG(0)) op_decode(.data_hi(instruction_hi), .data_mid(instruction_mid), .data_lo(instruction_lo), .rbus_dev, .lbus_dev, .targ_dev, .aluop);

    address_mode_decoder #(.LOG(1)) addr_decode( 
        .ctrl(instruction_hi[7:5]), 
        .phaseFetch, ._phaseFetch, .phaseDecode, .phaseExec, 
        ._addrmode_pc, ._addrmode_register, ._addrmode_direct 
    );


    // device decoders
    hct74138 lbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(lbus_dev[3]), .A(lbus_dev[2:0]));
    hct74138 lbus_dev_16_demux(.Enable3(lbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(lbus_dev[2:0]));
    
    hct74138 rbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(rbus_dev[3]), .A(rbus_dev[2:0]));
    hct74138 rbus_dev_16_demux(.Enable3(rbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(rbus_dev[2:0]));

    wire [3:0] _targ_dev_block_sel, un4; 
    hct74139 targ_dev_block_demux(._Ea(1'b0), ._Eb(1'b0), .Aa(targ_dev[4:3]), .Ab(2'b0), ._Ya(_targ_dev_block_sel), ._Yb(un4));
    hct74138 targ_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[0]), .A(targ_dev[2:0]));
    hct74138 targ_dev_16_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[1]), .A(targ_dev[2:0]));
    hct74138 targ_dev_24_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[2]), .A(targ_dev[2:0]));
    hct74138 targ_dev_32_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_dev_block_sel[3]), .A(targ_dev[2:0]));

    // control lines for device selection
    wire [31:0] tset = {targ_dev_32_demux.Y, targ_dev_24_demux.Y, targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    wire [15:0] lset = {lbus_dev_16_demux.Y, lbus_dev_08_demux.Y};
    wire [15:0] rset = {rbus_dev_16_demux.Y, rbus_dev_08_demux.Y};

    `define WIRE_LDEV_SEL(DNAME) wire _ldev_``DNAME`` = lset[control.DEV_``DNAME``];
    `define WIRE_RDEV_SEL(DNAME) wire _rdev_``DNAME`` = rset[control.DEV_``DNAME``];
    `define WIRE_TDEV_SEL(DNAME) wire _``DNAME``_in = tset[control.TDEV_``DNAME``];

    `WIRE_TDEV_SEL(ram)
    `WIRE_RDEV_SEL(ram)

    `WIRE_RDEV_SEL(rom)

    `WIRE_LDEV_SEL(marlo)
    `WIRE_RDEV_SEL(marlo)
    `WIRE_TDEV_SEL(marlo)

    `WIRE_LDEV_SEL(marhi)
    `WIRE_RDEV_SEL(marhi)
    `WIRE_TDEV_SEL(marhi)

    `WIRE_LDEV_SEL(rega)
    `WIRE_RDEV_SEL(rega)
    `WIRE_TDEV_SEL(rega)

    `WIRE_LDEV_SEL(regb)
    `WIRE_RDEV_SEL(regb)
    `WIRE_TDEV_SEL(regb)

    `WIRE_LDEV_SEL(regc)
    `WIRE_RDEV_SEL(regc)
    `WIRE_TDEV_SEL(regc)

    `WIRE_LDEV_SEL(regd)
    `WIRE_RDEV_SEL(regd)
    `WIRE_TDEV_SEL(regd)

    `WIRE_LDEV_SEL(uart)
    `WIRE_TDEV_SEL(uart)

    `WIRE_RDEV_SEL(instreg)
   
    `WIRE_TDEV_SEL(pchitmp)
    `WIRE_TDEV_SEL(pclo)
    `WIRE_TDEV_SEL(pc)
    `WIRE_TDEV_SEL(jmpo)
    `WIRE_TDEV_SEL(jmpz)
    `WIRE_TDEV_SEL(jmpc)
    `WIRE_TDEV_SEL(jmpdi)
    `WIRE_TDEV_SEL(jmpdo)
     
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

    rom #(.AWIDTH(16)) rom_hi(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_mid(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_lo(._CS(1'b0), ._OE(1'b0), .A(address_bus)); 
    
    // ROM OUT when direct rom addressing is being used
    hct74245ab rom_rbus_buf(.A(rom_lo.D), .B(rbus), .nOE(_rdev_rom));

    // ROM OUT TO RBUS VIA IR PERMITS SIMULTANEOUS REG ADDRESSING OF RAM
    hct74245ab rom_instreg_rbus_buf(.A(instruction_lo), .B(rbus), .nOE(_rdev_instreg));

    hct74245ab rom_addrbuslo_buf(.A(instruction_lo), .B(address_bus[7:0]), .nOE(_addrmode_direct)); // optional - needed for direct addressing
    hct74245ab rom_addrbushi_buf(.A(instruction_mid), .B(address_bus[15:8]), .nOE(_addrmode_direct)); // optional - needed for direct addressing

    // RAM =============================================================================================

    wire #(8) _gated_ram_in = _phaseExec | _ram_in;
    ram #(.AWIDTH(16)) ram64(._WE(!phaseExec | _ram_in), ._OE(1'b0), .A(address_bus));
    
    hct74245ab ram_alubus_buf(.A(alu_result_bus), .B(ram64.D), .nOE(_ram_in));
    hct74245ab ram_rbus_buf(.A(ram64.D), .B(rbus), .nOE(_rdev_ram));

    // MAR =============================================================================================
    hct74377 #(.LOG(0)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(0)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));

    hct74245ab marlo_lbus_buf(.A(MARLO.Q), .B(lbus), .nOE(_ldev_marlo)); // optional - needed for marlo arith
    hct74245ab marlo_rbus_buf(.A(MARLO.Q), .B(rbus), .nOE(_rdev_marlo)); // optional - needed for marlo arith

    hct74245ab marhi_lbus_buf(.A(MARHI.Q), .B(lbus), .nOE(_ldev_marhi)); // optional - needed for marlo arith
    hct74245ab marhi_rbus_buf(.A(MARHI.Q), .B(rbus), .nOE(_rdev_marhi)); // optional - needed for marlo arith

    hct74245ab marhi_addrbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register));
    hct74245ab marlo_addrbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register));

    // ALU ==============================================================================================
    logic _flag_cin=1; // 1 = not cin TODO
    wire _flag_cout=1; // TODO
    wire _flag_z=1; // TODO

	alu #(.LOG(1)) Alu(
        .o(alu_result_bus), 
        .x(lbus),
        .y(rbus),
        .alu_op(aluop),
        ._flag_cin,
        ._flag_cout,
        ._flag_z
    );

    // REGISTER FILE =====================================================================================
    /*
    syncRegisterFile #(.LOG(1)) regFile(
    _MR,
    clk,
    _wr_en,
    wr_addr,
    wr_data,
    
    _rdL_en,
    rdL_addr,
    rdL_data,
    
    _rdR_en,
    rdR_addr,
    rdR_data
    );
    */
    

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    task INIT_ROM;
    begin
        `define ROM(A) {rom_hi.Mem[A], rom_mid.Mem[A], rom_lo.Mem[A]}
        `define RAM(A) ram64.Mem[A]

        // CODE

        // dev_eq_rom_direct tdev=00010(MARLO), address=ffaa     
        `ROM(0)= { 8'b100_00010, 16'hffaa };                // MARLO=whats at ROM address ffaa ie 42

        // dev_eq_const8 tdev=00011(MARHI), const8=0           
        `ROM(1)= { 8'b001_00011, 8'hx, 8'h0 };                  // MARHI=const 0      implies ALUOP=R

        // dev_eq_xy_alu tdev=00010(MARLO) ldev=0010(MARLO) rdev=0010(MARLO) alu=00101(5=A+1)
        `ROM(2)= { 8'b000_00010, 16'bzzz_0010_0010_00101 };     // MARLO=MARLO+1 = 43 (ALUOP=A+1)

        // dev_eq_const8 tdev=00000(RAM[MAR]), const8=0x22           
        `ROM(3)= { 8'b001_00000, 8'hx, 8'h22 };                  // RAM[MAR=0043]=const h22      implies ALUOP=R

        // dev_eq_ram_direct tdev=00010(MARLO), address=ffaa     
        `ROM(4)= { 8'b101_00010, 16'h0043 };                // MARLO=RAM[MAR=0043]=h22     implies ALUOP=R

        // ram_direct_eq_dev tdev=00001(RAM), rdev=MARLO  address=abcd     
        `ROM(5)= { 8'b110_00010, 16'habcd };                // RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R

        // DATA 
        // initialise rom[ffaa] = 0x42
        `ROM(16'hffaa) = { 8'b0, 8'b0, 8'h42 }; 
    end
    endtask : INIT_ROM

    // tests
    initial begin
        localparam TCLK=1000;   // clock cycle
        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        CLK_DN;

        #TCLK
        `Equals( phase, control.PHASE_NONE)

        `Equals( seq, `SEQ(1))
        `Equals( _addrmode, 3'b111)

        `Equals(PCHI, 8'bx)
        `Equals(PCLO, 8'bx)

        `Equals(address_bus, 16'bz); // noone providing address

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

        `DISPLAY("inst 1 - clock fetch")
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

        `DISPLAY("inst 1 - clock decode")
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

        `DISPLAY("inst 1 - clock exec")
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
        `DISPLAY("inst 2 - clock fetch")
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

        `DISPLAY("inst 2 - clock decode")
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

        `DISPLAY("inst 2 - clock exec")
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
        `DISPLAY("inst 3 - clock fetch")
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

        `DISPLAY("inst 3 - clock decode")
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

        `DISPLAY("inst 3 - clock exec")
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

        `DISPLAY("init 4 - RAM[MAR=0x0043]=0x22 ")
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

        `DISPLAY("init 5 - MARLO=RAM[MAR=0x0043]=0x22")
        // dev_eq_ram_direct tdev=00010(REGA), address=ffaa     
        for (count =0; count < 1* (phaseFetchLen+phaseDecodeLen); count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        for (count =0; count < phaseExecLen; count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        `Equals(MARLO.Q, 8'h22)
        `Equals(MARHI.Q, 8'h00)

        `DISPLAY("init 6 - // RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R")
        // dev_eq_ram_direct tdev=00010(REGA), address=ffaa     
        for (count =0; count < 1* (phaseFetchLen+phaseDecodeLen); count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        for (count =0; count < phaseExecLen; count++) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
        end
        `Equals(`RAM(16'habcd), 8'h22);

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
        $display("END OF TEST");
        $finish();

    end

    integer count;
    integer phaseFetchLen=1;
    integer phaseDecodeLen=1;
    integer phaseExecLen=1;

    always @(PCHI or PCLO) begin
      $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {PCHI, PCLO});
    end

    task DUMP;
            $display ("%9t ", $time,  "DUMP  ",
                 ": %-s", label
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 " seq=%-2d", $clog2(seq)+1);
            $display ("%9t ", $time,  "DUMP  ",
                 " instruction=%08b:%08b:%08b", instruction_hi, instruction_mid, instruction_lo);
            $display ("%9t ", $time,  "DUMP  ",
                " op=%d(%-s)", instruction_hi[7:5], control.opName(instruction_hi[7:5]),
                 " FDE=%1b%1b%1b(%-s)", phaseFetch, phaseDecode, phaseExec, control.fPhase(phaseFetch, phaseDecode, phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                 " addrbus=0x%4x", address_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D);
            $display ("%9t ", $time,  "DUMP  ",
                 " ram=%08b", ram64.D);
            $display ("%9t ", $time,  "DUMP  ",
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " aluop=%5b(%s)", aluop, alu_func.aluopName(aluop)
            );            
            $display ("%9t ", $time,  "DUMP  ",
                 " MAR=%8b:%8b (0x%2x:%2x)", MARHI.Q, MARLO.Q, MARHI.Q, MARLO.Q);
            $display ("%9t ", $time,  "DUMP  ",
                 " PC=%02h:%02h", PCHI, PCLO);
    endtask 

    task CLK_UP; 
    begin
        $display("\n%9t", $time, " CLK  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"); 
        clk = 1;
    end
    endtask

    task CLK_DN; 
    begin
        $display("\n%9t", $time, " END CLOCK STATE"); 
        // op_decode.DUMP();
        // addr_decode.DUMP();
        DUMP;
        $display("\n%9t", $time, " CLK  -----------------------------------------------------------------------\n"); 
        clk = 0;
    end
    endtask

    if (1) always @* begin
        $display ("%9t ", $time,  "MON     ",
                 "rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                 " seq=%-2d", $clog2(seq)+1,
                 " amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                 " addrbus=0x%4x", address_bus,
                 " FDE=%-s  %1b%1b%1b", control.fPhase(phaseFetch, phaseDecode, phaseExec), phaseFetch, phaseDecode, phaseExec,
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
                 " rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop,
                 " tset=%32b ", tset,
                 " PC=%02h:%02h", PCHI, PCLO,
                 "     : %-s", label
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
        $display("%9t", $time, " PHASE: FDE=%-s  %1b%1b%1b seq=%10b", control.fPhase(phaseFetch, phaseDecode, phaseExec), 
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
        $display ("%9t ", $time,  "ADDRESSING      amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct), " addrbus=0x%4x", address_bus);

    if (0) always @* 
        $display ("%9t ", $time,  "ROM      rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                " amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                " addrbus=0x%4x", address_bus);
        
    if (1) always @* 
        $display ("%9t ", $time,  "RAM      ram=%08b", ram64.D,
                " amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct),
                " addrbus=0x%4x", address_bus,
                " _ram_in=%1b _gated_ram_in=%1b", _ram_in, _gated_ram_in,
                );
        
    if (0) always @* 
        $display("%9t ... seq=%-2d  %8b................", $time, $clog2(seq)+1, seq); 
        
    if (0) always @* 
        $display("%9t ", $time, "ROMBUFFS rom_addrbuslo_buf=0x%-2x", rom_addrbuslo_buf.B, 
            " rom_addrbus_hi_buf=0x%-2x", rom_addrbushi_buf.B,
            " instruction_hi=%8b", instruction_hi,
            " _oe=%1b(_addrmode_direct)", _addrmode_direct
            ); 

                
    if (0) always @* 
        $display("%9t ", $time, "DEVICE-SEL ", 
                    "rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop
        ); 

    if (0) always @* 
        $display("%9t ", $time, "MAR  %02x:%02x    _marhi_in=%b _marlo_in=%b", MARHI.Q, MARLO.Q, _marhi_in, _marlo_in);

    if (0) always @* 
        $display("%9t ", $time, "tset=%032b  lset=%016b rset=%016b", tset, lset, rset);

    if (0) always @* 
        $display("%9t ", $time, "ALU BUS ",
            " rbus=0x%-2x", rbus, 
            " lbus=0x%-2x", lbus,
            " alu_result_bus=%-2x", alu_result_bus
            ); 
        
        
    // constraints
    always @(*) begin
        if (phaseDecode & instruction_hi === 'x) begin
            $display("rom_hi.D", rom_hi.D); 
            $display("instruction_hi", instruction_hi); 
            DUMP;
            $display("END OF PROGRAM - CONTROL BYTE = XX "); 
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
        //         // " amode=%-3s", sAddrMode,
        //         // " addrbus=0x%4x", address_bus,
        //         " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
        //         " rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop,
        //         " PC=%02h:%02h", PCHI, PCLO,
        //         //"      %-s", label
        //         );

        `endif
    end

endmodule : test
