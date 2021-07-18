package terminal

import org.apache.commons.io.input.{Tailer, TailerListener}

import java.awt.Color
import java.io.{File, FileOutputStream, OutputStream, PrintStream}
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.BorderFactory
import scala.collection.mutable
import scala.swing.ScrollPane.BarPolicy
import scala.swing._
import scala.swing.event._

object UARTTerminal extends Terminal {

  val replay = true

  val uartOut = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.control"
  val controlStream = new PrintStream(new FileOutputStream(uartControl, true))

  var tailer: Tailer = null

  def run(): Unit = {

    val buttons = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(brefresh, bpaint, bstop, bnext, breplay)
    }

    val label = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(labelChar)
    }

    val head = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(buttons, label, textArea, logLine, scrText)
    }

    uiapp.contents = head

    outputStream().print(f"t100000\n")
    outputStream().flush()

    val gotoEnd = !replay
    startTailer(gotoEnd)
  }

  override def doReplay(): Unit = {
    startTailer(false)
  }

  override def outputStream(): PrintStream = {
    controlStream
  }

  private def startTailer(gotoEnd: Boolean) = {
    if (tailer != null) tailer.stop()

    val listener = new FileListener
    tailer = Tailer.create(new File(uartOut), listener, 0, gotoEnd, false, 1000)
  }

  class FileListener extends TailerListener {
    var count = 0;

    override def init(tailer: Tailer): Unit = {}

    override def fileNotFound(): Unit = {
      //      println("file not found")
      Thread.sleep(100)
    }

    override def fileRotated(): Unit = {
      println("file rotated")
    }

    override def handle(ex: Exception): Unit = {
      println(ex)
    }

    override def handle(line: String): Unit = {
      handleLine(line)
    }
  }
}