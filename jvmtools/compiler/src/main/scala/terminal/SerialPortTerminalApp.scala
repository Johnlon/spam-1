package terminal

import com.fazecast.jSerialComm.{SerialPort, SerialPortTimeoutException}
import terminal.VerilogUARTTerminalApp.uiapp

import java.io._
import scala.swing._
import scala.swing.event.{ButtonClicked, SelectionChanged}

/** A terminal that does comms with the real SPAM-1 hardware.
 * Sends bytes from a Windows app to a serial port,
 * paints draw instructions arriving from the serial port to the console.
 * Uses a set of drawing rules defined for the Chip 8 emulation.
 */
object SerialPortTerminalApp extends Terminal {

  val comAdapter = new ComAdapter

  def run(): Unit = {

    def portSelections = {
      val portsList = ComAdapter.portsNames
      PortName("", "select port") +: portsList
    }

    val comboPorts = new ComboBox(portSelections)
    val cbRefresh = new Button("Refresh")
    val btnTest = new Button("Test")

    reactions += {
      case ButtonClicked(`cbRefresh`) =>
        comboPorts.peer.setModel(ComboBox.newConstantModel(portSelections))

      case ButtonClicked(`btnTest`) =>
        if (comAdapter.isOpen) {
//          send('J')
//          send('\n')
//          send('\r')
          // send something
        }

      case SelectionChanged(`comboPorts`) =>

        val comPortName: String = comboPorts.selection.item.name
        if (comPortName == "")
          comAdapter.close()
        else
          comAdapter.open(comPortName)
    }
    listenTo(cbRefresh, comboPorts.selection, btnTest)

    val ports = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(new Label("Ports"), new Label("  "), comboPorts)
    }

    val buttons = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(brefresh, bpaint, bstepMode, bstep, bclk, btnTest)
    }

    val label = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(labelChar)
    }

    val head = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(ports, buttons, label, textArea, logLine, scrText)
    }
    uiapp.contents = head

    comAdapter.startReader(handleLine)
  }

  override def doReplay(): Unit = {
    Dialog.showMessage(uiapp, "Not supported")
  }

  override def outputStream(): PrintStream = {
    comAdapter.getOutputStream
  }

  override def gamepadStream(): PrintStream = {
    ???
  }
}

case class PortName(name: String, desc: String) {
  override def toString: String = {
    if (name.nonEmpty)
      name + " : " + desc
    else
      desc
  }
}

// see https://fazecast.github.io/jSerialComm/
object ComAdapter {
  def portsNames = SerialPort.getCommPorts.map(x =>
    PortName(x.getSystemPortName, x.getPortDescription)
  )
}

class ComAdapter {

  import com.fazecast.jSerialComm.SerialPort

  @volatile private var comPort: Option[SerialPort] = None
  @volatile private var input: InputStream = null
  @volatile private var output: PrintStream = null

  def open(portName: String): Unit = {
    close()

    val foundPort = SerialPort.getCommPorts.find(p => p.getSystemPortName == portName)
    foundPort match {
      case Some(port) =>
        port.openPort()
        input = port.getInputStream
        output = new PrintStream(port.getOutputStream)
        comPort = foundPort
      case _ =>
        throw new IOException("Can't find port " + portName)
    }
  }

  def isOpen: Boolean = {
    comPort.exists(_.isOpen)
  }

  def close(): Unit = {
    comPort match {
      case Some(port) =>
        input = null
        output = null
        port.closePort()
        comPort = None
      case _ =>
    }
  }

  def startReader(handler: String => Unit): Unit = {
    val r = new Runnable {
      def run : Unit = {
        try {
          var hadError = false
          while (true) {

            if (isOpen) {
              try {
                val line = input.read()
                if (line != -1) {
                  val hex = line.toHexString
                  if (line.toChar.isLetterOrDigit) {
                    println(s"$line : ${line.toChar}")
                  }else {
                    println(line)
                  }
                  if (hex == "ff")
                    println("stop")
                  handler.apply(hex)
                  //hadError = false
                } else {
                  Thread.sleep(10)
                }
              }
              catch {
                case to: SerialPortTimeoutException =>
                  // no problem
                  Thread.sleep(10)
                case ex: Throwable =>
                  if (!hadError)
                    Dialog.showMessage(uiapp, "failed read: " + ex.getMessage)
                  hadError = true
                  Thread.sleep(10)
              }
            }
            else {
              Thread.sleep(10)
            }
          }
        } catch {
          case ex: Throwable =>
            Dialog.showMessage(uiapp, "reader error: " + ex.getMessage)
        }
      }
    }

    val thread = new Thread(r)
    thread.setDaemon(true)
    thread.start()
    Thread.sleep(10)
  }

  def getOutputStream: PrintStream = {
    if (!isOpen) {
      Dialog.showMessage(uiapp, "Not open")
      null
    } else {
      output
    }
  }
}
