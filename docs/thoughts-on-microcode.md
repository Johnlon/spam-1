# Thoughts on microcoded instructions

Ben Eater and others use an instruction processing sub-cycle where a given op code is executed as a sequence of microcode instructions.

My thoughts (Aug 2019) on microcoded instructions such as those used by Ben Eater are that they offer a few benefits.  

- Higher level operations are easier to work as they are somewhat abstracted from the technicalities of enabling and disabling specific control lines. In a commercial CPU this abstraction is one of the ways that a single binary program can run on multiple variants of CPU's without change.

- Program size in memory can be smaller. One can think of the microcodes for each high level instruction as a subroutine. In a horizonally microcoded CPU the same sequences of microcode will appear many times over in the program code and so the program code will be longer, though not necessarily any slower because we are merely swaping microcoded instructions for coded instructions and each takes a clock cycle in both cases.

- Variable length instructions, a special case of the first bullet above, are perhaps facilitated? Eg to load an 8 bit value from a 16 bit address location into A one might have the following 3 byte instruction _"LD A, $HHLL"_ to execute it we use the following micro instructions, assuming we've just loaded the opcode byte then ....
   - PC_inc / LD MAR_hi, ROM[PC]
   - PC_inc / LD MAR_lo, ROM[PC]
   - LD A, RAM[MAR]

However, my thinking is that I ought to be able to achieve similar high level operations in SPAM1 but without microcoded instructions. I can achieve this by having the assembler expand high level opcodes, rather like C macros, into what would have otherwise been their component microinstructions. 

If I'm able to work with 32k or more of program space then this approach should be tolerable. We'll see in any case.

# Links

- [Microprogramming slides - University of Virginia](http://www.cs.virginia.edu/~cs333/notes/microprogramming.pdf)
- [Microprogramming - MIT](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-823-computer-system-architecture-fall-2005/lecture-notes/l04_microprog.pdf)
