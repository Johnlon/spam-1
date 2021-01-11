# "Single Cycle" ... really??

I have heard the term "Single Cycle Cpu" many times and was trying to understand what single cycle cpu actually meant, honestly. Is there a clear agreed definition and consensus and what is means?

Some home brew "single cycle cpu's" I've come across seem to use both the rising and the falling edges of the clock to complete a single instruction. Typically, the rising edge acts as fetch/decode and the falling edge as execute.

This was all ok and I accepted the parlance until I came across 
the page https://zipcpu.com/blog/2017/08/21/rules-for-newbies.html

That page made a point that really made sense to me ...

    "Do not transition on any negative (falling) edges. 
    Falling edge clocks should be considered a violation of the one clock principle, as they act like separate clocks.".
   
This rings true to me.

Needing both the rising and falling edges (or high and low phases) is effectively the same as either 
- needing the a single edge on two cycles of a single clock that's running twice as fast
- or using two clocks that are out of phase

Either way it seems that using the rising AND falling edges of a single clock is equivalant and so "two cycle" or "dual clock" respectively might be more hones.

Again, is it honest to state that a design is a "single cycle CPU" when both the rising and falling edges are actively used for state change?

Where is the discussion, reasoning and then concensus. 

It would seem that a true single cycle cpu must perform all state changing operations on a single clock edge of a single clock cycle.

I can imagine such a thing is possible providing the data strorage is all synchronous. If we have a synchronous system that has settled then on the next clock edge we can clock the results into a synchronous data store and simultaneously clock the program counter on to the next address.

But if the target data store is for example async RAM then the surely control lines would be changing whilst that data is being stored leading to unintended behaviours.

Am I wrong, are there any examples of a "single cycle cpu" that include async storage in the mix?

It would seem that using async RAM in ones design means one must use at least two logical clock cycles to achive the state change.

Of course, with some more complexity one could perhaps add anhave a cpu that uses a single edge where instructions use solely synchronout components, but relies on an extra cycle when storing to async data; but then that still wouldn't be a single cycle cpu, but rather a a mostly single cycle cpu.

So no CPU that writes to async RAM (or other async component) can honestly be considered a single cycle CPU because the entire instruction cannot be carried out on a single clock edge. The RAM write needs two edges (ie falling and rising) and this breaks the single clock principal.

So is there a commonly accepted single cycle CPU and are we applying the term consistently?

What's the story?

(Also posted in my hackday log https://hackaday.io/project/166922-spam-1-8-bit-cpu/log/181036-single-cycle-cpu-confusion and also on a private group in hackaday)

=====

There are many references to "single" cycle" in the context of MIPS. However, when I look I find that these designs are always ones where the storage including RAM is a synchronous device. As, such then I believe it's entirely possible for these impls to achieve all their work on a single edge of the clock.

And perhaps this is also true of FPGA based CPU designs because I believe their memory is always synchronous - I don't know about that for sure.

HOWEVER, is the term being used inconsistently elsewhere - this includes most Homebrew TTL Computers out there?

Or am I just plain wrong?