process.setMaxListeners(0)

var events = require('events');
import { abort } from "process";
import { getAllJSDocTags } from "typescript";
import { runInThisContext } from "vm";
import * as control_lines from "./control_lines";
import { Op, ADev, BDev, TDev, Cond } from "./control_lines";


 function tolist<E>(s: Set<E>): Array<E> {
    let l = new Array<E>()
    for (let p of s) {
      l.push(p)
    }
    return l
  }

/* 
  optionally slices the result in the same manner as verilog
*/
function tobin(value: number, pad: number, left?: number, right?: number) {
  if (typeof (left) === "undefined") left = pad - 1
  if (typeof (right) === "undefined") right = 0

  let v = value.toString(2);
  v = v.padStart(pad, "0")

  v = v.slice((pad - left) - 1, pad - right)

  return v
}

function tobinB(value: boolean) {
  return value ? "1" : "0"
}

enum Flag {
  Set, Keep
}
enum CInv {
  CInv, CStd
}
enum AMode {
  Dir, Reg
}

function assemble(
  rom: ROM,
  locn: number,
  aluOp: Op,
  t: TDev,
  a: ADev,
  b: BDev,
  condition: Cond,
  setFlags: Flag, // set=true
  conditionInvert: CInv, // invert=true
  amode: AMode,  // direct=true, register=false
  address: number,
  immed: number) {

  let strF = ""
    + " O:" + tobin(aluOp, 5)
    + " T:" + tobin(t, 5, 3, 0)
    + " A:" + tobin(a, 3)
    + " B:" + tobin(b, 4, 2, 0)
    + " C:" + tobin(condition, 4)
    + " F:" + tobinB(setFlags == Flag.Set)
    + " I:" + tobinB(conditionInvert == CInv.CInv)
    + " b:" + tobin(b, 4, 3, 3)
    + " t:" + tobin(t, 5, 4, 4)
    + " a:" + tobinB(amode == AMode.Dir)
    + " @:" + tobin(address, 16)
    + " #:" + tobin(immed, 8)

  //println("MC : " + strF)

  let str = strF.replaceAll(/\s.:/g, "")
  let ctrl = new Control(() => BigInt(parseInt(str, 2)))
  ctrl.dump()

  //  println("STORE @ " + locn +  " = " + strF)
  rom.set(locn, str)

  //  let ctrl1 = new Control(() => rom.getAt(locn))
  //  ctrl1.dump("!")
  return str
}

function println(str: string) {
  console.log(str);
}

function xor(a: any, b: any) {
  return !!a !== !!b;
}

function dec2bin(dec: number, pad?: number) {
  if (typeof (pad) === "undefined") pad = 8

  let v = dec.toString(2);
  v = v.padStart(pad, "0") // pad out the string to the reqd extent
  return v
}

function big2bin(big: BigInt, pad?: number) {
  if (typeof (pad) === "undefined") pad = 8

  let v = big.toString(2);
  v = v.padStart(pad, "0") // pad out the string to the reqd extent
  return v
}

/* like verilog bit selection left and right are inclusive */
function slice(dec: BigInt, width: number, top: number, bot?: number) {
  if (typeof (bot) === "undefined") bot = top
  if (top >= width) throw new Error("slice upper bound " + top + " exceeds width " + width)

  var bin = big2bin(dec, (top - bot) + 1)
  bin = bin.padStart(width, "0") // pad out the string to the reqd extent
  var part = bin.slice((bin.length - top) - 1, bin.length - bot)
  return parseInt(part, 2)
}

interface BusSrc {
  get(): number
  name: string
}



class Bus {
  name: string
  devices: BusSrc[] = []

  constructor(name: string) {
    this.name = name
  }

  attach(...devices: BusSrc[]) {
    devices.forEach(d => {
      this.devices.push(d)
    })
  }

  get(): number {
    let activeDevice = this.devices.find(device => {
      let v = device.get()
      // println(this.name + " checking " + device.name + " = " + v)
      return typeof v !== "undefined"
    }
    ) // find any device that returns a value

    let r: number = undefined
    if (typeof activeDevice !== "undefined") {
      r = activeDevice.get()
      //println("BUS "+ this.name + " = " + r + " : " + activeDevice.name)
    } else {
      //println("BUS "+ this.name + " = " + r)
    }
    return r
  }
}

class ALU {
  flags = new Set<Cond>()
  latchedFlags = new Set<Cond>()

  control: Control
  abus: Bus
  bbus: Bus
  latchedSetFlags: boolean

  constructor(clock: Clock, control: Control, abus: Bus, bbus: Bus) {
    this.control = control
    this.abus = abus
    this.bbus = bbus

    clock.on(clock.EXEC_LATCH, () => this.latch())
    clock.on(clock.EXEC_UPDATE, () => this.update())
  }


  value() {
    let ret = 0
    const op: number = this.control.alu_op()
    let a = this.abus.get()
    let b = this.bbus.get()

    let carryIn = this.flags.has(Cond.C)

    switch (+op) {
      case Op.ZERO: ret = 0
        break
      case Op.A: ret = a;
        break
      case Op.B: ret = b;
        break
      case Op.A_PLUS_B: ret = a + b
        break
      default: {
        println("alu op " + Op[control.alu_op()] + " not yet supported")
        throw new Error("alu op " + control.alu_op() + " not yet supported")
      }
    }
    return ret
  }

  get() {
    //println("ALU OUT " + ret)
    return this.value() & 0xff
  }

  latch() {
    this.latchedSetFlags = this.control.set_flags()
    println("LSF " + this.latchedSetFlags)
    let ret = this.value()

    this.latchedFlags.clear()

    this.latchedFlags.add(Cond.A)
    if (ret > 255) this.latchedFlags.add(Cond.C)
    if (ret < 0) this.latchedFlags.add(Cond.C)
    if (ret == 0) this.latchedFlags.add(Cond.Z)
    if ((ret & 0x80) != 0) this.latchedFlags.add(Cond.N)
    // !!!! NOT DONE OVERFLOW YET
    if (this.abus.get() == this.bbus.get()) this.latchedFlags.add(Cond.EQ)
    if (this.abus.get() > this.bbus.get()) this.latchedFlags.add(Cond.GT)
    if (this.abus.get() < this.bbus.get()) this.latchedFlags.add(Cond.LT)
    if (this.abus.get() != this.bbus.get()) this.latchedFlags.add(Cond.NE)
  }

  update() {
    if (this.latchedSetFlags) {
      this.flags.clear()
      this.latchedFlags.forEach(f => this.flags.add(f))
      //println(`!!!! DUMP: flags:${tolist(alu.flags).map(x => Cond[x])}`)
    }
  }


}

class ROM implements BusSrc {
  pc: PC
  name = "ROM"
  data1 = new Uint8Array(Math.pow(2, 16))
  data2 = new Uint8Array(Math.pow(2, 16))
  data3 = new Uint8Array(Math.pow(2, 16))
  data4 = new Uint8Array(Math.pow(2, 16))
  data5 = new Uint8Array(Math.pow(2, 16))
  data6 = new Uint8Array(Math.pow(2, 16))

  constructor(pc: PC) {

    let f = (~0xaa & 0xff) + 2

    this.pc = pc;
    this.data1.fill(f);
    this.data2.fill(f);
    this.data3.fill(f);
    this.data4.fill(f);
    this.data5.fill(f);
    this.data6.fill(f);
  }

  getRom(): BigInt {
    let address = this.pc.get()
    return this.getAt(address)
  }

  get() {
    let address = this.pc.get()
    return this.data1[address]
  }


  getAt(address: number) {

    //println("get ROM[" + address + "]");

    let result =
      (BigInt(this.data6[address]) << (5n * 8n)) +
      (BigInt(this.data5[address]) << (4n * 8n)) +
      (BigInt(this.data4[address]) << (3n * 8n)) +
      (BigInt(this.data3[address]) << (2n * 8n)) +
      (BigInt(this.data2[address]) << (1n * 8n)) +
      (BigInt(this.data1[address]))

    return result
  }

  immed() {
    let address = this.pc.get()
    return this.data1[address]
  }

  address() {
    let address = pc.get()
    return (this.data2[address] << 8) + this.data1[address]
  }

  // data is a 48 bit number
  set(locn: number, data: String) {
    if (data.length != 48) throw new Error("data must be 48 bits")

    let datas = data.match(/.{1,8}/g);

    this.data6[locn] = parseInt(datas[0], 2)
    this.data5[locn] = parseInt(datas[1], 2)
    this.data4[locn] = parseInt(datas[2], 2)
    this.data3[locn] = parseInt(datas[3], 2)
    this.data2[locn] = parseInt(datas[4], 2)
    this.data1[locn] = parseInt(datas[5], 2)
  }
}



class Control extends control_lines.Pins {
  rom: () => BigInt

  constructor(rom: () => BigInt) {
    super()
    this.rom = rom;
  }

  romSlice(left: number, right?: number) {
    if (typeof (right) === "undefined") right = left

    return () => {
      return slice(this.rom(), 48, left, right)
    }
  }
  romSliceB(left: number) {
    return () => {
      return slice(this.rom(), 48, left, left) == 1
    }
  }

  // instruction decoding - same names and slices as verilog for consistency
  immed = this.romSlice(7, 0)
  address = this.romSlice(23, 8)
  amode_bit = this.romSliceB(24)
  targ_dev_4 = this.romSlice(25)
  bbus_dev_3 = this.romSlice(26)
  cond_invert_bit = this.romSliceB(27)
  set_flags_bit = this.romSliceB(28)
  condition_bot = this.romSlice(31, 29)
  condition_top = this.romSlice(32)
  bbus_dev_2_0 = this.romSlice(35, 33)
  abus_dev = this.romSlice(38, 36)
  targ_dev_3_0 = this.romSlice(42, 39)
  alu_op = this.romSlice(47, 43)

  condition = () => (this.condition_top() << 4) + this.condition_bot()

  bbus_dev = () => (this.bbus_dev_3() << 3) + this.bbus_dev_2_0()
  targ_dev = () => (this.targ_dev_4() << 4) + this.targ_dev_3_0()

  condition_met() {
    return this.condition() == Cond.A || alu.flags.has(this.condition())
  }

  do_exec() {
    //println("COND INV "+ this.cond_invert_bit()) // xor
    return xor(this.condition_met(), this.cond_invert_bit())
  }

  set_flags() {
    return (this.set_flags_bit() && this.do_exec())
  } 

  addrmode_register = () => !this.amode_bit()
  addrmode_direct = () => this.amode_bit()

  // "one bit hot"
  // NOTE ! positive logic - the hardware is neg logic
  chk_t_dev(bit: TDev) {
    // println("TARG = " + this.targ_dev());
    let selected = (bit == this.targ_dev())
    if (this.do_exec()) {
      return selected
    } else {
      //println("SKIP EXEC")
      return false
    }
  }

  chk_a_dev(bit: ADev) {
    // println("ADEV = " + this.abus_dev());
    return bit == this.abus_dev()
  }
  chk_b_dev(bit: BDev) {
    // println("BDEV = " + this.bbus_dev());
    return bit == this.bbus_dev()
  }

  dump(prefix?: string) {
    if (typeof prefix == "undefined") prefix = ""

    println("DUMP: " + prefix + " " + this.rom().toString(2).padStart(48, "0"))
    println(`DUMP: ${this.targ_dev()} = ${this.abus_dev()} (${this.alu_op()}) ${this.bbus_dev()}`)
    println(`DUMP: ${TDev[this.targ_dev()]} = ${ADev[this.abus_dev()]} (${Op[this.alu_op()]}) ${BDev[this.bbus_dev()]}`)
    println(`DUMP: ex:${this.do_exec()} cond_met:${this.condition_met()}     condition: ${this.cond_invert_bit() ? "!" : ""}${Cond[this.condition()]}`)
    println(`DUMP: sf:${this.set_flags_bit()}`)
    println(`DUMP: flags:${tolist(alu.flags).map(x => Cond[x])}`)
//    println(`????? DUMP: flags:${tolist(alu.flags).map(x => Cond[x])}`)
  }

  tolist<E>(s: Set<E>) {
    let l = new Array<E>()
    for (let p of l) {
      l.push(p)
    }
    return l
  }
}

class RAM implements BusSrc {
  control: Control
  aluBus: () => number
  mar: MAR
  ram: number[] = new Array()
  name = "RAM"

  ramIn: boolean
  latchedValue: number
  latchedAddress: number

  constructor(control: Control, aluBus: () => number, mar: MAR) {
    this.control = control;
    this.aluBus = aluBus;
    this.mar = mar;

    this.ram.fill(undefined);
    Object.seal(this.ram);

    clock.on(clock.EXEC_LATCH, () => this.latch())
    clock.on(clock.EXEC_UPDATE, () => this.update())
  }

  latch() {
    this.ramIn = this.control.ram_in()()
    this.latchedValue = this.aluBus();
    this.latchedAddress = this.mar.get()
  }

  update() {
    if (this.ramIn) {
      println(`LOADING RAM[${this.latchedAddress}] <- ${this.latchedValue}`)
      this.ram[this.latchedAddress] = this.latchedValue
    }
  }

  get() {
    let address = this.mar.get()
    return this.ram[address]
  }
}

class Register implements BusSrc {
  writeEnabled: () => boolean
  outputEnabled: () => boolean
  d: () => number
  value: number = undefined
  name: string

  latchedWE: boolean
  latchedValue: number
  dump: boolean
  updated: boolean

  constructor(name: string, clock: Clock, writeEnabled: () => boolean, outputEnabled: () => boolean, d: () => number, dump: boolean) {
    this.name = name
    this.d = d
    this.writeEnabled = writeEnabled;
    this.outputEnabled = outputEnabled;
    this.dump = dump

    clock.on(clock.EXEC_LATCH, () => this.latch())
    clock.on(clock.EXEC_UPDATE, () => this.update())
  }

  latch() {
    this.latchedWE = this.writeEnabled()
    this.latchedValue = this.d();
  }
  update() {
    //println("CLK " + this.name + " : WE " + this.writeEnabled())
    if (this.latchedWE) {
      this.value = this.latchedValue
      if (this.dump) println("LOADING REG " + this.name + " <- " + this.value)
    }
  }

  get(): number {
    if (this.outputEnabled()) {
      //      println("REG " + this.name + " OUT ENABLED - OUT " + this.value);
      return this.value
    } else {
      //      println("REG " + this.name + " OUT DISABLED");
      return undefined
    }
  }
}

class DualPortRegister {
  public a: Register
  public b: Register

  constructor(
    name: string,
    clock: Clock,
    aluBus: () => number,
    we: () => boolean,
    oea: () => boolean,
    oeb: () => boolean
  ) {
    this.a = new Register(name, clock, we, oea, aluBus, true)
    this.b = new Register(name + "_b", clock, we, oeb, aluBus, false)
  }
}

class MAR {
  public lo: DualPortRegister
  public hi: DualPortRegister

  constructor(clock: Clock, control: Control, aluBus: () => number) {
    this.lo = new DualPortRegister("MARLO", clock, aluBus, control.marlo_in(), control.adev_marlo(), control.bdev_marlo())
    this.hi = new DualPortRegister("MARHI", clock, aluBus, control.marhi_in(), control.adev_marhi(), control.bdev_marhi())
  }

  // 16 bit
  get(): number {
    const hi = this.hi.a.get() << 8
    return hi + this.lo.a.get()
  }
}

class RegisterFile {
  public reg: DualPortRegister[]

  constructor(clock: Clock, control: Control, aluBus: () => number) {
    this.reg = [
      new DualPortRegister("REGA", clock, aluBus, control.rega_in(), control.adev_rega(), control.bdev_rega()),
      new DualPortRegister("REGB", clock, aluBus, control.regb_in(), control.adev_regb(), control.bdev_regb()),
      new DualPortRegister("REGC", clock, aluBus, control.regc_in(), control.adev_regc(), control.bdev_regc()),
      new DualPortRegister("REGD", clock, aluBus, control.regd_in(), control.adev_regd(), control.bdev_regd()),
    ]
  }
}

class PC {
  control: Control;
  aluBus: () => number;
  hitmp: number = 0;
  hi: number = 0;
  lo: number = 0
  jumped: boolean = false

  latchedBus: number
  pcIn: boolean
  pcloIn: boolean
  hitmpIn: boolean

  constructor(clock: Clock, control: Control, aluBus: () => number) {
    this.control = control;
    this.aluBus = aluBus;
    clock.on(clock.EXEC_LATCH, () => this.latch())
    clock.on(clock.EXEC_UPDATE, () => this.update())
    clock.on(clock.INCPC, () => this.inc())
  }

  latch() {
    this.latchedBus = this.aluBus();
    this.pcIn = this.control.pc_in()()
    this.pcloIn = this.control.pclo_in()()
    this.hitmpIn = this.control.pchitmp_in()()
  }

  update() {
    this.jumped = false

    if (this.pcIn) {
      this.hi = this.hitmp;
      this.lo = this.latchedBus;
      this.jumped = true
      println("PC LOADING HI:LO = " + this.hitmp + ":" + this.lo)
    }

    if (this.pcloIn) {
      this.lo = this.latchedBus;
      this.jumped = true
      println("PC LOADING LO = " + this.lo)
    }

    if (this.hitmpIn) {
      this.hitmp = this.latchedBus;
      println("PC LOADING HITMP = " + this.hitmp)
    }
    println(`PC dump lo:${this.lo} hi:${this.hi} hitmp:${this.hitmp}`)
  }

  get() {
    var address = (this.hi << 8) + this.lo
    /*
    var err = new Error();
    println("PC STACK " + err.stack);
    println("PC RETURNING " + address)
    println("PC HI " + this.hi)
    println("PC LO " + this.lo)
    */
    return address
  }

  cycles: number = 0
  inc() {
    if (!this.jumped) {
      var newPc = this.get() + 1
      println("PC ADVANCE : PC = " + newPc + "      Cycles = " + this.cycles)
      this.lo = newPc & 0xff
      this.hi = (newPc >> 8) & 0xff
      this.cycles++
    }
  }
}

function tohex(v: number) {
  if (typeof (v) === "undefined") return "undef"
  return v.toString(16).padStart(2, "0")
}

// Wiring and Clocking

class Clock extends events.EventEmitter {
  EXEC_LATCH = "EXEC1"
  EXEC_UPDATE = "EXEC2"
  INCPC = "INCPC"

  halted = false

  cycle() {
    println("")
    println("CYCLE")
    //println(`----- DUMP: flags:${tolist(alu.flags).map(x => Cond[x])}`)
    control.dump()

    println("--- EXEC LATCH")
    this.emit(this.EXEC_LATCH)

    println("--- EXEC UPDATE")
    this.emit(this.EXEC_UPDATE)

    if (control.halt_in()()) {
      println("!!! HALTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
      println(`!!! PC   HI:LO ${tohex(pc.hi)}:${tohex(pc.lo)} = ${pc.get()}`)
      println(`!!! MAR  HI:LO ${tohex(mar.hi.a.value)}:${tohex(mar.lo.a.value)} = ${mar.lo.a.value}`)
      println(`!!! REG      A ${tohex(regfile.reg[0].a.get())} = ${regfile.reg[0].a.get()}`)
      println(`!!! REG      B ${tohex(regfile.reg[1].a.get())} = ${regfile.reg[1].a.get()}`)
      println(`!!! REG      C ${tohex(regfile.reg[2].a.get())} = ${regfile.reg[2].a.get()}`)
      println(`!!! REG      D ${tohex(regfile.reg[3].a.get())} = ${regfile.reg[3].a.get()}`)

      this.halted = true
    }
    println("--- INCPC")
    this.emit(this.INCPC)
  }
}
const clock = new Clock()
clock.setMaxListeners(0)
clock.on(Clock.EXEC, () => println("CLOCK UP "))

const bBus = new Bus("bBus")
const aBus = new Bus("aBus")

let rom: ROM

println("SETTING UP CONTROL")
export const control = new Control(() => {
  if (typeof (rom) === "undefined") {
    println("warning : reading uninitialised rom")
    return 0n
  }
  return rom.getRom()
}
)

println("SETTING UP ALU")
const alu = new ALU(clock, control, aBus, bBus)

println("SETTING UP PC")
const pc = new PC(clock, control, () => alu.get())

println("SETTING UP ROM")
rom = new ROM(pc)

println("SETTING UP MAR")
const mar = new MAR(clock, control, () => alu.get())

println("SETTING UP REGFILE")
const regfile = new RegisterFile(clock, control, () => alu.get())

println("SETTING UP RAM")
const ram = new RAM(control, () => alu.get(), mar)


println("ATTACHING BUS")
aBus.attach(
  mar.hi.a, mar.lo.a,
  regfile.reg[0].a,
  regfile.reg[1].a,
  regfile.reg[2].a,
  regfile.reg[3].a
)
bBus.attach(mar.hi.b, mar.lo.b,
  regfile.reg[0].b,
  regfile.reg[1].b,
  regfile.reg[2].b,
  regfile.reg[3].b,
  ram,
  rom)

println("ASSEMBLING")

//  rom, locn, aluOp, t, a, b, condition, setFlags, conditionInvert, amode, address, immed

assemble(rom, 0, Op.B, TDev.marlo, ADev.nu, BDev.immed, Cond.A, Flag.Keep, CInv.CStd, AMode.Dir, 0, 0)
assemble(rom, 1, Op.B, TDev.marhi, ADev.nu, BDev.immed, Cond.A, Flag.Keep, CInv.CStd, AMode.Dir, 0, 0)
assemble(rom, 2, Op.A_PLUS_B, TDev.marlo, ADev.marlo, BDev.immed, Cond.A, Flag.Set, CInv.CStd, AMode.Dir, 0, 1)
assemble(rom, 3, Op.B, TDev.pchitmp, ADev.nu, BDev.immed, Cond.A, Flag.Keep, CInv.CStd, AMode.Dir, 0, 0)
assemble(rom, 4, Op.B, TDev.pc, ADev.nu, BDev.immed, Cond.C, Flag.Keep, CInv.CInv, AMode.Dir, 0, 2)
assemble(rom, 5, Op.B, TDev.halt, ADev.nu, BDev.immed, Cond.A, Flag.Keep, CInv.CStd, AMode.Dir, 0, 0)


while (!clock.halted) {
  clock.cycle();
}

println("-----")
println(`DEV_SELECT T:${control.targ_dev()} B:1 A:2`) // .f(control.targ_dev(), control.bbus_dev(), control.abus_dev()))
println("TARG_DEV 12 = " + control.chk_t_dev(12))
println("BBUS_DEV 1 = " + control.chk_b_dev(1))
println("ABUS_DEV 0 = " + control.chk_a_dev(0))
println("ABUS_DEV rega = " + control.adev_rega()())
println("ABUS_DEV regb = " + control.adev_regb()())
println("BBUS_DEV immed = " + control.bdev_immed()())


export function run() {
  throw new Error("run called -> DONE")
}

clock.removeAllListeners()

println("DONE")
process.exit(0)


