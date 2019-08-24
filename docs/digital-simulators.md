# Simulators

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

# Upgrading from Logisim Original to Evolution

Upgrading wasn't trivial, buit wasn't too difficult either. Unfortunately, "original" and "evo" are not 100% compatible from an upgrade perspective. It seems that the problems occur if using the "memory" components like RAM, ROM and register for instance
 as these have changed in the "evo" dist.   

:thumbsup: The [video from MrMcsoftware](https://www.youtube.com/watch?v=yd7DeWTbfWQ) was incredibly helpful in giving me my first pointers on how to sort out the issues
without starting from scratch. It does mean editing the "circ" file in a text editor but the changes were pretty straightforward. Pity there isn't a better written guide to this.

One gripe myself and [MrMcsoftware](https://mrmcsoftware.wordpress.com/author/mrmcsoftware/) have in common is our dislike of the clunk ROM and RAM figures used by Evo.
The new images take up a lot more space needlessly.
I think they are some kind of ANSI rendition of the component - see the document [_"Texas Instruments paper Overview of IEEE Standard 91-1984 - 
Explanation of Logic Symbols "_](http://www.ti.com/lit/ml/sdyz001a/sdyz001a.pdf) (also copied into the docs folder of this repo).

MrMcsoftware has a replacement library (ForEv.jar) but I decided just to accept the change.
Another, gripe is that the rendering in evo is much chunkier so less seems to fit in the same area.
I find the original rendering much more pleasing to the eye, however, there are sufficient improvements in evo to persuade me to accept these changes.

For comparison: Original vs Evolution ...

![Original](docs/logisim-original-sample.png)
![Evolution](docs/logisim-evolution-sample.png)

# Logism Libraries

In the end I didn't use any libraries (yet) and used just the build in components, however I spent ages faffing about looking at them.
When I come to moving to hardware I may go looking for libraries then, I don't know, at that point I might try one of the other simulators.   

In any case my thought on libs follow ..
 
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

# Getting Logism Evolution

See the downloads in guthub https://github.com/reds-heig/logisim-evolution/releases
  
  
# Yet more Simulators

## [circuitverse](https://circuitverse.org/)   

Can't recall why I was put off by this product.
 
It's online which is cool but for some reason when I was playing originally I got stuck and gave up.
There does seem to be a community of project folk have worked that you can form and extend.
Loads of existing circuits to play with and learn from.
Looking again just now after having spent two weeks with Evo I think CircuitVerse looks pretty decent; however I still can't work out how to make CircuitVerse's plot feature work (Logism's Cronogram was useful to me).  
So, CircuitVerse might be worth considering.

   
## [Digital](https://github.com/hneemann/Digital)

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

UPDATE: 11 Aug 2019 - went back and had a play with Digital. I found it difficult to work with. Specifically there is no convenient way to paste contents into the ROM component (unlike Logism Evo) so you have to edit each byte in turn or use a file. I was able to entry multibyte values despite it being an 8 bit wide EEPROM. Eventually the program errored with a null pointer exception (I have reported stack trace). So I don't think I'll be going back again for now. 

