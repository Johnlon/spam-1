`include "hct7474.v"
`include "../lib/assertion.v"

module tb();

logic cp, d, sd, rd;
wire q, qb;

localparam BLOCKS = 1;

// names: https://assets.nexperia.com/documents/data-sheet/74HC_HCT74.pdf
ttl_7474 #(.BLOCKS(BLOCKS), .DELAY_RISE(5), .DELAY_FALL(3)) dut(
  .Preset_bar(sd),
  .Clear_bar(rd),
  .D(d),
  .Clk(cp),
  .Q(q),
  .Q_bar(qb)
);

initial begin
    $monitor("%6d ", $time, " : cp=%1b,d=%1b,sd=%1b, rd=%1b, q=%1b, qb=%1b", cp,d,sd, rd, q, qb);

    cp=0;
    d=1;

    #10
    rd=1;
    sd=1;

    #10
    `Equals(q, 1'bx);
    `Equals(qb, 1'bx);

    #10
    rd=0;
    sd=0;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b1);

    #10
    rd=1;
    sd=1;

    #10
    `Equals(q, 1'bx); // still no definite value set
    `Equals(qb, 1'bx); // still no definite value set

    // should reset async
    #10
    rd=0;
    sd=1;

    #10
    `Equals(q, 1'b0);
    `Equals(qb, 1'b1); 

    // should stay same
    #10
    rd=1;
    sd=1;

    #10
    `Equals(q, 1'b0);
    `Equals(qb, 1'b1); 

    // should set async
    #10
    rd=1;
    sd=0;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b0); 

    // should stay same
    #10
    rd=1;
    sd=1;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b0); 

    // should stay same even if d changes
    #10
    d=0;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b0); 

    // should override clock +ve edge with set
    #10
    rd=1;
    sd=0;
    cp=1;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b0); 


    // should clock in d
    d=0;
    rd=1;
    sd=1;
    cp=0;

    #10
    cp=1;

    #10
    `Equals(q, 1'b0);
    `Equals(qb, 1'b1); 

    // should clock in d
    d=1;
    rd=1;
    sd=1;
    cp=0;

    #10
    cp=1;

    #10
    `Equals(q, 1'b1);
    `Equals(qb, 1'b0); 

    // should clear async
    d=1;
    rd=0;
    sd=1;

    #10
    `Equals(q, 1'b0);
    `Equals(qb, 1'b1); 
end

endmodule
