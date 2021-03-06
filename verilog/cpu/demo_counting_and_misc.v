
//////////////// TO RUN TEST ... RUN AND GREP FOR  "OK" TO SEE COUNTER

// ADDRESSING TERMINOLOGY
//  IMMEDIATE ADDRESSING = INSTRUCTION CONTAINS THE CONSTANT VALUE DATA TO USE
//  DIRECT ADDRESSING = INSTRUCTION CONTAINS THE ADDRESS IN MEMORY OF THE DATA TO USE
//  REGISTER ADDRESSING = INSTRUCTION CONTAINS THE NAME OF THE REGISTER FROM WHICH TO FETCH THE DATA

//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
//`include "../control/controller.v"
`include "cpu.v"
`include "../lib/assertion.v"
`include "psuedo_assembler.sv"
// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

`define SEMICOLON ;
`define COMMA ,

`define regEquals(A,B,C,D) begin \
        `Equals( CPU.regFile.get(0), 8'(A)); \
        `Equals( CPU.regFile.get(1), 8'(B)); \
        `Equals( CPU.regFile.get(2), 8'(C)); \
        `Equals( CPU.regFile.get(3), 8'(D)); \
    end

module test();
    import alu_ops::*;

    `include "../lib/display_snippet.sv"

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    // CLOCK ===================================================================================
    localparam HALF_CLK=365;   // half clock cycle - if phases are shorter then make this clock longer etc 100ns
    //localparam HALF_CLK=1335;   // half clock cycle - if phases are shorter then make this clock longer etc 100ns

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

    localparam MAX_PC=2048;
    `DEFINE_CODE_VARS(MAX_PC)
    //string_bits CODE_TEXT [MAX_PC];
    //string_bits CODE [MAX_PC];

    integer counter =0;

    // SETUP ROM
    task INIT_ROM;
    begin
    // JLJL
        // RAM[ffaa] = 42h
        `RAM_DIRECT_EQ_IMMED8(counter, 16'hffaa, 8'h42); counter++;
    // JLJL

        // marlo = 42h
        `DEV_EQ_RAM_DIRECT(counter, marlo, 16'hffaa); counter++;

        // marhi = 0
        `DEV_EQ_IMMED8(counter, marhi, 0); counter++;

        // marlo = 43h
        `DEV_EQ_XI_ALU(counter, marlo, marlo, 1, A_PLUS_B) ; counter++;

        // RAM[00:43] = 22h
        `DEV_EQ_IMMED8(counter, ram, 8'h22); counter++;

        // marlo = 22h
        `DEV_EQ_RAM_DIRECT(counter, marlo, 16'h0043); counter++;

        // RAM[abcd] = 43h
        `RAM_DIRECT_EQ_DEV(counter, 16'habcd, marlo); counter++;

        // regb = 22h
        `DEV_EQ_RAM_DIRECT(counter, regb, 16'h0043); counter++;

        // RAM[fcba] = 22h
        `RAM_DIRECT_EQ_DEV(counter, 16'hdcba, regb); counter++;

        // test all registers read write
        `TEXT(counter, "WRITING TO ALL REGA");
        `DEV_EQ_IMMED8(counter, rega, 1); counter++;
        `TEXT(counter, "WRITING TO ALL REGB");
        `DEV_EQ_IMMED8(counter, regb, 2); counter++;
        `TEXT(counter, "WRITING TO ALL REGC");
        `DEV_EQ_IMMED8(counter, regc, 3); counter++;
        `TEXT(counter, "WRITING TO ALL REGD");
        `DEV_EQ_IMMED8(counter, regd, 4); counter++;

        `TEXT(counter, "READING FROM ALL REGA");
        `RAM_DIRECT_EQ_DEV(counter, 16'h0001, rega); counter++;
        `TEXT(counter, "READING FROM ALL REGB");
        `RAM_DIRECT_EQ_DEV(counter, 16'h0002, regb); counter++;
        `TEXT(counter, "READING FROM ALL REGC");
        `RAM_DIRECT_EQ_DEV(counter, 16'h0003, regc); counter++;
        `TEXT(counter, "READING FROM ALL REGD");
        `RAM_DIRECT_EQ_DEV(counter, 16'h0004, regd); counter++;

/*
        // test all registers on L and R channel into ALU
        `DEV_EQ_XY_ALU(counter, marlo, rega,     not_used, A); counter++;  
        `DEV_EQ_XY_ALU(counter, marhi, not_used, rega,     B)  ; counter++;
        `DEV_EQ_XY_ALU(counter, marlo, regb,     not_used, A)  ; counter++;
        `DEV_EQ_XY_ALU(counter, marhi, not_used, regb,     B)  ; counter++;
        `DEV_EQ_XY_ALU(counter, marlo, regc,     not_used, A)  ; counter++;
        `DEV_EQ_XY_ALU(counter, marhi, not_used, regc,     B)  ; counter++;
        `DEV_EQ_XY_ALU(counter, marlo, regd,     not_used, A)  ; counter++;
        `TEXT(counter, "REG D into marhi");
        `DEV_EQ_XY_ALU(counter, marhi, not_used, regd,     B)  ; counter++;

        `INSTRUCTION_S(counter, marlo, not_used, immed, B, A, `SET_FLAGS, `NA_AMODE, 1'bz, 8'b0); counter++;
        `INSTRUCTION_S(counter, marhi, not_used, immed, B, A, `SET_FLAGS, `NA_AMODE, 1'bz, 8'b0); counter++;
*/

        `DEV_EQ_IMMED8(counter, marlo, 8'd254); counter++;

`define ADD_ONE 256
        // PC=17
        `JMP_IMMED16(counter, `ADD_ONE); counter+=2;
        // implement 16 bit counter
        counter=`ADD_ONE;
        `TEXT(counter, "START OF MAIN LOOP BLOCK - ADD ONE TO MARLO");
        `INSTRUCTION_S(counter, marlo, not_used, marlo, B_PLUS_1, A, `SET_FLAGS, `NA_AMODE, 1'bz, 8'bz); counter++;

        `TEXT(counter, "CONDITIONAL ADD ONE TO MARHI");
        `INSTRUCTION_S(counter, marhi, not_used, marhi, B_PLUS_1, C, `NA_FLAGS, `NA_AMODE, 1'bz, 8'bz); counter++;

        `TEXT(counter, "GOTO LOOP");
        `JMP_IMMED16(counter, `ADD_ONE); counter+=2;

    end
    endtask : INIT_ROM

    integer icount=0;

    wire [15:0] pc = {CPU.PCHI, CPU.PCLO};

    task CLK_UP; 
    begin
        if (_RESET_SWITCH) icount++; else icount=0;

        $display("\n%9t", $time, " CLK GOING HIGH  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ INSTRUCTION %1d\n", icount); 
        clk = 1;
    end
    endtask

    task CLK_DN; 
    begin
        $display("\n%9t", $time, " CLK GOING LOW  -----------------------------------------------------------------------"); 
        $display("\n%9t", $time, " EXECUTING ..."); 
        DUMP; 
        clk = 0;
    end
    endtask


    // TESTS

    integer count;

    task noop;
        // do nothing - just for syntax
    endtask: noop


    `define DUMP_REG $display("%9t", $time, " REGISTERS:", "  REGA:%08b", CPU.regFile.get(0), "  REGB:%08b", CPU.regFile.get(1), "  REGC:%08b", CPU.regFile.get(2), "  REGD:%08b", CPU.regFile.get(3));

    wire [1:0] phaseFE = {CPU.phaseFetch, CPU.phaseExec};

    initial begin
        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        `define CYCLE begin CLK_UP; #HALF_CLK CLK_DN; #HALF_CLK; noop(); end
        `define FULL_CYCLE(N) for (count =0; count < N; count++) begin CLK_UP; #HALF_CLK; CLK_DN; #HALF_CLK; noop(); end

        INIT_ROM();
        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH = 0;
        CLK_DN;
        #1000
        CLK_UP;
        #1000

        `Equals(phaseFE, control::PHASE_EXEC)
        `Equals({CPU.PCHI,CPU.PCLO}, 16'bx)
        `Equals(CPU._addrmode_register, 1'bx)
        `Equals(CPU.address_bus, 16'hxxxx); // because first instruction will be on system and it's using dierct ram addressing

        #1000
        `DISPLAY("_mr=0  - so clocking is ineffective");
        `Equals(CPU._mr, 0);

        `FULL_CYCLE(1)
        #1000
        `Equals({CPU.PCHI,CPU.PCLO}, 16'bx)
        

        `DISPLAY("_RESET_SWITCH releasing");
        _RESET_SWITCH = 1;
        `Equals(CPU._mr, 0);
        `Equals(CPU._mrPC, 0);
        `Equals(phaseFE, control::PHASE_EXEC)

        `DISPLAY("CLOCK DOWN - NOTHING SHOULD HAPPEN");
        CLK_DN;
        #1000
        `Equals(CPU._mr, 0);
        `Equals(CPU._mrPC, 0);
        `Equals({CPU.PCHI,CPU.PCLO}, 16'bx)

        `DISPLAY("CLOCK UP - PC SHOULD RESET");
        CLK_UP;
        #1000
        `Equals(CPU._mr, 1);
        `Equals(CPU._mrPC, 0);
        `Equals({CPU.PCHI,CPU.PCLO}, 16'b0)
        `Equals(phaseFE, control::PHASE_FETCH)
        `Equals(CPU._addrmode_register, 1);
        DUMP();
        `Equals(CPU.address_bus, 16'hffaa);

        `DISPLAY("FIRST INSTRUCTION FETCHED");

        `DISPLAY("phase - advancing to exec")
        CLK_DN;
        #HALF_CLK
        `Equals(CPU._mr, 1);
        `Equals(CPU._mrPC, 1);

        `Equals(phaseFE, control::PHASE_EXEC)

        `Equals(CPU.PCHI, 8'h00) // doesn't advnce yet
        `Equals(CPU.PCLO, 8'h00) // doesn't advnce yet

        `Equals(`RAM(16'hffaa), 8'h42); 

        `DISPLAY("NEXT INSTRUCTION");
        `DISPLAY("phase - fetch")
        CLK_UP;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_FETCH)
        `Equals(CPU.PCHI, 8'h00) 
        `Equals(CPU.PCLO, 8'h01)
        `Equals(CPU._addrmode_register, 1);
        `Equals(CPU.address_bus, 16'hffaa);

        `Equals(CPU.MARLO.Q, CPU.MARLO.UNDEF);
        `Equals(CPU.MARHI.Q, CPU.MARHI.UNDEF);

        CLK_DN;
        #HALF_CLK

        `Equals(phaseFE, control::PHASE_EXEC)
        `Equals(CPU.MARLO.Q, 8'h42)
        `assertTrue(CPU.MARHI.isUndef());

        `DISPLAY("NEXT INSTRUCTION");
        `DISPLAY("phase - fetch")
        CLK_UP;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_FETCH)
        `Equals(CPU.PCHI, 8'h00)
        `Equals(CPU.PCLO, 8'h02)
        `Equals(CPU._addrmode_register, 0);
        //`Equals(CPU.address_bus, {CPU.MARHI.UNDEF, 8'h42});
        `Equals(CPU.address_bus, {8'b0xx00xx0, 8'h42});

        CLK_DN;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_EXEC)
        `Equals(CPU.MARLO.Q, 8'h42)
        `Equals(CPU.MARHI.Q, 8'h00)


        `DISPLAY("NEXT INSTRUCTION")
        `DISPLAY("phase - fetch")
        CLK_UP;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_FETCH)
        `Equals(CPU.PCHI, 8'h00)
        `Equals(CPU.PCLO, 8'h03)
        `Equals(CPU._addrmode_register, 0);
        `Equals(CPU.address_bus, 16'h0042); 

        CLK_DN;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_EXEC) 
        `Equals(CPU.MARLO.Q, 8'h43)
        `Equals(CPU.MARHI.Q, 8'h00)


        `DISPLAY("NEXT INSTRUCTION")
        `DISPLAY("phase - fetch")
        CLK_UP;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_FETCH)
        `Equals(`RAM(16'h0043), CPU.ram64.UNDEF); //8'hxx); // Should still be XX as we've not entered EXECUTE yet

        CLK_DN;
        #HALF_CLK
        `Equals(phaseFE, control::PHASE_EXEC) 
        `Equals(`RAM(16'h0043), 8'h22);


        `DISPLAY("NEXT INSTRUCTION")
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'h22)
        `Equals(CPU.MARHI.Q, 8'h00)

        `DISPLAY("instruction - fetch/exec");
        `FULL_CYCLE(1)
        `Equals(`RAM(16'habcd), 8'h22);

        `DISPLAY("instruction - fetch/exec");
        `FULL_CYCLE(1)
        `Equals( CPU.regFile.get(1), 8'h22);

        `DISPLAY("instruction  - fetch/exec");
        `FULL_CYCLE(1)
        `Equals(`RAM(16'hdcba), 8'h22);

        `DUMP_REG

        `DISPLAY("instruction 9 to 16 - values set as REGA=1 / B=2 / C=3 / E=4 round trip const to reg to ram");
        `regEquals(8'h00,8'h22,8'h22,8'h33);
        `FULL_CYCLE(1)
        `regEquals(8'h01,8'h22,8'h22,8'h33);
        `FULL_CYCLE(1)
        `regEquals(8'h1,8'h2,8'h22,8'h33);
        `FULL_CYCLE(1)
        `regEquals(8'h1,8'h2,8'h3,8'h33);
        `FULL_CYCLE(1)
        `regEquals(8'h1,8'h2,8'h3,8'h4);
        `FULL_CYCLE(1)
        `Equals(`RAM(1), 1);
        `FULL_CYCLE(1)
        `Equals(`RAM(2), 2);
        `FULL_CYCLE(1)
        `Equals(`RAM(3), 3);
        `FULL_CYCLE(1)
        `Equals(`RAM(4), 4);

        // COUNTING UP
        `Equals(CPU.MARLO.Q, 8'h22)
        `Equals(CPU.MARHI.Q, 8'd0)

        `FULL_CYCLE(1)
        `Equals(CPU.pc_addr, 16'd17)
        `Equals(CPU.MARLO.Q, 8'd254)
        `Equals(CPU.MARHI.Q, 8'd0)
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd254)
        `Equals(CPU.MARHI.Q, 8'd0)

        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd254)
        `Equals(CPU.MARHI.Q, 8'd0)
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd255)
        `Equals(CPU.MARHI.Q, 8'd0)

        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd255)
        `Equals(CPU.MARHI.Q, 8'd0)
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd255)
        `Equals(CPU.MARHI.Q, 8'd0)

        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd255)
        `Equals(CPU.MARHI.Q, 8'd0)
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd0)
        `Equals(CPU.MARHI.Q, 8'd0)

        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd0)
        `Equals(CPU.MARHI.Q, 8'd1)
        `FULL_CYCLE(1)
        `Equals(CPU.MARLO.Q, 8'd0)
        `Equals(CPU.MARHI.Q, 8'd1)
        #1

        $display("END OF TEST CASES ==============================================");
/*
*/

//`include "./generated_tests.v"
/*
        #HALF_CLK
        count=100;
        while (count -- > 0) begin
            #HALF_CLK
            CLK_UP;
            #HALF_CLK
            CLK_DN;
            $display("PC %2x:%2x !!!!!!!!!!!!!!!!!!!!!!!! CLK COUNT REMAINING=%-d", PCHI, PCLO, count);
        end
*/

        // consume any remaining code
        // verilator lint_off INFINITELOOP
        $display("FREE RUN CPU ==================================");
         while (1==1) begin
             #HALF_CLK
             CLK_UP;
             #HALF_CLK
             CLK_DN;
         end
        // verilator lint_on INFINITELOOP

        $display("END OF TEST");
        $finish();

    end


    integer pcval;
    assign pcval={CPU.PCHI, CPU.PCLO};

    string_bits currentCode; // create field so it can appear in dump file
    string_bits currentCodeText; // create field so it can appear in dump file

    always @(CPU.PCHI or CPU.PCLO) begin
        currentCode = string_bits'(CODE[pcval]); // assign outside 'always' doesn't work so do here instead
        currentCodeText = string_bits'(CODE_TEXT[pcval]);
        $display("%9t ", $time, "INCREMENTED PC=%1d    INSTRUCTION: %1s", {CPU.PCHI, CPU.PCLO}, currentCode);
        if (currentCodeText != "") $display("%9t ", $time, "COMMENT: %1s", currentCodeText);
    end

    `define DD  $display ("%9t ", $time,  "DUMP  ",
    task DUMP_OP;
          `DD ": CODE: %1s", currentCode);
          `DD ": %1s", label);
          label="";
    endtask


    task DUMP;
            DUMP_OP;
            `DD " phase=%1s", control::fPhase(CPU.phaseFetch, CPU.phaseExec));
            `DD " PC=%01d (0x%4h) PCHItmp=%0d (%2x)", CPU.pc_addr, CPU.pc_addr, CPU.PC.PCHITMP, CPU.PC.PCHITMP);
            `DD " instruction=%08b:%08b:%08b:%08b:%08b:%08b", CPU.ctrl.instruction_6, CPU.ctrl.instruction_5, CPU.ctrl.instruction_4, CPU.ctrl.instruction_3, CPU.ctrl.instruction_2, CPU.ctrl.instruction_1);
            `DD " FE=%1b%1b(%1s)", CPU.phaseFetch, CPU.phaseExec, control::fPhase(CPU.phaseFetch, CPU.phaseExec));
            `DD " DIRECT=%02x:%02x", CPU.ctrl.direct_address_hi, CPU.ctrl.direct_address_lo);
            `DD " amode=%1s", control::fAddrMode(CPU._addrmode_register), " addbbus=0x%4x", CPU.address_bus);
            `DD " rom=%08b:%08b:%08b:%08b:%08b:%08b",  CPU.ctrl.rom_6.D, CPU.ctrl.rom_5.D, CPU.ctrl.rom_4.D, CPU.ctrl.rom_3.D, CPU.ctrl.rom_2.D, CPU.ctrl.rom_1.D);
            `DD " immed8=%08b", CPU.ctrl.immed8);
            `DD " ram=%08b", CPU.ram64.D);
            `DD " tdev=%b(%s)", CPU.targ_dev, control::tdevname(CPU.targ_dev),
                " adev=%b(%s)", CPU.abus_dev, control::adevname(CPU.abus_dev),
                " bdev=%b(%s)", CPU.bbus_dev,control::bdevname(CPU.bbus_dev),
                " alu_op=%b(%1s)", CPU.alu_op, aluopName(CPU.alu_op)
            );            
            `DD " abus=%8b bbus=%8b alu_result_bus=%8b", CPU.abus, CPU.bbus, CPU.alu_result_bus);
            `DD " condition=%02d(%1s) _do_exec=%b", CPU.ctrl.condition, control::condname(CPU.ctrl.condition), CPU.ctrl._do_exec);
            `DD " FLAGS czonGLEN=%8b gated_flags_clk=%1b", CPU.status_register_czonGLEN.Q, CPU.gated_flags_clk);
            `DD " MAR=%8b:%8b (0x%2x:%2x)", CPU.MARHI.Q, CPU.MARLO.Q, CPU.MARHI.Q, CPU.MARLO.Q);
            `DD "  REGA:%08b", CPU.regFile.get(0),
                "  REGB:%08b", CPU.regFile.get(1),
                "  REGC:%08b", CPU.regFile.get(2),
                "  REGD:%08b", CPU.regFile.get(3)
                );
            `define LOG_ADEV_SEL(DNAME) " _adev_``DNAME``=%1b", CPU._adev_``DNAME``
            `define LOG_BDEV_SEL(DNAME) " _bdev_``DNAME``=%1b", CPU._bdev_``DNAME``
            `define LOG_TDEV_SEL(DNAME) " _``DNAME``_in=%1b",  CPU._``DNAME``_in
            `DD " WIRES ", `CONTROL_WIRES(LOG, `COMMA));
    endtask 


/*
    if (0) always @* begin
        $display ("%9t ", $time,  "MON     ",
                 "rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                 " _amode=%-2s", control::fAddrMode(_addrmode_register, _addrmode_direct),
                 " address_bus=0x%4x", address_bus,
                 " FE=%-6s (%1b%1b)", control::fPhase(phaseFetch, phaseExec), phaseFetch, phaseExec,
                 " bbus=%8b abus=%8b alu_result_bus=%8b", bbus, abus, alu_result_bus,
                 " bdev=%04b adev=%04b targ=%05b alu_op=%05b (%1s)", bbus_dev, abus_dev, targ_dev, alu_op, aluopName(alu_op),
                 " tsel=%32b ", tsel,
                 " PC=%02h:%02h", PCHI, PCLO,
                 "     : %1s", label
                 );
    end

*/

    always @* 
        if (_RESET_SWITCH)  
            $display("\n%9t RESET SWITCH RELEASE   _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
        else      
            $display("\n%9t RESET SWITCH SET       _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 

    always @* 
        if (CPU._mr)  
            $display("\n%9t PC RESET RELEASE   _mr=%1b  ======================================================================\n", $time, CPU._mr); 
        else      
            $display("\n%9t PC RESET SET       _mr=%1b  ======================================================================\n", $time, CPU._mr); 


    

    integer instCount = 0;

/*
    always @(CPU.clk) begin
        $display("%9t", $time, " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CLK %1b  <<<<<<<<<<<<<<<<<<<<<<<", CPU.clk);
    end

    always @(posedge CPU.phaseExec) begin
        $display("%9t", $time, " START PHASE: EXECUTE (posedge) =============================================================="); 
    end

    always @(negedge CPU.phaseExec) begin
        $display("%9t", $time, " END PHASE: EXECUTE (negedge) =============================================================="); 
        DUMP;
    end
    always @(posedge CPU.phaseFetch) begin
        instCount ++;
        $display("%9t", $time, " START PHASE: FETCH (posedge)  INTRUCTION#=%-d ==============================================================", instCount); 
    end
*/
    
///////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRAINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////

    always @(posedge CPU.gated_flags_clk) begin
        if (CPU._phaseExec) begin
            $display("ILLEGAL FLAGS LOAD DURING FETCH PHASE");
            $finish();
        end
    end 
        
    // constraints

    always @(posedge CPU.phaseExec) begin
        if (_RESET_SWITCH && CPU.ctrl.instruction_6 === 'x) begin
           $display("instruction_6", CPU.ctrl.instruction_6); 
            DUMP;
            $display("ERROR END OF PROGRAM - PROGRAM BYTE = XX "); 
            `FINISH_AND_RETURN(1);
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
                $display("%9t ", $time, " prev = %8b, now = %8b", prev_alu_result_bus, CPU.alu_result_bus);
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
        if (CPU._mrPC & CPU.phaseExec) begin
            if (CPU._addrmode_register === 1'bx) begin
                $display("\n\n%9t ", $time, " ERROR ILLEGAL INDETERMINATE ADDR MODE _REG=%1b", CPU._addrmode_register );
                DUMP;
                $display("\n\n%9t ", $time, " ABORT");
                $finish();
            end
        end
    end

    always @( * )
    begin
        $display("%9t", $time, " OK MARHI:MARLO = %2h:%2h = %1d", CPU.MARHI.Q, CPU.MARLO.Q, (256*CPU.MARHI.Q)+ CPU.MARLO.Q);
    end

endmodule : test
