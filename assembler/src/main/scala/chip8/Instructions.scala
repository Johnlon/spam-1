package chip8

import chip8.Chip8Compiler.{STATUS_REGISTER_VF, State}

import scala.language.{implicitConversions, postfixOps}
import scala.util.matching.Regex

sealed trait Instruction {
  def op: String

  def exec(state: State): State
}

object Instructions {
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
    newState.copy(pc = pc + 2)
  }
}

case class ClearScreen(op: String) extends Instruction {
  def exec(state: State): State = {
    state.copy(
      screen = state.screen.clear(),
      pc = state.pc + 2
    )
  }
}

case class SkipIfXEqN(op: String, xReg: UByte, nn: UByte) extends Instruction {
  override def exec(state: State): State = {
    val xVal = state.register.apply(xReg)
    if (xVal == nn) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXNeN(op: String, xReg: UByte, nn: UByte) extends Instruction {
  override def exec(state: State): State = {
    val xVal: Int = state.register(xReg)
    if (xVal != nn) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXEqY(op: String, xReg: UByte, yReg: UByte) extends Instruction {
  override def exec(state: State): State = {
    val xVal: Int = state.register(xReg)
    val yVal: Int = state.register(yReg)
    if (xVal == yVal) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }
}

case class SkipIfXNeY(op: String, xReg: UByte, yReg: UByte) extends Instruction {
  override def exec(state: State): State = {
    val xVal: Int = state.register(xReg)
    val yVal: Int = state.register(yReg)
    if (xVal != yVal) {
      state.copy(pc = state.pc + 4)
    } else
      state.copy(pc = state.pc + 2)
  }

}

case class SetX(op: String, xReg: UByte, nn: UByte) extends Instruction {
  override def exec(state: State): State = {
    state.copy(
      register = state.register.set(xReg, nn),
      pc = state.pc + 2)
  }

}

case class AddX(op: String, xReg: UByte, nn: UByte) extends Instruction { // Does not set carry
  override def exec(state: State): State = {
    val current = state.register(xReg)
    state.copy(
      register = state.register.set(xReg, (current + nn).toByte),
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

case class Display(op: String, xReg: UByte, yReg: UByte, nHeight: UByte) extends Instruction {
  override def exec(state: State): State = {
    val xPos: UByte = state.register(xReg)
    val yPos: UByte = state.register(yReg)

    val st = updateScreen(state, nHeight, xPos, yPos)
    st.copy(pc = state.pc + 2)
  }

  private def updateScreen(state: State, height: UByte, xPos: UByte, yPos: UByte): State = {

    var st = state.copy(register = state.register.set(STATUS_REGISTER_VF, 0)) // no collision yet

    (0 until height).foreach { y =>
      val location = state.index + y
      val memory = st.memory
      val c = memory.apply(location)
      var spr: Int = c

      (0 to 7).foreach {
        x =>
          // look at top bit
          val bit = spr & 0x80
          val isSet = bit > 0
          if (isSet) {
            val (newScreen, isErased) = st.screen.setPixel(xPos + x, yPos + y)
            st = st.copy(screen = newScreen)
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


case class SetXEqY(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val YVal: UByte = state.register(yReg)
    val updatedRegs = state.register.set(xReg, YVal)
    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqXMinusY(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val xVal: UByte = state.register(xReg)
    val yVal: UByte = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, (xVal - yVal).toByte).
      set(STATUS_REGISTER_VF, if (xVal > yVal) UByte1 else UByte0)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqYMinusX(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val xVal: UByte = state.register(xReg)
    val yVal: UByte = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, (yVal - xVal).toByte).
      set(STATUS_REGISTER_VF, if (yVal > xVal) UByte1 else UByte0)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalOr(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val xVal: UByte = state.register(xReg)
    val yVal: UByte = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, (yVal | xVal).toByte)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalAnd(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val xVal: UByte = state.register(xReg)
    val yVal: UByte = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, (yVal & xVal).toByte)

    state.copy(
      register = updatedRegs,
      pc = state.pc + 2)
  }
}

case class XEqLogicalXor(op: String, xReg: Int, yReg: Int) extends Instruction {
  override def exec(state: State): State = {
    val xVal: UByte = state.register(xReg)
    val yVal: UByte = state.register(yReg)
    val updatedRegs = state.register.
      set(xReg, (yVal ^ xVal).toByte)

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
    val v: UByte = state.register(xReg)
    val bottom = (if ((v & 1) != 0) 1 else 0).toByte
    val shifted = (v >> 1).toByte

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
    val v: UByte = state.register(xReg)
    val top = (if ((v & 0x80) != 0) 1 else 0).toByte
    val shifted = (v << 1).toByte

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
    val registerValuesToSave = state.register.take(xReg + 1)

    var st = state
    registerValuesToSave.foreach {
      v: UByte =>
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
    val data = state.memory.slice(state.index, state.index + xReg + 1)

    var st = state.copy(index = state.index + xReg + 1)

    data.zipWithIndex.foreach {
      case (v, i) =>
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
    println(s"${state.pc} :  " + this)
    state.copy(
      pc = state.pc + 2
    )
    sys.exit(1)
  }
}

