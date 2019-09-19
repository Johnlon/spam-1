/* octal d-type transparent latch
 Same model as 74373.
 
 LE = H is transparent
 OE_N = H is Z
 
 Timings have are for 74HCT573
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT573.pdf
 https://assets.nexperia.com/documents/data-sheet/74HC_HCT373.pdf
 */

`timescale 1ns/100ps
module hct74573 (input D0,
                 input D1,
                 input D2,
                 input D3,
                 input D4,
                 input D5,
                 input D6,
                 input D7,
                 output Q0,
                 output Q1,
                 output Q2,
                 output Q3,
                 output Q4,
                 output Q5,
                 output Q6,
                 output Q7,
                 input LE,
                 input OE_N);
    
    reg q0, q1, q2, q3, q4, q5, q6, q7;
    
    // X is initial state of Q
    //initial
    //    {q0, q1, q2, q3, q4, q5, q6, q7} <= 8'b0;
    
    specify
    (D0 => Q0) = (17);
    (D1 => Q1) = (17);
    (D2 => Q2) = (17);
    (D3 => Q3) = (17);
    (D4 => Q4) = (17);
    (D5 => Q5) = (17);
    (D6 => Q6) = (17);
    (D7 => Q7) = (17);
    
    (LE => Q0) = (15);
    (LE => Q1) = (15);
    (LE => Q2) = (15);
    (LE => Q3) = (15);
    (LE => Q4) = (15);
    (LE => Q5) = (15);
    (LE => Q6) = (15);
    (LE => Q7) = (15);
    
    (OE_N => Q0) = (18);
    (OE_N => Q1) = (18);
    (OE_N => Q2) = (18);
    (OE_N => Q3) = (18);
    (OE_N => Q4) = (18);
    (OE_N => Q5) = (18);
    (OE_N => Q6) = (18);
    (OE_N => Q7) = (18);
    
    endspecify
    
    always @(D0 or D1 or D2 or D3 or D4 or D5 or D6 or D7 or LE)
        if (LE)
        begin
            {q0,q1,q2,q3,q4,q5,q6,q7} <= {D0,D1,D2,D3,D4,D5,D6,D7};
        end
    
    assign Q0 = OE_N ? 1'bz : q0;
    assign Q1 = OE_N ? 1'bz : q1;
    assign Q2 = OE_N ? 1'bz : q2;
    assign Q3 = OE_N ? 1'bz : q3;
    assign Q4 = OE_N ? 1'bz : q4;
    assign Q5 = OE_N ? 1'bz : q5;
    assign Q6 = OE_N ? 1'bz : q6;
    assign Q7 = OE_N ? 1'bz : q7;
    
endmodule
