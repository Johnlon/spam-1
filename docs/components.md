# Hardware 

These components may or may mot be useful.

## Data sheets and books

[Texas Instruments classic data books as PDF](https://www.rsp-italy.it/Electronics/Databooks/Texas%20Instruments/index.htm)

## Presettable Counter Regsters

  NB 74160/74162 seem hard to find

- [74HCT161](http://www.ti.com/lit/ds/symlink/cd74hct161.pdf)  4-bit presettable synchronous binary counter; with Asynchronous reset (Error in data sheet - these are not decade counters)

- [74HCT163](http://www.ti.com/lit/ds/symlink/cd74hct161.pdf)  4-bit presettable synchronous binary counter; with Synchronous reset (Error in data sheet - these are not decade counters)

- [74HC160/74HC161/74HC162/74HC163](http://www.edutek.ltd.uk/Binaries/Datasheets/7400/74HC161.pdf) - 74LS160/74LS160 are BCD and 74LS161 and 74LS163 are binary 

- [74LS160A/74LS161A/74LS162A/74LS163A](http://www.sycelectronica.com.ar/semiconductores/74LS161-3.pdf) - 74LS160/74LS162 are BCD and 74LS161/74LS163 are binary & the 'A' variant has fewer electrical restrictions

## Registers

- [74HC377](https://assets.nexperia.com/documents/data-sheet/74HC_HCT377.pdf) 8 bit reg - convenient pin out at sides 

- [74HCT670](http://www.ti.com/lit/ds/symlink/cd74hct670.pdf) - 4x4 register file with simulataneous read/write - but not synchronous so probably need to add edge detect to it. Not common but [Mouser has 74HCT670](https://www.mouser.co.uk/ProductDetail/Texas-Instruments/CD74HCT670E?qs=sGAEpiMZZMutXGli8Ay4kCmqQhNNfHG%2FfP%252B1EEY4uvo%3D) in DIP package 

## Binary Counter / Register - Shared In/Out - curiosity

- [Understanding the 8 bit 74LS593 - Warren Toomey](https://minnie.tuhs.org/Blog/2019_04_26_Understanding_74LS593.html)
 with referenences to his successor to CrazySmallCPU called [CSCvon8](https://github.com/DoctorWkt/CSCvon8)

- [74LS592/74LS593](https://www.zpag.net/Electroniques/Datasheet/SN74LS592N.pdf) 8 bit binary counter, interestingly the 74LS593 is single port with shared In/Out and tristate 

## Shift registers

- [74HC165 using the parallel in / serial out shift register](https://iamzxlee.wordpress.com/2014/05/13/74hc165-8-bit-parallel-inserial-out-shift-register/)

- [74HC165](http://www.ti.com/lit/ds/symlink/sn74hc165.pdf) 8-Bit parallel in or serial in, with serial out Shift Registers

- [74HC585](http://www.ti.com/lit/ds/symlink/sn74hc595.pdf) 8 bit serial in parallel out shift reg - 3 state - with built in D type output latch 

- [74HCT4094](https://assets.nexperia.com/documents/data-sheet/74HC_HCT4094.pdf) 8 bit serial in with serial or parallel out

- [74LS674](http://www.ti.com/lit/ds/symlink/sn74ls673.pdf) 16 bit parallel in, serial out shift register

- [74LS673](http://www.ti.com/lit/ds/symlink/sn74ls673.pdf) 16 bit serial in, parallel out shift register

## Buffers 

- [74HCT245](https://assets.nexperia.com/documents/data-sheet/74HC_HCT245.pdf) Octal transceiver - 3 state. Has convenient pinout than the [74HCT244](https://assets.nexperia.com/documents/data-sheet/74HC_HCT244.pdf)

## ALU

- [Using the 74181](https://www.youtube.com/watch?v=Fq0MIJjlGsw)

- [74LS382](http://digsys.upc.es/ed/components/combinacionals/arithmetic/74LS381.pdf) 4 bit ALU with Cn+4 (74LS381 needs 74LS182)

## VGA 

- [NovaVga](https://static1.squarespace.com/static/545510f7e4b034f1f6ee64b3/t/56396b06e4b0dbf1a09516d5/1446603526737/novavga_rm.pdf) VGA adapter with frame buffer

- See also the "Propellor" range eg http://dangerousprototypes.com/blog/2012/06/09/parallax-propeller-retro-pocket-mini-computer/

- [MicroVGA](http://microvga.com/) - text only I think

- [VGATonic](https://hackaday.io/project/6309-vga-graphics-over-spi-and-serial-vgatonic)

## RAM / ROM

64kx16 MRAM - £11.38 - https://www.mouser.co.uk/datasheet/2/144/MR0A16A_Datasheet-1511324.pdf - non volatile 35ns - less hassle than EEPROM and faster but needs an adapter to DIP - use in place of 32k RAM / 32k ROM - and more expensive too though. Because of separate 2x8 bit tristate output enable it can work as 64kx16 or 128kx8. ??? Data sheet says 4v


## Adapters 

44 pin TSOP II to 44 pin DIP - https://www.digikey.com/product-detail/en/chip-quik-inc/PA0217/PA0217-ND/5014801

Pay more ad get free soldering - https://www.epboard.com/eproducts/protoadapter1.htm#TSOPI_TSOPIItoPGAAdapter



## Breadboard

- [Bus Board Systems BB830](https://www.mouser.co.uk/ProductDetail/BusBoard-Prototype-Systems/BB830?qs=sGAEpiMZZMtgbBHFKsFQgkU9HqdjFsiq3piHUASHS%252BU%3D)

[Ben Eater discusses this board](https://youtu.be/HtFro0UKqkk?t=810) and also fakes. There is also a great YT video by someone else that compares the various bread board brands. 

Mine come from Mouser, a distributor for Bus Board.
But mine look different to those Ben used. Mine have "_BusBoard.com_" in a small font at one end, as [shown on the manufacturer's website](https://www.busboard.com/BB830). Ben's do not have the URL but Ben's do seem to match the [image shown on Mouser](https://www.mouser.co.uk/images/busboardprototypesystems/lrg/bb830_SPL.jpg) and also on the [BB830 data sheet on Mouser](https://www.mouser.co.uk/datasheet/2/58/BPS-DAT-(BB830-KIT)-Datasheet-1282386.pdf). 

Presumably the design of the board has changed slightly since 2016; lets hope the change is cosmetic and doesn't impact the quality of the boards. This diversity in appearance might make it more difficult to identify genuine parts. 
