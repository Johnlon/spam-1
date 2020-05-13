
    task devname;
        input [4:0] dev;

        output string name;
        begin
            case(dev)
                0: name = "RAM";
                1: name = "MARLO";
                2: name = "MARHI";
                3: name = "UART";
                4: name = "PCHITMP";
                5: name = "PCLO";
                6: name = "PC";
                7: name = "ROM";
                8: name = "A";
                9: name = "B";
               10: name = "C";
               11: name = "D";
               12: name = "E";
               13: name = "F";
               14: name = "G";
               15: name = "H"; // or flags?
                // JUMPS 
               16: name = "JMPO";
               17: name = "JMPZ";
               18: name = "JMPC";
               19: name = "JMPDI";
               20: name = "JMPDO";
               21: name = "JMPEQ";
               22: name = "JMPNE";
               23: name = "JMPGT";
               24: name = "JMPLT";
               25: name = "-na-";
               26: name = "-na-";
               27: name = "-na-";
               28: name = "-na-";
               29: name = "-na-";
               30: name = "-na-";
               31: name = "-na-";
            endcase
        end
    endtask

