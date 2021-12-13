//var events = require('events');
//var c = require('./control_lines');
//import * from "./control_lines";


let ADEV_rega = 0
let ADEV_regb = 1
let ADEV_regc = 2
let ADEV_regd = 3
let ADEV_marlo = 4
let ADEV_marhi = 5
let ADEV_uart = 6
let ADEV_not_used = 7

let adev_devices = {
  ADEV_rega: ADEV_rega,
  ADEV_regb: ADEV_regb,
  ADEV_regc: ADEV_regc,
  ADEV_regd: ADEV_regd,
  ADEV_marlo: ADEV_marlo,
  ADEV_marhi: ADEV_marhi,
  ADEV_uart: ADEV_uart,
  ADEV_not_used: ADEV_not_used,
}

// B BUS
let BDEV_rega = 0
let BDEV_regb = 1
let BDEV_regc = 2
let BDEV_regd = 3
let BDEV_marlo = 4
let BDEV_marhi = 5
let BDEV_immed = 6 // IMMED READ FROM THE INSTRUCTION
let BDEV_ram = 7
let BDEV_not_used = 8 // DOES IT MAKE SENSE TO HAVE A LITERAL NOT USED DEVICE OR SELECT RAND OR CLOCK INSTEAD FOR INTANCE WHEN WE DONT CARE.
let BDEV_vram = 9
let BDEV_port = 10

let bdev_devices = {
  BDEV_rega: BDEV_rega,
  BDEV_regb: BDEV_regb,
  BDEV_regc: BDEV_regc,
  BDEV_regd: BDEV_regd,
  BDEV_marlo: BDEV_marlo,
  BDEV_marhi: BDEV_marhi,
  BDEV_immed: BDEV_immed,
  BDEV_ram: BDEV_ram,
  BDEV_not_used: BDEV_not_used,
  BDEV_vram: BDEV_vram,
  BDEV_port: BDEV_port,
}

// DEST
let TDEV_rega = 0
let TDEV_regb = 1
let TDEV_regc = 2
let TDEV_regd = 3
let TDEV_marlo = 4
let TDEV_marhi = 5
let TDEV_uart = 6
let TDEV_ram = 7
let TDEV_halt = 8
let TDEV_vram = 9
let TDEV_port = 10
let TDEV_portsel = 11
let TDEV_not_used12 = 12
let TDEV_pchitmp = 13 // only load pchitmp
let TDEV_pclo = 14     // only load pclo
let TDEV_pc = 15       // load pclo from instruction and load pchi from pchitmp

let targ_devices = {
  TDEV_rega: TDEV_rega,
  TDEV_regb: TDEV_regb,
  TDEV_regc: TDEV_regc,
  TDEV_regd: TDEV_regd,
  TDEV_marlo: TDEV_marlo,
  TDEV_marhi: TDEV_marhi,
  TDEV_uart: TDEV_uart,
  TDEV_ram: TDEV_ram,
  TDEV_halt: TDEV_halt,
  TDEV_vram: TDEV_vram,
  TDEV_port: TDEV_port,
  TDEV_portsel: TDEV_portsel,
  TDEV_not_used12: TDEV_not_used12,
  TDEV_pchitmp: TDEV_pchitmp,
  TDEV_pclo: TDEV_pclo,
  TDEV_pc: TDEV_pc,
}


/*
usage 
"asd {foo} {bar}".f( { foo: f, bar: "kkkk" } )
"asd {1} {2}".f(4 , "kkkk")
*/
/*
String.prototype.f = function (col) {
  var col = typeof col === 'object' ? col : Array.prototype.slice.call(arguments, 0);

  return this.replace(/\{\{|\}\}|\{(\w+)\}/g, function (m, n) {
    if (m == "{{") { return "{"; }
    if (m == "}}") { return "}"; }
    return col[n];
  });
};
*/

function println(str) {
  console.log(str);
}

function dec2bin(dec, pad) {
  if (typeof (pad) == "undefined") pad = 8

  let v = dec.toString(2);
  v = v.padStart(pad, "0") // pad out the string to the reqd extent
  return v
}

/* like verilog bit selection left and right are inclusive */
function slice(dec, top, bot) {
  if (typeof (bot) == "undefined") bot = top

  var bin = dec2bin(dec)
  bin = bin.padStart(top + 1, "0") // pad out the string to the reqd extent
  var part = bin.slice((bin.length - top) - 1, bin.length - bot)
  return parseInt(part, 2)
}

class Uint8 {

  constructor(v) {
    this.set(v)
  }

  set(nval) {
    this.v = nval < 0 ? (256 + (nval % 256)) : nval % 256;
    return this;
  }

  add(nval) {
    if (typeof nval == Uint8)
      this.set(this.v + nval.v)
    else
      this.set(this.v + nval)
    return this;
  }

  get() {
    return this.v;
  }
}

function uint8(x) {
  return new Uint8(x);
}

class Bus {

  constructor(name) {
    this.name = name
    this.devices = []
  }

  attach(...devices) {
    devices.forEach(d => {
      println(this.name + " : ATTACHING : " + d.constructor.name);
      this.devices.push(d)
    })
  }

  get() {
    let activeDevice = this.devices.find(device => {
      return device.get()
    }
    ) // find any definedvalue
    return activeDevice ? activeDevice.get() : undefined;
  }
}

class ALU {
  constructor() {
    this.value = 123
  }
  get() {
    return this.value
  }

}

class ROM {
  constructor(pc) {
    let f = !0xaa + 2
    this.data1 = new Uint8Array(Math.pow(2, 16))
    this.data1.fill(f);
    this.data2 = new Uint8Array(Math.pow(2, 16))
    this.data2.fill(f);
    this.data3 = new Uint8Array(Math.pow(2, 16))
    this.data3.fill(f);
    this.data4 = new Uint8Array(Math.pow(2, 16))
    this.data4.fill(f);
    this.data5 = new Uint8Array(Math.pow(2, 16))
    this.data5.fill(f);
    this.data6 = new Uint8Array(Math.pow(2, 16))
    this.data6.fill(f);
    this.pc = pc;
  }
  get() {
    let address = pc.get()

    let result = dec2bin(this.data6[address]) +
      dec2bin(this.data5[address]) +
      dec2bin(this.data4[address]) +
      dec2bin(this.data3[address]) +
      dec2bin(this.data2[address]) +
      dec2bin(this.data1[address])

    //println("ROM[" + address + "] >> " + result)
    return result
  }
}

class Control {
  constructor(rom) {
    this.rom = rom;
    this.pc = false
    this._mr = false

    // a bit accessors for the targs
    for (let prop in targ_devices) {
      this[prop.replace("TARG_", "") + "_in"] = function () {
        return this.chk_targ_dev(targ_devices[prop])
      }
    }
    for (let prop in adev_devices) {
      this[prop.replace("ADEV_", "adev_")] = function () {
        return this.chk_abus_dev(adev_devices[prop])
      }
    }
    for (let prop in bdev_devices) {
      this[prop.replace("BDEV_", "bdev_")] = function () {
        return this.chk_bbus_dev(bdev_devices[prop])
      }
    }
  }

  romSlice(left, right) {
    return () => {
      return slice(this.rom.get(), left, right)
    }
  }

  // instruction decoding - same names and slices as verilog for consistency
  immed = this.romSlice(7, 0)
  address = this.romSlice(23, 8)
  amode_bit = this.romSlice(24, 24)
  targ_dev_4 = this.romSlice(25, 25)
  bbus_dev_3 = this.romSlice(26, 26)
  cond_invert_bit = this.romSlice(27, 27)
  set_flags_bit = this.romSlice(28, 28)
  condition_bot = this.romSlice(31, 29)
  condition_top_bit = this.romSlice(32, 32)
  bbus_dev_2_0 = this.romSlice(35, 33)
  abus_dev = this.romSlice(38, 36)
  targ_dev_3_0 = this.romSlice(42, 39)
  alu_op = this.romSlice(47, 43)

  condition = () => (this.condition_top_bit() << 4) & this.condition_bot()

  bbus_dev = () => (this.bbus_dev_3() << 3) + this.bbus_dev_2_0()
  targ_dev = () => (this.targ_dev_4() << 3) + this.targ_dev_3_0()

  condition_met = () => {
    let _flags = 0b0000000000000000 // TODO: all flags set
    let cond_flag_bit = 1 << this.condition()
    return (_flags & cond_flag_bit) == 0
  }

  do_exec = () => {
    //println("COND INV "+ this.cond_invert_bit()) // xor
    return this.condition_met() ^ this.cond_invert_bit() // xor
  }

  set_flags = () => (this.set_flags_bit() & this.do_exec())

  addrmode_register = () => !this.amode_bit()
  addrmode_direct = () => this.amode_bit()

  // "one bit hot"
  // NOTE ! positive logic - the hardware is neg logic
  chk_targ_dev(bit) {
    let selected = bit == this.targ_dev()
    if (this.do_exec()) {
      //println("TARGSEL TARG_DEV=" + this.targ_dev() + " EXEC")
      return selected
    } else {
      println("TARGSEL TARG_DEV=undefned NOEXEC")
      return false
    }
  }

  chk_abus_dev(bit) {
    return bit == this.abus_dev()
    //let adev_sel = 1 << this.abus_dev()
    //return slice(adev_sel, bit) ? 0 : 1
  }
  chk_bbus_dev(bit) {
    return bit == this.bbus_dev()

    //    let bdev_sel = 1 << this.bbus_dev()
    //    println("BDEVSEL " + bdev_sel)
    //    return slice(this.bdev_sel, bit) ? 0 : 1
  }


  // signals TODO
  _MR() {
    return this._mr;
  }
  _pclo_in() {
    println("PCLO " + this.chk_targ_dev(TDEV_pc))
    return this.chk_targ_dev(TDEV_pclo)
  }
  _pchitmp_in() {
    println("PCLO " + this.chk_targ_dev(TDEV_pc))
    return this.chk_targ_dev(TDEV_pchitmp)
  }
  _pc_in() {
    println("PCLO " + this.chk_targ_dev(TDEV_pc))
    return this.chk_targ_dev(TDEV_pc)
  }

}


class RAM {

  constructor(control, aluBus, mar) {
    this.console = control;
    this.aluBus = aluBus;
    this.mar = mar;

    this.ram = new Uint8Array(Math.pow(2, 16))
    this.ram.fill(undefined);
    Object.seal(this.ram);
  }
}

class Register {
  constructor(outputEnabledFn) {
    // should be a function ref that can be called like register.outputEnabled()
    this.outputEnabled = outputEnabledFn;

    this.value = undefined
  }

  set(v) {
    this.value = v;
  }

  get() {
    if (this.outputEnabled())
      return this.value.get()
    else
      return undefined
  }
}


class MAR {
  constructor() {
    this.lo = uint8(0);
    this.hi = uint8(0);
  }
  get() {
    return (this.hi << 8) + this.lo;
  }
}

class PC {
  constructor(clock, control, aluBus) {
    this.control = control;
    this.aluBus = aluBus;
    this.hitmp = 0;
    clock.on(CLOCKUP, () => this.clk())
  }
  clk() {
    var bus = aluBus.get();


    if (!this.control._MR()) {
      println("PC RESETTING LO & HI")
      this.hi = 0
      this.lo = 0
    } else {

      var _load_pclo = this.control._pc_in() && this.control._pclo_in();

      // make sure to use hitmp before potentially updating it below
      if (!this.control._pc_in()) {
        this.hi = this.hitmp;
        this.lo = bus;
        println("PC LOADING HI:LO = " + this.hitmp + ":" + bus + " = " + this.get())
      }


      if (!_load_pclo) {
        println("PC LOADING LO = " + bus)
        this.lo = bus;
      }

      // if the low reg wasn't just loaded then clock the PC
      if (_load_pclo) {
        this.inc();
      }
    }

    if (!this.control._pchitmp_in()) {
      println("PC LOADING HITMP = " + bus)
      this.hitmp = bus;
    }

  }
  get() {
    var pc = (this.hi << 8) + this.lo
    return pc
  }

  inc() {
    var newPc = this.get() + 1
    this.lo = newPc & 0xff
    this.hi = (newPc >> 8) & 0xff
    println("PC CLOCKING = " + newPc)
    this.pc = newPc
  }
}

// Wiring and Clocking

const clock = new events.EventEmitter()
const CLOCKUP = "CLOCKUP"
const CLOCKDN = "CLOCKND"
clock.on(CLOCKUP, () => println("CLOCK UP "))

function cycle() {
  clock.emit(CLOCKUP)
  clock.emit(CLOCKDN)
}


const aluBus = new Bus("aluBus")
const bBus = new Bus("bBus")
const aBus = new Bus("aBus")
const romDataBus = new Bus("romDataBus")

const control = new Control(romDataBus)

const pc = new PC(clock, control, aluBus)

const rom = new ROM(pc)
romDataBus.attach(rom)

const mar = new MAR()
const ram = new RAM()
const alu = new ALU()

aluBus.attach(alu)

cycle();

for (let prop in targ_devices) {

  let r = control[prop.replace("TARG_", "") + "_in"]()
  println(prop + ":: " + r)
}
println("")

for (let prop in adev_devices) {
  let r = control[prop.replace("ADEV", "adev")]()
  println(prop + ":: " + r)
}
println("")

for (let prop in bdev_devices) {
  let r = control[prop.replace("BDEV", "bdev")]()
  println(prop + ":: " + r)
}

println("-----")
println("DEV_SELECT T:{0} B:{1} A:{2}".f(control.targ_dev(), control.bbus_dev(), control.abus_dev()))
println("TARG_DEV 12 = " + control.chk_targ_dev(12))
println("BBUS_DEV 1 = " + control.chk_bbus_dev(1))
println("ABUS_DEV 0 = " + control.chk_abus_dev(0))
println("ABUS_DEV rega = " + control.adev_rega())
println("ABUS_DEV regb = " + control.adev_regb())
return

console.log("aluBus = " + aluBus.get())
control.pc = true


return
cycle();
alu.value = 10
cycle();
control._mr = true
cycle();
control.pc = false
cycle();
control.pc = true
cycle();
control._mr = false
cycle();
cycle();

/*
mar.lo.set(10)
console.log(ram)
console.log("mar = " + mar.lo.get())
console.log("mar = " + (mar.lo.get()+1))

mar.lo.set(260)
console.log("mar = " + mar.lo.get())
console.log("mar = " + (mar.lo.get()+1))

mar.lo.set(-1)
console.log("mar = " + mar.lo.get())
console.log("mar = " + (mar.lo.get()+1))
console.log("mar = " + (mar.lo.add(1).get()))

console.log("pc = " + pc.get())
console.log("pc = " + pc.get())

let oe1 = false;
let reg1 = new Register( () => oe1)
reg1.set(123);

let oe2 = true;
let reg2 = new Register(() => oe2)
reg2.set(99);

let bus = new Bus();
bus.attach(reg1, reg2);

console.log("bus = " + bus.get())

console.log("1 = " + reg1.get())
console.log("2 = " + reg2.get())
*/
