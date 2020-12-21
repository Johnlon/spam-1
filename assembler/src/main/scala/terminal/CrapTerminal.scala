package terminal

import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger

import chip8.Chip8Compiler.State
import chip8.{Chip8Compiler, Instruction, Pixel}
import javax.swing.BorderFactory
import terminal.CrapTerminal._

import scala.collection.mutable
import scala.swing.event.{Key, _}
import scala.swing.{Rectangle, _}

object CrapTerminal {

  val FONT = "Courier New"

  def BLANK: Pixel = ' '

  def PIXEL: Pixel = 0x2588.toChar

  val NO_CHAR: Char = 0

  // control
  val WRITE: Char = 'd'
  val SETX: Char = 'x'
  val SETY: Char = 'y'

  // data
  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

}

class CrapTerminal(
                    width: Int = 64,
                    height: Int = 34,
                    source: () => (Char, Char),
                    receiveKey: KeyEvent => Unit) extends SimpleSwingApplication with Publisher {
  term =>

  var last: Char = 0
  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  var screen: mutable.Seq[mutable.Buffer[Char]] = fill()
  //
  //  val brefresh = new Button("Reset")
  //  val bpaint = new Button("Paint")

  private val PaneHeight = 600

  val gameScreen = new TextArea()
  gameScreen.border = BorderFactory.createLineBorder(Color.BLUE)
  gameScreen.preferredSize = new Dimension(750, PaneHeight)
  gameScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  gameScreen.editable = false

  val stateScreen = new TextArea()
  stateScreen.border = BorderFactory.createLineBorder(Color.RED)
  stateScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  stateScreen.preferredSize = new Dimension(700, 150)
  stateScreen.editable = false
  stateScreen.focusable = false

  val instScreen = new TextArea()
  instScreen.border = BorderFactory.createLineBorder(Color.GREEN)
  instScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  instScreen.preferredSize = new Dimension(700, PaneHeight - 150)
  instScreen.editable = false
  instScreen.focusable = false

  val timer = new ScalaTimer(1000 / 60)

  var start: Long = System.currentTimeMillis()

  var count = 0

  var rate = 0.0

  def updateData(): Unit = {
    val st = state.map {
      s =>
        val registers = f"""reg: ${s.register.zipWithIndex.map { case (z, i) => f"R$i=${z.ubyte.toHexString}%2s" }.mkString(" ")}"""
        val stack = f"""stack: ${s.stack.zipWithIndex.map { case (z, i) => f"R$i=${z.toHexString}%2s" }.mkString(" ")}"""
        val index = f"""index: ${s.index}%04x"""
        val soundTimer = f"""sound timer: ${s.soundTimer.ubyte}%02x"""
        val delayTimer = f"""delay timer: ${s.delayTimer.ubyte}%02x"""
        val pc = f"""pc: ${s.pc}%04x"""
        val keys = f"""keys: ${s.pressedKeys.map { case k => f"${k}%5s" }.mkString(" ")}"""

        f"""
           |$pc
           |$registers
           |$index
           |$stack
           |$keys
           |$soundTimer
           |$delayTimer
           |""".stripMargin
    }.getOrElse("")

    stateScreen.text =
      f"""paint rate : $rate%.2f/s
         |$st
         |""".stripMargin


//    val curText = instScreen.text
//    val text = instruction.map(_.toString + "\n").getOrElse("") + curText.substring(0, Math.min(curText.length, 10000))
//    instScreen.text = text
//    instruction = None

  }

  def top: Frame = new MainFrame {
    bounds = new Rectangle(50, 100, 200, 30)

    gameScreen.requestFocus()

    listenTo(gameScreen.keys, timer, term) //, brefresh, bpaint )

    timer.start()

    reactions += {
      //      case ButtonClicked(b) if b == brefresh =>
      //        screen = fill()
      //        doRepaint()
      //
      //      case ButtonClicked(b) if b == bpaint =>
      //        doRepaint()

      case e@KeyPressed(_, _, _, _) =>
        receiveKey(e)

      case e@KeyReleased(_, _, _, _) =>
        receiveKey(e)

      case StateUpdated(state) =>


      case InstructionProcessed(inst) =>

      case TimerEvent() =>
        val now = System.currentTimeMillis()
        val c = source()

        if (c._1 != NO_CHAR) {
          count += 1

          if (count > 100) {
            val elapsed = now - start
            rate = (1000.0 * count) / elapsed

            count = 0
            start = System.currentTimeMillis()
            updateData()
          }
          plot(c)

        }

      case e@KeyReleased(_, _, _, _) =>
        receiveKey(e)

    }

    contents = new BoxPanel(Orientation.Horizontal) {
      val left: BoxPanel = new BoxPanel(Orientation.Horizontal) {
        contents ++= Seq(gameScreen)
      }
      val right: BoxPanel = new BoxPanel(Orientation.Vertical) {
        contents ++= Seq(stateScreen, instScreen)
      }
      contents ++= Seq(left, right)
    }

  }

  def doRepaint(): Unit = {
    val LeftMargin = "___"
    val t = "\n" + screen.map(LeftMargin + _.mkString(".")).mkString("\n")
    //    val t = "\n" + screen.map("   " + _.mkString(" ")).mkString("\n")
    gameScreen.text = s"""$t"""
  }

  def plot(c: (Char, Char)): Unit = synchronized {
    val (ctrl, data) = c

    ctrl match {
      case SETX =>
        x.set(data)
      case SETY =>
        y.set(data)
      case WRITE =>
        val yi = y.get()
        val xi = x.get()

        val row: mutable.Buffer[Char] = screen(yi)

        if (data == 0)
          row(xi) = BLANK
        else
          row(xi) = PIXEL

        doRepaint()
      case 0 =>
      // no data
    }
  }

  def fill(): mutable.Seq[mutable.Buffer[Char]] = {
    (0 until height).map {
      d =>
        (BLANK.toString * width).toBuffer
    }.toBuffer
  }

  @volatile
  var state: Option[State] = None
  @volatile
  var instruction: Option[Instruction] = None

  def update(updState: Chip8Compiler.State): Unit = {
    state = Some(updState)
    updateData()
  }

  def update(inst: Instruction): Unit = {
    instruction = Some(inst)
    updateData()
  }

}

case class StateUpdated(state: State) extends scala.swing.event.Event

case class InstructionProcessed(inst: Instruction) extends scala.swing.event.Event

case class TimerEvent() extends scala.swing.event.Event

class ScalaTimer(val delay: Int) extends Publisher {

  private val thread = new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        publish(TimerEvent())
        //        Thread.sleep(100)
      }
    }
  })

  def start(): Unit = {
    thread.setDaemon(true)
    thread.start()
  }
}