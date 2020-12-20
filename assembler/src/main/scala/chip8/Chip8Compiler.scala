package chip8

/*
* BC_Text can be found here ...
*  https://github.com/stianeklund/chip8/tree/master/roms
*
* IBM Logo ...
* https://github.com/loktar00/chip8/blob/master/roms/IBM%20Logo.ch8
* also in https://gitgud.io/Dorin/pych8/-/tree/master/roms/programs
* */

import asm.EnumParserOps
import chip8.Chip8Compiler.State.{INITIAL_PC, emptyMemory, emptyRegisters}
import chip8.Instructions._

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

object Chip8Compiler extends EnumParserOps with JavaTokenParsers {

  val AddressRegex: Regex = "([0-9A-F][0-9A-F][0-9A-F][0-9A-F]):" r

  def opCode: Parser[Instruction] = "[0-9a-fA-F]{4}".r ^^ {
    case op => op.toUpperCase match {
      case ClearScreenRegex() => ClearScreen(op)
      case JumpRegex(nnn) => Jump(op, nnn.hexToInt)
      case GoSubRegex(nnn) => GoSub(op, nnn.hexToInt)
      case ReturnSubRegex() => ReturnSub(op)
      case SkipIfVxEqNRegex(x, nn) => SkipIfXEqN(op, x.hexToByte, nn.hexToByte)
      case SkipIfVxNENRegex(xReg, nn) => SkipIfXNeN(op, xReg.hexToByte, nn.hexToByte)
      case SkipIfVxEqVyRegex(xReg, yReg) => SkipIfXEqY(op, xReg.hexToByte, yReg.hexToByte)
      case SkipIfVxNeVyRegex(xReg, yReg) => SkipIfXNeY(op, xReg.hexToByte, yReg.hexToByte)
      case SetVxRegex(xReg, nn) => SetX(op, xReg.hexToByte, nn.hexToByte)
      case AddXPlusYCarryRegex(xReg, yReg) => AddXPlusYCarry(op, xReg.hexToByte, yReg.hexToByte)
      case AddVxRegex(xReg, nn) => AddX(op, xReg.hexToByte, nn.hexToByte)
      case SetIndexRegex(nnn) => SetIndex(op, nnn.hexToInt)
      case DisplayRegex(xReg, yReg, n) => Display(op, xReg.hexToByte, yReg.hexToByte, n.hexToByte)
      case DelayTimerSetRegex(xReg) => DelayTimerSet(op, xReg.hexToByte)
      case DelayTimerGetRegex(xReg) => DelayTimerGet(op, xReg.hexToByte)
      case ObsoleteMachineJumpRegex(nnn) => ObsoleteMachineJump(op, nnn.hexToInt)
      case SetXEqYRegex(xReg, yReg) => SetXEqY(op, xReg.hexToByte, yReg.hexToByte)
      case XShiftRightRegex(xReg) => XShiftRight(op, xReg.hexToByte)
      case XShiftLeftRegex(xReg) => XShiftLeft(op, xReg.hexToByte)
      case SkipIfKeyRegex(xReg) => SkipIfKey(op, xReg.hexToByte)
      case SkipIfNotKeyRegex(xReg) => SkipIfNotKey(op, xReg.hexToByte)
      case FontCharacterRegex(xReg) => FontCharacter(op, xReg.hexToByte)
      case XEqXMinusYRegex(xReg, yReg) => XEqXMinusY(op, xReg.hexToByte, yReg.hexToByte)
      case XEqYMinusXRegex(xReg, yReg) => XEqYMinusX(op, xReg.hexToByte, yReg.hexToByte)
      case XEqLogicalOrRegex(xReg, yReg) => XEqLogicalOr(op, xReg.hexToByte, yReg.hexToByte)
      case XEqLogicalAndRegex(xReg, yReg) => XEqLogicalAnd(op, xReg.hexToByte, yReg.hexToByte)
      case XEqLogicalXorRegex(xReg, yReg) => XEqLogicalXor(op, xReg.hexToByte, yReg.hexToByte)
      case StoreRegistersRegex(xReg) => StoreRegisters(op, xReg.hexToByte)
      case LoadRegistersRegex(xReg) => LoadRegisters(op, xReg.hexToByte)
      case StoreBCDRegex(xReg) => StoreBCD(op, xReg.hexToByte)
      case IEqIPlusXRegex(xReg) => IEqIPlusX(op, xReg.hexToByte)
      case XEqRandomRegex(xReg, kk) => XEqRandom(op, xReg.hexToByte, kk.hexToByte)
      case SetSoundTimerRegex(xReg) => SetSoundTimer(op, xReg.hexToByte)
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

  private var nextAddress: Address = Address(INITIAL_PC)

  def line: Parser[Line] = opt(address) ~ opCode ^^ {
    case a ~ o => {
      val thisAddress: Address = a.getOrElse(nextAddress) // two bytes per instruction
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

  object State {
    val INITIAL_PC = 0x200
    val emptyRegisters: List[U8] = List.fill(16)(U8(0))
    val emptyMemory: List[U8] = List.fill(4096)(U8(0))
  }

  case class State(
                    screen: Screen = Screen(),
                    pc: Int = INITIAL_PC,
                    index: Int = 0,
                    stack: Seq[Int] = Nil,
                    register: Seq[U8] = emptyRegisters,
                    memory: Seq[U8] = emptyMemory,
                    delayTimer: U8 = U8(0),
                    fontCharLocation: Int => Int = Fonts.fontCharLocation) {

    if (stack.length > 16) {
      sys.error("Stack may not exceed 16 levels but got " + stack.length)
    }

    def push(i: Int): State = copy(stack = i +: stack)

    def pop: (State, Int) = {
      val popped :: tail = stack
      (copy(stack = tail), popped)
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

