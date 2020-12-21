package chip8

import chip8.Chip8Compiler.State
import chip8.Instructions.{Legacy, LoadStoreBehaviour}
import chip8.KeyMap.{isKeyPressed, keyMap}

import scala.language.{implicitConversions, postfixOps}
import scala.swing.event.Key
import scala.util.Random
import scala.util.matching.Regex

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

  val ClearScreenRegex: Regex = "00E0" r
  val ReturnSubRegex: Regex = "00EE" r
  val ObsoleteMachineJumpRegex: Regex = "0([0-9A-F][0-9A-F][0-9A-F])" r
  val JumpRegex: Regex = "1([0-9A-F][0-9A-F][0-9A-F])" r
  val GoSubRegex: Regex = "2([0-9A-F][0-9A-F][0-9A-F])" r
  val SkipIfVxEqNRegex: Regex = "3([0-9A-F])([0-9A-F][0-9A-F])" r
  val SkipIfVxNENRegex: Regex = "4([0-9A-F])([0-9A-F][0-9A-F])" r
  val SkipIfVxEqVyRegex: Regex = "5([0-9A-F])([0-9A-F])0" r
  val SetVxRegex: Regex = "6([0-9A-F])([0-9A-F][0-9A-F])" r
  val AddXPlusYCarryRegex: Regex = "8([0-9A-F])([0-9A-F])4" r
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
  val XEqRandomRegex: Regex = "C([0-9A-F])([0-9A-F][0-9A-F])" r
  val DisplayRegex: Regex = "D([0-9A-F])([0-9A-F])([0-9A-F])" r
  val DelayTimerSetRegex: Regex = "F([0-9A-F])15" r
  val DelayTimerGetRegex: Regex = "F([0-9A-F])07" r
  val SkipIfNotKeyRegex: Regex = "E([0-9A-F])A1" r
  val SkipIfKeyRegex: Regex = "E([0-9A-F])9E" r
  val FontCharacterRegex: Regex = "F([0-9A-F])29" r
  val StoreRegistersRegex: Regex = "F([0-9A-F])55" r
  val LoadRegistersRegex: Regex = "F([0-9A-F])65" r
  val StoreBCDRegex: Regex = "F([0-9A-F])33" r
  val SetSoundTimerRegex: Regex = "F([0-9A-F])18" r
  val IEqIPlusXRegex: Regex = "F([0-9A-F])1E" r
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

case class SetIndex(op: String, nnn: Int) extends Instruction {
  override def exec(state: State): State = {
    state.copy(index = nnn,
      pc = state.pc + 2)
  }
}

case class Display(op: String, xReg: U8, yReg: U8, nHeight: U8) extends Instruction {
  override def exec(state: State): State = {
    val xPos: U8 = state.register(xReg)
    val yPos: U8 = state.register(yReg)

    val st = updateScreen(state, nHeight.ubyte, xPos.ubyte, yPos.ubyte)
    st.copy(pc = state.pc + 2)
  }

  private def updateScreen(state: State, height: Int, xPos: Int, yPos: Int): State = {

    var st = state.copy(register = state.register.set(STATUS_REGISTER_VF, U8(0))) // no collision yet

    (0 until height).foreach { y =>
      val location = state.index + y
      val memory = st.memory
      val c = memory.apply(location)
      var spr: Int = c.ubyte

      (0 to 7).foreach {
        x =>
          // look at top bit
          val bit = spr & 0x80
          val isSet = bit > 0
          if (isSet) {
            val (newScreen, isErased) = st.screen.setPixel(xPos + x, yPos + y)
            st = st.copy(screen = newScreen)
            if (isErased) {
              st = st.copy(register = st.register.set(STATUS_REGISTER_VF, U8(1)))
            }
          }
          spr <<= 1
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
      set(STATUS_REGISTER_VF, if (xVal < yVal) U8(0) else U8(1)) // active low carry flag

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
      set(STATUS_REGISTER_VF, if (yVal < xVal) U8(0) else U8(1)) // active low carry flag

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
      set(STATUS_REGISTER_VF, shiftOut)

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
      set(STATUS_REGISTER_VF, shiftOut)

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
        st = st.copy(memory = st.memory.set(st.index + i, v))
    }

    st.copy(
      pc = state.pc + 2,
      index = if (LoadStoreBehaviour == Legacy) (st.index + xReg.ubyte + 1) else st.index
    )
  }
}

case class LoadRegisters(op: String, xReg: U8) extends Instruction {
  override def exec(state: State): State = {
    val data = state.memory.slice(state.index, state.index + xReg.ubyte + 1)

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
        set(state.index, U8.valueOf(i100)).
        set(state.index + 1, U8.valueOf(i10)).
        set(state.index + 2, U8.valueOf(i1)),
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
    val carry = if (newAddress > MAX_ADDRESS) U8(1) else U8(0)

    state.copy(
      index = newAddress,
      register = state.register.set(STATUS_REGISTER_VF, carry),
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

object KeyMap {
  val keyMap = Map(
    U8(0) -> Key.Key0,
    U8(1) -> Key.Key1,
    U8(2) -> Key.Key2,
    U8(3) -> Key.Key3,
    U8(4) -> Key.Key4,
    U8(5) -> Key.Key5,
    U8(6) -> Key.Key6,
    U8(7) -> Key.Key7,
    U8(8) -> Key.Key8,
    U8(9) -> Key.Key9,
    U8(10) -> Key.A,
    U8(11) -> Key.B,
    U8(12) -> Key.C,
    U8(13) -> Key.D,
    U8(14) -> Key.E,
    U8(15) -> Key.F
  )

  def isKeyPressed(state : State, key: U8): Boolean = {
     keyMap.get(key).exists {
       c8Key => state.pressedKeys.contains(c8Key)
     }
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
      register = state.register.set(xReg, U8.valueOf(result & 0xff)).set(STATUS_REGISTER_VF, U8.valueOf(result > U8.MAX_INT)),
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

