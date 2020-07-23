/* verilator lint_off ASSIGNDLY */

// EG USE M27C322 21bit address x16 data

// TODO TODO TODO - generate rom images for Warren

/// FIXME NEED A BINARY TO BCD OPERATION AS THIS IS HARD - https://www.nandland.com/vhdl/modules/double-dabble.html

// FIXME - need to be able to shift with carry in or do this with an OR/AND operation?

/// EG USING ROM 28C512


/// MANY OPS NOT REQUIRED IF R CAN BE IMMEDIATE eg "A+1" is same as "A+immediate 1" as long as both treat carry the same
/// HMMMM .. But can't do "B+immediate 1" unless instreg is available on A bus too.

//// THIS IS THE MOST RECENT Jun 2020 ALU layout from CSCVon8
// | 0           | B-1               | A*B (low bits)    | A ROR B       |
// | A           | __A+B+Cin (0)__   | A*B (high bits)   | A AND B       |
// | B           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B (special)     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B logical    | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B arithmetic | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A ROL B           | A-B (BCD)     |

// My wiring here is ....
// | 0           | B-1               | A*B (low bits)    | A RRC B       |
// | A           | __A+B+Cin (0)__   | A*B (high bits)   | A AND B       |
// | B           | __A-B-Cin (0)__   | A/B               | A OR B        |
// | -A          | __B-A-Cin (0)__   | A%B               | A XOR B       |
// | -B          | A-B signedmag     | A << B            | NOT A         |
// | A+1         | __A+B+Cin (1)__   | A >> B logical    | NOT B         |
// | B+1         | __A-B-Cin (1)__   | A >> B arithmetic | A+B (BCD)     |
// | A-1         | __B-A-Cin (1)__   | A RLC B           | A-B (BCD)     |


`include "../74138/hct74138.v"
`include "alu_ops.v"
`include "alu_rom.v"

// See also http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt#:~:text=The%20ALU%20doesn't%20know,after%20the%20math%20is%20done.

`ifndef  V_ALU
`define  V_ALU

`timescale 1ns/1ns

/* 
    If Inputs to arithmentic are two's complement then value will be 7 bits plus sign and Overflow is relevant, but ignore Carry

    The comparator outputs GT/LT are only valid for logical values (not twos complement).
    For contrast: 74AS885 permits selection of logical or arithmetic magnitude comparison https://www.ti.com/lit/ds/symlink/sn74as885.pdf?ts=1592517566383&ref_url=https%253A%252F%252Fwww.google.com%252F

    If I wanted to do a signed magnitude check then I have to do maths. 
    If do A-B with carry-cleared and then look at the Z and C flags. 
    
    Z set means they were equal, 
    if Overflow is not set and
    C set means B>A, 
    Z and C unset means A>B  
    ... but only as long as O is not set
    what about -2 and 3 which will become -5 which is not Z and not C
*/

module alu #(parameter LOG=0, PD=120) (
    output [7:0] o,
    output _flag_c,
    output _flag_z,
    output _flag_n,
    output _flag_o,
    output _flag_gt,
    output _flag_lt,
    output _flag_eq,
    output _flag_ne,
    input  [7:0] a,
    input  [7:0] b,
    input  [4:0] alu_op,
    input  _flag_c_in
);
    import alu_ops::*;

    //////////////////////////////////////////////////////
    // CARRY-IN LOGIC - EXTERNAL LOGIC
    //////////////////////////////////////////////////////

    wire #(8) flag_c_in = !_flag_c_in; // inverter

    wire D,E,C,B,A;
    assign E=alu_op[4];
    assign D=alu_op[3];
    assign C=alu_op[2];
    assign B=alu_op[1];
    assign A=alu_op[0];

    wire [7:0] _decoded;
    hct74138 decoder
    (
      .Enable1_bar(E),
      .Enable2_bar(1'b0),
      .Enable3(D),
      .A({C,B,A}),
      .Y(_decoded)
    );

    wire #(8) _use_cin = _decoded[7] & _decoded[6] & _decoded[5]; // AND GATE - BUT USE TRIPLE 3 INPUT NAND AS WE NEED A NOT ON THE _flag_c_in ABOVE

    wire effective_bit3 = _use_cin ? alu_op[2]: flag_c_in; // multiplexer
        
    wire [4:0] alu_op_effective = {alu_op[4:3], effective_bit3, alu_op[1:0]};


    //////////////////////////////////////////////////////
    // ROM PROGRAMMING
    // ROM PROGRAMMING
    // ROM PROGRAMMING
    //////////////////////////////////////////////////////

    alu_rom ALU_ROM( .o, ._flag_c, ._flag_z, ._flag_n, ._flag_o, ._flag_gt, ._flag_lt, ._flag_eq, ._flag_ne, .a, .b, .alu_op(alu_op_effective));

endmodule: alu

`endif
