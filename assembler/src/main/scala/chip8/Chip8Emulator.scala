package chip8

import java.util.concurrent.ConcurrentLinkedQueue

import terminal.CrapTerminal

import scala.collection.mutable.ListBuffer
import scala.swing.{Frame, SimpleSwingApplication}

object Chip8Emulator extends SimpleSwingApplication {
  private val TETRIS = "BLINKY"
  private val Tank = "TANK"
  private val Pong = "PONG"
  private val BC_Test = "BC_Test"

  val asm: List[Short] = Loader.read(Loader.rom(TETRIS))

  val ast: List[Chip8Compiler.Line] = Chip8Compiler.compile(asm)
  ast.zipWithIndex.foreach(println)
  val emulator = new Chip8Emulator()

  val emulatorThread = new Thread(new Runnable() {
    override def run(): Unit = {
      while (true) {
        emulator.run(ast)
        System.exit(1)
        System.out.println("exit!")
      }
    }
  })

  override def top: Frame = {

    val t = new CrapTerminal(
      width = Screen.WIDTH,
      height = Screen.HEIGHT,
      separator = " ",
      source = screenToTerminalAdaptor.source,
      receiveKey = null
    ).top

    emulatorThread.start()

    t
  }
}

class Chip8Emulator {

  import Chip8Compiler._

  val maxMem = 4096

  def run(program: List[Line]): Unit = {
    println("Init rom ...")
    val rom = ListBuffer.empty[Instruction]
    (0 until maxMem).foreach { _ =>
      rom.append(null)
    }

    var state = State()

    println("Init fonts ...")
    state = Fonts.installFonts(state)

    println("Loading ...")
    program.foreach {
      case Chip8Compiler.Line(i, o) =>

        rom(i.location) = o

        val nh = o.op.b32
        val nl = o.op.b10

        val newMemory = state.memory.set(i.location, nh).set(i.location + 1, nl)
        state = state.copy(memory = newMemory)
    }

    state = state.copy(state.screen.copy(pixelListener = screenToTerminalAdaptor))

    println("Run ...")
    var loopCount = 0
    var last = System.currentTimeMillis()

    while (true) {
      Thread.sleep(1)
      loopCount = loopCount + 1
      val now = System.currentTimeMillis()
      val elapsed = now - last

      //      if (elapsed > 1000) {
      //        val rate = loopCount / (elapsed / 1000.0)
      //
      //        println(f"rate = $rate%6.2f   count = $loopCount   took = $elapsed")
      //
      //        loopCount = 0
      //        last = now
      //      }

      val inst = rom(state.pc)
      //println(inst)
      val lastState = state
      state = inst.exec(state)

      // TODO FIX TIMING AND DELAYS
      val newDelay: U8 = if (state.delayTimer > U8(0)) {
        state.delayTimer - 1
      } else
        U8(0)

      state = state.copy(
        delayTimer = newDelay
      )

      //    System.in.read()

//      if (lastState.screen != state.screen) {
//        paintScreen(state.screen)
//      }
    }
  }
}


object screenToTerminalAdaptor extends PixelListener {
  private val fifo = new ConcurrentLinkedQueue[Int]()

  private def control(c: Char, d: Char): Int = {
    val encoded = (c << 8) | (d & 0xff)
//    println("PUSH " + encoded)
    encoded
  }

  private def decode(i: Int): (Char, Char) = {
//    if (i!=0) println("PULL " + i)

    val c = ((i & 0xff00) >> 8).toChar
    val d = (i & 0xff).toChar
    (c, d)
  }

  override def apply(x: Int, y: Int, set: Boolean): Unit = {
    def BLANK: Pixel = ' '
//    def PIXEL: Pixel =  0x2588.toChar
      def PIXEL: Pixel = '#'

    fifo.add(control(CrapTerminal.SETX, x.toChar))
    fifo.add(control(CrapTerminal.SETY, y.toChar))
    if (set)
      fifo.add(control(CrapTerminal.WRITE, PIXEL ))
    else
      fifo.add(control(CrapTerminal.WRITE, BLANK))

//    System.out.println("push buf = "+ fifo.size())
  }

  def source(): (Char, Char) = {
//    System.out.println("pop buf = "+ fifo.size())
    val i = fifo.poll()
    val tuple = decode(i)
    tuple
  }

}
