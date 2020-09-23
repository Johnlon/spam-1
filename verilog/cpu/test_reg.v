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
    //localparam TCLK=350;   // clock cycle
    localparam TCLK=1000;   // clock cycle

    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html
    logic _RESET_SWITCH;

    bit clk=0;

    always begin
       #TCLK clk = !clk;
       $display ("----");
       $display ("%9t ", $time,  " >>>>>>>> CLK = %b", clk);
    end

    cpu CPU(_RESET_SWITCH, clk);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    `define RAM(A) CPU.ram64.Mem[A]
    `define DATA(D) {40'bz, D} /* padded to rom width with z */

    localparam MAX_PC=100;
    string_bits CODE [MAX_PC];

    // SETUP ROM
    integer icount;
    task INIT_ROM;
    begin
        for (icount=0; icount<MAX_PC; icount++) begin
            CODE[icount]="";
        end

         // implement 16 bit counter
         icount = 0;

         `DEV_EQ_IMMED8(icount, rega, 1); icount++;
         `DEV_EQ_IMMED8(icount, regb, 5); icount++;
         `DEV_EQ_XY_ALU(icount, rega, rega, rega, B_PLUS_1); icount++;
         `DEV_EQ_XY_ALU(icount, marlo, rega, rega, A); icount++;
            

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

    always @( negedge CPU.phaseExec )
       $display ("%9t ", $time,  "DUMP  ", " abus=%8b bbus=%8b alu_result_bus=%8b", CPU.abus, CPU.bbus, CPU.alu_result_bus);

    integer pcval;
    assign pcval={CPU.PCHI, CPU.PCLO};
    always @(CPU.PCHI or CPU.PCLO) begin
        $display("");
        $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {CPU.PCHI, CPU.PCLO});
        $display ("%9t ", $time,  "DUMP  ", ": OPERATION: '%-1s'        PC=%4h", CODE[pcval],pcval);
        $display ("%9t ", $time,  "DUMP  ", ": PC    : %04h", pcval);
    end

    logic [47:0] instruction;
    always @* begin
        instruction = {
            CPU.ctrl.instruction_6,
            CPU.ctrl.instruction_5,
            CPU.ctrl.instruction_4,
            CPU.ctrl.instruction_3,
            CPU.ctrl.instruction_2,
            CPU.ctrl.instruction_1
        };
        $display ("%9t ", $time,  "DUMP  : %48b ", 
            instruction,
            " OP:%-d T:%-d A:%-d B:%-d M:%1b ADDR:%04h I:%-d",
            instruction[47:43], // 5
            instruction[42:38], // 5
            instruction[37:34], // 4
            instruction[33:30], // 4
            //instruction[29:25], // 5
            instruction[24:24], // 1
            instruction[23:8], // C161
            instruction[7:0] // 8
        );

    end

    always @( CPU.MARLO.Q )
    begin
        $display("%9t", $time, " MARLO = ", CPU.MARLO.Q);
        
        $display("REGFILE %8h", CPU.regFile.binding_for_tests );
    end

    always @* begin
        $display("%9t", $time, " ALU : abus=%8b bbus=%8b alu_result_bus=%8b", CPU.abus, CPU.bbus, CPU.alu_result_bus);
    end
    
    always @* begin
        $display("%9t", $time, " REGFILE : LATCH Q UPDATED %8b ", 
                CPU.regFile.input_register.Q
            );
    end

    always @* begin
        $display("%9t", $time, " DEVICES ",
            " tdev=%5b(%s)", CPU.targ_dev, control::tdevname(CPU.targ_dev),
            " adev=%4b(%s)", CPU.abus_dev, control::adevname(CPU.abus_dev),
            " bdev=%4b(%s)", CPU.bbus_dev,control::bdevname(CPU.bbus_dev),
            " alu_op=%5b(%1s)", CPU.alu_op, aluopName(CPU.alu_op)
        );            
    end

    always @* begin
            `define LOG_ADEV_SEL(DNAME) " _adev_``DNAME``=%1b", CPU._adev_``DNAME``
            `define LOG_BDEV_SEL(DNAME) " _bdev_``DNAME``=%1b", CPU._bdev_``DNAME``
            `define LOG_TDEV_SEL(DNAME) " _``DNAME``_in=%1b",  CPU._``DNAME``_in
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
    end

    initial begin
            $display("%9t", $time, " DUMP   WIRES ", `CONTROL_WIRES(LOG, `COMMA));
    end

///////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRAINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    always @(*) begin
        if (CPU._mrN && CPU.phaseExec && CPU.ctrl.instruction_6 === 'x) begin
            #1
            $display("rom value instruction_6", CPU.ctrl.instruction_6); 
            $error("ERROR END OF PROGRAM - PROGRAM BYTE = XX "); 
            $finish_and_return(1);
        end
    end
*/


endmodule : test
