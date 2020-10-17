
    task disassambler;
        input [7:0] hi;
        input [7:0] lo;
        string opname;
        string aluname;
        string xname;
        string xname_non_reg;
        string xname_reg;
        string yname_reg;
    begin
        devname({hi[0], hi[4:1]}, xname);
        devname({1'b0, hi[4:1]}, xname_non_reg);
        devname({1'b1, hi[4:1]}, xname_reg);
        devname({1'b1, lo[3:0]}, yname_reg);
        aluopname({hi[0], lo[7:4]}, aluname);

        $write("\n%9t  %5d >> %02x:%02x    ", $time, cpcount, PCHI, PCLO);

        case (hi[7:5]) 
            0:  begin
                    opname = "DEV_eq_ROM";
                    $write("%s=%dd", xname, lo);
                end
            1:  begin
                    opname = "DEV_eq_RAM";
                    $write("%s=RAM[]", xname);
                end
            2:  begin
                    opname = "DEV_eq_RAMZP";
                    $write("%s=RAM[%d]", xname, lo);
                end
            3:  begin
                    opname = "DEV_eq_UART";
                    $write("%s=UART", xname);
                end
            4:  begin
                    opname = "NONREG_eq_OPREGY";
                    $write("%s=0(%s)%s", xname_non_reg, aluname, yname_reg);
                end
            5:  begin
                    opname = "REGX_eq_ALU";
                    $write("%s=%s(%s)%s", xname_reg, xname_reg, aluname, yname_reg);
                end
            6:  begin
                    opname = "RAMZP_eq_REG";
                    $write("RAM[%dd]=%-s", lo, xname_reg);
                end
            7:  begin
                    opname = "RAMZP_eq_UART";
                    $write("RAM[%dd]=UART", lo);
                end
            default:  begin
                    opname = "UNKNOWN";
                    $write("%s", opname);
                end
        endcase
        $display("\t\t# %20s %08b,%08b\n", opname, hi, lo);
    end
    endtask


