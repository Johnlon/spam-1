# Control Logic

The control logic decodes the 48 bit instruction coming out of the [Program ROM](program_rom.md) into the select lines that drive every other device.

There is no microcode and no instruction register. The decode is purely combinational: the program counter addresses the six ROMs, and the ROM outputs feed the decoders directly.

## What it does

- Selects which device drives ALU input bus A
- Selects which device drives ALU input bus B
- Selects which device latches the ALU result
- Passes the ALU operation to the ALU
- Decides whether the instruction executes, based on the condition
- Decides whether the status flags are updated
- Selects the RAM addressing mode
- Drives the immediate value and the direct address out of the ROM

## Instruction fields

ROM 6 holds the top byte, ROM 1 the bottom byte.

| Bits  | ROM | Field | Purpose |
|-------|-----|-------|---------|
| 47:43 | 6 | ALU op | 5 bits, one of 32 ALU operations |
| 42:39 | 6, 5 | Target device, bits 3:0 | Device that latches the ALU result |
| 38:36 | 5 | A bus device | 3 bits, device driving ALU input A |
| 35:33 | 5 | B bus device, bits 2:0 | Device driving ALU input B |
| 32:29 | 5, 4 | Condition | 4 bits, flag that must be set for the instruction to execute |
| 28 | 4 | Set flags | 1 = update the status register |
| 27 | 4 | Condition invert | 1 = execute when the flag is NOT set |
| 26 | 4 | B bus device, bit 3 | |
| 25 | 4 | Target device, bit 4 | Not decoded at present, spare |
| 24 | 4 | Address mode | 0 = register (MAR), 1 = direct |
| 23:8 | 3, 2 | Direct address | 16 bit RAM address |
| 7:0 | 1 | Immediate | 8 bit value for ALU input B |

See also [instruction bitfield](instruction_bitfield.md).

## Device selection

Each field is decoded to one active low select line per device using 74HCT138 3-to-8 decoders.

| Bus | Decoders | Lines |
|-----|----------|-------|
| A bus | one 74HCT138 | 8 |
| B bus | two 74HCT138 as a 4-to-16 | 16 |
| Target | two 74HCT138 as a 4-to-16 | 16 |

The top bit of the B and Target fields picks which of the pair of decoders is enabled.

### Device ids

| Id | A bus | B bus | Target |
|----|-------|-------|--------|
| 0 | REGA | REGA | REGA |
| 1 | REGB | REGB | REGB |
| 2 | REGC | REGC | REGC |
| 3 | REGD | REGD | REGD |
| 4 | MARLO | MARLO | MARLO |
| 5 | MARHI | MARHI | MARHI |
| 6 | UART | IMMED | UART |
| 7 | not used | RAM | RAM |
| 8 | | not used | HALT |
| 9 | | VRAM | VRAM |
| 10 | | PORT | PORT |
| 11 | | | PORTSEL |
| 12 | | | not used (NOOP) |
| 13 | | | PCHITMP |
| 14 | | | PCLO |
| 15 | | | PC |

- PCHITMP loads only the temporary high byte of the program counter.
- PCLO loads only the low byte of the program counter.
- PC loads the low byte and at the same time moves PCHITMP into the high byte.

## Conditional execution

Every instruction carries a 4 bit condition.

| Id | Condition | Meaning |
|----|-----------|---------|
| 0 | A | Always |
| 1 | C | Carry |
| 2 | Z | Zero |
| 3 | O | Overflow |
| 4 | N | Negative |
| 5 | EQ | Equal |
| 6 | NE | Not equal |
| 7 | GT | Greater than |
| 8 | LT | Less than |
| 9 | DI | UART data in ready |
| 10 | DO | UART data out ready |

How it works:

- The flags are active low and feed two 74HCT151 8-to-1 multiplexers arranged as a 16-to-1.
- The condition field picks one flag. The "Always" input is tied low so it is always met.
- A NAND gate combines the two multiplexer outputs into `_condition_met`.
- An XOR gate with the condition invert bit flips the sense, giving `_do_exec`.
- `_do_exec` drives an enable pin on both Target decoders.

So when the condition is not met no target select line goes active and nothing is written. The instruction becomes a no-op.

The invert bit makes the NE flag redundant, as NE is the same as EQ inverted.

## Flag update

The status register is updated only when both are true:

- the set flags bit is set in the instruction (the `_S` suffix in assembler)
- the instruction executes

A NAND of the set flags bit and `do_exec` generates the active low `_set_flags` line. An XOR gate used as an inverter and a NAND gate are used in place of an OR gate, which avoids adding another chip.

## Address mode

| Address mode bit | Mode | RAM address comes from |
|------------------|------|------------------------|
| 0 | Register | MARHI and MARLO |
| 1 | Direct | Bits 23:8 of the instruction |

In direct mode two 74HCT245 buffers drive the address bus from ROMs 3 and 2.

## Immediate value

A third 74HCT245 buffer drives ROM 1 onto ALU input bus B when the B bus device is IMMED.

## Chips

| Chip | Count | Use |
|------|-------|-----|
| ROM | 6 | 48 bit instruction |
| 74HCT138 | 5 | Device select decoders |
| 74HCT151 | 2 | Condition multiplexer |
| 74HCT245 | 3 | Immediate and direct address buffers |
| 74HCT00 | 1 | NAND gates |
| 74HCT86 | 1 | XOR gates |

## Gotcha

Do not write to the UART in an instruction that is conditional on DO. Writing changes the DO flag, which changes the condition, and the UART and control logic oscillate. The simulation halts with an error if a program does this.

## Verilog Models

- [Control logic](https://github.com/Johnlon/spam-1/blob/master/verilog/cpu/controller.v)
- [Device and condition ids](https://github.com/Johnlon/spam-1/blob/master/verilog/cpu/control_lines.v)
- [CPU](https://github.com/Johnlon/spam-1/blob/master/verilog/cpu/cpu.v)
