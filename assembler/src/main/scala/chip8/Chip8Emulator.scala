package chip8

import java.util.concurrent.ConcurrentLinkedQueue

import chip8.Chip8Compiler.{Line, State}
import terminal.CrapTerminal

import scala.collection.mutable.ListBuffer
import scala.swing.event.{Key, KeyEvent, KeyPressed, KeyReleased}
import scala.swing.{Frame, SimpleSwingApplication}

object Chip8Emulator extends SimpleSwingApplication {
  private val BLITZ = "BLITZ"
  private val UFO = "UFO"
  private val BLINKY = "BLINKY"
  private val TETRIS = "TETRIS"
  private val TANK = "TANK"
  private val PONG = "PONG"
  private val BC_Test = "BC_Test"

  val asm: List[Short] = Loader.read(Loader.rom("PONG"))

  val ast: List[Chip8Compiler.Line] = Chip8Compiler.compile(asm)
  ast.zipWithIndex.foreach(println)

  val emulatorThread = new Thread(new Runnable() {
    override def run(): Unit = {
      while (true) {
        Chip8Emulator.run(ast)
        System.exit(1)
        System.out.println("exit!")
      }
    }
  })

  private val terminalComponent = new CrapTerminal(
    width = Screen.WIDTH,
    height = Screen.HEIGHT,
    source = ScreenToTerminalAdaptor.source,
    receiveKey = KeypressAdaptor.registerKeypress
  )

  val terminal = terminalComponent.top

  override def top: Frame = {
    emulatorThread.start()
    terminal
  }

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

    state = state.copy(state.screen.copy(pixelListener = ScreenToTerminalAdaptor))

    println("Run ...")
    var stepMode = false

    def debugHandler: Unit = {
      if (KeypressAdaptor.pressedKeys.contains(Key.Escape)) {
        stepMode = !stepMode
        while (KeypressAdaptor.pressedKeys.contains(Key.Escape)) {
          // wait for release
        }
      }
      if (stepMode) {
        while (!KeypressAdaptor.pressedKeys.contains(Key.Enter) && !KeypressAdaptor.pressedKeys.contains(Key.Escape)) {
          // wait for key
        }
        if (KeypressAdaptor.pressedKeys.contains(Key.Escape)) {
          stepMode = !stepMode
          while (KeypressAdaptor.pressedKeys.contains(Key.Escape)) {
            // wait for release
          }
        }
        if (KeypressAdaptor.pressedKeys.contains(Key.Enter)) {
          while (KeypressAdaptor.pressedKeys.contains(Key.Enter)) {
            // wait for release
          }
        }
      }
    }

    while (true) {
      Thread.sleep(1)

      debugHandler

      val inst = rom(state.pc)
      state = inst.exec(state)

      terminalComponent.updateView(inst)
      terminalComponent.updateView(state)

      // TODO FIX TIMING AND DELAYS

      state = state.copy(
        delayTimer = decrementDelayToZero(state),
        soundTimer = decrementSoundToZero(state),
        pressedKeys = KeypressAdaptor.pressedKeys
      )

      //      if (lastState.screen != state.screen) {
      //        paintScreen(state.screen)
      //      }
    }
  }

  private def decrementDelayToZero(state: State) = {
    if (state.delayTimer > U8(0)) {
      state.delayTimer - 1
    } else
      U8(0)
  }

  private def decrementSoundToZero(state: State) = {
    if (state.soundTimer > U8(0)) {
      state.soundTimer - 1
    } else
      U8(0)
  }
}


case class WritePixel(x: Int, y: Int, set: Boolean)

object ScreenToTerminalAdaptor extends PixelListener {

  private val fifo = new ConcurrentLinkedQueue[WritePixel]()

  override def apply(x: Int, y: Int, set: Boolean): Unit = {
    fifo.add(WritePixel(x, y, set))
  }

  def source(): WritePixel = {
    fifo.poll()
  }
}

object KeypressAdaptor {

  @volatile
  private var keys = Set.empty[Key.Value]

  def pressedKeys: Set[Key.Value] = {
    keys
  }

  def registerKeypress(ke: KeyEvent): Unit = {
    ke match {
      case KeyPressed(_, k, _, _) =>
        val effK: Key.Value = keyAlternatives(k)
        keys = keys + effK
      case KeyReleased(_, k, _, _) =>
        val effK: Key.Value = keyAlternatives(k)
        keys = keys - effK
      case _ => // ignore
    }
  }

  // seek http://www.sunrise-ev.com/photos/1802/Chip8interpreter.pdf
  private def keyAlternatives(k: Key.Value): Key.Value = {
    if (k == Key.Up || k == Key.I) Key.Key2
    else if (k == Key.Left || k == Key.J) Key.Key4
    else if (k == Key.Right || k == Key.K) Key.Key6
    else if (k == Key.Down || k == Key.M) Key.Key8
    else if (k == Key.Space) Key.Key5
    else k
  }
}
