# ALU 

This ALU design is based on [Warren Toomey's CSCvon8 ALU](https://github.com/DoctorWkt/CSCvon8/blob/master/Docs/CSCvon8_design.md) but with a few small changes to incorporate a carry-in for a few of the ops.

In the second column the three A+B+Cin functions appear twice and also I've substituted function 12 for the constant 1 because the original CSCvon8 function in that slot . 

| 0-7 ALU Ops | 8-15 ALU Ops  | 16-23 ALU Ops     | 24-31 ALU Ops |
|-------------|---------------|-------------------|---------------|
| 0           | B-1           | A*B (high bits)   | A ROR B       |
| A           | A+B+Cin (0)   | A*B (low bits)    | A AND B       |
| B           | A-B-Cin (0)   | A/B               | A OR B        |
| -A          | B-A-Cin (0)   | A%B               | A XOR B       |
| -B          | A-B (special) | A << B            | NOT A         |
| A+1         | A+B+Cin (1)   | A >> B arithmetic | NOT B         |
| B+1         | A-B-Cin (1)   | A >> B logical    | A+B (BCD)     |
| A-1         | B-A-Cin (1)   | A ROL B           | A-B (BCD)     |
|             |               |                   |               |

I wanted a set of add operations that could take the carry flag into account. But carry applies only to a few of the functions so it would be wasteful to have one of the 5 control lines hard-wired to the carry flag. 

In this design bit 4 of the select lines into the ALU will be multiplexed across bit 4 from the control logic and the carry line.
So in most cases alu select line 4 is determined by the control line but for 5 of the operations the multiplexer will swap the 4th select line for the carry flag value. 

In order to make the external logic easier I've made these 5 carry-in sensitive operations the last 5 ops in cols 3 and 4. So I've moved what was originally in cols 3 and 4 in CSCvon8 to sit at cols 1 and 2 instead.

If I had merely reworked column two then the control http://www.32x8.com/sop5_____A-B-C-D-E_____m_9-10-11-13-14-15___________option-0_____889788875878823595647

http://www.32x8.com/sop5_____A-B-C-D-E_____m_25-26-27-29-30-31___________option-0_____889788975578823595647

With this setup then when accessing operation 19 "A+B+Cin" then select line 4 will actually be the carry flag.
However, for those last 5 ops the hardware decides whether to use the variant of the op from column 3 or from column 4 based on the status of the CFlag. The column 3 variants are appropriate for where CFlag=0, and column 4 where CFlag=1.

