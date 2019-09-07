# Hardware 

These components may or may mot be useful.

## Data sheets and books

[Texas Instruments classic data books as PDF](https://www.rsp-italy.it/Electronics/Databooks/Texas%20Instruments/index.htm)

[Logic Levels and chip types doc](https://www.onsemi.com/pub/Collateral/AN-368.pdf.pdf): _"The input high logic level of HC is the only source of incompatibility. 54HC/74HC can drive TTL easily and its input low
level is TTL compatible. Again referring to Table 1, the logic
output of the TTL type device will be recognized to be a valid
logic low (0) level, so there is no incompatibility here. Table 2
shows that the specified output drive of HC is capable of
driving many LS-TTL inputs, so there is no incompatibility
here either (although one should be aware of possible fanout
restrictions similar to that encountered when designing with
TTL)."_ 

[Phillips - Logic User Guide 1997](https://assets.nexperia.com/documents/user-manual/HCT_USER_GUIDE.pdf
)
VHC = HC but faster

## Registers

Where is the 8 bit register with +ve edge, /WE and tristate!!

|Chip|Org|Trig|Input Enable|Tristate|Pinout|Other|Desc|
|--|----|--|--|--|--|--|--|
|[74HC574](https://assets.nexperia.com/documents/data-sheet/74HC_HCT574.pdf)|8|+ve||/OE|bus||8 bit reg - edge triggered - tristate|
|[74HC273](https://assets.nexperia.com/documents/data-sheet/74HC_HCT273.pdf)|8|+ve|||bus|/RESET|8 bit reg - convenient pin out at sides 
|[74HC377](https://assets.nexperia.com/documents/data-sheet/74HC_HCT377.pdf)|8|+ve|/E||pairs|| 8 bit reg - convenient pin out at sides - includes write enable 
|[74HC646](https://assets.nexperia.com/documents/data-sheet/74HC_HCT646_CNV.pdf)|8 bi-dir|+ve|no - see note|/OE|wrap|direction|interestng chip but this [other datasheet](http://noel.feld.cvut.cz/hw/motorola/books/dl129/pdf/mc74hc646rev6.pdf) carries an important caveat.. "_The user should note that because the clocks are not gated with the Direction and Output Enable pins, data at the A and B ports may be clocked into the storage flip–flops at any time"_ - so no Write enable facility.|

These 4 bit registers have it all; +ve edge, write enable and output tristate.

[74HC173](https://assets.nexperia.com/documents/data-sheet/74HC_HCT173.pdf)

20Bit (2x10) TSOP like a wide low voltage 74574

[74ALVCH16821DGG](https://assets.nexperia.com/documents/data-sheet/74ALVCH16821.pdf) (can leave unused disconnected)

## Register Files

|Chip|Org|Trig|Input Enable|Tristate|Pinout|Other|Desc|
|--|----|--|--|--|--|--|--|
|[74HC670 & HCT](http://www.ti.com/lit/ds/symlink/cd74hct670.pdf)|4x4 transparent|/W or /R||/GR|wrap||4x4 register file with simulataneous read/write - but not synchronous so probably need to add edge detect to it. Not common but [Mouser has 74HCT670](https://www.mouser.co.uk/ProductDetail/Texas-Instruments/CD74HCT670E?qs=sGAEpiMZZMutXGli8Ay4kCmqQhNNfHG%2FfP%252B1EEY4uvo%3D) in DIP package 
|[MC14580](http://radio-hobby.org/uploads/datasheets/mc/mc14580.pdf)|4bit-in, 2x4-out|+ve|WE|yes each side|wrap||4x4 three port register file - perfect shape for a register file behind an ALU, but haven't looked closely at the clocking. However data sheet indicates that it is very slow 1.2us for some ops. Useful for low complexity circuit that's not trying to be fast. 
|[74ALC870](http://pdf.dzsc.com/88889/18493.pdf)|dual 16x4 universal|level|function|when writing|sides||register file - multifunction - high capability - impossible to find|
|[MC10H145](http://pdf.datasheetcatalog.com/datasheet/on_semiconductor/MC10H145-D.PDF)|16x4|level|/WE||wrap|/CS driver output low|small fast RAM - on EBay |
|[9338](https://pdf1.alldatasheet.com/datasheet-pdf/view/835569/TI1/9338.html)|1bit-in, 2x1bit out|+ve|||wrap|Tripe port, two stage, slave enable to delay propagation|8 bit multple port register [available at Utsource](https://uk.utsource.net/itm/p/6909560.html) - 5v|
|[74F410](http://pdf.datasheetcatalog.com/datasheet/nationalsemiconductor/DS009538.PDF)|16x4 latched|+ve|/WE|/OE|bus|/CS chip select|Looks like everything for a 4bit reg! 5v VCC, output hi 2.4v/2.7v, so less than 74HC 3.2v but ok for 74HCT 2.0v (at [UTsource](https://www.ti.com/lit/ds/symlink/cd74hc670.pdf)|
|[74LS170](https://utcdn.utsource.info/pdfjs/index.html?119732_ETC_74LS170)|4x4 transparent|level|/WE|/R|wrap|open collector so can level shift output|like 74670 but open collector|
Other interesing chips include : 74172 
|[DM85S68N](http://www.icpdf.com/download.asp?id=1716908_142824)|16x4 clocked in, async out|+ve|/WE|OD (output sidable)|/OS (output store)|???|DM85S/DM75S - 5v - interesting shape but only one but annoying timings as address must have address in place before OS signal|


## Presettable Counter Regsters

  NB 74160/74162 seem hard to find

- [74HCT161](http://www.ti.com/lit/ds/symlink/cd74hct161.pdf)  4-bit presettable synchronous binary counter; with Asynchronous reset (Error in data sheet - these are not decade counters)

- [74HCT163](http://www.ti.com/lit/ds/symlink/cd74hct161.pdf)  4-bit presettable synchronous binary counter; with Synchronous reset (Error in data sheet - these are not decade counters)

- [74HC160/74HC161/74HC162/74HC163](http://www.edutek.ltd.uk/Binaries/Datasheets/7400/74HC161.pdf) - 74LS160/74LS160 are BCD and 74LS161 and 74LS163 are binary 

- [74LS160A/74LS161A/74LS162A/74LS163A](http://www.sycelectronica.com.ar/semiconductores/74LS161-3.pdf) - 74LS160/74LS162 are BCD and 74LS161/74LS163 are binary & the 'A' variant has fewer electrical restrictions

## Binary Counter combined Register - Shared In/Out - curiosity

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

- [74LVC16240](https://assets.nexperia.com/documents/data-sheet/74LVC16240A.pdf) - 16 bit 5v tolerant low voltage buffer - might be useful at some point? 3.6v VCC (6.5v absolute max), input voltage up to 5.5v, output voltage Vcc, but withstand 5.5v when Z. TSOP48

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
