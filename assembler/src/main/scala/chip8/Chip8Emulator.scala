package chip8

import java.io.File

import chip8.Chip8Compiler.{Line, State}

import scala.collection.mutable.ListBuffer
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

  private val rom: File = Loader.rom("WormV4.ch8")
  val asm: List[Short] = Loader.read(rom)

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

    state = state.copy(state.screen.copy(publishDrawEvent = terminalComponent.publish))

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

      val inst = rom(state.pc)
      terminalComponent.updateView(inst)

      debugHandler
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

