

// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
//`include "../control/controller.v"
`include "cpu.v"
`include "../lib/assertion.v"
// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

`define SEMICOLON ;
`define COMMA ,

module test();

    `include "../lib/display_snippet.v"

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    // CLOCK ===================================================================================
    localparam T=1000;

    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
    logic _RESET_SWITCH;

    logic clk;

    //always begin
    //   #CLOCK_INTERVAL clk = !clk;
    //end
    cpu CPU(_RESET_SWITCH, clk);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    `define RAM(A) CPU.ram64.Mem[A]
    `define DATA(D) {40'bz, D} /* padded to rom width with z */

    //``define DUMP_ROM(ADDR)    $display ("%9t ", $time,  "PROGRAM  ", " rom=%08b:%08b:%08b:%08b:%08b:%08b",  CPU.ctrl.rom_6.Mem[ADDR], CPU.ctrl.rom_5.Mem[ADDR], CPU.ctrl.rom_4.Mem[ADDR], CPU.ctrl.rom_3.Mem[ADDR], CPU.ctrl.rom_2.Mem[ADDR], CPU.ctrl.rom_1.Mem[ADDR]);

    localparam MAX_PC=100;
    string_bits CODE [MAX_PC];

    // SETUP ROM
    task INIT_ROM;
    begin

        // CODE SEGMENT
        `DEV_EQ_ROM_DIRECT(0, marlo, 'hffaa)
       // `DUMP_ROM(0)

        // dev_eq_const8 tdev=00011(CPU.MARHI), const8=0           
        `DEV_EQ_IMMED8(1, marhi, 0)                  // MARHI=const 0      implies ALUOP=R
        //`DUMP_ROM(1)

        // dev_eq_xy_alu tdev=00010(CPU.MARLO) ldev=0010(MARLO) rdev=0010(MARLO) alu=00101(5=A+1)
        `DEV_EQ_XY_ALU(2, marlo, marlo, marlo, A_PLUS_1) 

        // dev_eq_const8 tdev=00000(RAM[MAR]), const8=0x22           
        `DEV_EQ_IMMED8(3, ram, 'h22)

        // dev_eq_ram_direct tdev=00010(CPU.MARLO), address=ffaa     
        `DEV_EQ_RAM_DIRECT(4, marlo, 'h0043)

        // ram_direct_eq_dev tdev=00001(RAM), rdev=MARLO  address=abcd     
        //`ROM(5)= { 8'b110_00010, 16'habcd }                // RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R
        `RAM_DIRECT_EQ_DEV(5, 'habcd, marlo)

        // write RAM into regb
        `DEV_EQ_RAM_DIRECT(6, regb, 'h0043)

        // write regb into RAM
        `RAM_DIRECT_EQ_DEV(7, 'hdcba, regb)

        // test all registers read write
        `DEV_EQ_IMMED8(8, rega, 1)
        `DEV_EQ_IMMED8(9, regb, 2)
        `DEV_EQ_IMMED8(10, regc, 3)
        `DEV_EQ_IMMED8(11, regd, 4)
        `RAM_DIRECT_EQ_DEV(12, 'h0001, rega)
        `RAM_DIRECT_EQ_DEV(13, 'h0002, regb)
        `RAM_DIRECT_EQ_DEV(14, 'h0003, regc)
        `RAM_DIRECT_EQ_DEV(15, 'h0004, regd)

        // test all registers on L and R channel into ALU
        `DEV_EQ_XY_ALU(16, marlo, rega, rom, A)  // rom is a noop here
        `DEV_EQ_XY_ALU(17, marhi, rom, rega, B)  // rom is a noop here
        `DEV_EQ_XY_ALU(18, marlo, regb, rom, A)  // rom is a noop here
        `DEV_EQ_XY_ALU(19, marhi, rom, regb, B)  // rom is a noop here
        `DEV_EQ_XY_ALU(20, marlo, regc, rom, A)  // rom is a noop here
        `DEV_EQ_XY_ALU(21, marhi, rom, regc, B)  // rom is a noop here
        `DEV_EQ_XY_ALU(22, marlo, regd, rom, A)  // rom is a noop here
        `DEV_EQ_XY_ALU(23, marhi, rom, regd, B)  // rom is a noop here


        //`JMP_IMMED16(24, 16'h0100)  // rom is a noop here
        `JMP_IMMED16(24, 23)  // rom is a noop here
        //`DEV_EQ_IMMED8(24, pchitmp, 10)

        // DATA SEGMENT - ONLY LOWER 8 BITS ACCESSIBLE AT THE MOMENT AS ITS AN 8 BITS OF DATA CPU
        // initialise rom[ffaa] = 0x42
        //`ROM(16'hffaa) = { 8'b0, 8'b0, 8'h42 }; 
        `ROM('hffaa) = `DATA(8'h42);

    end
    endtask : INIT_ROM

    wire [2:0] _addrmode = {CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct}; 

    task CLK_UP; 
    begin
        $display("\n%9t", $time, " CLK  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"); 
        clk = 1;
    end
    endtask

    task CLK_DN; 
    begin
        $display("\n%9t", $time, " END OF CLOCK STATE %s", clk ? "HI" : "LO"); 
        DUMP;
        $display("\n%9t", $time, " CLK  -----------------------------------------------------------------------"); 
        clk = 0;
    end
    endtask


    // TESTS
    integer count;
    integer phaseFetchLen=1;
    integer phaseDecodeLen=1;
    integer phaseExecLen=1;

    initial begin
        `define EXECUTE_CYCLE(N) for (count =0; count < N*(phaseFetchLen+phaseDecodeLen+phaseExecLen); count++) begin #TCLK CLK_UP; #TCLK CLK_DN; end

        localparam TCLK=1000;   // clock cycle
        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        CLK_DN;

        #TCLK
        `Equals( CPU.phase, control.PHASE_NONE)

        `Equals( CPU.seq, `SEQ(1))
        `Equals( _addrmode, 3'b1xx)
        //HACK`Equals( _addrmode, 3'b111)

        `Equals(CPU.PCHI, 8'bx)
        `Equals(CPU.PCLO, 8'bx)

        //HACK`Equals(CPU.address_bus, 16'bz); // noone providing address
        `Equals(CPU.address_bus, 16'bx); // noone providing address

        #TCLK
        `DISPLAY("_mrPC=0  - so clocking is ineffective = stay in PC addressing mode")
        `Equals(CPU._mrPC, 0);

        for (count =0; count < 3; count++) begin
            count++; 
            #TCLK
            CLK_UP; //CLK_UP;
            #TCLK
            CLK_DN;
        end
        #TCLK
        `Equals(CPU.PCHI, 8'bx)
        `Equals(CPU.PCLO, 8'bx)
        

        `DISPLAY("_RESET_SWITCH released : still in PC addressing mode after settle and PC=0")
        _RESET_SWITCH <= 1;
        `Equals(CPU._mrPC, 0);
        `Equals(CPU.phase, control.PHASE_NONE)

        #TCLK

        `DISPLAY("instruction 1 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals(CPU.phase, control.PHASE_FETCH)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(CPU._mrPC, 1'b1); // +clock due to phaseFetch on SR plus the release of the reset on the SR
            `Equals(CPU.PCHI, 8'b0) 
            `Equals(CPU.PCLO, 8'b0)
            `Equals(CPU.address_bus, 16'h0000);
            `Equals(CPU.seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 1 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals(CPU.phase, control.PHASE_DECODE)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_DIR);
            `Equals(CPU.address_bus, 16'hffaa); // FROM ROM[15:0] 
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 1 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_EXEC)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_DIR);
            `Equals(CPU.address_bus, 16'hffaa); // FROM ROM[15:0] 
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen+phaseExecLen));
        end

        // operation result 
        `Equals(CPU.MARLO.Q, 8'h42)
        `Equals(CPU.MARHI.Q, 8'hxx)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("instruction 2 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_FETCH)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'b1)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(CPU.address_bus, 16'h0001); // FROM PC
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 2 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_DECODE)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 2 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_EXEC)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end

        // operation result 
        `Equals(CPU.MARLO.Q, 8'h42)
        `Equals(CPU.MARHI.Q, 8'h00)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("instruction 3 - clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_FETCH)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(CPU.address_bus, 16'd2); // FROM PC
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1));
        end

        `DISPLAY("instruction 3 - clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_DECODE)
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            `Equals(CPU.address_bus, 16'h0042); // FROM MAR
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("instruction 3 - clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals(CPU.phase, control.PHASE_EXEC) 
            `Equals(CPU.PCHI, 8'b0)
            `Equals(CPU.PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            //`Equals(CPU.address_bus, 16'h0000); // FROM MAR - NOT MATERIAL TO THE TEST BUT A SIDE EFFECT OF SETTING MAR=0000
            CLK_DN;
            #TCLK
            `Equals(CPU.seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end
        
        `Equals(CPU.MARLO.Q, 8'h43)
        `Equals(CPU.MARHI.Q, 8'h00)

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
        `Equals(CPU.MARLO.Q, 8'h22)
        `Equals(CPU.MARHI.Q, 8'h00)

        `DISPLAY("instruction 6 - RAM[DIRECT=abcd]=MARLO=h22     implies ALUOP=R")
        `EXECUTE_CYCLE(1)
        `Equals(`RAM(16'habcd), 8'h22);

        `DISPLAY("instruction 7 - DEV_EQ_RAM_DIRECT(regb, 'habcd) write to Register File");
        `EXECUTE_CYCLE(1)
        `Equals( CPU.regFile.get(1), 8'h22);

        `DISPLAY("instruction 8 - RAM_DIRECT_EQ_DEV('hdcba, regb) read from Register File");
        `EXECUTE_CYCLE(1)
        `Equals(`RAM(16'hdcba), 8'h22);

        `DISPLAY("instruction 9 to 16 - REGA=1 / B=2 / C=3 / E=4 round trip const to reg to ram");
        `EXECUTE_CYCLE(8)
        `Equals( CPU.regFile.get(0), 8'h1);
        `Equals( CPU.regFile.get(1), 8'h2);
        `Equals( CPU.regFile.get(2), 8'h3);
        `Equals( CPU.regFile.get(3), 8'h4);
        `Equals(`RAM(1), 1);
        `Equals(`RAM(2), 2);
        `Equals(`RAM(3), 3);
        `Equals(`RAM(4), 4);

        `DISPLAY("instruction - REGA ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd1)
        `Equals(CPU.MARHI.Q, 8'd0)
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd1)
        `Equals(CPU.MARHI.Q, 8'd1)

        `DISPLAY("instruction - REGB ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd2)
        `Equals(CPU.MARHI.Q, 8'd1)
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd2)
        `Equals(CPU.MARHI.Q, 8'd2)

        `DISPLAY("instruction - REGC ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd3)
        `Equals(CPU.MARHI.Q, 8'd2)
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd3)
        `Equals(CPU.MARHI.Q, 8'd3)

        `DISPLAY("instruction - REGD ON L and R CHANNELS");
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd4)
        `Equals(CPU.MARHI.Q, 8'd3)
        `EXECUTE_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd4)
        `Equals(CPU.MARHI.Q, 8'd4)
        #1
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
        $display("END OF TEST CASES ==============================================");
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
         while (1==1) begin
             #TCLK
             CLK_UP;
             #TCLK
             CLK_DN;
         end

        $display("END OF TEST");
        $finish();

    end

    integer pcval;
    assign pcval={CPU.PCHI, CPU.PCLO};

    string_bits currentCode; // create field so it can appear in dump file

    always @(CPU.PCHI or CPU.PCLO) begin
        $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {CPU.PCHI, CPU.PCLO});
        currentCode = string_bits'(CODE[pcval]); // assign outside 'always' doesn't work so do here instead
    end

    `define LOG_LDEV_SEL(DNAME) " _ldev_``DNAME``=%1b", CPU._ldev_``DNAME``
    `define LOG_RDEV_SEL(DNAME) " _rdev_``DNAME``=%1b", CPU._rdev_``DNAME``
    `define LOG_TDEV_SEL(DNAME) " _``DNAME``_in=%1b",  CPU._``DNAME``_in

    task DUMP;
            $display ("%9t ", $time,  "DUMP  ",
                 ": CODE: %-s", currentCode,
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 ": %-s", label
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 " phase=%-6s", control.fPhase(CPU.phaseFetch, CPU.phaseDecode, CPU.phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " seq=%-2d", $clog2(CPU.seq)+1);
            $display ("%9t ", $time,  "DUMP  ",
                 " PC=%1d (0x%4h) PCHItmp=%d (%2x)", CPU.pc_addr, CPU.pc_addr, CPU.PC.PCHITMP, CPU.PC.PCHITMP);
            $display ("%9t ", $time,  "DUMP  ",
//                 " instruction=%08b:%08b:%08b", ctrl.instruction_hi, ctrl.instruction_mid, ctrl.instruction_lo);
                 " instruction=%08b:%08b:%08b:%08b:%08b:%08b", CPU.ctrl.instruction_6, CPU.ctrl.instruction_5, CPU.ctrl.instruction_4, CPU.ctrl.instruction_3, CPU.ctrl.instruction_2, CPU.ctrl.instruction_1);
            $display ("%9t ", $time,  "DUMP  ",
                 " FDE=%1b%1b%1b(%-s)", CPU.phaseFetch, CPU.phaseDecode, CPU.phaseExec, control.fPhase(CPU.phaseFetch, CPU.phaseDecode, CPU.phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " _amode=%-3s", control.fAddrMode(CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct),
                 " (%03b)", {CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct},
                 " addrbus=0x%4x", CPU.address_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 //" rom=%08b:%08b:%08b", ctrl.rom_hi.D, ctrl.rom_mid.D, ctrl.rom_lo.D);
                 " rom=%08b:%08b:%08b:%08b:%08b:%08b",  CPU.ctrl.rom_6.D, CPU.ctrl.rom_5.D, CPU.ctrl.rom_4.D, CPU.ctrl.rom_3.D, CPU.ctrl.rom_2.D, CPU.ctrl.rom_1.D);
            $display ("%9t ", $time,  "DUMP  ",
                 " direct8=%08b", CPU.direct8,
                 " immed8=%08b", CPU.immed8);
            $display ("%9t ", $time,  "DUMP  ",
                 " ram=%08b", CPU.ram64.D);
            $display ("%9t ", $time,  "DUMP  ",
                " tdev=%5b(%s)", CPU.targ_dev, control.tdevname(CPU.targ_dev),
                " ldev=%4b(%s)", CPU.lbus_dev, control.devname(CPU.lbus_dev),
                " rdev=%4b(%s)", CPU.rbus_dev,control.devname(CPU.rbus_dev),
                " alu_op=%5b(%s)", CPU.alu_op, alu_func.aluopName(CPU.alu_op)
            );            
            $display ("%9t ", $time,  "DUMP  ",
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", CPU.rbus, CPU.lbus, CPU.alu_result_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " MAR=%8b:%8b (0x%2x:%2x)", CPU.MARHI.Q, CPU.MARLO.Q, CPU.MARHI.Q, CPU.MARLO.Q);
            $display("%9t", $time, " DUMP:",
                 "  REGA:%08b", CPU.regFile.get(0),
                 "  REGB:%08b", CPU.regFile.get(1),
                 "  REGC:%08b", CPU.regFile.get(2),
                 "  REGD:%08b", CPU.regFile.get(3)
                 );
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
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
        if (CPU._mrPC)  
            $display("\n%9t PC RESET RELEASE   _mrPC=%1b  ======================================================================\n", $time, CPU._mrPC); 
        else      
            $display("\n%9t PC RESET SET       _mrPC=%1b  ======================================================================\n", $time, CPU._mrPC); 


    
    if (0) always @(*) begin
        $display("%9t", $time, " PHASE CHANGE: FDE=%-s  %1b%1b%1b seq=%10b", control.fPhase(phaseFetch, phaseDecode, phaseExec), 
                                                        phaseFetch, phaseDecode, phaseExec, seq); 
    end

    if (0) always @(*) begin
        $display("%9t", $time, " control._AMODE: PRI=%-s  %1b%1b%1b seq=%10b", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_direct), 
                                                        _addrmode_pc, _addrmode_register, _addrmode_direct, seq); 
    end

    integer instCount = 0;
    always @(posedge CPU.phaseFetch) begin
        instCount ++;
        $display("%9t", $time, " PHASE: FETCH  INTRUCTION#=%-d", instCount); 
    end

    always @(posedge CPU.phaseDecode) begin
        $display("%9t", $time, " PHASE: DECODE"); 
    end

    always @(posedge CPU.phaseExec) begin
        $display("%9t", $time, " PHASE: EXECUTE"); 
    end

    if (0) always @* 
        $display ("%9t ", $time,  "ADDRESSING      _amode=%s", control.fAddrMode(CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct), " addrbus=0x%4x", CPU.address_bus);

    if (0) always @* 
        $display ("%9t ", $time,  "RAM     ram=%08b", CPU.ram64.D,
                " _amode=%s", control.fAddrMode(CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct),
                " addrbus=0x%4x", CPU.address_bus,
                " _ram_in=%1b _gated_ram_in=%1b", CPU._ram_in, CPU._gated_ram_in,
                );
        
    if (0) always @* 
        $display("%9t ... seq=%-2d  %8b................", $time, $clog2(CPU.seq)+1, CPU.seq); 
        
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
        
    
///////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRAINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////

        
    // constraints

    always @(*) begin
        if (CPU.phaseDecode & CPU.ctrl.instruction_6 === 'x) begin
           $display("instruction_6", CPU.ctrl.instruction_6); 
            DUMP;
            $display("ERROR END OF PROGRAM - PROGRAM BYTE = XX "); 
            $finish_and_return(1);
        end
    end

    logic [15:0] prev_address_bus;
    logic [7:0] prev_alu_result_bus;

    // constraints
    always @* begin
        // expect address and data to remain stable while ram write enabled
        if (!CPU._gated_ram_in) begin
            if (prev_address_bus != CPU.address_bus) begin
                $display("\n\n%9t ", $time, " ADDRESS CHANGED WHILE GATED RAM WRITE ENABLED");
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
            end
            if (prev_alu_result_bus != CPU.alu_result_bus) begin
                $display("\n\n%9t ", $time, " DATA CHANGED WHILE GATED RAM WRITE ENABLED");
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
            end
        end
        prev_address_bus = CPU.address_bus;
        prev_alu_result_bus = CPU.alu_result_bus;
    end

    always @* begin
        // permits a situation where the control lines conflict.
        // this is ok as long as they settle quickly and are settled before exec phase.
        if (_RESET_SWITCH & CPU.phaseDecode) begin
            if (CPU._addrmode_pc === 1'bx |  CPU._addrmode_register === 1'bx |  CPU._addrmode_direct === 1'bx) begin
                $display("\n\n%9t ", $time, " ERROR ILLEGAL INDETERMINATE ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b", CPU._addrmode_pc , CPU._addrmode_register , CPU._addrmode_direct );
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
            if (CPU._addrmode_pc + CPU._addrmode_register + CPU._addrmode_direct < 2) begin
                $display("\n\n%9t ", $time, " ERROR CONFLICTING ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b sAddrMode=%-s", CPU._addrmode_pc , CPU._addrmode_register , CPU._addrmode_direct,
                                            control.fAddrMode(CPU._addrmode_pc, CPU._addrmode_register, CPU._addrmode_direct));
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


endmodule : test
