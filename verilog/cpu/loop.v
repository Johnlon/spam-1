
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

    `include "../lib/display_snippet.v"

    localparam SETTLE_TOLERANCE=50; // perhaps not needed now with new control logic impl

    // CLOCK ===================================================================================
    localparam TCLK=50;   // clock cycle

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

    // SETUP ROM
    task INIT_ROM;
    begin

// init
 `define ADD_ONE 4
 `define DO_CARRY 30
         `DEV_EQ_IMMED8(0, rega, '0);
         `DEV_EQ_IMMED8(1, regb, '0);
         `DEV_EQ_IMMED8(2, marlo, '0);
         `DEV_EQ_IMMED8(3, marhi, '0);

         // implement 16 bit counter
         `DEV_EQ_XY_ALU(`ADD_ONE, rega, not_used, rega, B_PLUS_1)
         `JMPC_IMMED16(`ADD_ONE+1, `DO_CARRY)
         `DEV_EQ_XY_ALU(`ADD_ONE+3, marlo, not_used, rega, B_PLUS_1)
         `JMP_IMMED16(`ADD_ONE+4, `ADD_ONE)

         `DEV_EQ_XY_ALU(`DO_CARRY, regb, not_used, regb, B_PLUS_1)
         `DEV_EQ_XY_ALU(`DO_CARRY+1, marlo, not_used, rega, B_PLUS_1)
         `JMP_IMMED16(`DO_CARRY+2, `ADD_ONE)
 

    end
    endtask : INIT_ROM

    initial begin
        $timeformat(-3, 0, "ms", 10);

        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        clk=0;

        #1000
        _RESET_SWITCH <= 1;

    end

   // $timeformat [(unit_number, precision, suffix, min_width )] ;


    always @* begin
        if (_RESET_SWITCH)  
            $display("\n%9t RESET SWITCH RELEASE   _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
        else      
            $display("\n%9t RESET SWITCH SET       _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
    end

    always @* begin
        if (CPU._mrPC)  
            $display("\n%9t PC RESET RELEASE   _mrPC=%1b  ======================================================================\n", $time, CPU._mrPC); 
        else      
            $display("\n%9t PC RESET SET       _mrPC=%1b  ======================================================================\n", $time, CPU._mrPC); 
    end

    integer pcval;
    assign pcval={CPU.PCHI, CPU.PCLO};

    string_bits currentCode; // create field so it can appear in dump file

    always @(CPU.PCHI or CPU.PCLO) begin
        $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {CPU.PCHI, CPU.PCLO});
        currentCode = string_bits'(CODE[pcval]); // assign outside 'always' doesn't work so do here instead
        $display ("%9t ", $time,  "DUMP  ", ": CODE: %-s", currentCode);
    end

    integer not_initialised = 16'hffff + 1;
    integer last_count = not_initialised;
    integer count;

    always @( CPU.MARLO.Q )
    begin
        count = { CPU.regFile.get(1), CPU.regFile.get(0) };

        $display("%9t", $time, " COUNT = %4h ", 16'(count));


        if (last_count !== not_initialised) begin
            if (last_count == 65535 && count != 0) begin 
                $display("wrong MAR roll value : count=%d  last_count=%d", count , last_count);
                $finish();
            end
            
            if (last_count != 65535 & count != last_count+1) begin 
                $display("wrong MAR next +1 value : count=%d  last_count=%d", count , last_count);
                $finish();
            end
        end
        last_count=count;
    end

    
///////////////////////////////////////////////////////////////////////////////////////////////////////
// CONSTRAINTS
///////////////////////////////////////////////////////////////////////////////////////////////////////

endmodule : test
