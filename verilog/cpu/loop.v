

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
    localparam TCLK=1000;   // clock cycle

    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
    logic _RESET_SWITCH;

    logic clk=0;

/*
    always begin
       #TCLK clk = !clk;
    end
*/

    cpu CPU(_RESET_SWITCH, clk);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    `define RAM(A) CPU.ram64.Mem[A]
    `define DATA(D) {40'bz, D} /* padded to rom width with z */

    localparam MAX_PC=100;
    string_bits CODE [MAX_PC];

    // SETUP ROM
    task INIT_ROM;
    begin

        `DEV_EQ_ROM_DIRECT(0, marlo, 'hffaa);
        `DEV_EQ_IMMED8(1, marhi, 0);

        `INSTRUCTION(2, marhi, marhi, instreg, A_PLUS_B, `REGISTER, 1, `NA);
        `INSTRUCTION(3, pchitmp, not_used, instreg, B, `REGISTER, 0, `NA);
        `INSTRUCTION(4, pc, not_used, instreg, B, `REGISTER, 1, `NA);


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
        $display("\n%9t", $time, " END OF CLOCK STATE %d", clk); 
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

        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        CLK_DN;

        #10000
        _RESET_SWITCH <= 1;

        #100000

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
//                 " instruction=%08b:%08b:%08b", ctrl.instruction_hi, ctrl.instruction_mid, ctrl.instruction_lo);
                 " instruction=%08b:%08b:%08b:%08b:%08b:%08b", CPU.ctrl.instruction_6, CPU.ctrl.instruction_5, CPU.ctrl.instruction_4, CPU.ctrl.instruction_3, CPU.ctrl.instruction_2, CPU.ctrl.instruction_1);
            $display ("%9t ", $time,  "DUMP  ",
                " op=%d(%-s)", CPU.ctrl.op_ctrl, control.opName(CPU.ctrl.op_ctrl),
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
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
            $display ("%9t ", $time,  "DUMP  ",
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", CPU.rbus, CPU.lbus, CPU.alu_result_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " MAR=%8b:%8b (0x%2x:%2x)", CPU.MARHI.Q, CPU.MARLO.Q, CPU.MARHI.Q, CPU.MARLO.Q);
            $display ("%9t ", $time,  "DUMP  ",
                 " PC=%02h:%02h", CPU.PCHI, CPU.PCLO);
            $display("%9t", $time, " DUMP:",
                 "  REGA:%08b", CPU.regFile.get(0),
                 "  REGB:%08b", CPU.regFile.get(1),
                 "  REGC:%08b", CPU.regFile.get(2),
                 "  REGD:%08b", CPU.regFile.get(3)
                 );
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
            $display("END OF PROGRAM - PROGRAM BYTE = XX "); 
            $finish();
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
