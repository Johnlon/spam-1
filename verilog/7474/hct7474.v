// Dual D flip-flop with set and clear; positive-edge-triggered
// Timings from https://assets.nexperia.com/documents/data-sheet/74HC_HCT74.pdf

// Corrected based on - https://raw.githubusercontent.com/TimRudy/ice-chips-verilog/master/source-7400/7474.v
// 
// TimRudy version doesn't match datasheet
// - the clear/preset should be async (see note below)
// - clear/preset should be not be edge sensitive (see note below)
// - when clear/preset are both set then Q and Qb are both 1

// Tim Rudy's version has these compromises.
//      Note: Note: _SD is synchronous, not asynchronous as specified in datasheet for this device,
//      in order to meet requirements for FPGA circuit design (see IceChips Technical Notes)

module ttl_7474 #(parameter BLOCKS = 2, DELAY_RISE = 100, DELAY_FALL = 100)
(
  input [BLOCKS-1:0] _SD,
  input [BLOCKS-1:0] _RD,
  input [BLOCKS-1:0] D,
  input [BLOCKS-1:0] CP,
  output [BLOCKS-1:0] Q,
  output [BLOCKS-1:0] _Q
);

//------------------------------------------------//
reg [BLOCKS-1:0] Q_current;
reg [BLOCKS-1:0] Qb_current;
reg [BLOCKS-1:0] _SD_previous;
reg [BLOCKS-1:0] Q_defined = 0;

generate
  genvar i;
  for (i = 0; i < BLOCKS; i = i + 1)
  begin: gen_blocks
    always @(posedge CP[i])
    begin
      if (_RD[i] && _SD[i])
      begin
        //$display("Setting");
        Q_defined[i] <= 1'b1;
        Q_current[i] <= D[i];
        Qb_current[i] <= !D[i];
      end
        else
        begin
        //$display("Setting disabled by Clear or Preset");
        end
    end

    always @(_RD[i] or _SD[i])
    begin
      if (!_RD[i] && !_SD[i])
        begin
            //$display("Clear and Preset");
            Q_current[i] <= 1'b1;
            Qb_current[i] <= 1'b1;
        end
      else if (!_RD[i])
        begin
            //$display("Clear");
            
            Q_defined[i] <= 1'b1;
            Q_current[i] <= 1'b0;
            Qb_current[i] <= 1'b1;
        end
      else if (!_SD[i])
        begin
            //$display("Preset");
            Q_defined[i] <= 1'b1;
            Q_current[i] <= 1'b1;
            Qb_current[i] <= 1'b0;
        end
      else //
        begin
            //$display("Not Clear and Not Preset");
            if (!Q_defined[i]) begin
                // no value has been defined - realistically a random value would be settled on, we'll use X
                Q_current[i] <= 1'bx;
                Qb_current[i] <= 1'bx;
            end
        end
      end
  end
endgenerate
//------------------------------------------------//

assign #(DELAY_RISE, DELAY_FALL) Q = Q_current;
assign #(DELAY_RISE, DELAY_FALL) _Q = Qb_current;

endmodule

