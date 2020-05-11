// NOT implemented fully https://assets.nexperia.com/documents/data-sheet/74HC_HCT109.pdf
`timescale 1ns/1ns

module hct74109 ( 
    input j,
    input _k,
    input clk,
    input _sd, _rd,
    output q, 
    output _q
);

    parameter PROP_DELAY=17; 
    parameter ASYNC_DELAY=15; 
    parameter SETUP_TIME=8;  // JK must be setup AHEAD fo the clock - see datasheet

    reg Q,J,_K;

    logic fSR=1'bz;// force set / reset

    wire Qresult = (fSR !== 1'bz)? fSR: Q;
 
    assign #(SETUP_TIME) J = j;
    assign #(SETUP_TIME) _K = _k;

    always @*
        $display($time, " 74109  clk ", clk, 
                    " j ", J, " _k ", _K, " q ", Qresult,
                    " _sd " , _sd, " _rd ", _rd,
                    " fSR ", fSR, " regQ ", Q
                );

    task checkSR;
        // explicitely state args so these become part fo sensitivity list
        // https://www.verilogpro.com/systemverilog-always_comb-always_ff/
        input _sd, _rd;
        output fSR;

        if (!_sd & _rd) fSR<=1;
        else if (_sd & !_rd) fSR<=0;
        else if (!_sd & !_rd) fSR<=1;
        else fSR<=1'bz;
    endtask

    initial begin
        // explicitely state args so these become part fo sensitivity list
        // https://www.verilogpro.com/systemverilog-always_comb-always_ff/
        checkSR(_sd, _rd, fSR);
    end 

    always @* begin
        // explicitely state args so these become part fo sensitivity list
        // https://www.verilogpro.com/systemverilog-always_comb-always_ff/
        checkSR(_sd, _rd, fSR);
    end 
 
    always @(posedge clk)
    begin
        if (_sd & _rd) begin
            case ({J,_K})
                2'b01 :  
                    #(PROP_DELAY) Q = Q;
                2'b00 :  
                    #(PROP_DELAY) Q = 0;
                2'b11 :  
                    #(PROP_DELAY) Q = 1;
                2'b10 :  
                    #(PROP_DELAY) Q = !Q;
            endcase
        end

    end

    //assign #(PROP_DELAY) q = Q;
    //assign #(PROP_DELAY) _q = !Q;


    assign q = Qresult;
    assign _q = !Qresult;

endmodule

