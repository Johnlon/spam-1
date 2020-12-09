package terminal

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.io.input.{Tailer, TailerListener}

import scala.swing.event._
import scala.swing.{Rectangle, _}

object Terminal extends SimpleSwingApplication {

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
  val ORIGIN: Char = 6
  val CENTRE: Char = 7
  val SETX: Char = 8
  val SETY: Char = 9

  var data = fill()

  val text = new TextArea("Java Version: " + util.Properties.javaVersion + "\n" + "john")

  private def run(): Unit = {
    val listener = new MyListener
    //public Tailer (file: File, cset: Charset, listener: TailerListener, delayMillis: Long, `end`: Boolean, reOpen: Boolean, bufSize: Int) {

    val tailer = Tailer.create(  new File("C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"), listener, 0, true, false, 1000)

  }

  class MyListener extends TailerListener {
    def handle(line: String): Unit = {

      if (line.trim.length > 0) {
        println("! " + line)
        val c = Integer.parseInt(line, 16)
        plot(c.toChar)
      }
    }

    override def init(tailer: Tailer): Unit = {}

    override def fileNotFound(): Unit = {
      println("file not found")
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


    listenTo(brefresh, bpaint)
    reactions += {
      case ButtonClicked(b) if b == brefresh =>
        data = fill()
        plot(CENTRE)

      case ButtonClicked(b) if b == bpaint =>
        doRepaint()

      case _ =>
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(brefresh, bpaint, text)
    }

    //t.start()
    run()

  }


  def doRepaint(): Unit ={
    text.repaint()
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

    val t = "\n" + data.map("   " + _.mkString(" ")).mkString("\n")
    text.text = t

    //doRepaint()
  }

  def fill() = {
    (0 until height).map {
      d =>
        (BLANK * width).toBuffer
    }.toBuffer
  }



}