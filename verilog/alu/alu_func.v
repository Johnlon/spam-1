
`ifndef V_ALU_FUNC
`define V_ALU_FUNC

`timescale 1ns/1ns

module alu_func;

    // ALUOP
    localparam [4:0] ALUOP_PASSL = 0;
    localparam [4:0] ALUOP_PASSR = 1;
    localparam [4:0] ALUOP_ZERO = 2;

    function string aluopName;
        input [4:0] opcode;
        
        string ret;
        begin
            case(opcode)
                 0 : aluopName =    "A";
                 1 : aluopName =    "B";
                 2 : aluopName =    "0";
                 3 : aluopName =    "-A";
                 4 : aluopName =    "-B";
                 5 : aluopName =    "A+1";
                 6 : aluopName =    "B+1";
                 7 : aluopName =    "A-1";

                 8 : aluopName =    "B-1";
                 9 : aluopName =    "A+B"; 
                10 : aluopName =    "A-B"; 
                11 : aluopName =    "B-A"; 
                12 : aluopName =    "A-B spec";
                //13 : aluopName =    "+ cin=1";  not used directly
                //14 : aluopName =    "- cin=1";  not used directly
                //15 : aluopName =    "B-A cin=1"; not used directly
                13 : aluopName =    "??notused(+ cin=1)"; 
                14 : aluopName =    "??notused(- cin=1)"; 
                15 : aluopName =    "??notused(B-A cin=1)"; 

                16 : aluopName =    "A*B HI";
                17 : aluopName =    "A*B LO";
                18 : aluopName =    "A/B";
                19 : aluopName =    "A%B";
                20 : aluopName =    "A<<B";
                21 : aluopName =    "A>>B arith";
                22 : aluopName =    "A>>B log" ;
                23 : aluopName =    "A ROL B";

                24 : aluopName =    "A ROR B";
                25 : aluopName =    "A AND B";
                26 : aluopName =    "A OR B";
                27 : aluopName =    "A XOR B"; 
                28 : aluopName =    "NOT A";
                29 : aluopName =    "NOT B";
                30 : aluopName =    "A+B BCD";
                31 : aluopName =    "A-B BCD";
                default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    aluopName = ret;
                end
            endcase

        end
    endfunction
endmodule

`endif