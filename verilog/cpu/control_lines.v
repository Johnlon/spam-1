// FIXME MAKE ALL THE tri WIRES tri0
//
// CONSTANTS AND FUNCTIONS AND MACROS
//

`ifndef V_CONTROL
`define V_CONTROL

`define DECODE_PHASES   (phaseFetch ? "fetch" : phaseExec? "exec": "---")
`define DECODE_PHASE   logic [6*8-1:0] sPhase; assign sPhase = `DECODE_PHASES;

// unlike an assign this executes instantaneously but not referentially transparent
`define DECODE_ADDRMODES (!_addrmode_register?  "register" : !_addrmode_direct? "direct": "--") 
`define DECODE_ADDRMODE  logic [9*8-1:0] sAddrMode; assign sAddrMode = `DECODE_ADDRMODES;

`define toADEV(DEVNAME) control::ADEV_``DEVNAME``
`define toBDEV(DEVNAME) control::BDEV_``DEVNAME``
`define toTDEV(DEVNAME) control::TDEV_``DEVNAME``

`define AMODE_TUPLE    wire [1:0] _addrmode = {CPU._addrmode_register, CPU._addrmode_direct}; 

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY
// verilator lint_off MULTITOP
// verilator lint_off DECLFILENAME
// verilator lint_off DISABLED-UNUSED
`timescale 1ns/1ns

package control;
 
    localparam PHASE_NONE = 2'b00;
    localparam PHASE_FETCH = 2'b10;
    localparam PHASE_EXEC = 2'b01;
   
    // see _addrmode_tuple
    localparam _AMODE_NONE=2'b11;
    localparam _AMODE_REG=2'b01;
    localparam _AMODE_DIR=2'b10;

    // A BUS
    localparam [3:0] ADEV_rega = 0; 
    localparam [3:0] ADEV_regb = 1; 
    localparam [3:0] ADEV_regc = 2; 
    localparam [3:0] ADEV_regd = 3; 
    localparam [3:0] ADEV_marlo = 4;
    localparam [3:0] ADEV_marhi = 5;
    localparam [3:0] ADEV_uart = 6;
    localparam [3:0] ADEV_not_used = 7;

    // B BUS
    localparam [3:0] BDEV_rega = 0; 
    localparam [3:0] BDEV_regb = 1; 
    localparam [3:0] BDEV_regc = 2; 
    localparam [3:0] BDEV_regd = 3; 
    localparam [3:0] BDEV_marlo = 4;
    localparam [3:0] BDEV_marhi = 5;
    localparam [3:0] BDEV_immed = 6; // READ FROM THE INSTRUCTION
    localparam [3:0] BDEV_ram = 7;
    localparam [3:0] BDEV_not_used = BDEV_immed; // noop alias

    // DEST
    localparam [3:0] TDEV_rega = 0; 
    localparam [3:0] TDEV_regb = 1; 
    localparam [3:0] TDEV_regc = 2; 
    localparam [3:0] TDEV_regd = 3; 
    localparam [3:0] TDEV_marlo = 4;
    localparam [3:0] TDEV_marhi = 5;
    localparam [3:0] TDEV_uart = 6;
    localparam [3:0] TDEV_ram = 7;
    localparam [3:0] TDEV_not_used = 12;
    localparam [3:0] TDEV_pchitmp = 13; // only load pchitmp
    localparam [3:0] TDEV_pclo= 14; // only load pclo
    localparam [3:0] TDEV_pc= 15;   // load pclo from instruction and load pchi from pchitmp

    localparam [3:0] TDEV_jmpc= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpz= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpo= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpn= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpeq= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpne= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpgt= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmplt= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpdi= 15;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] TDEV_jmpdo= 15;   // load pclo from instruction and load pchi from pchitmp

    // ALL
/*
    localparam [3:0] DEV_rega = 0; 
    localparam [3:0] DEV_regb = 1; 
    localparam [3:0] DEV_regc = 2; 
    localparam [3:0] DEV_regd = 3; 
    localparam [3:0] DEV_nu4 = 4; 
    localparam [3:0] DEV_nu5 = 5; 
    localparam [3:0] DEV_nu6 = 6; 
    localparam [3:0] DEV_pchitmp = 7; // only load pchitmp
    localparam [3:0] DEV_pclo= 8; // only load pclo
    localparam [3:0] DEV_pc= 9;   // load pclo from instruction and load pchi from pchitmp
    localparam [3:0] DEV_nu10 = 10;
    localparam [3:0] DEV_immed = 11; // READ FROM THE INSTRUCTION
    localparam [3:0] DEV_ram = 12;
    localparam [3:0] DEV_marlo = 13;
    localparam [3:0] DEV_marhi = 14;
    localparam [3:0] DEV_uart = 15;
*/

    // targets
 /*   function [4:0] TDEV([3:0] x);
        TDEV = {1'b0, x};
    endfunction
*/

    // these can be src or dest
    //`define TARGL(DNAME) localparam [4:0] TDEV_``DNAME`` = {1'b0, DEV_``DNAME``};

    // dest only
    //`define TARGH(DNAME) localparam [4:0] TDEV_``DNAME`` = DEV_``DNAME``;

    // define constants
/*
    `TARGL(ram)
    `TARGL(immed)
    `TARGL(marlo)
    `TARGL(marhi)
    `TARGL(uart)
    `TARGL(rega)
    `TARGL(regb)
    `TARGL(regc)
    `TARGL(regd)

    // 9-15 - todo
    `TARGH(pchitmp)
    `TARGH(pclo)
    `TARGH(pc)
    `TARGH(jmpc)
    `TARGH(jmpz)
    `TARGH(jmpo)
    `TARGH(jmpn)
    `TARGH(jmpgt)
    `TARGH(jmplt)
    `TARGH(jmpeq)
    `TARGH(jmpne)
    `TARGH(jmpdi)
    `TARGH(jmpdo)
    `TARGH(nu1)
    `TARGH(nu2)
    `TARGH(nu3)
*/

    function string adevname([2:0] dev); 
    begin
        case (dev)
            ADEV_rega: adevname = "REGA"; // 1
            ADEV_regb: adevname = "REGB"; // 2
            ADEV_regc: adevname = "REGC"; // 3
            ADEV_regd: adevname = "REGD"; // 4
            ADEV_marlo: adevname = "MARLO";
            ADEV_marhi: adevname = "MARHI";
            ADEV_uart: adevname = "UART";
            default: begin
                string n; 
                $sformat(n,"??(unknown A device %3b)", dev);
                adevname = n;
            end
        endcase
    end
    endfunction    

    function string bdevname([2:0] dev); 
    begin
        case (dev)
            BDEV_rega: bdevname = "REGA"; // 1
            BDEV_regb: bdevname = "REGB"; // 2
            BDEV_regc: bdevname = "REGC"; // 3
            BDEV_regd: bdevname = "REGD"; // 4
            BDEV_marlo: bdevname = "MARLO";
            BDEV_marhi: bdevname = "MARHI";
            BDEV_immed: bdevname = "IMMED";
            BDEV_ram: bdevname = "RAM";
            default: begin
                string n; 
                $sformat(n,"??(unknown B device %3b)", dev);
                bdevname = n;
            end
        endcase
    end
    endfunction    

    function string tdevname([3:0] tdev); 
    begin
        case (tdev)
            TDEV_rega: tdevname = "REGA"; // 1
            TDEV_regb: tdevname = "REGB"; // 2
            TDEV_regc: tdevname = "REGC"; // 3
            TDEV_regd: tdevname = "REGD"; // 4
            TDEV_marlo: tdevname = "MARLO";
            TDEV_marhi: tdevname = "MARHI";
            TDEV_uart: tdevname = "UART";
            TDEV_ram: tdevname = "RAM";

            TDEV_not_used: tdevname = "NOTUSED";
            TDEV_pchitmp: tdevname = "PCHITMP";
            TDEV_pclo: tdevname = "PCLO";
            TDEV_pc: tdevname = "PC";
            default: begin
                string n; 
                $sformat(n,"??(unknown T device %4b)", tdev);
                tdevname = n;
            end
        endcase
    end
    endfunction    

    function string fPhase(phaseFetch, phaseExec); 
    begin
            fPhase = `DECODE_PHASES;
    end
    endfunction

    function string fAddrMode(_addrmode_register, _addrmode_direct); 
    begin
            fAddrMode = `DECODE_ADDRMODES;
    end
    endfunction

endpackage: control

import control::*;


`define CONTROL_WIRES(FN, SEP)  \
    ```FN``_ADEV_SEL(rega)    SEP\
    ```FN``_ADEV_SEL(regb)    SEP\
    ```FN``_ADEV_SEL(regc)    SEP\
    ```FN``_ADEV_SEL(regd)    SEP\
    ```FN``_ADEV_SEL(marhi)    SEP\
    ```FN``_ADEV_SEL(marlo)    SEP\
    ```FN``_ADEV_SEL(uart)    SEP\
    ```FN``_BDEV_SEL(rega)    SEP\
    ```FN``_BDEV_SEL(regb)    SEP\
    ```FN``_BDEV_SEL(regc)    SEP\
    ```FN``_BDEV_SEL(regd)    SEP\
    ```FN``_BDEV_SEL(marhi)    SEP\
    ```FN``_BDEV_SEL(marlo)    SEP\
    ```FN``_BDEV_SEL(immed)    SEP\
    ```FN``_BDEV_SEL(ram)    SEP\
    ```FN``_TDEV_SEL(rega)    SEP\
    ```FN``_TDEV_SEL(regb)    SEP\
    ```FN``_TDEV_SEL(regc)    SEP\
    ```FN``_TDEV_SEL(regd)    SEP\
    ```FN``_TDEV_SEL(marhi)    SEP\
    ```FN``_TDEV_SEL(marlo)    SEP\
    ```FN``_TDEV_SEL(uart)    SEP\
    ```FN``_TDEV_SEL(ram)    SEP\
    ```FN``_TDEV_SEL(not_used)    SEP\
    ```FN``_TDEV_SEL(pc)    SEP\
    ```FN``_TDEV_SEL(pchitmp)    SEP\
    ```FN``_TDEV_SEL(jmpc)    SEP\
    ```FN``_TDEV_SEL(jmpz)    SEP\
    ```FN``_TDEV_SEL(jmpo)    SEP\
    ```FN``_TDEV_SEL(jmpn)    SEP\
    ```FN``_TDEV_SEL(jmpeq)    SEP\
    ```FN``_TDEV_SEL(jmpne)    SEP\
    ```FN``_TDEV_SEL(jmpgt)    SEP\
    ```FN``_TDEV_SEL(jmplt)    SEP\
    ```FN``_TDEV_SEL(jmpdi)    SEP\
    ```FN``_TDEV_SEL(jmpdo)    SEP\
    ```FN``_TDEV_SEL(pclo)    

`define SEMICOLON ;
`define COMMA ,


`define OUT_ADEV_SEL(DNAME) output _adev_``DNAME``
`define OUT_BDEV_SEL(DNAME) output _bdev_``DNAME``
`define OUT_TDEV_SEL(DNAME) output _``DNAME``_in

`endif

// verilator lint_on ASSIGNDLY
// verilator lint_on ASSIGNDLY
// verilator lint_on MULTITOP
