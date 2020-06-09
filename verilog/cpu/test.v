
//#!/usr/bin/iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp 
`include "../control/control.v"
`include "../phaser/phaser.v"
`include "../pc/pc.v"
`include "../lib/assertion.v"
`include "../74245/hct74245.v"
`include "../74573/hct74573.v"
`include "../7474/hct7474.v"
`include "../74139/hct74139.v"
`include "../74377/hct74377.v"
`include "../rom/rom.v"
`include "../ram/ram.v"

// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY

`timescale 1ns/1ns

module test();

    localparam SETTLE_TOLERANCE=50;

    `include "../lib/display_snippet.v"
    
    tri [15:0] address_bus;

    tri [7:0] rbus, lbus, alu_result_bus;
    wire [3:0] rbus_dev, lbus_dev;
    wire [4:0] targ_dev;
    wire [4:0] aluop;

    assign alu_result_bus = rbus; // FIXME - IMPLEMENT ALU

    wire _addrmode_register, _addrmode_pc, _addrmode_immediate;
    wire [2:0] _addrmode = {_addrmode_pc, _addrmode_register, _addrmode_immediate}; 

    wire phaseFetch, phaseDecode, phaseExec, _phaseFetch;
    wire [2:0] phase = {phaseFetch, phaseDecode, phaseExec};

    // CLOCK ===================================================================================
    localparam T=1000;

    logic clk;
    //always begin
    //   #CLOCK_INTERVAL clk = !clk;
    //end

    //wire #8 _clk = ! clk; // GATE + PD
    
    // RESET CIRCUIT ===================================================================================
    // "Do not use an asynchronous reset within your design." - https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html

    logic _RESET_SWITCH;
    wire #9 RESET_SWITCH = ! _RESET_SWITCH;

    wire mrPC, _mrPC;
    wire mrPH, _mrPH;

    // syncs the reset with the clock.
    // mrPH forces phase to 000 so clock to PC will be low.
    // at this point _mrPC will also be low so when the clock releases _mrPH then 
    // there will be a phase transition to 100 which the PC will see as a clock pulse that resets the PC   
    hct7474 #(.BLOCKS(1), .LOG(0)) resetPH(
          ._SD(1'b1),
          ._RD(_RESET_SWITCH),
          .D(1'b1),
          .CP(clk),
          .Q(_mrPH),
          ._Q(mrPH)
        );

    hct7474 #(.BLOCKS(1), .LOG(0)) resetPCFF(
          ._SD(1'b1),
          ._RD(_mrPH), // reset released after clock on resetPH
          .D(1'b1),
          .CP(phaseFetch),
          .Q(_mrPC),
          ._Q(mrPC)
        );


    // CLOCK PHASING ===================================================================================

    wire [9:0] seq;
    `define SEQ(x) (10'd2 ** (x-1))

    // releasing reset allows phaser to go from 000 to 100 whilst _mrPC is low which resets the PC
    phaser #(.LOG(0)) ph(.clk, .mr(mrPH), .seq, ._phaseFetch, .phaseFetch , .phaseDecode , .phaseExec);

    // CONTROL ===========================================================================================
   
    wire [7:0] instruction_hi, instruction_mid, instruction_lo;

    // instruction reg buffer
    hct74573 rom_hi_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_hi.D),
         .Q(instruction_hi) 
    );

    hct74573 rom_mid_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_mid.D),
         .Q(instruction_mid) 
    );

    hct74573 rom_lo_inst_reg(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(1'b0),
         .D(rom_lo.D),
         .Q(instruction_lo) 
    );

    op_decoder #(.LOG(0)) op_decode(.data_hi(instruction_hi), .data_mid(instruction_mid), .data_lo(instruction_lo), .rbus_dev, .lbus_dev, .targ_dev, .aluop);

    address_mode_decoder #(.LOG(1)) addr_decode( 
        .ctrl(instruction_hi[7:5]), 
        .phaseFetch, ._phaseFetch, .phaseDecode, .phaseExec, 
        ._addrmode_pc, ._addrmode_register, ._addrmode_immediate 
    );


    hct74138 lbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(lbus_dev[3]), .A(lbus_dev[2:0]));
    hct74138 lbus_dev_16_demux(.Enable3(lbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(lbus_dev[2:0]));
    
    hct74138 rbus_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(rbus_dev[3]), .A(rbus_dev[2:0]));
    hct74138 rbus_dev_16_demux(.Enable3(rbus_dev[3]), .Enable2_bar(1'b0), .Enable1_bar(1'b0), .A(rbus_dev[2:0]));

    wire [3:0] _targ_sel, un4; 
    hct74139 targ_demux(._Ea(1'b0), ._Eb(1'b0), .Aa(targ_dev[4:3]), .Ab(2'b0), ._Ya(_targ_sel), ._Yb(un4));
    hct74138 targ_dev_08_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_sel[0]), .A(targ_dev[2:0]));
    hct74138 targ_dev_16_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_sel[1]), .A(targ_dev[2:0]));
    hct74138 targ_dev_24_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_sel[2]), .A(targ_dev[2:0]));
    hct74138 targ_dev_32_demux(.Enable3(1'b1), .Enable2_bar(1'b0), .Enable1_bar(_targ_sel[3]), .A(targ_dev[2:0]));

    wire [31:0] tset = {targ_dev_32_demux.Y, targ_dev_24_demux.Y, targ_dev_16_demux.Y, targ_dev_08_demux.Y};
    wire [15:0] lset = {lbus_dev_16_demux.Y, lbus_dev_08_demux.Y};
    wire [15:0] rset = {rbus_dev_16_demux.Y, rbus_dev_08_demux.Y};

    `define WIRE_LBUS(DNAME) wire _ldev_``DNAME`` = lset[control.DEV_``DNAME``];
    `define WIRE_RBUS(DNAME) wire _rdev_``DNAME`` = rset[control.DEV_``DNAME``];
    `define WIRE_TBUS(DNAME) wire _``DNAME``_in = tset[control.TDEV_``DNAME``];

    // control lines for bufs need better names

    `WIRE_TBUS(ram)
    `WIRE_RBUS(ram)

    `WIRE_RBUS(rom)

    `WIRE_LBUS(marlo)
    `WIRE_RBUS(marlo)
    `WIRE_TBUS(marlo)

    `WIRE_LBUS(marhi)
    `WIRE_RBUS(marhi)
    `WIRE_TBUS(marhi)

    `WIRE_LBUS(rega)
    `WIRE_RBUS(rega)
    `WIRE_TBUS(rega)

    `WIRE_LBUS(regb)
    `WIRE_RBUS(regb)
    `WIRE_TBUS(regb)

    `WIRE_LBUS(regc)
    `WIRE_RBUS(regc)
    `WIRE_TBUS(regc)

    `WIRE_LBUS(regd)
    `WIRE_RBUS(regd)
    `WIRE_TBUS(regd)

    `WIRE_LBUS(uart)
    `WIRE_TBUS(uart)

    `WIRE_RBUS(instreg)
   
    `WIRE_TBUS(pchitmp)
    `WIRE_TBUS(pclo)
    `WIRE_TBUS(pc)
    `WIRE_TBUS(jmpo)
    `WIRE_TBUS(jmpz)
    `WIRE_TBUS(jmpc)
    `WIRE_TBUS(jmpdi)
    `WIRE_TBUS(jmpdo)
     
    // PROGRAM COUNTER ======================================================================================

    wire [7:0] PCHI, PCLO; // output of PC
    
    // PC reset is sync with +ve edge of clock
    pc #(.LOG(1))  PC (
        .clk(phaseFetch),
        ._MR(_mrPC),
        ._pc_in(_pc_in),
        ._pclo_in(_pclo_in),
        ._pchitmp_in(_pchitmp_in),
        .D(alu_result_bus),

        .PCLO(PCLO),
        .PCHI(PCHI)
    );

    hct74245ab pchi_addrbushi_buf(.A(PCHI), .B(address_bus[15:8]), .nOE(_addrmode_pc));
    hct74245ab pclo_addrbuslo_buf(.A(PCLO), .B(address_bus[7:0]), .nOE(_addrmode_pc));

    // ROM =============================================================================================

    rom #(.AWIDTH(16)) rom_hi(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_mid(._CS(1'b0), ._OE(1'b0), .A(address_bus));
    rom #(.AWIDTH(16)) rom_lo(._CS(1'b0), ._OE(1'b0), .A(address_bus)); 
    
    // ROM OUT when direct rom addressing is being used
    hct74245ab rom_rom_rbus_buf(.A(rom_lo.D), .B(rbus), .nOE(_rdev_rom));

    // ROM OUT TO RBUS VIA IR PERMITS SIMULTANEOUS REG ADDRESSING OF RAM
    hct74245ab rom_instreg_rbus_buf(.A(instruction_lo), .B(rbus), .nOE(_rdev_instreg));

    // immediate addressing buffer - FIXME USE 74245
    /*
    hct74573 rom_addrbushi_buf(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(_addrmode_immediate),
         .D(instruction_mid),
         .Q(address_bus[15:8])
    );

    hct74573 rom_addrbuslo_buf(
         .LE(phaseFetch), // data latches when fetch ends
         ._OE(_addrmode_immediate), // outputs turn on when 
         .D(instruction_lo),
         .Q(address_bus[7:0])
    );
    */

    hct74245ab rom_addrbuslo_buf(.A(instruction_lo), .B(address_bus[7:0]), .nOE(_addrmode_immediate)); // optional - needed for immed addressing
    hct74245ab rom_addrbushi_buf(.A(instruction_mid), .B(address_bus[15:8]), .nOE(_addrmode_immediate)); // optional - needed for immed addressing

    // MAR =============================================================================================
    hct74377 #(.LOG(1)) MARLO(._EN(_marlo_in), .CP(phaseExec), .D(alu_result_bus));    
    hct74377 #(.LOG(1)) MARHI(._EN(_marhi_in), .CP(phaseExec), .D(alu_result_bus));

    hct74245ab marlo_lbus_buf(.A(MARLO.Q), .B(lbus), .nOE(_ldev_marlo)); // optional - needed for marlo arith
    hct74245ab marlo_rbus_buf(.A(MARLO.Q), .B(rbus), .nOE(_rdev_marlo)); // optional - needed for marlo arith

    hct74245ab marhi_lbus_buf(.A(MARHI.Q), .B(lbus), .nOE(_ldev_marhi)); // optional - needed for marlo arith
    hct74245ab marhi_rbus_buf(.A(MARHI.Q), .B(rbus), .nOE(_rdev_marhi)); // optional - needed for marlo arith

    hct74245ab marhi_addrbushi_buf(.A(MARHI.Q), .B(address_bus[15:8]), .nOE(_addrmode_register));
    hct74245ab marlo_addrbuslo_buf(.A(MARLO.Q), .B(address_bus[7:0]), .nOE(_addrmode_register));



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS ===========================================================================================
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    initial begin
        `ifndef verilator

        $dumpfile("dumpfile.vcd");
        $dumpvars(0, test);

        // $monitor ("%9t ", $time,  "TEST     ",
        //         //"rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
        //         //" seq=%-2d", nSeq,
        //         // " amode=%-3s", sAddrMode,
        //         // " addrbus=0x%4x", address_bus,
        //         " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
        //         " rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop,
        //         " PC=%02h:%02h", PCHI, PCLO,
        //         //"      %-s", label
        //         );

        `endif
    end

    integer count;
    integer phaseFetchLen=1;
    integer phaseDecodeLen=1;
    integer phaseExecLen=1;

    always @(PCHI or PCLO) begin
      $display("%9t ", $time, "INCREMENTED PC=%-d ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^", {PCHI, PCLO});
    end

    task DUMP;
            $display ("%9t ", $time,  "DUMP  ",
                 ": %-s", label
                 );
            $display ("%9t ", $time,  "DUMP  ",
                 " seq=%-2d", $clog2(seq)+1);
            $display ("%9t ", $time,  "DUMP  ",
                 " instruction=%08b:%08b:%08b", instruction_hi, instruction_mid, instruction_lo);
            $display ("%9t ", $time,  "DUMP  ",
                " op=%d(%-s)", rom_hi.D[7:5], control.opName(rom_hi.D[7:5]),
                 " FDE=%1b%1b%1b(%-s)", phaseFetch, phaseDecode, phaseExec, control.fPhase(phaseFetch, phaseDecode, phaseExec));
            $display ("%9t ", $time,  "DUMP  ",
                 " amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate),
                 " addrbus=0x%4x", address_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus);
            $display ("%9t ", $time,  "DUMP  ",
                 " rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D);
            $display ("%9t ", $time,  "DUMP  ",
                " tdev=%5b(%s)", targ_dev, control.tdevname(targ_dev),
                " ldev=%4b(%s)", lbus_dev, control.devname(lbus_dev),
                " rdev=%4b(%s)", rbus_dev,control.devname(rbus_dev),
                " aluop=%5b(%s)", aluop, alu_func.aluopName(aluop)
            );            
            $display ("%9t ", $time,  "DUMP  ",
                 " PC=%02h:%02h", PCHI, PCLO);
            
    endtask 

    task CLK_UP; 
    begin
        $display("\n%9t", $time, " CLK  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"); 
        clk = 1;
    end
    endtask

    task CLK_DN; 
    begin
        $display("\n%9t", $time, " END CLOCK STATE"); 
        // op_decode.DUMP();
        // addr_decode.DUMP();
        DUMP;
        $display("\n%9t", $time, " CLK  -----------------------------------------------------------------------\n"); 
        clk = 0;
    end
    endtask

    if (0) always @* begin
        $display ("%9t ", $time,  "MON     ",
                 "rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                 " seq=%-2d", $clog2(seq)+1,
                 " amode=%-3s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate),
                 " addrbus=0x%4x", address_bus,
                 " FDE=%-s  %1b%1b%1b", control.fPhase(phaseFetch, phaseDecode, phaseExec), phaseFetch, phaseDecode, phaseExec,
                 " rbus=%8b lbus=%8b alu_result_bus=%8b", rbus, lbus, alu_result_bus,
                 " rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop,
                 " PC=%02h:%02h", PCHI, PCLO,
                 "     : %-s", label
                 );
    end

    always @* 
        if (_RESET_SWITCH)  
            $display("\n%9t RESET SWITCH RELEASE   _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 
        else      
            $display("\n%9t RESET SWITCH SET       _RESET_SWITCH=%1b  ======================================================================\n", $time, _RESET_SWITCH); 

    always @* 
        if (_mrPC)  
            $display("\n%9t PC RESET RELEASE   _mrPC=%1b  ======================================================================\n", $time, _mrPC); 
        else      
            $display("\n%9t PC RESET SET       _mrPC=%1b  ======================================================================\n", $time, _mrPC); 


    
    if (0) always @(*) begin
        $display("%9t", $time, " PHASE: FDE=%-s  %1b%1b%1b seq=%10b", control.fPhase(phaseFetch, phaseDecode, phaseExec), 
                                                        phaseFetch, phaseDecode, phaseExec, seq); 
    end

    if (0) always @(*) begin
        $display("%9t", $time, " control._AMODE: PRI=%-s  %1b%1b%1b seq=%10b", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate), 
                                                        _addrmode_pc, _addrmode_register, _addrmode_immediate, seq); 
    end

    integer instCount = 0;
    always @(posedge phaseFetch) begin
        instCount ++;
        $display("%9t", $time, " PHASE: FETCH  INTRUCTION#=%-d", instCount); 
    end

    always @(posedge phaseDecode) begin
        $display("%9t", $time, " PHASE: DECODE"); 
    end

    always @(posedge phaseExec) begin
        $display("%9t", $time, " PHASE: EXECUTE"); 
    end

    if (1) always @* 
        $display ("%9t ", $time,  "ADDRESSING      amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate), " addrbus=0x%4x", address_bus);

    if (0) always @* 
        $display ("%9t ", $time,  "ROM      rom=%08b:%08b:%08b", rom_hi.D, rom_mid.D, rom_lo.D, 
                " amode=%s", control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate),
                " addrbus=0x%4x", address_bus);
        
    if (0) always @* 
        $display("%9t ... seq=%-2d  %8b................", $time, $clog2(seq)+1, seq); 
        
    if (0) always @* 
        $display("%9t ", $time, "ROMBUFFS rom_addrbuslo_buf=0x%-2x", rom_addrbuslo_buf.data, 
            " rom_addrbus_hi_buf=0x%-2x", rom_addrbushi_buf.data,
            " rom_inst_reg=%8b", rom_inst_reg.data,
            " _oe=%1b(_addrmode_immediate)", _addrmode_immediate
            ); 

                
    if (0) always @* 
        $display("%9t ", $time, "DEVICE-SEL ", 
                    "rdev=%04b ldev=%04b targ=%05b aluop=%05b ", rbus_dev, lbus_dev, targ_dev, aluop
        ); 

    if (1) always @* 
        $display("%9t ", $time, "MAR  %02x:%02x    _marhi_in=%b _marlo_in=%b", MARHI.Q, MARLO.Q, _marhi_in, _marlo_in);

    if (1) always @* 
        $display("%9t ", $time, "tset=%032b  lset=%016b rset=%016b", tset, lset, rset);

    if (0) always @* 
        $display("%9t ", $time, "ALU BUS ",
            " rbus=0x%-2x", rbus, 
            " lbus=0x%-2x", lbus,
            " alu_result_bus=%-2x", alu_result_bus
            ); 
        
        
    // constraints
    always @(*) begin
        if (phaseDecode & instruction_hi === 'x) begin
            $display("rom_hi.D", rom_hi.D); 
            $display("instruction_hi", instruction_hi); 
            DUMP;
            $display("END OF PROGRAM - CONTROL BYTE = XX "); 
            $finish();
        end
    end

    // constraints
    always @* begin
        // permits a situation where the control lines conflict.
        // this is ok as long as they settle quickly and are settled before exec phase.
        if (_RESET_SWITCH & phaseDecode) begin
            if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_immediate === 1'bx) begin
                $display("\n\n%9t ", $time, " ERROR ILLEGAL INDETERMINATE ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b", _addrmode_pc , _addrmode_register , _addrmode_immediate );
                #SETTLE_TOLERANCE
            // only one may be low at a time
                if (_addrmode_pc === 1'bx |  _addrmode_register === 1'bx |  _addrmode_immediate === 1'bx) begin
                    DUMP;
                    $display("\n\n%9t ", $time, " ABORT");
                    $finish();
                end
            end
            if (_addrmode_pc + _addrmode_register + _addrmode_immediate < 2) begin
                $display("\n\n%9t ", $time, " ERROR CONFLICTING ADDR MODE _PC=%1b/_REG=%1b/_IMM=%1b sAddrMode=%-s", _addrmode_pc , _addrmode_register , _addrmode_immediate,
                                            control.fAddrMode(_addrmode_pc, _addrmode_register, _addrmode_immediate));
                #SETTLE_TOLERANCE
                if (_addrmode_pc + _addrmode_register + _addrmode_immediate < 2) begin
                    DUMP;
                    $display("\n\n%9t ", $time, " ABORT");
                    $finish();
                end
            end
        end
    end

    task INIT_ROM;
    begin
        `define ROM(A) {rom_hi.Mem[A], rom_mid.Mem[A], rom_lo.Mem[A]}

        // CODE
        // dev_eq_rom_immed tdev=00010(MARLO), address=ffaa     
        `ROM(0)= { 8'b100_00010, 8'hff, 8'haa };                // MARLO=whats at address FFaa

        // dev_eq_const8 tdev=00011(MARHI), const8=0           
        `ROM(1)= { 8'b001_00011, 8'hx, 8'h0 };                  // MARHI=const 0

        // dev_eq_xy_alu tdev=00010(MARLO) ldev=0010(MARLO) rdev=0010(MARLO) alu=00010(0)
        `ROM(2)= { 8'b000_00010, 16'bzzz_0010_0010_00010 };     // MARLO=0 (ALUOP=0)


        // DATA 
        // initialise rom[ffaa] = 0x42
        `ROM(16'hffaa) = { 8'b0, 8'b0, 8'h42 }; 
    end
    endtask : INIT_ROM

    // tests
    initial begin
        localparam TCLK=1000;   // clock cycle
        INIT_ROM();

        `DISPLAY("init : _RESET_SWITCH=0")
        _RESET_SWITCH <= 0;
        CLK_DN;

        #TCLK
        `Equals( phase, control.PHASE_NONE)

        `Equals( seq, `SEQ(1))
        `Equals( _addrmode, 3'b111)

        `Equals(PCHI, 8'bx)
        `Equals(PCLO, 8'bx)

        `Equals(address_bus, 16'bz); // noone providing address

        #TCLK
        `DISPLAY("_mrPC=0  - so clocking is ineffective = stay in PC addressing mode")
        `Equals( _mrPC, 0);

        for (count =0; count < 3; count++) begin
            count++; 
            #TCLK
            CLK_UP; //CLK_UP;
            #TCLK
            CLK_DN;
        end
        #TCLK
        `Equals(PCHI, 8'bx)
        `Equals(PCLO, 8'bx)
        

        `DISPLAY("_RESET_SWITCH released : still in PC addressing mode after settle and PC=0")
        _RESET_SWITCH <= 1;
        `Equals( _mrPC, 0);
        `Equals( phase, control.PHASE_NONE)

        #TCLK

        `DISPLAY("clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals( _mrPC, 1'b1); // +clock due to phaseFetch on SR plus the release of the reset on the SR
            `Equals(PCHI, 8'b0) 
            `Equals(PCLO, 8'b0)
            `Equals(address_bus, 16'h0000);
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            CLK_DN;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_IMM);
            `Equals(address_bus, 16'hffaa); // FROM ROM[15:0] 
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b0)
            `Equals( _addrmode, control._AMODE_IMM);
            `Equals(address_bus, 16'hffaa); // FROM ROM[15:0] 
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseExecLen));
        end

        // operation result 
        `Equals(MARLO.Q, 8'h42)
        `Equals(MARHI.Q, 8'hxx)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(address_bus, 16'h0001); // FROM PC
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'b1)
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end

        // operation result 
        `Equals(MARLO.Q, 8'h42)
        `Equals(MARHI.Q, 8'h00)

        `DISPLAY("NEXT CYCLE STARTS")
        `DISPLAY("clock fetch")
        for (count =0; count < phaseFetchLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_FETCH)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_PC);
            `Equals(address_bus, 16'd2); // FROM PC
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1));
        end

        `DISPLAY("clock decode")
        for (count =0; count < phaseDecodeLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_DECODE)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            `Equals(address_bus, 16'h0042); // FROM MAR
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen));
        end

        `DISPLAY("clock exec")
        for (count =0; count < phaseExecLen; count++) begin
            CLK_UP;
            #TCLK
            `Equals( phase, control.PHASE_EXEC)
            `Equals(PCHI, 8'b0)
            `Equals(PCLO, 8'd2)
            `Equals( _addrmode, control._AMODE_REG);
            `Equals(address_bus, 16'h0042); // FROM MAR
            CLK_DN;
            #TCLK
            `Equals( seq, `SEQ(count+1+phaseFetchLen+phaseDecodeLen));
        end


//`include "./generated_tests.v"
/*
        #TCLK
        count=100;
        while (count -- > 0) begin
            #TCLK
            CLK_UP;
            #TCLK
            CLK_DN;
            $display("PC %2x:%2x !!!!!!!!!!!!!!!!!!!!!!!! CLK COUNT REMAINING=%-d", PCHI, PCLO, count);
        end
*/
        $display("END OF TEST");
        $finish();

    end

endmodule : test
