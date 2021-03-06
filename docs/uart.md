# UART - UM245R

SPAM-1 uses a UART development module called [UM245R](../otherdoc/UM245R-UART.pdf) that translates between USB and 8 bit parallel TTL level I/O.

<img src="board-uart.jpg" width="500">

Ths block diagram is ..

![UART](uart.png)

SPAM-1 uses UART to provide keyboard input and console or external control output.

This UART can send arbitrary 8 bit data to the outside world and some of the simulations take advantage of this to provide a primitive graphical output on a GUI terminal I've written. 
This approach has been employed to provide a user interface to the [CHIP-8 emulator](../jvmtools/programs/Chip8Emulator.scc) that I have written for SPAM-1. The emulator is written in a custom high level language SPAM-C that I've created to make development easier. 

See the [UM245R data sheet](../otherdoc/UM245R-UART.pdf) for more info on the UART. Thanks Warren Toomey for suggesting this component.

As well as the 8 bit parallel IO that the CPU core uses to integrate with the UART, the UART also provides a pair of output signal lines RXF and TXE that can be used to detect if the UART is ready to receive (data is available to read by the CPU from the UART) or the UART ready to send (can be written to by the CPU). The CPU should not attempt to read or write the UART unless these signal lines are indicating that it is safe to do so.

These two signal lines are wired into SPAM-1's control logic and are used to allow conditional jumps based on the status of these lines. For example, using the TXE line we can create a _busy wait_ loop that blocks until the UART is ready to send, at which point we would write a byte at it. Or alternatively, we can use the RXF control line to detect if input is available and then jump to handler code as needed to read the byte from the UART. 

The UM245R is actually one of the more novel pieces of the development as I say it provides console IO but also a means to drive primitive graphics by sending control codes to the UI (inspired by VT100 but utterly different impl). 

Additionally, the Verilog implmentation of the UART is pretty unique in that it is a pretty complete implementation that provides bidirectional data exchange with the running verilog CPU simulation. The UART verilog model communicates with the outside world by reading a _control file_ to decide what the CPU will read from the UART and also any data written by the CPU to the UART ends up in a _console file_. An external program like my home grown GUI terminal can interact with the control file and the console file to provide multiple IO approaches and user interfaces.

## Verilog Models

- [UM245R Development Module](../verilog/uart/um245r.v) ([tests](../verilog/uart/test.v))
- [UART Demo Program (using my _psuedo assembler_ written in Verilog that I used for CPU tests)](../verilog/cpu/demo_uart_loop.v)
