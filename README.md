# simplecpu
Simple CPU simulation built using Logism Evolution and including and Assembler build using google sheets

## Motivation

A bit of fun!

I started working life in 1986 as a hardware engineer but quickly switched to software. As a teenager I'dbeen fascinated with
discrete electronics and then later on with integrated circuits and build many little home projects; nothing too exciting as we didn't have the resources (ie ££) back then.

Recently, like many other folk, I came across [Ben Eater's series of YT videos](https://www.youtube.com/watch?v=HyznrdDSSGM&list=PLowKtXNTBypGqImE405J2565dvjafglHU) and also those of quite a few others that inspired me to have a go at building my
own CPU. Back in 1980 even the relatively few parts needed would probably have been beyond my means, but not anymore !!

However, back in the 1980's  I would have been building more of less blind. I still don't have an oscilloscope but what I do have is a simulator.   
Having spent the last few weeks getting to know [Logisim Evolution](https://github.com/reds-heig/logisim-evolution)  and having hours trying to figure out details of my simulated processor
it's clear if I have attempted to build a CPU back in 1980 then I'd have fallen flat on my face the first time something didn't work.
It's been a great learning experience, if frustrating at times.


## Further reading and links

I'd also encourage you to look at [James Bates' series](https://www.youtube.com/watch?v=gqYFT6iecHw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui) of  Ben Eater inspired videos. 
I found James' discussion much more detailed in many cases and very useful. 

And definitly the [Crazy Small CPU series](https://www.youtube.com/playlist?list=PL9YEAcq-5hHIJnflTcLA45sVxr900ziEy). 

### Ben's links

https://www.youtube.com/watch?v=X7rCxs1ppyY&t=4m29s Ben Eater comment on clock sync, the need for a separate ("inverted") clock for the enablement of registers, and also the need to do enablement of registers ahead of the synchronous clock - his solution is to buffer the clock line so that it is delayed by some nanoseconds compared to the clock used for register enablement.   

### James Bates links

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=4m52s  Mentions "extensions to Bens video"

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=5m15s  Bus and no need for pulls up/downs on bus in his case 

https://www.youtube.com/watch?v=AALVh39X3xw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=3#t=6m30s  Discussion of program counter & bus width; Ben has 4 vs this 8

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=43m30s Need to pulse the write as this isn’t synchronous ram

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=46m09s Clock pulse and edge detector - Enable AND Rising Edge of clock via RC net ( Schmitt trigger??)

https://www.youtube.com/watch?v=hRJO97PbPlw&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=4#t=49m09s Discussion on the need to buffer the clock before James' edge detection signal to avoid pollution of the raw clock

https://www.youtube.com/watch?v=tUXboOaisAY&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=5#t=27m59s Good discussion on instruction encoding (compression) in 8 bits

https://www.youtube.com/watch?v=DfuFNBJn1hk&list=PL_i7PfWMNYobSPpg1_voiDe6qBcjvuVui&index=6#t=6m48s Fixes one of the operands to reduce instruction space B is always second input   so can’t do A=C+D

### Crazy Small Cpu

https://minnie.tuhs.org/Programs/CrazySmallCPU/ Home page

https://www.youtube.com/playlist?list=PL9YEAcq-5hHIJnflTcLA45sVxr900ziEy Playlist for the Crazy Small CPU

https://www.youtube.com/watch?v=zJw7WcikX9A RAM and flags

https://minnie.tuhs.org/Programs/UcodeCPU/index.html Microcode Logism Sim

### Simple CPU

http://www.simplecpudesign.com/ Home page

http://www.simplecpudesign.com/simple_cpu_v1/index.html  V1

http://www.simplecpudesign.com/simple_cpu_v1a/index.html  V1a

http://www.simplecpudesign.com/simple_cpu_v2/index.html   V3

 


### Other links

https://www.youtube.com/watch?v=Fq0MIJjlGsw Using the 74181

https://www.youtube.com/watch?v=WN8i5cwjkSE Doing binary arithmetic

http://teaching.idallen.com/dat2343/10f/notes/040_overflow.txt What we mean by Carry and Overflow


## Other Computers

https://github.com/DoctorWkt/CSCvon8/blob/master/Docs/CSCvon8_design.md

https://hackaday.io/project/24511-jaca-1-2-homebrew-computer

https://minnie.tuhs.org/Programs/CrazySmallCPU/description.html


 






## Simulators

[Logisim (original)](http://www.cburch.com/logisim/) is no longer supported however there seems to be a lot of reusable circuits out there for it (eg github). 
I started building in Logisim "original" and got a long way. However, at one point I was having difficulty with something and
I went investigating and only then discovered that it was no longer under development. Apparently the author had decided to go off and build a new shiny product 
but that project ran out of steam and never materialised. In the mean time Logism went stale. 
Fortunately however, Logisim "original" was adopted by [Reds Institute](http://blog.reds.ch) and was reborn as "Logisim Evolution".
 
[Logisim Evolution](https://github.com/reds-heig/logisim-evolution) is a fork that has added many features and also fixed various problems apparently.
It's worked pretty well for me. It's not perfect and a bunch of the UI interactions are non-intuitive but on the whole it works pretty well. Occasionally 
the simulation engine inside it crashed and I had to restart but the app told me to save and restart and this always worked fine without any loss of work so it wasn't
 much of an inconvenience. 

Unfortunately, there seems to be a bit of fragmentation in Logism as there appear to be a further two "active" forks.

[Cornell fork of Logism Evolution](https://github.com/cs3410/logisim-evolution) which is used by their [computer science course](
http://www.cs.cornell.edu/courses/cs3410/2019sp/labs/lab1/).
 
As a user of logism I don't welcome forks. I have to choose which to use.
I'm not sure what the incompatibilities might be, there might be features in either that I want. Its unclear which fork 
has the best chance of support and development going forward.

As a user I'd prefer it if all these clever folk pooled their resources into one mighty version of Evo.
The forking risks leaking resources away from the product generally and damages reputation.

### Upgrading from Logisim Original to Evolution

Upgrading wasn't trivial, buit wasn't too difficult either. Unfortunately, "original" and "evo" are not 100% compatible from an upgrade perspective. It seems that the problems occur if using the "memory" components like RAM, ROM and register for instance
 as these have changed in the "evo" dist.   

:thumbsup: The [video from MrMcsoftware](https://www.youtube.com/watch?v=yd7DeWTbfWQ) was incredibly helpful in giving me my first pointers on how to sort out the issues
without starting from scratch. It does mean editing the "circ" file in a text editor but the changes were pretty straightforward. Pity there isn't a better written guide to this.

One gripe myself and [MrMcsoftware](https://mrmcsoftware.wordpress.com/author/mrmcsoftware/) have in common is our dislike of the clunk ROM and RAM figures used by Evo.
The new images take up a lot more space needlessly. MrMcsoftware has a replacement library (ForEv.jar) but I decided just to accept the change.
Another, gripe is that the rendering in evo is much chunkier so less seems to fit in the same area.
I find the original rendering much more pleasing to the eye, however, there are sufficient improvements in evo to persuade me to accept these changes.

For comparison: Original vs Evolution ...

![Original](docs/logisim-original-sample.png)
![Evolution](docs/logisim-evolution-sample.png)

### Logism Libraries

I wasn't able to find a good resource listing the various libraries that exist out there in github and elsewhere. Whether a given library works in original or evolution 
is uncertain until you actually try it. Many "original" circuit libs do work functionally in evolution but often the rendering of the component in evolution is utterly 
different than in the original product. My guess is that evo often ignores the rendering info in libraries from original and just renders a big clunky default.

For example is what you get if you import the 7477 from [stsvetkov](https://github.com/stsvetkov/L8cpu). To be fair this library doesn't pretend to be evo-compatible, but below you can see that the 747D latch gets rendered as some kind of evo default rendering.
Also, notice the mangled chip name "L_7474_bd25d20c. This name mangling is a feature of the auto upgrade that evo does to incompatible files from original. This name mangling typically occurs
when the name in the library breaks one of evo's name rules. Evo substitutes a valid name instead. This name mangling doesn't seem to mess with the functionality however.
In anycase if there is a library you like and want to use then you can always copy the file and edit it to remove offending characters like whitespace and hyphen.
You will still get a chunky glyph however then names will be more sensible. For contrast below you can see the 74181 from the same library after I've fixed various naming issues. 

![Original](docs/logisim-evolution-7474-and-181.png)

What worked for me where I didn't like the appearance of a component was that I imported it into Evo into a sub circuit and then I edited the appearance of the subcircuit in evo to make it
smaller and more convenient. This approach doesn't of course work for the chunk ROM and RAM I mentioned earlier because they have interactive UI's that I'd end up hiding
if I wrapped it and hid it inside a subcircuit. Anyway, it's worth considering.
     
Another, gripe with some libs is that they aren't accurate. In at least two cases I found for instance that outputs that were documented in the datasheet as "open collector"
had been implemented with regular logic; the 74181 "A=B" output is an example. It's not that hard to fix this if you wrap the component with output 
buffered as shown below

![OpenCollectorBuf](docs/open-collector.png)

Here's an example of usage of open collector outputs connecting in a "wired and" configuration ...
 
![OpenCollectorBuf](docs/open-collector-181.png)

I ended up putting the open collector buffer component into a subcircuit and then created an "appearance" for it in Evo's appearance editor that looks like a buffer with an asterisk next to it. 
I understand this is the correct icon for such.

### Getting Logism Evolution

See the downloads in guthub https://github.com/reds-heig/logisim-evolution/releases
  
  
### Yes more Simulators

[circuitverse](https://circuitverse.org/)   

Can't recall why I was put off by this product.
 
It's online which is cool but for some reason when I was playing originally I got stuck and gave up.
There does seem to be a community of project folk have worked that you can form and extend.
Looking again just now after having spent two weeks with Evo I think CircuitVerse looks pretty decent; however I still can't work out how to make
CV's plot feature work (Logism's Cronogram was useful to me).  
So, might be worth considering.

   
[Digital](https://github.com/hneemann/Digital)

Seems like a cousin of Logism and is actively supported too. 
The author responded very quickly to comments I made on an old closed ticket!.
I think I'd already started with Logisim by the time I discovered Digital.

Rather like Logisim this product is merely a jar you download and run; so it's not online.

The author provides a bunch of history on Logism that illustrates the fragmentation in the Logisim space. 

Digital's author says that he fixes a bunch of the long standing architectural issues in all variants of Logism.

The author also states that error detection in Digital is better than Logism. 
Any improvements in that space are a definite plus. 
It's a huge pain sometimes trying to figure out where an oscillation is coming from or where there's transient conflict 
on a level on a wire.

I found the UI interactions unintuitive at times. I couldn't work out how to reroute a wire without deleting it.

There's a lot of cool stuff in there including documentation on extending Digital. Docs on Logism are a bit lacking or fragmented.
I particularly like the claimed 80% test coverage. I understand Logism also lack good automated testing. 

Again probably worth a look.

Given Digital's connection back to Logism it's a pity there's no way to import a Logism circuit :(

 

      