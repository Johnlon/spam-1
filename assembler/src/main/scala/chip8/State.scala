package chip8

import chip8.State.{INITIAL_PC, emptyMemory, emptyRegisters}

import scala.swing.event.Key

object State {
  val MAX_MEM = 4096

  val INITIAL_PC = 0x200
  val emptyRegisters: List[U8] = List.fill(16)(U8(0))
  val emptyMemory: List[U8] = List.fill(MAX_MEM)(U8(0))
}

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

  def push(i: Int): State = copy(stack = i +: stack)

  def pop: (State, Int) = {
    if (stack.size==0)
      sys.error("attempt to pop empty stack ")
    val popped :: tail = stack
    (copy(stack = tail), popped)
  }
}
