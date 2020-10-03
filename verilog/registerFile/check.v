    
    task check;
        input [7:0] A,B,C,D;
    begin
        _rdL_en  = enabled;
        _rdR_en  = enabled;

        rdL_addr = 0;
        rdR_addr = 0;
        #cycle;
        `Equals(rdL_data, A);
        `Equals(rdR_data, A);
        
        rdL_addr = 1;
        rdR_addr = 1;
        #cycle;
        `Equals(rdL_data, B);
        `Equals(rdR_data, B);
        
        rdL_addr = 2;
        rdR_addr = 2;
        #cycle;
        `Equals(rdL_data, C);
        `Equals(rdR_data, C);
        
        rdL_addr = 3;
        rdR_addr = 3;
        #cycle;
        `Equals(rdL_data, D);
        `Equals(rdR_data, D);
        
    end
    endtask

