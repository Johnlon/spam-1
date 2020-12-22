package chip8

import java.awt.Color
import java.util.Objects

import chip8.C8Terminal._
import chip8.Screen.{HEIGHT, WIDTH}
import javax.swing.BorderFactory

import scala.collection.mutable
import scala.swing.event._
import scala.swing.{Rectangle, _}

object C8Terminal {

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

class C8Terminal(
                  width: Int = WIDTH,
                  height: Int = HEIGHT,
                  receiveKey: KeyEvent => Unit) extends SimpleSwingApplication with Publisher {
  term =>

  var screen: mutable.Seq[mutable.Buffer[Char]] = fill()

  private val PaneWidth = 675
  private val PaneHeight = 400
  private val BotHeight = 300
  private val statWidth = PaneWidth * 2 / 3

  val gameScreen = new TextArea()
  gameScreen.border = BorderFactory.createLineBorder(Color.BLUE)
  gameScreen.preferredSize = new Dimension(PaneWidth, PaneHeight)
  gameScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  gameScreen.editable = false

  val stateScreen = new TextArea()
  stateScreen.border = BorderFactory.createLineBorder(Color.RED)
  stateScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  stateScreen.preferredSize = new Dimension(statWidth, BotHeight)
  stateScreen.editable = false
  stateScreen.focusable = false

  val instScreen = new TextArea()
  instScreen.border = BorderFactory.createLineBorder(Color.GREEN)
  instScreen.font = new Font(FONT, scala.swing.Font.Plain.id, 10)
  instScreen.preferredSize = new Dimension(PaneWidth - statWidth, BotHeight)
  instScreen.editable = false
  instScreen.focusable = false

  //  val timer = new PlotIntervalTimer(1000 / 60)

  def updateStats(): Unit = {
    val st = state.map {
      s =>
        val regIdx = s.register.zipWithIndex
        val registers1 = printReg(regIdx.take(8))
        val registers2 = printReg(regIdx.drop(8))
        val stack = s.stack.map { d => f"$d%02x" }.mkString(" ")
        val index = s.index
        val soundTimer = s.soundTimer.ubyte.toInt
        val delayTimer = s.delayTimer.ubyte.toInt
        val pc = s.pc
        val keys = s.pressedKeys.map { k => f"$k%s" }.mkString(" ")

        f"""
           |pc          : $pc%04x
           |reg         : $registers1
           |              $registers2
           |idx         : $index%04x
           |stack       : $stack
           |keys        : $keys
           |sound timer : $soundTimer%-3d
           |delay timer : $delayTimer%-3d
           |""".stripMargin
    }

    stateScreen.text =
      f"""
         |instruction rate : $instructionRate%4d/s
         |pixel rate       : $drawRate%4d/s
         |${st.getOrElse("")}
         |""".stripMargin


    val curText = instScreen.text
    val text = instruction.
      map { i =>
        i.toString + "\n"
      }.getOrElse("") +
      curText.substring(0, Math.min(curText.length, 10000))

    instScreen.text = text
    instruction = None
  }

  private def printReg(regIdx: Seq[(chip8.U8, Int)]): String = {
    regIdx.map { case (z, i) =>
      val ubyte = z.ubyte.toInt
      f"R$i%02d=$ubyte%02x"
    }.mkString(" ")
  }

  def top: Frame = new MainFrame {
    var lastDraw: Long = System.currentTimeMillis()

    bounds = new Rectangle(50, 50, PaneWidth, PaneHeight + BotHeight)

    gameScreen.requestFocus()

    listenTo(gameScreen.keys, term)

    reactions += {

      case e@KeyPressed(_, _, _, _) =>
        receiveKey(e)

      case e@KeyReleased(_, _, _, _) =>
        receiveKey(e)

      case e@WritePixelEvent(_, _, _) =>
        drawCount += 1
        val now = System.currentTimeMillis()
        val elapsed = now - lastDraw
        drawRate = (1000 * drawCount) / (1 + elapsed)

        if (drawCount > 100) {
          drawCount = 0
          lastDraw = now
          updateStats()
        }
        plot(e)
    }

    import javax.swing.border.EmptyBorder

    contents = new BoxPanel(Orientation.Vertical) {
      val left: BoxPanel = new BoxPanel(Orientation.Vertical) {
        self: Component =>
        self.border = new EmptyBorder(10, 10, 10, 10)
        contents ++= Seq(gameScreen)
      }
      val right: BoxPanel = new BoxPanel(Orientation.Horizontal) {
        contents ++= Seq(stateScreen, instScreen)
      }
      contents ++= Seq(left, right)
    }
  }

  def doRepaint(): Unit = {
    val t = "\n" + screen.map { x =>
      x.map {
        y => s"$y$y"
      }.mkString("")
    }.mkString("\n")

    gameScreen.text = t
  }

  def plot(c: WritePixelEvent): Unit = synchronized {
    val row: mutable.Buffer[Char] = screen(c.y)

    if (c.set) {
      row(c.x) = PIXEL
    }
    else {
      row(c.x) = BLANK
    }

    doRepaint()
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

  @volatile
  private var instCount = 0
  @volatile
  private var instructionRate = 0L

  @volatile
  private var drawCount = 0
  @volatile
  private var drawRate = 0L

  def updateView(updState: State): Unit = {
    state = Some(updState)
    updateStats()
  }

  private var lastInstruction = System.currentTimeMillis()

  def updateView(inst: Instruction): Unit = {
    Objects.requireNonNull(inst)
    instruction = Some(inst)
    instCount += 1
    val elapsed = System.currentTimeMillis() - lastInstruction
    instructionRate = (1000 * instCount) / (1 + elapsed)
    if (elapsed > 1000) {
      instCount = 0
      lastInstruction = System.currentTimeMillis()
    }

    updateStats()
  }
}
