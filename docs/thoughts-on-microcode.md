# Thoughts on microcoded instructions

Ben Eater and others use an instruction processing sub-cycle where a given op code is executed as a sequence of microcode instructions.

My thoughts (Aug 2019) on microcoded instructions such as those used by Ben Eater are that they offer a few benefits.  

- The higher level functions of these operations are are easier to work as they are somewhat abstracted from the technicalities of enabling and disabling specific devices. In a commercial CPU this abstraction is one of the ways that a single binary program can run on multiple variants of CPU's without change.
- Program size in memory can be smaller. One can think of the microcodes for each high level instruction as a subroutine. In a horizonally microcoded CPU the same sequences of microcode will appear many times over in the program code and so the program code will be longer, though not necessarily any slower because we are merely swaping microcoded instructions for coded instructions and each takes a clock cycle in both cases.

However, my thinking is that I ought to be able to achieve similar high level operations in SPAM1 but without microcoded instructions. I can achieve this by having the assembler expand high level opcodes, rather like C macros, into what would have otherwise been their component microinstructions. 

If I'm able to work with 32k or more of program space then this approach should be tolerable. We'll see in any case.