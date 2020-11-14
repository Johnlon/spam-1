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
`define COND(COND) control::CONDITION_``COND``

`define AMODE_TUPLE    wire [1:0] _addrmode = {CPU._addrmode_register, CPU._addrmode_direct}; 

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
    localparam [2:0] ADEV_rega = 0; 
    localparam [2:0] ADEV_regb = 1; 
    localparam [2:0] ADEV_regc = 2; 
    localparam [2:0] ADEV_regd = 3; 
    localparam [2:0] ADEV_marlo = 4;
    localparam [2:0] ADEV_marhi = 5;
    localparam [2:0] ADEV_uart = 6;
    localparam [2:0] ADEV_not_used = 7;

    // B BUS
    localparam [2:0] BDEV_rega = 0; 
    localparam [2:0] BDEV_regb = 1; 
    localparam [2:0] BDEV_regc = 2; 
    localparam [2:0] BDEV_regd = 3; 
    localparam [2:0] BDEV_marlo = 4;
    localparam [2:0] BDEV_marhi = 5;
    localparam [2:0] BDEV_immed = 6; // READ FROM THE INSTRUCTION
    localparam [2:0] BDEV_ram = 7;
    localparam [2:0] BDEV_not_used = BDEV_immed; // noop alias

    // DEST
    localparam [3:0] TDEV_rega = 0; 
    localparam [3:0] TDEV_regb = 1; 
    localparam [3:0] TDEV_regc = 2; 
    localparam [3:0] TDEV_regd = 3; 
    localparam [3:0] TDEV_marlo = 4;
    localparam [3:0] TDEV_marhi = 5;
    localparam [3:0] TDEV_uart = 6;
    localparam [3:0] TDEV_ram = 7;
//    localparam [3:0] TDEV_not_used = 12;
    localparam [3:0] TDEV_pchitmp = 13; // only load pchitmp
    localparam [3:0] TDEV_pclo= 14; // only load pclo
    localparam [3:0] TDEV_pc= 15;   // load pclo from instruction and load pchi from pchitmp


    localparam [3:0] CONDITION_A= 0; 
    localparam [3:0] CONDITION_C= 1; 
    localparam [3:0] CONDITION_Z= 2; 
    localparam [3:0] CONDITION_O= 3; 
    localparam [3:0] CONDITION_N= 4; 
    localparam [3:0] CONDITION_EQ=5; 
    localparam [3:0] CONDITION_NE=6; 
    localparam [3:0] CONDITION_GT=7; 
    localparam [3:0] CONDITION_LT=8; 
    localparam [3:0] CONDITION_DI=9; 
    localparam [3:0] CONDITION_DO=10; 

    // ALL

    function string adevname([2:0] dev); 
    begin
        case (dev)
            ADEV_rega: adevname = "REGA";
            ADEV_regb: adevname = "REGB";
            ADEV_regc: adevname = "REGC";
            ADEV_regd: adevname = "REGD";
            ADEV_marlo: adevname = "MARLO";
            ADEV_marhi: adevname = "MARHI";
            ADEV_uart: adevname = "UART";
            ADEV_not_used: adevname = "NOT_USED";
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
            BDEV_rega: bdevname = "REGA"; 
            BDEV_regb: bdevname = "REGB";
            BDEV_regc: bdevname = "REGC";
            BDEV_regd: bdevname = "REGD";
            BDEV_marlo: bdevname = "MARLO";
            BDEV_marhi: bdevname = "MARHI";
            BDEV_immed: bdevname = "IMMED/NU";
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

            //TDEV_not_used: tdevname = "NOTUSED";
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

    function string condname([3:0] cond); 
    begin
        case (cond)
            CONDITION_A: condname = "A";
            CONDITION_C: condname = "C";
            CONDITION_Z: condname = "Z";
            CONDITION_O: condname = "O";
            CONDITION_N: condname = "N";
            CONDITION_EQ: condname = "EQ";
            CONDITION_NE: condname = "NE";
            CONDITION_GT: condname = "GT";
            CONDITION_LT: condname = "LT";
            CONDITION_DI: condname = "DI";
            CONDITION_DO: condname = "DO";
            default: begin
                string n; 
                $sformat(n,"??(unknown condition %4b)", cond);
                condname = n;
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
    ```FN``_ADEV_SEL(not_used)    SEP\
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
    ```FN``_TDEV_SEL(pc)    SEP\
    ```FN``_TDEV_SEL(pchitmp)    SEP\
    ```FN``_TDEV_SEL(pclo)    

    //```FN``_TDEV_SEL(not_used)    SEP\

`define SEMICOLON ;
`define COMMA ,


`endif
