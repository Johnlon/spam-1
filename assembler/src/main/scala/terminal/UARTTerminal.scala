package terminal

import java.awt.Color
import java.io.{File, FileOutputStream, PrintStream}
import java.util.concurrent.atomic.AtomicInteger

import javax.swing.BorderFactory
import org.apache.commons.io.input.{Tailer, TailerListener}

import scala.swing.event._
import scala.swing.{Rectangle, _}

object UARTTerminal extends SimpleSwingApplication   {
  val uartOut = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.control"

  val width = 50

  val height = 40
  val BLANK = "."

  var last: Char = 0
  var x = new AtomicInteger(0)

  var y = new AtomicInteger(0)

  val UP: Char = 2
  val DOWN: Char = 3
  val LEFT: Char = 4
  val RIGHT: Char = 5

  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

  val ORIGIN: Char = 6
  val CENTRE: Char = 7
  val SETX: Char = 8

  val SETY: Char = 9

  var data = fill()

  val text = new TextArea("Java Version: " + util.Properties.javaVersion + "\n" + "john")
  text.border = BorderFactory.createLineBorder(Color.BLUE)

  private def run(): Unit = {
    val listener = new FileListener
    Tailer.create(  new File(uartOut), listener, 0, true, false, 1000)
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

    plot(CENTRE)

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
        send(LEFT)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Right =>
        send(RIGHT)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Up =>
        send(UP)
        send('#')
      case KeyPressed(_, c, _, _) if c == Key.Down =>
        send(DOWN)
        send('#')
      case _ =>
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(brefresh, bpaint, text)
    }

    //t.start()
    run()

  }


  def doRepaint(): Unit ={
    val t = "\n" + data.map("   " + _.mkString(" ")).mkString("\n")
    text.text = t
//    text.repaint()
  }

  def plot(c: Char): Unit = synchronized {
    last match {
      case SETX =>
        x.set(c)
        last = 0
      case SETY =>
        y.set(c)
        last = 0
      case _ =>
        c match {
          case SETX =>
            last = SETX
          case SETY =>
            last = SETY
          case ORIGIN =>
            y.set(0)
            x.set(0)
          case CENTRE =>
            y.set(height / 2)
            x.set(width / 2)
          case UP =>
            y.decrementAndGet()
            y.set(Math.max(0, y.get()))
          case DOWN =>
            y.incrementAndGet()
            y.set(Math.min(height-1, y.get()))
          case LEFT =>
            x.decrementAndGet()
            x.set(Math.max(0, x.get()))
          case RIGHT =>
            x.incrementAndGet()
            if (x.get() >= width) {
              x.set(0)
              plot(DOWN)
            }
          case c =>
            val yi = y.get()
            val xi = x.get()
            data(yi)(xi) = c
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