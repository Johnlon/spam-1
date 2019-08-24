
# CPU Architecture Research and References

## Register vs Accumulator vs Stack based architectures

- [YCombinator question](https://news.ycombinator.com/item?id=16900113)

    But generally, things fall out this way:

    1) register based

    Has a pool of registers, and the registers are general purpose (meaning they can be used as source or destination for most any computation operation the CPU can perform).

    2) accumulator based

    Has a "dignified" register (the accumulator) and most computations only happen in/out of the accumulator. Other registers exist for more specialized purposes to facilitate feeding data into and out of the accumulator. Many DSP architectures are very highly "accumulator based". General purpose CPU's, less so unless you go back in time to late 70's or early 80's architectures.

    3) stack based

    Has no registers (or very few) other than a stack pointer. All temporary data is stored on the stack, and all computation instructions involve computing with data items at the top of the stack and returning the result to the top of the stack.

- [Quora question refines the above](https://www.quora.com/What-is-the-difference-between-accumulator-based-cpu-and-register-based-cpu)

    Within general purpose register machines, there are multiple varieties as well:  (This list is not exhaustive; also, some architectures blend these concepts.)

    - Register-Memory:  One operand comes from a register, and one operand  comes from memory.
    - Register-Register, 2-address:  Both operands come from registers, but the result must overwrites one of the inputs.
    - Register-Register, 3-address:  Both operands come from registers, and the result can go to its own register.
    
    The x86 processor, for example, is a Register-Memory machine that also offers 2-address Register-Register instructions.  Most RISC machines are 3-address Register-Register machines, with separate load/store instructions.  Both are general-purpose register machines.

    Compare those to the 6502, which is an accumulator machine.  Most arithmetic (addition, subtraction, rotates and shifts) operates on the A register.  The two other registers, X and Y, only support increment, decrement and compare; they're mainly used for indexing memory and loop counters.

- [HP 3000 Stack based](https://en.wikipedia.org/wiki/HP_3000#Use_of_stack_instead_of_registers)


## Binary Arithmetic

[Doing binary arithmetic YT vided](https://www.youtube.com/watch?v=WN8i5cwjkSE)

[Arbitrary-precision_arithmetic](Also : )https://en.wikipedia.org/wiki/Arbitrary-precision_arithmetic)

https://retrocomputing.stackexchange.com/questions/6640/how-did-8-bit-processors-perform-16-bit-arithmetic


## Status Flags

[What we mean by Carry and Overflow](http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt)

# Recommended reading, watching and time specific bookmarks

This section contains a list content I found useful and also bok marks at specific points in videos about specific topic.

I'd also encourage you to look at [Warren Toomey's CrazySmallCPU](https://www.youtube.com/playlist?list=PL9YEAcq-5hHIJnflTcLA45sVxr900ziEy) 

Also, [James Bates' series](https://www.youtube.com/watch?v=gqYFT6iecHw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui) of  Ben Eater inspired videos for his build. I found the discussion much more detailed in many cases and very useful. 

[MIT Computational Structures lectures](https://computationstructures.org/index.html)

## Ben Eater's links

https://www.youtube.com/watch?v=AwUirxi9eBg&list=PLowKtXNTBypGqImE405J2565dvjafglHU&index=35 - Control signal overview

https://youtu.be/dXdoim96v5A?list=PLowKtXNTBypGqImE405J2565dvjafglHU&t=463 - Control logic 1 - Step by step control logic

https://www.youtube.com/watch?v=X7rCxs1ppyY&t=4m29s - Control logic 2 - Ben Eater comment on clock sync, the need for a separate ("inverted") clock for the enablement of registers, and also the need to do enablement of registers ahead of the synchronous clock - his solution is to buffer the clock line so that it is delayed by some nanoseconds compared to the clock used for register enablement.   

https://www.youtube.com/watch?v=ObnosznZvHY&list=PLowKtXNTBypGqImE405J2565dvjafglHU&index=43 - CPU Flags

https://youtu.be/ObnosznZvHY?t=709 - Ben discusses problem where the subsequent Jump instruction needs the Carry but the previous microinstruction wiped it out. Needs flags register to fix.

https://youtu.be/ObnosznZvHY?t=1164 - Copy flags from ALU to reg whenever doing a "Sum out" ie whenever the ALU is being outputted.


## Warren Toomey -  Crazy Small Cpu

https://minnie.tuhs.org/Programs/CrazySmallCPU/ Home page

https://www.youtube.com/playlist?list=PL9YEAcq-5hHIJnflTcLA45sVxr900ziEy Playlist for the Crazy Small CPU

https://www.youtube.com/watch?v=zJw7WcikX9A RAM and flags

https://minnie.tuhs.org/Programs/UcodeCPU/index.html Original Logism impl of the CSCv1

https://youtu.be/nLo7Kt6sGmM?list=PL9YEAcq-5hHIJnflTcLA45sVxr900ziEy&t=223 ROM as ALU (8+8+4 control)


## James Bates links

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=4m52s  Mentions "extensions to Bens video"

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=5m15s  Bus and no need for pulls up/downs on bus in his case 

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=6m30s  Discussion of program counter & bus width; Ben has 4 vs this 8

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=43m30s Need to pulse the write as this isn’t synchronous ram

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=46m09s Clock pulse and edge detector - Enable AND Rising Edge of clock via RC net ( Schmitt trigger??)

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=49m09s Discussion on the need to buffer the clock before James' edge detection signal to avoid pollution of the raw clock

https://www.youtube.com/watch?v=tUXboOaisAY&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=5#t=27m59s Good discussion on instruction encoding (compression) in 8 bits

https://www.youtube.com/watch?v=DfuFNBJn1hk&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=6#t=6m48s Fixes one of the operands to reduce instruction space B is always second input   so can’t do A=C+D

## Simple CPU

http://www.simplecpudesign.com/ Home page

http://www.simplecpudesign.com/simple_cpu_v1/index.html  V1

http://www.simplecpudesign.com/simple_cpu_v1a/index.html V1a V1a

http://www.simplecpudesign.com/simple_cpu_v2/index.html   V2

## Gigatron

https://hackaday.io/project/20781/logs?sort=oldest&page=2

## Other Computers

https://hackaday.io/project/24511-jaca-1-2-homebrew-computer
