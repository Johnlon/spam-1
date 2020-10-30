// verilator lint_off ASSIGNDLY
// verilator lint_off STMTDLY


`ifndef V_74245
`define V_74245

`timescale 1ns/1ns

module hct74245( 
    input dir,
    input nOE,
    inout tri [7:0] A,
    inout tri [7:0] B
);
    parameter NAME="74245";
    parameter LOG=0;
    // HCT typical @ 5v according to https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf
    parameter PD_TRANS=10;
    parameter PD_DIR=16;
    parameter PD_OE=16;

    specify
        (A => B) = (PD_TRANS);
        (B => A) = (PD_TRANS);
        (dir *> A) = (PD_DIR);
        (dir *> B) = (PD_DIR);
        (nOE *> A) = (PD_OE);
        (nOE *> B) = (PD_OE);
    endspecify

    if (LOG) 
        always @(*) 
        begin
            $display("%9t", $time,  " %m BUF %s: A=%8b ", NAME, A, "B=%8b ", B, "dir=%1b", dir, " nOE=%1b", nOE);
        end

    
    wire #(PD_DIR)  dir_d = dir;
    wire #(PD_OE)   nOE_d = nOE;
    wire [7:0] #(PD_TRANS) A_d = A;
    wire [7:0] #(PD_TRANS) B_d = B;

    assign A= nOE_d ? 8'bzzzzzzzz :dir_d?8'bzzzzzzzz:B_d;
    assign B= nOE_d ? 8'bzzzzzzzz :dir_d?A_d:8'bzzzzzzzz;

endmodule: hct74245


`timescale 1ns/1ns

module hct74245ab( 
    input nOE,
    input [7:0] A,
    inout tri [7:0] B
);

    parameter NAME="74245ab";
    parameter LOG=0;

    wire [7:0] Ain;

    assign Ain = A;

    hct74245 #(.LOG(LOG), .NAME(NAME)) ab( 
        .dir(1'b1),
        .nOE,
        .A(Ain),
        .B
    );

    
    //if (LOG) 
        always @(*) 
            $display("%9t", $time, " BUF %s", NAME, ": A=%8b ", A, "B=%8b ", B, " nOE=%1b", nOE);
        
endmodule: hct74245ab

`endif

