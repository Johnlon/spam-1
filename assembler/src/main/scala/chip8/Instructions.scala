package chip8

import chip8.Instructions.{Legacy, LoadStoreBehaviour}
import chip8.KeyMap.{isKeyPressed, keyMap}

import scala.language.{implicitConversions, postfixOps}
import scala.util.Random

sealed trait Instruction {
  def op: String

  def exec(state: State): State
}

object Instructions {

  // Index register behaviour
  // https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#fx55-and-fx65-store-and-load-memory
  val Modern = 1
  val Legacy = 0
  val LoadStoreBehaviour = Modern

  def decode(state: State): Instruction = {
    val u1 = state.memory(state.pc.toInt).toInt
    val u0 = state.memory(state.pc.toInt + 1).toInt
    val op = (u1 << 8) + u0

    decode(op)
  }

  private def decode(op: Int): Instruction = {
    def m3 = op & 0xf000

    def _NNN = U12(op & 0xfff)

    def _X__ = U8.valueOf((op & 0xf00) >> 8)

    def __Y_ = U8.valueOf((op & 0xf0) >> 4)

    def ___N = U8.valueOf(op & 0x0f)

    def __NN = U8.valueOf(op & 0x0ff)

    val opS = f"$op%04x"

    m3 match {
      case 0x0000 => op match {
        case 0x00E0 => ClearScreen(opS)
        case 0x00EE => ReturnSub(opS)
        case _ => ObsoleteMachineJump(opS, _NNN)
      }
      case 0x1000 => Jump(opS, _NNN)
      case 0x2000 => GoSub(opS, _NNN)
      case 0x3000 => SkipIfXEqN(opS, _X__, __NN)
      case 0x4000 => SkipIfXNeN(opS, _X__, __NN)

      case 0x5000 if ___N == 0 => SkipIfXEqY(opS, _X__, __Y_)

      case 0x6000 => SetX(opS, _X__, __NN)
      case 0x7000 => AddX(opS, _X__, __NN)

      case 0x8000 if ___N == 0x0 => SetXEqY(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x1 => XEqLogicalOr(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x2 => XEqLogicalAnd(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x3 => XEqLogicalXor(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x4 => AddXPlusYCarry(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x5 => XEqXMinusY(opS, _X__, __Y_)
      case 0x8000 if ___N == 0x6 => XShiftRight(opS, _X__)  //  __N_ not used
      case 0x8000 if ___N == 0x7 => XEqYMinusX(opS, _X__, __Y_)
      case 0x8000 if ___N == 0xE => XShiftLeft(opS, _X__)   //  __N_ not used

      case 0x9000 if ___N == 0 => SkipIfXNeY(opS, _X__, __Y_)
      case 0xA000 => SetIndex(opS, _NNN)
      case 0xB000 => ???
      case 0xC000 => XEqRandom(opS, _X__, __NN)
      case 0xD000 => Display(opS, _X__, __Y_, ___N)
      case 0xE000 =>
        __NN.toInt match {
          case 0x9E => SkipIfKey(opS, _X__)
          case 0xA1 => SkipIfNotKey(opS, _X__)
        }

      case 0xF000 =>
        __NN.toInt match {
          case 0x07 => DelayTimerGet(opS, _X__)
          case 0x0A => WaitForKeypress(opS, _X__)
          case 0x15 => DelayTimerSet(opS, _X__)
          case 0x18 => SetSoundTimer(opS, _X__)
          case 0x1E => IEqIPlusX(opS, _X__)
          case 0x29 => FontCharacter(opS, _X__)
          case 0x33 => StoreBCD(opS, _X__)
          case 0x55 => StoreRegisters(opS, _X__)
          case 0x65 => LoadRegisters(opS, _X__)
        }
      case _ =>
        NotRecognised(opS)
    }
  }
}

case class GoSub(op: String, nnn: U12) extends Instruction {
  override def exec(state: State): State = {
    state.push(state.pc)
      .copy(pc = nnn)
  }
}

case class Jump(op: String, nnn: U12) extends Instruction {
  override def exec(state: State): State = {
    state.copy(pc = nnn)
  }
}

case class ReturnSub(op: String) extends Instruction {
  override def exec(state: State): State = {
    val (newState, pc) = state.pop
    newState.copy(pc = pc + 2)
  }
}

case class ClearScreen(op: String) extends Instruction {
  def exec(state: State): State = {
    state.
      clearScreen().
      copy(
        pc = state.pc + 2
    )
  }
}

case class SkipIfXEqN(op: String, xReg: U8, nn: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal = state.register(xReg)
    if (xVal == nn) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXNeN(op: String, xReg: U8, nn: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal = state.register(xReg)
    if (xVal != nn) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXEqY(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal = state.register(xReg)
    val yVal = state.register(yReg)
    if (xVal == yVal) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXNeY(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal = state.register(xReg)
    val yVal = state.register(yReg)
    if (xVal != yVal) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }

}

case class SetX(op: String, xReg: U8, nn: U8) extends Instruction {
  override def exec(state: State): State = {
    state.copy(
      register = state.register.set(xReg, nn),
      pc = state.pc + 2)
  }

}

case class AddX(op: String, xReg: U8, nn: U8) extends Instruction { // Does not set carry
  override def exec(state: State): State = {
    val current = state.register(xReg)
    val u = current + nn
    state.copy(
      register = state.register.set(xReg, u),
      pc = state.pc + 2
    )
  }
}

case class SetIndex(op: String, nnn: U12) extends Instruction {
  override def exec(state: State): State = {
    state.copy(index = nnn,
      pc = state.pc + 2)
  }
}

case class Display(op: String, xReg: U8, yReg: U8, nHeight: U8) extends Instruction {
  override def exec(state: State): State = {
    val xPos: U8 = state.register(xReg)
    val yPos: U8 = state.register(yReg)

    val st = drawSprite(state, nHeight.ubyte, xPos.ubyte, yPos.ubyte)
    st.copy(pc = state.pc + 2)
  }

  private def drawSprite(state: State, height: Int, xPos: Int, yPos: Int): State = {

    var st = state.copy(register = state.register.set(STATUS_REGISTER_ID, U8(0))) // no collision yet

    (0 until height).foreach { y =>
      val location = state.index + y
      val memory = st.memory
      val rowOfSprite: U8 = memory.apply(location.toInt)
      val spriteRow = rowOfSprite.ubyte

      // convert to a list of bits
      val rowBits = intTo8Bits(spriteRow)

      // scan across sprite bits in this row and paint as cells on screen
      rowBits.zipWithIndex.foreach {
        case (bit, x) =>
          // draw bit if true
          if (bit) {
            st = st.writePixel(xPos + x, yPos + y, true)
          }
      }
    }
    st
  }

}


case class SetXEqY(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val YVal: U8 = state.register(yReg)
    val updatedRegs = state.register.set(xReg, YVal)
    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqXMinusY(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal: U8 = state.register(xReg)
    val yVal: U8 = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, xVal - yVal).
      set(STATUS_REGISTER_ID, if (xVal < yVal) U8(0) else U8(1)) // active low carry flag

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqYMinusX(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal: U8 = state.register(xReg)
    val yVal: U8 = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, yVal - xVal).
      set(STATUS_REGISTER_ID, if (yVal < xVal) U8(0) else U8(1)) // active low carry flag

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalOr(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal: U8 = state.register(xReg)
    val yVal: U8 = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, yVal | xVal)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalAnd(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal: U8 = state.register(xReg)
    val yVal: U8 = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, yVal & xVal)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalXor(op: String, xReg: U8, yReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val xVal: U8 = state.register(xReg)
    val yVal: U8 = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, yVal ^ xVal)

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
case class XShiftRight(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v = state.register(xReg)
    val shiftOut: U8 = (v & 1).asOneZero
    val shifted: U8 = v >> 1

    val updatedRegs = state.register.
      set(xReg, shifted).
      set(STATUS_REGISTER_ID, shiftOut)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XShiftLeft(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v: U8 = state.register(xReg)
    val shiftOut = (v & 0x80).asOneZero
    val shifted = v << 1

    val updatedRegs = state.register.
      set(xReg, shifted).
      set(STATUS_REGISTER_ID, shiftOut)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class StoreRegisters(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val registerValuesToSave = state.register.take(xReg.ubyte + 1)

    var st = state

    registerValuesToSave.zipWithIndex.foreach {
      case (v: U8, i) =>
        st = st.copy(memory = st.memory.set(st.index.toInt + i, v))
    }

    st.copy(
      pc = state.pc + 2,
      index = if (LoadStoreBehaviour == Legacy) (st.index + xReg.ubyte + 1) else st.index
    )
  }
}

case class LoadRegisters(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val data = state.memory.slice(state.index.toInt, state.index.toInt + xReg.ubyte + 1)

    var st = state

    data.zipWithIndex.foreach {
      case (v: U8, i) =>
        st = st.copy(register = st.register.set(i, v))
    }

    st.copy(
      pc = state.pc + 2,
      index = if (LoadStoreBehaviour == Legacy) (st.index + xReg.ubyte + 1) else st.index
    )
  }
}

case class StoreBCD(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v: U8 = state.register(xReg)
    val i100 = v.ubyte / 100
    val i10 = (v.ubyte / 10) % 10
    val i1 = v.ubyte % 10

    state.copy(
      memory = state.memory.
        set(state.index.toInt, U8.valueOf(i100)).
        set(state.index.toInt + 1, U8.valueOf(i10)).
        set(state.index.toInt + 2, U8.valueOf(i1)),
      pc = state.pc + 2,
    )
  }
}

case class IEqIPlusX(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v: U8 = state.register(xReg)

    val newAddress = state.index + v.ubyte

    // https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#fx55-and-fx65-store-and-load-memory
    // later atari update V15
    val carry = if (newAddress.toInt > MEM_SIZE-1) U8(1) else U8(0)

    state.copy(
      index = newAddress,
      register = state.register.set(STATUS_REGISTER_ID, carry),
      pc = state.pc + 2,
    )
  }
}

case class XEqRandom(op: String, xReg: U8, kk: U8) extends Instruction {
  override def exec(state: State): State = {

    val rnd: Int = Random.nextBytes(1)(0)

    val r: U8 = U8.valueOf(rnd & kk.ubyte)

    state.copy(
      register = state.register.set(xReg.ubyte, r),
      pc = state.pc + 2,
    )
  }
}

case class FontCharacter(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v = state.register(xReg)
    val locn = state.fontCharLocation(v.ubyte)

    state.copy(
      index = locn,
      pc = state.pc + 2)
  }
}

// TODO: TEST ME
case class DelayTimerSet(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val v = state.register(xReg)
    state.copy(
      delayTimer = v,
      pc = state.pc + 2)
  }
}

// TODO: TEST ME
case class DelayTimerGet(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    state.copy(
      register = state.register.set(xReg, state.delayTimer),
      pc = state.pc + 2)
  }
}

// TODO: TEST ME
case class WaitForKeypress(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val maybeKe = keyMap.find {
      k =>
        isKeyPressed(state, k._1)
    }

    maybeKe.map {
      pressed =>
        state.copy(
          register = state.register.set(xReg, pressed._1),
          pc = state.pc + 2)
    }.getOrElse(
      state
    )
  }
}

case class SkipIfNotKey(op: String, xReg: U8) extends Instruction {

  override def exec(state: State): State = {
    val key: U8 = state.register(xReg)

    val k = isKeyPressed(state, key)
    val skip = if (k) 2 else 4
    state.copy(
      pc = state.pc + skip
    )
  }
}

case class SkipIfKey(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val key: U8 = state.register(xReg)

    val k = isKeyPressed(state, key)
    val skip = if (k) 4 else 2
    state.copy(
      pc = state.pc + skip
    )
  }
}

case class AddXPlusYCarry(op: String, xReg: U8, yReg: U8) extends Instruction { // Does not set carry
  override def exec(state: State): State = {
    val x = state.register(xReg)
    val y = state.register(yReg)
    val result: Int = x.ubyte + y.ubyte

    state.copy(
      register = state.register.set(xReg, U8.valueOf(result & 0xff)).set(STATUS_REGISTER_ID, U8.valueOf(result > U8.MAX_INT)),
      pc = state.pc + 2
    )
  }
}

case class SetSoundTimer(op: String, xReg: U8) extends Instruction { // Does not set carry
  override def exec(state: State): State = {
    val x = state.register(xReg)
    // TODO Implement me
    state.copy(
      pc = state.pc + 2,
      soundTimer = x
    )
  }
}


case class ObsoleteMachineJump(op: String, value: U12) extends Instruction {
  override def exec(state: State): State =
    state.copy(
      pc = state.pc + 2
    )
}


case class NotRecognised(op: String) extends Instruction {
  override def exec(state: State): State = {
    println(s"${state.pc} :  " + this)
    state.copy(
      pc = state.pc + 2
    )
    sys.exit(1)
  }
}

