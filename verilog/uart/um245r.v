// UART Verilog for https://www.ftdichip.com/Support/Documents/DataSheets/Modules/DS_UM245R.pdf
/* verilator lint_off ASSIGNDLY */

`timescale 1ns/1ns
`define EOF 32'hFFFF_FFFF 
`define NULL 0 
`define EOF 32'hFFFF_FFFF

module um245r #(parameter T3=50, T4=1, T5=25, T6=80, T11=25, T12=80,
        INPUT_FILE="", OUTPUT_FILE="", INPUT_FILE_DEPTH=0, HEXMODE=0, LOG=0)  (
            inout [7:0] D,    // Input data
    input WR,        // Writes data on -ve edge
    input _RD,        // When goes from high to low then the FIFO data is placed onto D (equates to _OE)
 
    output _TXE,        // When high do NOT write data using WR, when low write data by strobing WR
    output _RXF        // When high to NOT read from D, when low then data is available to read by strobing RD low
);

localparam MAX_LINE_LENGTH=80;

integer fOut=`NULL, fControl, c, r, txLength, tDelta;

reg [8*MAX_LINE_LENGTH:0] line; /* Line of text read from file */ 
reg TX_READY;
reg RX_READY;

integer verbose=0;
string str = "";

localparam BUFFER_SIZE=80;

//reg [8*BUFFER_SIZE:0] rxBuf; // Line of text read from file 
int rxBuf[BUFFER_SIZE]; // Line of text read from file 
int absWritePos = 0; // next place to write
int absReadPos = 0; // next place to read


wire dataAvailable = absReadPos < absWritePos;
wire writeAvailable = (absWritePos - absReadPos) < BUFFER_SIZE;

reg [7:0] Drx = 'x;

always @* begin
    if (verbose)
    $display("%t UART:", $time, 
        " D=%8b", D, ", WR=%1b", WR, ",_RD=%1b", _RD, 
        " _RXF=%1b", _RXF, 
        " _TXE=%1b", _TXE, 
        " Drx=%8b", Drx,
        " ARPOS=%-3d", absReadPos,
        " AWPOS=%-3d", absWritePos,
        " RPOS=%-3d", absReadPos % BUFFER_SIZE,
        " WPOS=%-3d", absWritePos % BUFFER_SIZE,
        " DAVAIL=%1b", dataAvailable,
        " TX_READY=%1b", TX_READY, 
        " RX_READY=%1b", RX_READY
        );

end

integer tx_count=0;
assign _TXE = !(fOut != `NULL && TX_READY && tx_count > 0);
assign _RXF = !(dataAvailable && RX_READY);

assign #T3 D= _RD? 8'bzzzzzzzz: dataAvailable ? Drx : 8'bxzxzxzxz;

/*
    Transmit only valid when _TXE is low.
    Transmit occurs when WR goes low.
*/
always @(negedge WR) begin
    if (_TXE) begin
            $display("%t ", $time, "UART: TRANSMITTING %8b", D);
            $display("%t ", $time, "UART: WR low while _TXE not ready");
            $finish_and_return(1);
    end

    $write("%t ", $time, "UART: TRANSMITTING 0x%02x (%c)\n", D, D);
    $fwrite(fOut, "%02x\n", D);

    #T11 // -WR to _TXE inactive delay
    $display("TX NOT READY");
    TX_READY=0; 

    tx_count --;
    if (tx_count < 0) begin
            $display("%t ", $time, "UART: tx_count went negative");
            $finish_and_return(1);
    end

    #T12 // min inactity period
    if (verbose) $display("TX INACTIVE PERIOD ENDS");
    TX_READY=1;

end

/*
    Transmit only valid when _TXE is low.
    Transmit occurs when WR goes low.
*/
always @(negedge _RD) begin
    if (_RXF) begin
            $display("%t ", $time, "UART: _RD low while _RXF not ready");
            $finish_and_return(1);
    end

    if (! dataAvailable) begin
            $display("%t ", $time, "UART: _RD low while data not available");
            $finish_and_return(1);
    end

    //#T3 
    //$display("0 = %d", rxBuf[0]);
    //$display("1 = %d", rxBuf[1]);
    //$display("2 = %d", rxBuf[2]);

    if (verbose) $display("%t ", $time, "UART: READING AT %-d", absReadPos);
    Drx = rxBuf[absReadPos%BUFFER_SIZE];
    $display("%t ", $time, "UART: Received %02x (%c) from buf at %-d", Drx, Drx, absReadPos);
    absReadPos++;
end

always @(posedge _RD) begin

    #T11 // -WR to _TXE inactive delay
    if (verbose) $display("%t ", $time, "UART: RX NOT READY");
    RX_READY=0; 

    #T12 // min inactity period
    if (verbose) $display("%t ", $time, "UART: RX INACTIVE PERIOD ENDS");
    RX_READY=1;
end



initial 
    begin : file_block 
    $timeformat(-9, 0, "ns", 6); 

    for(int i=0; i<BUFFER_SIZE; i++) begin
        rxBuf[i] = i;
    end

    RX_READY=0;
    TX_READY=0;
    #20

    // FIXME
    //absWritePos = 15; // next place to write into receive buffer

    TX_READY=1;
    #20

    if (1) begin
        $display("[%6t] ", $time, "opening fifo.control");
        fControl = $fopenr("/tmp/fifo.control"); 
        if (fControl == `NULL) // If error opening file 
                disable file_block; // Just quit 

        $display("[%6t] ", $time, "opening fifo.out");
        fOut = $fopenw("/tmp/fifo.out"); 
        if (fOut == `NULL) // If error opening file 
                disable file_block; // Just quit 

        $display("[%6t] ", $time, "fifos open");

        while (fControl != `NULL)  
        begin
            c = $fgetc(fControl); 
            while (c != `EOF) 
            begin 
                    /* Check the first character for comment */ 
                    if (c == "/") // just skip
                    begin 
                        line="";
                        r = $fscanf(fControl,"%s\n", line); 
                        str = line; // string type removes trailing empty space
                        $display("[%6t] ", $time, "/%s", str);
                    end

                    if (c == "r") // pass string back to simulatiom
                    begin
                        line="";
                        r = $fscanf(fControl,"%s\n", line); 
                        str = line; // string type removes trailing empty space

                        $display("[%6t] ", $time, "RX: '%s'", str);

                        for (int p=0; p<str.len() && writeAvailable; p++) begin
                            rxBuf[absWritePos%BUFFER_SIZE] = str[p];
                            absWritePos++;
                        end
                        if (! writeAvailable)
                            $display("%t ", $time, "UART: RECEIVE BUFFER FULL");

                        if (verbose) $display("%t ", $time, "UART: RECEIVE %3d / %3d", absWritePos, absWritePos%BUFFER_SIZE);
                    end
                    
                    if (c == "t") // wait for simulation to transmit N chars
                    begin
                        txLength=0;
                        r = $fscanf(fControl,"%d\n", txLength); 
                        if (verbose) $display("[%6t] ", $time, "TX: waiting for %1d chars", txLength);
                        tx_count = txLength;
                    end
                    
                    if (c == "#") // skil N ns
                    begin
                        tDelta=0;
                        r = $fscanf(fControl,"%d\n", tDelta); 
                        $display("[%6t] ", $time, "#%1d begin", tDelta);
                        #tDelta $display("[%6t] ", $time, "#%1d end", tDelta);
                    end

                    if (c == "q") // quit
                    begin
                        disable file_block; // Just quit 
                    end

                    c = $fgetc(fControl); // -1 == eof
            end // while not EOF 
        
            $fclose(fControl); 
                        
            fControl = $fopenr("/tmp/fifo.control"); 
            if (fControl == `NULL) // If error opening file 
                    disable file_block; // Just quit 

        end // restart read
    end
end // initial

endmodule




