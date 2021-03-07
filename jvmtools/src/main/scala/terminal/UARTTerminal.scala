package terminal

import java.awt.Color
import java.io.{File, FileOutputStream, PrintStream}
import java.util.concurrent.atomic.AtomicInteger

import javax.swing.BorderFactory
import org.apache.commons.io.input.{Tailer, TailerListener}

import scala.collection.mutable
import scala.swing.event._
import scala.swing.{Rectangle, _}


object UARTTerminal extends SimpleSwingApplication {
  val uartOut = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.control"

  val C8_SCREEN_HEIGHT = 32
  val C8_SCREEN_WIDTH = 64

  val width = C8_SCREEN_WIDTH; //50
  val height = C8_SCREEN_HEIGHT; // 40

  val BLANKCHAR = '.'
  val BLANK = BLANKCHAR.toString
  val BLOCKCHAR = 0x2588.toChar // '#'

  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

  // valid in NONE_STATE
  val GOTO_INIT_STATE: Char = 0
  val GOTO_SETX_STATE: Char = 1  // switch state
  val GOTO_SETY_STATE: Char = 2  // switch state
  val GOTO_DRAW_PIXEL_STATE: Char = 3 // switch state
  val GOTO_DRAW_BYTE_STATE: Char = 4 // switch state
  val DO_CLEAR: Char = 10
  val DO_UP: Char = 11
  val DO_DOWN: Char = 12
  val DO_LEFT: Char = 13
  val DO_RIGHT: Char = 14
  val DO_ORIGIN: Char = 15
  val DO_CENTRE: Char = 16

  sealed trait TerminalIOState
  object STATE_INIT extends TerminalIOState  // next is a direct op like "RIGHT", or a signal to start a two byte binary value op like DRAW
  object STATE_SETX  extends TerminalIOState
  object STATE_SETY  extends TerminalIOState
  object STATE_DRAW_PIXEL  extends TerminalIOState
  object STATE_DRAW_BYTE  extends TerminalIOState
  var state: TerminalIOState = STATE_INIT

  var data: mutable.Seq[mutable.Buffer[Char]] = fill()

  val text = new TextArea("Java Version: " + util.Properties.javaVersion + "\n" + "john")
  text.border = BorderFactory.createLineBorder(Color.BLUE)

  private def run(): Unit = {
    val listener = new FileListener
    Tailer.create(new File(uartOut), listener, 0, true, false, 1000)
  }

  class FileListener extends TailerListener {
    def handle(line: String): Unit = {


      if (line.trim.length > 0) {
        println("! " + line)
        val c = Integer.parseInt(line, 16)
        val char = c.toChar
        plot(char)
      }
    }

    override def init(tailer: Tailer): Unit = {}

    override def fileNotFound(): Unit = {
      //      println("file not found")
      Thread.sleep(1000)
    }

    override def fileRotated(): Unit = {
      println("file rotated")
    }

    override def handle(ex: Exception): Unit = {
      println(ex)
    }
  }

  def top: Frame = new MainFrame {
    //bounds = new Rectangle(50, 100, 300, 30)

    val brefresh = new Button("Reset")
    val bpaint = new Button("Paint")

    text.preferredSize = new Dimension(900, 600)
    text.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)

    plot(DO_CENTRE)

    listenTo(text.keys, brefresh, bpaint)

    val uartCtrl = new PrintStream(new FileOutputStream(uartControl, true))

    uartCtrl.print(f"t100000\n")
    uartCtrl.flush()

    def send(c: Char): Unit = {
      uartCtrl.print(f"x${c.toInt}%02X\n")
      uartCtrl.flush()
      println("sent " + c)
    }

    reactions += {
      case ButtonClicked(b) if b == brefresh =>
        data = fill()
        doRepaint()

      case ButtonClicked(b) if b == bpaint =>
        doRepaint()

      case KeyPressed(_, c, _, _) if c == Key.Left =>
        send(DO_LEFT)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Right =>
        send(DO_RIGHT)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Up =>
        send(DO_UP)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Down =>
        send(DO_DOWN)
        send('#')
      case _ =>
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(brefresh, bpaint, text)
    }

    //t.start()
    run()

  }


  def doRepaint(): Unit = {
    val LEFT_MARGIN = "   "
    val GAP = ""

    val t = "\n" + data.map(line => LEFT_MARGIN + duplicateChar(line).mkString(GAP)).mkString("\n")
    text.text = t
    //    text.repaint()
  }

  // double the width of the chars to make them more blocky
  private def duplicateChar(line: mutable.Buffer[Char]) = {
    line.map(x => s"$x$x")
  }

  def clearScreen(): Unit = {
    (0 until height) foreach {
      h =>
        (0 until width) foreach {
          w =>
            data(h)(w) = BLANKCHAR
        }
    }
  }

  def plot(c: Char): Unit = synchronized {
    state match {
      case STATE_SETX =>
        x.set(c)
        state = STATE_INIT
      case STATE_SETY =>
        y.set(c)
        state = STATE_INIT
      case STATE_DRAW_PIXEL =>
        val yi = y.get()
        val xi = x.get()
        data(yi)(xi) = c
        state = STATE_INIT
      case STATE_DRAW_BYTE =>
        val yi = y.get()
        val xi = x.get()
        val bits = f"${c.toBinaryString}%8s".replace(' ', '0')

        bits.zipWithIndex foreach {
          case (bit, i) =>
            val char = if (bit == '1') BLOCKCHAR else BLANKCHAR
              data(yi)(xi+i) = char
        }
        state = STATE_INIT
      case STATE_INIT =>
        c match {
          // FOLLOWING INIT CODES SIGNAL START OF 2 BYTE SEQ
          case GOTO_INIT_STATE =>
            // useful for resynchronisation - emitting two "0" bytes in sequence should push system into NONE_STATE
            // first byte either completes an existing SET/DRAW or it hits this DO_NONE_STATE noop
            state = STATE_INIT
          case GOTO_SETX_STATE =>
            state = STATE_SETX
          case GOTO_SETY_STATE =>
            state = STATE_SETY
          case GOTO_DRAW_PIXEL_STATE =>
            state = STATE_DRAW_PIXEL
          case GOTO_DRAW_BYTE_STATE =>
            state = STATE_DRAW_BYTE

          // FOLLOWING COMMANDS ARE ONE BYTE SEQ
          case DO_CLEAR =>
            clearScreen()
          case DO_ORIGIN =>
            y.set(0)
            x.set(0)
          case DO_CENTRE =>
            y.set(height / 2)
            x.set(width / 2)
          case DO_UP =>
            y.decrementAndGet()
            y.set(Math.max(0, y.get()))
          case DO_DOWN =>
            y.incrementAndGet()
            y.set(Math.min(height - 1, y.get()))
          case DO_LEFT =>
            x.decrementAndGet()
            x.set(Math.max(0, x.get()))
          case DO_RIGHT =>
            x.incrementAndGet()
            if (x.get() >= width) {
              x.set(0)
              plot(DO_DOWN)
            }
        }
    }

    doRepaint()
  }

  def fill(): mutable.Seq[mutable.Buffer[Char]] = {
    (0 until height).map {
      d =>
        (BLANK * width).toBuffer
    }.toBuffer
  }


}