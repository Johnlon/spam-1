# SPAM-1 CPU - Program Counter

The original program counter was based on a register and an adder. 

![Original PC](pc_using_latch_and_adder.png)

This approach seemed to work ok, especially in Logism, when I was thinking 4 bits but with 8 bits we end up with more complexity than I was happy with.

I've since switched to a more scalable design using a presetable counter.

![Updated PC](pc_using_counter.png)

One small but important detail of the new design is the inclusion of an SR latch. With the register approach the reset to address 0x0 was asynchronous but with the counter in Logism the counter reset is synchronous. Logism's counter has a synchronous reset similar to the [74HCT163](https://www.ti.com/lit/ds/symlink/cd54hc163.pdf) , whereas the [74HCT161](https://www.ti.com/lit/ds/symlink/cd54hc163.pdf) allows an asynchronous reset. In order for the reset to be reliably transmitted to the PC in Logism (or 74163) we need to latch the reset signal until the clock gets a chance to hit the counter. We use the same clock to reset the latch.