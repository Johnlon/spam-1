/*
* BC_Text can be found here ...
*  https://github.com/stianeklund/chip8/tree/master/roms
*
* IBM Logo ...
* https://github.com/loktar00/chip8/blob/master/roms/IBM%20Logo.ch8
* also in https://gitgud.io/Dorin/pych8/-/tree/master/roms/programs
* */

import java.io.PrintStream

import Chip8Emulator._
import Util.SeqOps
import asm.EnumParserOps

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

object Chip8Compiler extends EnumParserOps with JavaTokenParsers {

  val STATUS_REGISTER_VF = 0xF

  implicit class Ops(value: String) {
    val charArray: Array[Char] = value.toCharArray

    def b32: Int = Integer.valueOf(value.substring(0, 2), 16)

    def b10: Int = Integer.valueOf(value.substring(2, 4), 16)

    def b210: Int = Integer.valueOf(value.substring(1, 4), 16)

    def b3210: Int = Integer.valueOf(value, 16)

    def hexToInt: Int = Integer.valueOf(value, 16)
  }

  val AddressRegex: Regex = "([0-9A-F][0-9A-F][0-9A-F][0-9A-F]):" r

  val ClearScreenRegex: Regex = "00E0" r
  val ReturnSubRegex: Regex = "00EE" r
  val ObsoleteMachineJumpRegex: Regex = "0([0-9A-F][0-9A-F][0-9A-F])" r
  val JumpRegex: Regex = "1([0-9A-F][0-9A-F][0-9A-F])" r
  val GoSubRegex: Regex = "2([0-9A-F][0-9A-F][0-9A-F])" r
  val SkipIfVxEqNRegex: Regex = "3([0-9A-F])([0-9A-F][0-9A-F])" r
  val SkipIfVxNENRegex: Regex = "4([0-9A-F])([0-9A-F][0-9A-F])" r
  val SkipIfVxEqVyRegex: Regex = "5([0-9A-F])([0-9A-F])0" r
  val SetVxRegex: Regex = "6([0-9A-F])([0-9A-F][0-9A-F])" r
  val AddVxRegex: Regex = "7([0-9A-F])([0-9A-F][0-9A-F])" r
  val SetXEqYRegex: Regex = "8([0-9A-F])([0-9A-F])0" r
  val XEqLogicalOrRegex: Regex = "8([0-9A-F])([0-9A-F])1" r
  val XEqLogicalAndRegex: Regex = "8([0-9A-F])([0-9A-F])2" r
  val XEqLogicalXorRegex: Regex = "8([0-9A-F])([0-9A-F])3" r
  val XEqXMinusYRegex: Regex = "8([0-9A-F])([0-9A-F])5" r
  val XEqYMinusXRegex: Regex = "8([0-9A-F])([0-9A-F])7" r
  val XShiftRightRegex: Regex = "8([0-9A-F])06" r // as per https://github.com/mwales/chip8.git ambiguity
  val XShiftLeftRegex: Regex = "8([0-9A-F])0E" r // as per https://github.com/mwales/chip8.git ambiguity
  val SkipIfVxNeVyRegex: Regex = "9([0-9A-F])([0-9A-F])0" r
  val SetIndexRegex: Regex = "A([0-9A-F][0-9A-F][0-9A-F])" r
  val DisplayRegex: Regex = "D([0-9A-F])([0-9A-F])([0-9A-F])" r
  val FontCharacterRegex: Regex = "F([0-9A-F])29" r
  val StoreRegistersRegex: Regex = "F([0-9A-F])55" r
  val LoadRegistersRegex: Regex = "F([0-9A-F])65" r

  def opCode: Parser[Instruction] = "[0-9a-fA-F]{4}".r ^^ {
    case op => op.toUpperCase match {
      case ClearScreenRegex() => ClearScreen(op)
      case JumpRegex(nnn) => Jump(op, nnn.hexToInt)
      case GoSubRegex(nnn) => GoSub(op, nnn.hexToInt)
      case ReturnSubRegex() => ReturnSub(op)
      case SkipIfVxEqNRegex(x, nn) => SkipIfXEqN(op, x.hexToInt, nn.hexToInt)
      case SkipIfVxNENRegex(xReg, nn) => SkipIfXNeN(op, xReg.hexToInt, nn.hexToInt)
      case SkipIfVxEqVyRegex(xReg, yReg) => SkipIfXEqY(op, xReg.hexToInt, yReg.hexToInt)
      case SkipIfVxNeVyRegex(xReg, yReg) => SkipIfXNeY(op, xReg.hexToInt, yReg.hexToInt)
      case SetVxRegex(xReg, nn) => SetX(op, xReg.hexToInt, nn.hexToInt)
      case AddVxRegex(xReg, nn) => AddX(op, xReg.hexToInt, nn.hexToInt)
      case SetIndexRegex(nnn) => SetIndex(op, nnn.hexToInt)
      case DisplayRegex(xReg, yReg, n) => Display(op, xReg.hexToInt, yReg.hexToInt, n.hexToInt)
      case ObsoleteMachineJumpRegex(nnn) => ObsoleteMachineJump(op, nnn.hexToInt)
      case SetXEqYRegex(xReg, yReg) => SetXEqY(op, xReg.hexToInt, yReg.hexToInt)
      case XShiftRightRegex(xReg) => XShiftRight(op, xReg.hexToInt)
      case XShiftLeftRegex(xReg) => XShiftLeft(op, xReg.hexToInt)
      case FontCharacterRegex(xReg) => FontCharacter(op, xReg.hexToInt)
      case XEqXMinusYRegex(xReg, yReg) => XEqXMinusY(op, xReg.hexToInt, yReg.hexToInt)
      case XEqYMinusXRegex(xReg, yReg) => XEqYMinusX(op, xReg.hexToInt, yReg.hexToInt)
      case XEqLogicalOrRegex(xReg, yReg) => XEqLogicalOr(op, xReg.hexToInt, yReg.hexToInt)
      case XEqLogicalAndRegex(xReg, yReg) => XEqLogicalAnd(op, xReg.hexToInt, yReg.hexToInt)
      case XEqLogicalXorRegex(xReg, yReg) => XEqLogicalXor(op, xReg.hexToInt, yReg.hexToInt)
      case StoreRegistersRegex(xReg) => StoreRegisters(op, xReg.hexToInt)
      case LoadRegistersRegex(xReg) => LoadRegisters(op, xReg.hexToInt)
      case _ => NotRecognised(op)
    }
  }

  def address: Parser[Address] = "[0-9a-hA-H]{4}:".r ^^ {
    case AddressRegex(code) => Address(Integer.valueOf(code, 16))
  }


  case class Line(addr: Address, instruction: Instruction) {
    override def toString: String = {
      s"Line( $addr  $instruction )"
    }
  }

  private var nextAddress: Address = Address(0x200)

  def line: Parser[Line] = opt(address) ~ opCode ^^ {
    case a ~ o => {
      val thisAddress = a.getOrElse(nextAddress) // two bytes per instruction
      nextAddress = thisAddress.copy(thisAddress.location + 2)
      Line(thisAddress, o)
    }
  }

  case class Address(location: Int) {
    override def toString: String = {
      val hex = location.toHexString
      val pad = "0" * (4 - hex.length)
      s"$pad$hex"
    }
  }

  case class State(
                    out: PrintStream,
                    screen: Screen,
                    pc: Int,
                    index: Int,
                    stack: Seq[Int],
                    register: Seq[Int],
                    memory: Seq[Int],
                    fontCharLocation: Int => Int) {

    def push(i: Int): State =
      copy(stack = i +: stack)

    def pop: (State, Int) = {
      val popped :: tail = stack
      (copy(stack = tail), popped)
    }
  }

  sealed trait Instruction {
    def op: String

    def exec(state: State): State
  }

  case class GoSub(op: String, nnn: Int) extends Instruction {
    override def exec(state: State): State = {
      state.push(state.pc)
        .copy(pc = nnn)
    }
  }

  case class Jump(op: String, nnn: Int) extends Instruction {
    override def exec(state: State): State = {
      state.copy(pc = nnn)
    }
  }

  case class ReturnSub(op: String) extends Instruction {
    override def exec(state: State): State = {
      val (newState, pc) = state.pop
      newState.copy(pc = pc)
    }
  }

  case class ClearScreen(op: String) extends Instruction {
    def exec(state: State): State = {
      val newState = clearScreen(state)
      refreshScreen(newState)
      newState.copy(pc = newState.pc + 2)
    }
  }

  case class SkipIfXEqN(op: String, xReg: Int, nn: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      if (xVal == nn) {
        state.copy(pc = state.pc + 4)
      } else
        state.copy(pc = state.pc + 2)
    }
  }

  case class SkipIfXNeN(op: String, xReg: Int, nn: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      if (xVal != nn) {
        state.copy(pc = state.pc + 4)
      } else
        state.copy(pc = state.pc + 2)
    }
  }

  case class SkipIfXEqY(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      if (xVal == yVal) {
        state.copy(pc = state.pc + 4)
      } else
        state.copy(pc = state.pc + 2)
    }
  }

  case class SkipIfXNeY(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      if (xVal != yVal) {
        state.copy(pc = state.pc + 4)
      } else
        state.copy(pc = state.pc + 2)
    }

  }

  case class SetX(op: String, xReg: Int, nn: Int) extends Instruction {
    override def exec(state: State): State = {
      state.copy(
        register = state.register.set(xReg, nn),
        pc = state.pc + 2)
    }

  }

  case class AddX(op: String, xReg: Int, nn: Int) extends Instruction { // Does not set carry
    override def exec(state: State): State = {
      val current = state.register(xReg)
      state.copy(
        register = state.register.set(xReg, current + nn),
        pc = state.pc + 2
      )
    }
  }

  case class SetIndex(op: String, nnn: Int) extends Instruction {
    override def exec(state: State): State = {
      state.copy(index = nnn,
        pc = state.pc + 2)
    }
  }

  case class Display(op: String, xReg: Int, yReg: Int, nHeight: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)

      val st = updateScreen(state, nHeight, xVal, yVal)
      refreshScreen(state)
      st.copy(pc = state.pc + 2)
    }

    private def updateScreen(state: State, height: Int, xVal: Int, yVal: Int): State = {

      var st = state.copy(register = state.register.set(STATUS_REGISTER_VF, 0)) // no collision yet

      (0 until height).foreach { y =>
        var spr: Int = st.memory(state.index + y)

        (0 to 7).foreach {
          x =>
            // look at top bit
            val bit = spr & 0x80
            val isSet = bit > 0
            if (isSet) {
              val (newSt, isErased) = setPixel(st, xVal + x, yVal + y)
              st = newSt
              if (isErased) {
                st = st.copy(register = st.register.set(STATUS_REGISTER_VF, 1))
              }
            }
            spr <<= 1
        }
      }
      st
    }
  }

  def refreshScreen(state: State): Unit = {
    println("--------\n")
    (0 until HEIGHT).foreach { y =>
      val str = state.screen(y).mkString(GAP)
      val lStr = f"$y%02d| $str |"

      //        state.out.println(str)
      state.out.println(lStr)
    }
  }

  case class SetXEqY(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val updatedRegs = state.register.set(yReg, xVal)
      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  case class XEqXMinusY(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      val updatedRegs = state.register.
        set(xReg, xVal-yVal).
        set(STATUS_REGISTER_VF, if (xVal >yVal) 1 else 0)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }
  case class XEqYMinusX(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      val updatedRegs = state.register.
        set(xReg, yVal-xVal).
        set(STATUS_REGISTER_VF, if (yVal >xVal) 1 else 0)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  case class XEqLogicalOr(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      val updatedRegs = state.register.
        set(xReg, yVal|xVal)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }
  case class XEqLogicalAnd(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      val updatedRegs = state.register.
        set(xReg, yVal&xVal)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  case class XEqLogicalXor(op: String, xReg: Int, yReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val xVal: Int = state.register(xReg)
      val yVal: Int = state.register(yReg)
      val updatedRegs = state.register.
        set(xReg, yVal^xVal)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  /**
   * Ambiguous instruction ...
   * See 8XY6 doc https://tobiasvl.github.io/blog/write-a-chip-8-emulator/
   * this impl ignores vy ... https://github.com/JamesGriffin/CHIP-8-Emulator/blob/master/src/chip8.cpp
   *
   * */
  case class XShiftRight(op: String, xReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val v: Int = state.register(xReg)
      val bottom = if ((v & 1) != 0) 1 else 0
      val shifted = v >> 1

      val updatedRegs = state.register.
        set(xReg, shifted).
        set(STATUS_REGISTER_VF, bottom)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  case class XShiftLeft(op: String, xReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val v: Int = state.register(xReg)
      val top = if ((v & 0x80) != 0) 1 else 0
      val shifted = v << 1

      val updatedRegs = state.register.
        set(xReg, shifted).
        set(STATUS_REGISTER_VF, top)

      state.copy(
        register = updatedRegs,
        pc = state.pc + 2)
    }
  }

  case class StoreRegisters(op: String, xReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val registerValuesToSave = state.register.take(xReg+1)

      var st = state
      registerValuesToSave.foreach {
        v =>
          val updatedMemory = st.memory.set(st.index, v)
          val nextIndex = st.index + 1
          st = st.copy(
            memory = updatedMemory,
            index = nextIndex
          )
      }

      st.copy(pc = state.pc + 2)
    }
  }
  case class LoadRegisters(op: String, xReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val data = state.memory.slice(state.index, state.index+xReg+1)

      var st = state.copy(index = state.index+xReg+1)

      data.zipWithIndex.foreach {
        case (v,i)  =>
          val updatedRegisters = st.register.set(i, v)
          st = st.copy(
            register = updatedRegisters
          )
      }

      st.copy(pc = state.pc + 2)
    }
  }

  case class FontCharacter(op: String, xReg: Int) extends Instruction {
    override def exec(state: State): State = {
      val v: Int = state.register(xReg)
      val locn = state.fontCharLocation(v)

      state.copy(
        index = locn,
        pc = state.pc + 2)
    }
  }

  case class ObsoleteMachineJump(op: String, value: Int) extends Instruction {
    override def exec(state: State): State =
      state.copy(
        pc = state.pc + 2
      )
  }


  case class NotRecognised(op: String) extends Instruction {
    override def exec(state: State): State = {
      state.out.println(this)
      state.copy(
        pc = state.pc + 2
      )
      sys.exit(1)
    }
  }

  def program: Parser[List[Line]] = rep(line) ^^ {
    fns => fns
  }


  def compile(code: List[Short]): List[Line] = {

    val prog = code.map { x => f"$x%04x" }.mkString("\n")

    parse(program, prog) match {
      case Success(matched, _) =>
        matched
      case msg: Failure =>
        sys.error(s"FAILURE: $msg ")
      case msg: Error =>
        sys.error(s"ERROR: $msg")
    }
  }
}

object C8 extends App {
  //    val asm: List[Short] = Loader.read(Loader.IBMLogo)

  // error codes https://github.com/daniel5151/AC8E/blob/master/roms/bc_test.txt
  val asm: List[Short] = Loader.read(Loader.BC_Test)

  val ast: List[Chip8Compiler.Line] = Chip8Compiler.compile(asm)
  ast.zipWithIndex.foreach(println)

  Chip8Emulator.run(ast)

}

object Chip8Emulator {

  type Screen = Seq[Seq[Char]]

  import Chip8Compiler._

  def BLANK = ' '

  def PIXEL = '#'

  def GAP = ""

  def run(program: List[Line]): Unit = {

    val rom = ListBuffer.empty[Instruction]

    val maxMem = 4096

    println("Init rom ...")
    (0 until maxMem).foreach { _ =>
      rom.append(null)
    }

    println("Init ram ...")
    val memory = ListBuffer.empty[Int] // char is unsigned
    (0 to maxMem).foreach { _ =>
      memory.append(0)
    }

    Fonts.installFonts(memory)


    // load program
    println("Loading ...")
    program.foreach {
      case Chip8Compiler.Line(i, o) =>

        rom(i.location) = o

        val nh = o.op.b32.toChar
        memory(i.location) = nh

        val nl = o.op.b10.toChar
        memory(i.location + 1) = nl
    }

    val emptyRegisters = (0 to 15).map { _ => 0 }.toList

    var state = State(
      out = System.out,
      screen = emptyScreenBuffer,
      pc = 0x200,
      index = 0,
      register = emptyRegisters,
      stack = Nil,
      memory = memory.toSeq,
      fontCharLocation = Fonts.fontCharLocation)

    println("Run ...")
    while (true) {
      val inst = rom(state.pc)
      state = inst.exec(state)
    }
  }

  def setPixel(state: State, x: Int, y: Int): (State, Boolean) = {
    import Util.SeqOps
    val xMod = x % WIDTH
    val isSet = state.screen(y)(xMod) != BLANK
    var erased = false
    val row = state.screen(y)

    val updatedRow = if (isSet) {
      erased = true
      row.set(xMod, BLANK)
    } else {
      row.set(xMod, PIXEL)
    }

    val newScreen = state.screen.set(y, updatedRow)
    (state.copy(screen = newScreen), erased)
  }


  val HEIGHT = 32
  val WIDTH = 60

  def emptyScreenBuffer: Seq[Seq[Char]] = {
    (0 until HEIGHT).map {
      d =>
        BLANK.toString * WIDTH
    }
  }


  def clearScreen(state: State): State = {
    state.copy(screen = emptyScreenBuffer)
  }

}


object Util {

  implicit class SeqOps[T](list: Seq[T]) {
    def set(position: Int, t: T): Seq[T] = {
      val buf = list.toBuffer
      buf(position) = t
      buf.toSeq
    }
  }

}


object Fonts {
  // load fonts from Ram[0] onwards
  val FontMemAddress = 0
  val FontCharWidth = 5

  def installFonts(memory: ListBuffer[Int]): Unit = {
    hexFonts.zipWithIndex.foreach {
      case (f, i) =>
        memory(FontMemAddress + i) = f
    }
  }

  def fontCharLocation(n: Int): Int = {
    FontMemAddress + (n * FontCharWidth)
  }

  def hexFonts: Seq[Char] = {
    List(
      0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
      0x20, 0x60, 0x20, 0x20, 0x70, // 1
      0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
      0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
      0x90, 0x90, 0xF0, 0x10, 0x10, // 4
      0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
      0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
      0xF0, 0x10, 0x20, 0x40, 0x40, // 7
      0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
      0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
      0xF0, 0x90, 0xF0, 0x90, 0x90, // A
      0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
      0xF0, 0x80, 0x80, 0x80, 0xF0, // C
      0xE0, 0x90, 0x90, 0x90, 0xE0, // D
      0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
      0xF0, 0x80, 0xF0, 0x80, 0x80 // F
    )
  }
}