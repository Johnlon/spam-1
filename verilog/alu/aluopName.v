    task aluopname;
        input [4:0] opcode;

        output string name;
        begin
            case(opcode)
                 0 : name =    "L";
                 1 : name =    "R";
                 2 : name =    "0";
                 3 : name =    "-L";
                 4 : name =    "-R";
                 5 : name =    "L+1";
                 6 : name =    "R+1";
                 7 : name =    "L-1";

                 8 : name =    "R-1";
                 9 : name =    "+"; 
                10 : name =    "-"; 
                11 : name =    "R-L"; 
                12 : name =    "L-R spec";
                //13 : name =    "+ cin=1";  not used directly
                //14 : name =    "- cin=1";  not used directly
                //15 : name =    "R-L cin=1"; not used directly

                16 : name =    "*HI";
                17 : name =    "*LO";
                18 : name =    "/";
                19 : name =    "%";
                20 : name =    "<<";
                21 : name =    ">>A";
                22 : name =    ">>L" ;
                23 : name =    "ROL";

                24 : name =    "ROR";
                25 : name =    "AND";
                26 : name =    "OR";
                27 : name =    "XOR";
                28 : name =    "NOT A";
                29 : name =    "NOT B";
                30 : name =    "+BCD";
                31 : name =    "-BCD";
                default: begin
                    name = "??????";
                end
            endcase
        end
    endtask
