
// `ifndef V_ALU_FUNC
// `define V_ALU_FUNC

// `timescale 1ns/1ns

// module alu_func;

//     function string aluopName;
//         input [4:0] opcode;
        
//         string ret;
//         begin
//             case(opcode)
//                  0 : aluopName =    "0";
//                  1 : aluopName =    "A";
//                  2 : aluopName =    "B";
//                  3 : aluopName =    "-A";
//                  4 : aluopName =    "-B";
//                  5 : aluopName =    "A+1";
//                  6 : aluopName =    "B+1";
//                  7 : aluopName =    "A-1";

//                  8 : aluopName =    "B-1";
//                  9 : aluopName =    "A+B";   // CarryIn not considered
//                 10 : aluopName =    "A-B";   // CarryIn not considered
//                 11 : aluopName =    "B-A";   // CarryIn not considered
//                 12 : aluopName =    "A-B signedmag"; // CarryIn not considered
//                 13 : aluopName =    "A+B+c"; // If CarryIn=N then this op is automatically updated to A+B
//                 14 : aluopName =    "A-B-c"; // If CarryIn=N then this op is automatically updated to A-B
//                 15 : aluopName =    "B-A-C"; // If CarryIn=N then this op is automatically updated to B-A

//                 16 : aluopName =    "A*B LO";
//                 17 : aluopName =    "A*B HI";
//                 18 : aluopName =    "A/B";
//                 19 : aluopName =    "A%B";
//                 20 : aluopName =    "A<<B";
//                 21 : aluopName =    "A>>B log";
//                 22 : aluopName =    "A>>B arith" ;
//                 23 : aluopName =    "A ROL B";

//                 24 : aluopName =    "A ROR B";
//                 25 : aluopName =    "A AND B";
//                 26 : aluopName =    "A OR B";
//                 27 : aluopName =    "A XOR B"; 
//                 28 : aluopName =    "NOT A";
//                 29 : aluopName =    "NOT B";
//                 30 : aluopName =    "A+B BCD";
//                 31 : aluopName =    "A-B BCD";
//                 default: begin
//                     $sformat(ret,"??unknown(%b)",opcode);
//                     aluopName = ret;
//                 end
//             endcase

//         end
//     endfunction
// endmodule

// `endif