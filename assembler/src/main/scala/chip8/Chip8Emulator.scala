package chip8

import java.io.File

import chip8.Instructions.decode

import scala.swing.event.Key
import scala.swing.{Frame, SimpleSwingApplication}

object Chip8Emulator extends SimpleSwingApplication {
  private val BLITZ = "BLITZ"
  private val UFO = "UFO"
  private val BLINKY = "BLINKY"
  private val TETRIS = "TETRIS"
  private val TANK = "TANK"
  private val PONG = "PONG"
  private val BRIX = "BRIX"
  private val BC_Test = "BC_Test"
  private val PONG2 = "PONG2"

  private val PUZZLE = "PUZZLE"
  private val TICTAC = "TICTAC"

  private val VERS = "VERS"
  private val WIPEOFF = "WIPEOFF"
  private val kaleid = "KALEID"
  private val testProgram = "corax89__test_opcode.ch8" // DOESNT PASS TES YET
  private val AIRPLANE = "Airplane.ch8"

  private val rom: File = Loader.rom(PONG)
  val bytes: List[U8] = Loader.read(rom)
  //
  //  val ast: List[Chip8CDecoder.Line] = Chip8CDecoder.decode(bytes)
  //  ast.zipWithIndex.foreach(println)

  val emulatorThread = new Thread(new Runnable() {
    override def run(): Unit = {
      while (true) {
        Chip8Emulator.run(bytes)
        System.exit(1)
        System.out.println("exit!")
      }
    }
  })

  private val terminalComponent = new C8Terminal(
    width = Screen.WIDTH,
    height = Screen.HEIGHT,
    receiveKey = KeypressAdaptor.registerKeypress
  )

  val terminal = terminalComponent.top

  override def top: Frame = {
    emulatorThread.start()
    terminal
  }

  def run(program: List[U8]): Unit = {

    try {
      if (program.isEmpty) sys.error("program is empty")

      println("Init rom ...")

      var state = State()

      println("Init fonts ...")
      state = Fonts.installFonts(state)

      println("Loading program...")
      program.zipWithIndex.foreach {
        case (byte, z) =>

          val address = z + 0x200

          val newMemory = state.memory.set(address, byte)
          state = state.copy(memory = newMemory)
      }

      state = state.copy(state.screen.copy(publishDrawEvent = terminalComponent.publish))

      println("Run ...")
      var stepMode = false

      var lastTime = System.nanoTime()
      val stepTimeNs = (1000.0 * 1000000) / 60
      while (true) {
        // busy wait to eat up remaining time slice - to get more accurate timings
        var now = System.nanoTime()
        var remainingNs = stepTimeNs - (now - lastTime)
        while (remainingNs > 0) {
          now = System.nanoTime()
          remainingNs = stepTimeNs - (now - lastTime)
        }
        lastTime = now

        // process instructions
        val inst: Instruction = decode(state)
        terminalComponent.updateView(inst)

        stepMode = debugHandler(stepMode)

        state = inst.exec(state)
        terminalComponent.updateView(state)

        state = state.copy(
          delayTimer = decrementDelayToZero(state),
          soundTimer = decrementSoundToZero(state),
          pressedKeys = KeypressAdaptor.pressedKeys
        )

        //      if (lastState.screen != state.screen) {
        //        paintScreen(state.screen)
        //      }
      }
    } catch {
      case ex =>
        ex.printStackTrace(System.err)
        this.shutdown()
    }
  }


  def debugHandler(stepModeIn: Boolean): Boolean = {
    var stepMode = stepModeIn
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
    stepMode
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

