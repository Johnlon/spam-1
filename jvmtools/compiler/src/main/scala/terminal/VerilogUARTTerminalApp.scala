package terminal

import org.apache.commons.io.input.{Tailer, TailerListener}

import java.io.{File, FileOutputStream, PrintStream}

/** A terminal that does comms with the simulated verilog SPAM-1.
 * Sends bytes from a Windows app to a file,
 * paints draw instructions arriving in a different file to the console.
 * Uses a set of drawing rules defined for the Chip 8 emulation.
 * */

object VerilogUARTTerminalApp extends UARTTerminal {

  val replay = true

  val uartOut = "C:\\Users\\johnl\\work\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\work\\simplecpu\\verilog\\cpu\\uart.control"
  val gamepadControl = "C:\\Users\\johnl\\work\\simplecpu\\verilog\\cpu\\gamepad.control"

  val uartControlStream = new PrintStream(new FileOutputStream(uartControl, true))
  val gamepadControlStream = new PrintStream(new FileOutputStream(gamepadControl, true))

  def gamepadStream() = gamepadControlStream

  var tailer: Tailer = null

  override def run(): Unit = {
    super.run()

    gamepadControlStream.println("c1=0")

    val gotoEnd = !replay
    startTailer(gotoEnd)
  }

  override def doReplay(): Unit = {
    startTailer(false)
  }

  override def outputStream(): PrintStream = {
    uartControlStream
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
      println("file not found")
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