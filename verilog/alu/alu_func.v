
`ifndef V_ALU_FUNC
`define V_ALU_FUNC

`timescale 1ns/1ns

module alu_func;

    // ALUOP
    localparam [4:0] ALUOP_PASSL = 0;
    localparam [4:0] ALUOP_PASSR = 1;

    function string aluopName;
        input [4:0] opcode;
        
        string ret;
        begin
            case(opcode)
                 0 : aluopName =    "L";
                 1 : aluopName =    "R";
                 2 : aluopName =    "0";
                 3 : aluopName =    "-L";
                 4 : aluopName =    "-R";
                 5 : aluopName =    "L+1";
                 6 : aluopName =    "R+1";
                 7 : aluopName =    "L-1";

                 8 : aluopName =    "R-1";
                 9 : aluopName =    "+"; 
                10 : aluopName =    "-"; 
                11 : aluopName =    "R-L"; 
                12 : aluopName =    "L-R spec";
                //13 : aluopName =    "+ cin=1";  not used directly
                //14 : aluopName =    "- cin=1";  not used directly
                //15 : aluopName =    "R-L cin=1"; not used directly
                13 : aluopName =    "??notused(+ cin=1)"; 
                14 : aluopName =    "??notused(- cin=1)"; 
                15 : aluopName =    "??notused(R-L cin=1)"; 

                16 : aluopName =    "*HI";
                17 : aluopName =    "*LO";
                18 : aluopName =    "/";
                19 : aluopName =    "%";
                20 : aluopName =    "<<";
                21 : aluopName =    ">>A";
                22 : aluopName =    ">>L" ;
                23 : aluopName =    "ROL";

                24 : aluopName =    "ROR";
                25 : aluopName =    "AND";
                26 : aluopName =    "OR";
                27 : aluopName =    "XOR";
                28 : aluopName =    "NOT A";
                29 : aluopName =    "NOT B";
                30 : aluopName =    "+BCD";
                31 : aluopName =    "-BCD";
                default: begin
                    $sformat(ret,"??unknown(%b)",opcode);
                    aluopName = ret;
                end
            endcase

        end
    endfunction
endmodule

`endif