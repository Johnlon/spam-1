package terminal

import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger

import javax.swing.BorderFactory
import terminal.CrapTerminal._

import scala.collection.mutable
import scala.swing.event.{Key, _}
import scala.swing.{Rectangle, _}

object CrapTerminal {

  val FONT = "Courier New"

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
                    separator: String = ".",
                    source: () => (Char, Char),
                    receiveKey: Key.Value => Unit) extends SimpleSwingApplication {

  var last: Char = 0
  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  var screen: mutable.Seq[mutable.Buffer[Char]] = fill()

  val brefresh = new Button("Reset")
  val bpaint = new Button("Paint")

  val text = new TextArea()
  text.border = BorderFactory.createLineBorder(Color.BLUE)
  text.preferredSize = new Dimension(1000, 600)
  //  text.font = new Font(Font.Monospaced, scala.swing.Font.Plain.id, 10)
  text.font = new Font(FONT, scala.swing.Font.Plain.id, 10)

  val data = new TextArea()
  data.border = BorderFactory.createLineBorder(Color.RED)
  data.preferredSize = new Dimension(500, 30)

  val timer = new ScalaTimer(1000 / 60)

  var start: Long = System.currentTimeMillis()

  var count = 0

  def top: Frame = new MainFrame {
    bounds = new Rectangle(50, 100, 200, 30)

    listenTo(text.keys, brefresh, bpaint, timer)

    timer.start()

    reactions += {
      case ButtonClicked(b) if b == brefresh =>
        screen = fill()
        doRepaint()

      case ButtonClicked(b) if b == bpaint =>
        doRepaint()

      case KeyPressed(_, c, _, _) =>
        receiveKey(c)

      case TimerEvent() =>
        val now = System.currentTimeMillis()
        val c = source()

        if (c._1 != NO_CHAR) {
          count += 1

          if (count > 100) {
            val elapsed = now - start
            val rate = (1000.0 * count) / elapsed
            count = 0
            start = System.currentTimeMillis()
            data.text = f"paint rate : $rate%.2f/s"
          }
          plot(c)

        }

    }

    contents = new BoxPanel(Orientation.Vertical) {
      val top = new BoxPanel(Orientation.Horizontal) {
        contents ++= Seq(brefresh, bpaint, data)
      }
      contents ++= Seq(top, text)
    }

  }

  def doRepaint(): Unit = {
    val LeftMargin = "___"
    val t = "\n" + screen.map(LeftMargin + _.mkString(".")).mkString("\n")
    //    val t = "\n" + screen.map("   " + _.mkString(" ")).mkString("\n")
    text.text = s"""$t"""
  }

  def plot(c: (Char, Char)): Unit = synchronized {
    val (ctrl ,data) = c

    ctrl match {
      case SETX  =>
        x.set(data)
      case SETY =>
        y.set(data)
      case WRITE =>
        val yi = y.get()
        val xi = x.get()

        val row: mutable.Buffer[Char] = screen(yi)
        row(xi) = data

        doRepaint()
      case 0 =>
        // no data
    }
  }

  def fill(): mutable.Seq[mutable.Buffer[Char]] = {
    (0 until height).map {
      d =>
        (separator * width).toBuffer
    }.toBuffer
  }
}

case class TimerEvent() extends scala.swing.event.Event

//class ScalaTimer(val delay0: Int) extends javax.swing.Timer(delay0, null) with Publisher {
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