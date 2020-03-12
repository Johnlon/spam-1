
`define TEST199(expected) assign expected = 1'b1;

module testCase();

initial begin
    
    logic aa;

    `TEST199(aa);

end

endmodule : testCase
