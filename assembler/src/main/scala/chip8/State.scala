package chip8

import scala.swing.event.Key

case class State(
                  screen: Screen = Screen(),
                  pc: Int = INITIAL_PC,
                  index: Int = 0,
                  stack: Seq[Int] = Nil,
                  register: Seq[U8] = emptyRegisters,
                  memory: Seq[U8] = emptyMemory,
                  delayTimer: U8 = U8(0),
                  soundTimer: U8 = U8(0),
                  fontCharLocation: Int => Int = Fonts.fontCharLocation,
                  pressedKeys: Set[Key.Value] = Set()
                ) {

  if (stack.length > 16) {
    sys.error("Stack may not exceed 16 levels but got " + stack.length)
  }

  def clearScreen(): State = {
    ???
  }

  def writeScreen(x: Int, y: Int, set: Boolean): State = {
    //x=0,y=0 is at top left of screen and is lowest point in memory
    val offset = ((y * SCREEN_WIDTH) + x) / 8
    val byteNum = offset / 8
    val bitNum = offset % 8

    val bitMask : Int = 1 << bitNum
    val existingByte = memory(byteNum)

    val existingBit = (existingByte & bitMask) != 0
    //val newMem = memory.set(offset,)
    this
  }

  def push(i: Int): State = copy(stack = i +: stack)

  def pop: (State, Int) = {
    if (stack.size == 0)
      sys.error("attempt to pop empty stack ")
    val popped :: tail = stack
    (copy(stack = tail), popped)
  }
}
