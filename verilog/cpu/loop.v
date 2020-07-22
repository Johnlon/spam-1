

//// RUN  and grep for OK to see counter incrementing
/*

Unit number Time unit Unit number Time unit 
    0        1 s        -8         10 ns 
   -1        100 ms     -9         1 ns 
   -2        10 ms      -10        100 ps 
   -3        1 ms       -11        10 ps 
   -4        100 us     -12        1 ps 
   -5        10 us      -13        100 fs 
   -6        1 us       -14        10 fs 
   -7        100 ns     -15        1 fs 
*/

`include "cpu.v"
`include "../lib/assertion.v"
// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

//`timescale 1ns/1ns
`timescale 1ns/1ns



`define SEMICOLON ;
`define COMMA ,

module test();

    import alu_ops::*;

    `include "../lib/display_snippet.v"

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    // CLOCK ===================================================================================
    localparam TCLK=500;   // clock cycle

    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
    logic _RESET_SWITCH;

    logic clk=0;

    always begin
       #TCLK clk = !clk;
    end

    cpu CPU(_RESET_SWITCH, clk);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    `define RAM(A) CPU.ram64.Mem[A]
    `define DATA(D) {40'bz, D} /* padded to rom width with z */

    localparam MAX_PC=100;
    string_bits CODE [MAX_PC];

    integer ADD_ONE;
    `define WRITE_UART 60
    `define READ_UART 80
    integer WAIT_UART_OUT;

    // SETUP ROM
    integer icount;
    task INIT_ROM;
    begin

         // implement 16 bit counter

         icount = 0;
         `DEV_EQ_IMMED8(icount, rega, '0); icount++;
         `DEV_EQ_IMMED8(icount, regb, '0); icount++;
         `DEV_EQ_IMMED8(icount, marlo, '0); icount++;

         ADD_ONE=icount;

         `DEV_EQ_XI_ALU(icount, rega, rega, 1, A_PLUS_B) ; icount++; // A_PLUS_B - doesn't consume carry but sets it // TODO The "lower" bank of arith in the design can do this +1 nocarryin without this specialised instruction
         `DEV_EQ_XI_ALU(icount, regb, regb, 0, A_PLUS_B_PLUS_C); icount++; // B=B+0+Carryin   - sets and consumes carry 
         `DEV_EQ_XY_ALU(icount, marlo, not_used, rega, B_PLUS_1); icount++;

         WAIT_UART_OUT = icount;
         `JMPDI_IMMED16(icount, `READ_UART); icount+=2;
         `JMPDO_IMMED16(icount, `WRITE_UART); icount+=2;
         `JMP_IMMED16(icount, WAIT_UART_OUT); icount+=2;

         icount = `WRITE_UART;
         `DEV_EQ_XY_ALU(icount, uart, rega, rega, A); icount++;
         `JMP_IMMED16(icount, ADD_ONE); icount+=2;

         icount = `READ_UART;
         `DEV_EQ_XY_ALU(icount, marhi, uart, uart, A); icount++;
         `JMP_IMMED16(icount, ADD_ONE); icount+=2;
            

    end
    endtask : INIT_ROM

    initial begin
        //$timeformat(-3, 0, "ms", 10);

        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH = 0;
        clk=0;

        #1000
        _RESET_SWITCH = 1;

    end

   // $timeformat [(unit_number, precision, suffix, min_width )] ;
    task DUMP;
            DUMP_OP;
            `define DD $display ("%9t ", $time,  "DUMP  ",

            `DD " phase=%-6s", control::fPhase(CPU.phaseFetch, CPU.phaseExec));
            `DD " PC=%1d (0x%4h) PCHItmp=%d (%2x)", CPU.pc_addr, CPU.pc_addr, CPU.PC.PCHITMP, CPU.PC.PCHITMP);
            `DD " instruction=%08b:%08b:%08b:%08b:%08b:%08b", CPU.ctrl.instruction_6, CPU.ctrl.instruction_5, CPU.ctrl.instruction_4, CPU.ctrl.instruction_3, CPU.ctrl.instruction_2, CPU.ctrl.instruction_1);
            `DD " FDE=%1b%1b(%-s)", CPU.phaseFetch, CPU.phaseExec, control::fPhase(CPU.phaseFetch, CPU.phaseExec));
            `DD " _amode=%-1s", control::fAddrMode(CPU._addrmode_register, CPU._addrmode_direct),
                 " (%02b)", {CPU._addrmode_register, CPU._addrmode_direct},
                 " addbbus=0x%4x", CPU.address_bus);
            `DD " rom=%08b:%08b:%08b:%08b:%08b:%08b",  CPU.ctrl.rom_6.D, CPU.ctrl.rom_5.D, CPU.ctrl.rom_4.D, CPU.ctrl.rom_3.D, CPU.ctrl.rom_2.D, CPU.ctrl.rom_1.D);
            `DD " direct8=%08b", CPU.direct8,
                 " immed8=%08b", CPU.immed8);
            `DD " ram=%08b", CPU.ram64.D);
            `DD " tdev=%5b(%s)", CPU.targ_dev, control::tdevname(CPU.targ_dev),
                " adev=%4b(%s)", CPU.abus_dev, control::devname(CPU.abus_dev),
                " bdev=%4b(%s)", CPU.bbus_dev,control::devname(CPU.bbus_dev),
                " alu_op=%5b(%s)", CPU.alu_op, aluopName(CPU.alu_op)
            );            
            `DD " abus=%8b bbus=%8b alu_result_bus=%8b", CPU.abus, CPU.bbus, CPU.alu_result_bus);
            `DD " FLAGS czonGLEN=%8b gated_flags_clk=%1b", CPU.flags_czonGLEN.Q, CPU.gated_flags_clk);
            `DD " MAR=%8b:%8b (0x%2x:%2x)", CPU.MARHI.Q, CPU.MARLO.Q, CPU.MARHI.Q, CPU.MARLO.Q);
            `DD "  REGA:%08b", CPU.regFile.get(0),
                 "  REGB:%08b", CPU.regFile.get(1),
                 "  REGC:%08b", CPU.regFile.get(2),
                 "  REGD:%08b", CPU.regFile.get(3)
                 );

            `define LOG_ADEV_SEL(DNAME) " _adev_``DNAME``=%1b", CPU._adev_``DNAME``
            `define LOG_BDEV_SEL(DNAME) " _bdev_``DNAME``=%1b", CPU._bdev_``DNAME``
            `define LOG_TDEV_SEL(DNAME) " _``DNAME``_in=%1b",  CPU._``DNAME``_in
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
    endtask 


    always @* begin
        if (_RESET_SWITCH)  
            $display("\n%9t RESET SWITCH RELEASE   _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
        else      
            $display("\n%9t RESET SWITCH SET       _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
    end

    always @* begin
        if (CPU._mrN)  
            $display("\n%9t PC RESET RELEASE   _mrN=%1b  ======================================================================\n", $time, CPU._mrN); 
        else      
            $display("\n%9t PC RESET SET       _mrN=%1b  ======================================================================\n", $time, CPU._mrN); 
    end

    integer pcval;
    assign pcval={CPU.PCHI, CPU.PCLO};

    string_bits currentCode; // create field so it can appear in dump file

    always @( negedge CPU.phaseExec )
       $display ("%9t ", $time,  "DUMP  ", " abus=%8b bbus=%8b alu_result_bus=%8b", CPU.abus, CPU.bbus, CPU.alu_result_bus);

    always @(CPU.PCHI or CPU.PCLO) begin
        $display("");
        $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {CPU.PCHI, CPU.PCLO});
        currentCode = string_bits'(CODE[pcval]); // assign outside 'always' doesn't work so do here instead
        $display ("%9t ", $time,  "OPERATION:  ", ": %-s", currentCode);
    end

    task DUMP_OP;
        $display ("%9t ", $time,  "DUMP  ", ": OPERATION: %-1s        PC=%4h", currentCode,pcval);
        $display ("%9t ", $time,  "DUMP  ", ": PC    : %04h", pcval);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 1 : %02h     %8b", CPU.ctrl.instruction_1, CPU.ctrl.instruction_1);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 2 : %02h     %8b", CPU.ctrl.instruction_2, CPU.ctrl.instruction_2);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 3 : %02h     %8b", CPU.ctrl.instruction_3, CPU.ctrl.instruction_3);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 4 : %02h     %8b", CPU.ctrl.instruction_4, CPU.ctrl.instruction_4);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 5 : %02h     %8b", CPU.ctrl.instruction_5, CPU.ctrl.instruction_5);
        $display ("%9t ", $time,  "DUMP  ", ": ROM 6 : %02h     %8b", CPU.ctrl.instruction_6, CPU.ctrl.instruction_6);
    endtask


    integer not_initialised = 16'hffff + 1;
    integer last_count = not_initialised;
    integer count;

    always @( CPU.MARLO.Q )
    begin
        count = { CPU.regFile.get(1), CPU.regFile.get(0) };

        $display("%9t", $time, " COUNT = %4h ", 16'(count));

        if (last_count !== not_initialised) begin
            if (last_count == 65535 && count != 0) begin 
                $error("ERROR wrong count roll value : count=%d  last_count=%d but expected count=0", count , last_count);
                $finish_and_return(2);
            end
            
            if (last_count != 65535 && count != last_count+1) begin 
                $error("ERROR wrong count next +1 value : count=%d  last_count=%d but expected count=%d", count , last_count, last_count+1);
                $finish_and_return(2);
            end
        end
        else 
        begin
            if (count != 0) begin 
                $error("ERROR wrong initial count : count=%d", count);
                $finish_and_return(2);
            end
    
        end
        
        $display("OK %4h", {CPU.regFile.get(1), CPU.regFile.get(0) });
        last_count=count;
    end

    
///////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRAINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////
    always @(*) begin
        if (CPU._mrN && CPU.phaseExec && CPU.ctrl.instruction_6 === 'x) begin
            #1
            DUMP;
            $display("rom value instruction_6", CPU.ctrl.instruction_6); 
            $error("ERROR END OF PROGRAM - PROGRAM BYTE = XX "); 
            $finish_and_return(1);
        end
    end


endmodule : test
