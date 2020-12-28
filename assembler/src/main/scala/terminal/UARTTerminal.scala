package terminal

import java.awt.Color
import java.io.{File, FileOutputStream, PrintStream}
import java.util.concurrent.atomic.AtomicInteger

import javax.swing.BorderFactory
import org.apache.commons.io.input.{Tailer, TailerListener}

import scala.swing.event._
import scala.swing.{Rectangle, _}


object UARTTerminal extends SimpleSwingApplication {
  val uartOut = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.control"

  val width = 50
  val height = 40

  val BLANKCHAR = '.'
  val BLANK = BLANKCHAR.toString

  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

  val DO_NONE: Char = 0
  val DO_CLEAR: Char = 1
  val DO_UP: Char = 2
  val DO_DOWN: Char = 3
  val DO_LEFT: Char = 4
  val DO_RIGHT: Char = 5
  val DO_ORIGIN: Char = 6
  val DO_CENTRE: Char = 7
  val DO_SETX: Char = 8
  val DO_SETY: Char = 9

  val STATE_NONE: Char = 100  // next is a direct op like "RIGHT", or a signal to start a two byte binary value op like DRAW
  val STATE_SETX: Char = 101
  val STATE_SETY: Char = 102
  val STATE_DRAW: Char = 103  // TODO THIS COULD BE START_DRAW & LENGTH TO AVOID COST OF "DRAW/CHAR","DRAW/DHAR" pairs
  var state: Char = STATE_NONE

  var data = fill()

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
    bounds = new Rectangle(50, 100, 200, 30)

    val brefresh = new Button("Reset")
    val bpaint = new Button("Paint")

    text.preferredSize = new Dimension(650, 600)
    text.font = new Font(Font.Monospaced, scala.swing.Font.Plain.id, 10)

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
    val GAP = " "

    val t = "\n" + data.map(LEFT_MARGIN + _.mkString(GAP)).mkString("\n")
    text.text = t
    //    text.repaint()
  }

  def clearScreen(): Unit = {
    (0 to height) foreach {
      h =>
        (0 to width) foreach {
          w =>
            data(h)(w) = BLANKCHAR
        }
    }
  }

  def plot(c: Char): Unit = synchronized {
    state match {
      case STATE_SETX =>
        state = STATE_NONE
        x.set(c)
      case STATE_SETY =>
        state = STATE_NONE
        y.set(c)
      case STATE_DRAW =>
        state = STATE_NONE
        val yi = y.get()
        val xi = x.get()
        data(yi)(xi) = c
      case STATE_NONE =>
        c match {
          case DO_NONE => // useful for resynchronisation - emit two 00 bytes in sequence
          case DO_CLEAR =>
            clearScreen()
          case DO_SETX =>
            state = STATE_SETX
          case DO_SETY =>
            state = STATE_SETY
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

  def fill() = {
    (0 until height).map {
      d =>
        (BLANK * width).toBuffer
    }.toBuffer
  }


}