package chip8

import chip8.State.NullListener

import scala.swing.event.{Event, Key}

object State {
  def NullListener(event: Event): Unit = {}
}
case class State(
                  pc: Int = INITIAL_PC,
                  index: Int = 0,
                  stack: Seq[Int] = Nil,
                  register: Seq[U8] = emptyRegisters,
                  memory: Seq[U8] = emptyMemory,
                  delayTimer: U8 = U8(0),
                  soundTimer: U8 = U8(0),
                  fontCharLocation: Int => Int = Fonts.fontCharLocation,
                  pressedKeys: Set[Key.Value] = Set(),
                ) {

  if (stack.length > 16) {
    sys.error("Stack may not exceed 16 levels but got " + stack.length)
  }

  def clearScreen(): State = {
    var st = this
    (0 until SCREEN_HEIGHT) foreach { y =>
      (0 until SCREEN_WIDTH) foreach { x =>
        st = st.writePixel(x,y,false)
      }
    }
    st
  }

  def screenBuffer: Seq[U8] = {
    memory.slice(SCREEN_BUF_BOT, SCREEN_BUF_TOP+1)
  }

  // if flip = False then bit is cleared, otherwise bit is flipped
  def writePixel(x: Int, y: Int, flip: Boolean): State = {
    //x=0,y=0 is at top left of screen and is lowest point in memory
    val xMod = x % SCREEN_WIDTH
    val yMod = y % SCREEN_HEIGHT

    val offset = ((yMod * SCREEN_WIDTH) + xMod)
    val byteNum = offset / 8
    val bitNum = offset % 8

    val memoryLocation = SCREEN_BUF_BOT + byteNum
    if (memoryLocation > SCREEN_BUF_TOP) {
      sys.error(s"memory error : $memoryLocation > $SCREEN_BUF_TOP")
    }

    val bitMask: Int = 1 << bitNum
    val existingByte = memory(memoryLocation)
    val existingBitSet = (existingByte.toInt & bitMask) != 0
    val signalOverwrite = flip && existingBitSet

    val newByte = if (flip) {
      val newBit = !existingBitSet
      if (newBit)
        existingByte | bitMask // set the bit
      else
        existingByte & (~bitMask) // clear the bit
    } else {
      existingByte & (~bitMask) // clear the bit
    }

    val newReg = if (signalOverwrite)
      register.set(STATUS_REGISTER_ID, U8(1))
    else
      register

    copy(memory = memory.set(memoryLocation, newByte), register = newReg)
  }

  def push(i: Int): State = copy(stack = i +: stack)

  def pop: (State, Int) = {
    if (stack.size == 0)
      sys.error("attempt to pop empty stack ")
    val popped :: tail = stack
    (copy(stack = tail), popped)
  }
}
