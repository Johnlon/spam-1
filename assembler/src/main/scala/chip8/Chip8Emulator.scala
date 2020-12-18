package chip8

import chip8.Screen.paintScreen

import scala.collection.mutable.ListBuffer

object Chip8Emulator {

  import Chip8Compiler._

  def BLANK: Pixel = ' '
  def PIXEL : Pixel = '#'

  def GAP = ""

  def run(program: List[Line]): Unit = {

    val rom = ListBuffer.empty[Instruction]

    val maxMem = 4096

    println("Init rom ...")
    (0 until maxMem).foreach { _ =>
      rom.append(null)
    }

    var state = State()

    println("Init fonts ...")
    state = Fonts.installFonts(state)

    // load program
    println("Loading ...")
    program.foreach {
      case Chip8Compiler.Line(i, o) =>

        rom(i.location) = o

        val nh = o.op.b32
        val nl = o.op.b10

        val newMemory = state.memory.set(i.location, nh).set(i.location + 1, nl)
        state = state.copy(memory = newMemory)
    }

    println("Run ...")
    while (true) {
      val inst = rom(state.pc)

      val screen = state.screen
      state = inst.exec(state)

      if (screen != state.screen) {
        paintScreen(state.screen)
      }

    }
  }

}
