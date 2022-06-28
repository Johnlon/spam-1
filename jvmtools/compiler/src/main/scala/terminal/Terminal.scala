package terminal

import java.awt.Color
import java.io.PrintStream
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import javax.swing.BorderFactory
import scala.collection.mutable
import scala.swing.ScrollPane.BarPolicy
import scala.swing._
import scala.swing.event._

object TerminalStates {

  // valid in NONE_STATE
  val GOTO_INIT_STATE: Char = 0
  val GOTO_SETX_STATE: Char = 1 // switch state
  val GOTO_SETY_STATE: Char = 2 // switch state
  val GOTO_DRAW_PIXEL_STATE: Char = 3 // switch state
  val GOTO_DRAW_BYTE_STATE: Char = 4 // switch state
  val GOTO_LOG_CHAR_STATE: Char = 5 // switch state
  val GOTO_LOG_BYTE_STATE: Char = 6 // switch state
  val DO_CLEAR: Char = 8
  val DO_UP: Char = 11
  val DO_DOWN: Char = 12
  val DO_LEFT: Char = 13
  val DO_RIGHT: Char = 14
  val DO_ORIGIN: Char = 15
  val DO_CENTRE: Char = 16
  val GOTO_LOG_OPCODE: Char = 17
  val GOTO_LOG_STRING: Char = 18
  val GOTO_LOG_BIN_STATE: Char = 19 // switch state

}

import terminal.TerminalStates._

sealed trait TerminalIOState

object STATE_INIT extends TerminalIOState // next is a direct op like "RIGHT", or a signal to start a two byte binary value op like DRAW

object STATE_SETX extends TerminalIOState

object STATE_SETY extends TerminalIOState

object STATE_DRAW_PIXEL extends TerminalIOState

object STATE_DRAW_BYTE extends TerminalIOState

object STATE_LOG_CHAR extends TerminalIOState

object STATE_LOG_BIN extends TerminalIOState

object STATE_LOG_BYTE extends TerminalIOState

object STATE_LOG_OPCODE1 extends TerminalIOState

object STATE_LOG_OPCODE2 extends TerminalIOState

object STATE_LOG_STRING_START extends TerminalIOState

object STATE_LOG_STRING_WRITE extends TerminalIOState

abstract class Terminal extends SimpleSwingApplication {

  var uiapp = new MainFrame()

  def run(): Unit

  def doReplay(): Unit

  def outputStream(): PrintStream

  def gamepadStream(): PrintStream

  @volatile protected var stopped = true

  var nextInst = new AtomicInteger(0)

  val C8_SCREEN_HEIGHT = 32
  val C8_SCREEN_WIDTH = 64

  val width = C8_SCREEN_WIDTH; //50
  val height = C8_SCREEN_HEIGHT; // 40

  val BLANKCHAR = ' '
  val BLANK = BLANKCHAR.toString
  val BLOCKCHAR = 0x2588.toChar // '#'
  val BACKGROUND_CHAR = " "


  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

  var state: TerminalIOState = STATE_INIT

  var data: mutable.Seq[mutable.Buffer[Char]] = fill()

  val textArea = new TextArea("Java Version: " + util.Properties.javaVersion + "\n" + "john")
  textArea.border = BorderFactory.createLineBorder(Color.BLUE)
  textArea.background = Color.BLACK
  textArea.foreground = Color.WHITE
  textArea.preferredSize = new Dimension(900, 450)
  textArea.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)
  textArea.editable = false

  val logLine = new TextArea(1, 100)
  logLine.border = BorderFactory.createLineBorder(Color.GREEN)
  logLine.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)

  val logPanel = new TextArea(20, 100)
  logPanel.border = BorderFactory.createLineBorder(Color.GREEN)
  logPanel.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)
  val scrText = new scala.swing.ScrollPane(logPanel)
  scrText.verticalScrollBarPolicy = BarPolicy.Always

  var lineCount = 0;

  // one char per line in hex
  def handleLine(hexCharLine: String): Unit = {
    try {
      var wasStopped = false

      //      if (hexCharLine == "ff")
      //        println("STOP")

      while (stopped && nextInst.get() == 0) {
        wasStopped = true
        Thread.sleep(10)
      }

      var gotCR = false
      if (hexCharLine.trim.nonEmpty) {
        lineCount += 1
        val c = Integer.parseInt(hexCharLine, 16)
        val char = c.toChar
        if (char == '\n') gotCR = true
        if (char == '\r') gotCR = true
        plot(char)
      }

      if (gotCR) {
        if (nextInst.get() > 0) nextInst.decrementAndGet()
        while (stopped && nextInst.get() == 0) {
          Thread.sleep(100)
        }
      }

      if (wasStopped) {
        Thread.sleep(1000)
        // if was stopped then don't recover speed immediately
        // this allows one to quickly press top again to allow app to advance slowly
      }
    } catch {
      case ex: Exception =>
        Dialog.showMessage(uiapp, "ERROR : " + ex)
        ex.printStackTrace()
        throw ex
    }
  }

  val labelChar = new Label("<No data yet>")

  val brefresh = new Button("Reset")
  val bpaint = new Button("Paint")
  val bstop = new Button("Stop")
  val bnext = new Button("Next")
  val bclk = new Button("Clk")
  val breplay = new Button("Replay")

  //    def send(c: Char, sendDelay: Boolean = true): Unit = {
  def sendUart(c: Char): Unit = {
    outputStream().print(f"x${c.toInt}%02X\n")
    //      if (sendDelay) outputStream().print(f"#\n")

    // outputStream().flush()
    //println("sent " + c)
  }

  def sendGamepad(cid: Int, c: Int): Unit = {
    gamepadStream().print(f"c${cid}=${c.toInt}%02X\n")
    gamepadStream().flush()
  }


  def doRepaint(): Unit = {
    mightNeedRepaint.set(true)
  }

  def top: Frame = {

    //bounds = new Rectangle(50, 100, 300, 30)

    //plot(DO_CENTRE)

    listenTo(textArea.keys, brefresh, bpaint, bstop, bnext, bclk, breplay)

    def updateStopButton() = {
      if (stopped) {
        bstop.background = Color.RED
      } else {
        bstop.background = Color.WHITE
      }
    }

    updateStopButton()


    // as per https://github.com/Johnlon/NESInterfaceAndPeripherals/blob/main/controller.h#L25
    val gamepad = Map(
      Key.Up -> 1,
      Key.Down -> 2,
      Key.Left -> 4,
      Key.Right -> 8,
      Key.Space -> 16, // SELECT BUTTON
      Key.Enter -> 32, // START BUTTON
      Key.B -> 64, // B BUTTON
      Key.A -> 128, // A BUTTON
    )

    // map the arrow keys to a WASD-like set - see keypad numbering https://tobiasvl.github.io/blog/write-a-chip-8-emulator/
    val hexpad: Map[scala.swing.event.Key.Value, Char] = Map(
      Key.Key0 -> 0,
      Key.Key1 -> 1,
      Key.Key2 -> 2,
      Key.Key3 -> 3,
      Key.Key4 -> 4,
      Key.Key5 -> 5,
      Key.Key6 -> 6,
      Key.Key7 -> 7,
      Key.Key8 -> 8,
      Key.Key9 -> 9,
      Key.A -> 10,
      Key.B -> 11,
      Key.C -> 12,
      Key.D -> 12,
      Key.E -> 14,
      Key.F -> 15,
      // map the arrow keys to a WASD-like set - see keypad numbering https://tobiasvl.github.io/blog/write-a-chip-8-emulator/
      // games may us other keys
      Key.Up -> 4,
      Key.Down -> 7,
      Key.Left -> 5,
      Key.Right -> 6,
      Key.Space -> 32,
    )

    var controllerState: Int = 0

    def gamepadPressKey(c: Key.Value): Unit = {
      controllerState = controllerState | gamepad.getOrElse(c, 0)
      sendGamepad(1, controllerState)
    }

    def gamepadReleaseKey(c: Key.Value): Unit = {
      controllerState = controllerState ^ gamepad.getOrElse(c, 0)
      sendGamepad(1, controllerState)
    }

    reactions += {
      case ButtonClicked(b) if b == brefresh =>
        data = fill()
        doRepaint()

      case ButtonClicked(b) if b == bnext =>
        nextInst.incrementAndGet()
      case ButtonClicked(b) if b == bpaint =>
        doRepaint()
      case ButtonClicked(b) if b == bstop =>
        stopped = !stopped
        updateStopButton()
      case ButtonClicked(b) if b == breplay =>
        doReplay()

      case KeyPressed(_, c, _, _) if gamepad.keySet.contains(c) =>
        gamepadPressKey(c)
      case KeyReleased(_, c, _, _) if gamepad.keySet.contains(c) =>
        gamepadReleaseKey(c)

      case KeyPressed(_, c, _, _) if hexpad.keySet.contains(c) =>
        sendUart(hexpad(c))
      case KeyReleased(_, c, _, _) if hexpad.keySet.contains(c) =>
        sendUart(hexpad(c))

      case _ =>
    }

    run()

    uiapp
  }

  var tc = 0
  var mightNeedRepaint = new AtomicBoolean(true)
  val paintThread = new Thread {
    override def run(): Unit = {
      while (true) {
        if (mightNeedRepaint.get()) {
          mightNeedRepaint.set(false)

          val LEFT_MARGIN = "   "
          val GAP = ""

          val t = "\n" + data.map(line => LEFT_MARGIN + duplicateChar(line).mkString(GAP)).mkString("\n")
          textArea.text = t
          tc = tc + 1
        }
      }
    }
  }
  paintThread.setDaemon(true)
  paintThread.start()


  // double the width of the chars to make them more blocky
  private def duplicateChar(line: mutable.Buffer[Char]) = {
    line.map(x => s"$x$x")
  }

  def clearScreen(): Unit = {
    (0 until height) foreach {
      h =>
        (0 until width) foreach {
          w =>
            drawPixel(w, h, '0', false)
        }
    }
  }

  def log(c: Char): Unit = {
    if (c == '\n') {
      // insert at top : https://stackoverflow.com/questions/12565358/how-to-insert-or-append-new-line-on-top-of-the-jtextarea-in-java-swing
      logPanel.peer.getDocument.insertString(0, logLine.text + "\n", null);
      logLine.text = ""
    } else {
      logLine.text += c
    }
  }

  def logByte(c: Char): Unit = {
    logLine.text += f"$c%02x"
  }

  def logBin(c: Char): Unit = {
    logLine.text += ("00000000" + c.toBinaryString).takeRight(8)
  }

  def logOpcode(opcode1: Int, opcode2: Int) = {
    logLine.text += f"OPCODE $opcode1%02x$opcode2%02x"
  }

  def logString(str: String) = {
    logLine.text += str
  }


  def report(s: String, c: Char) = {
    labelChar.text = s + " D[" + c.toInt + "] " + "H[" + c.toHexString + "] " + "C[" + c + "] B[" + c.toBinaryString + "]"
  }

  def report(s: String) = {
    labelChar.text = s
  }

  var opcode1 = 0
  var logStringRemaining = 0
  var logString = ""

  def plot(c: Char): Unit = synchronized {
    try {
      state match {
        case STATE_SETX =>
          report("SETTING X", c)
          x.set(c)
          state = STATE_INIT
        case STATE_SETY =>
          report("SETTING Y", c)
          y.set(c)
          state = STATE_INIT
        case STATE_DRAW_PIXEL =>
          val yi = y.get()
          val xi = x.get()
          report(s"DRAW PIXEL AT X=$xi Y=$yi", c)
          drawPixel(xi, yi, c, false)
          state = STATE_INIT
        case STATE_DRAW_BYTE =>
          val yi = y.get()
          val xi = x.get()
          val bits = f"${c.toBinaryString}%8s".replace(' ', '0')

          report(s"DRAW BYTE AT X=$xi Y=$yi Bits=$bits")
          bits.zipWithIndex foreach {
            case (bit, bitIndex) =>
              val pixelX = xi + bitIndex
              drawPixel(pixelX, yi, bit, true)
          }
          state = STATE_INIT
        case STATE_LOG_CHAR =>
          report(s"LOG CHAR", c)
          log(c)
          state = STATE_INIT
        case STATE_LOG_BYTE =>
          report(s"LOG BYTE", c)
          logByte(c)
          state = STATE_INIT
        case STATE_LOG_BIN =>
          report(s"LOG BIN", c)
          logBin(c)
          state = STATE_INIT
        case STATE_LOG_OPCODE1 =>
          report(s"LOG OPCODE1", c)
          opcode1 = c;
          state = STATE_LOG_OPCODE2
        case STATE_LOG_OPCODE2 =>
          report(s"LOG OPCODE2", c)
          logOpcode(opcode1, c)
          state = STATE_INIT
        case STATE_LOG_STRING_START =>
          report(s"LOG STRING START", c)
          logStringRemaining = c
          state = STATE_LOG_STRING_WRITE
        case STATE_LOG_STRING_WRITE =>
          logString = logString + c
          logStringRemaining -= 1

          report(s"LOG STRING WRITE " + logStringRemaining, c)
          if (logStringRemaining == 0) {
            report(s"WRITING " + logString)
            logString(logString)
            logString = ""
            state = STATE_INIT
          }
        case STATE_INIT =>
          c match {
            // FOLLOWING INIT CODES SIGNAL START OF 2 BYTE SEQ
            case GOTO_INIT_STATE =>
              // useful for resynchronisation - emitting two "0" bytes in sequence should push system into NONE_STATE
              // first byte either completes an existing SET/DRAW or it hits this DO_NONE_STATE noop
              state = STATE_INIT
              report(s"STATE=$state")
            case GOTO_SETX_STATE =>
              state = STATE_SETX
              report(s"STATE=$state")
            case GOTO_SETY_STATE =>
              state = STATE_SETY
              report(s"STATE=$state")
            case GOTO_DRAW_PIXEL_STATE =>
              state = STATE_DRAW_PIXEL
              report(s"STATE=$state")
            case GOTO_DRAW_BYTE_STATE =>
              state = STATE_DRAW_BYTE
              report(s"STATE=$state")
            case GOTO_LOG_CHAR_STATE =>
              state = STATE_LOG_CHAR
              report(s"STATE=$state")
            case GOTO_LOG_BIN_STATE =>
              state = STATE_LOG_BIN
              report(s"STATE=$state")
            case GOTO_LOG_BYTE_STATE =>
              state = STATE_LOG_BYTE
              report(s"STATE=$state")

            case GOTO_LOG_OPCODE =>
              state = STATE_LOG_OPCODE1
              report(s"STATE=$state")

            case GOTO_LOG_STRING =>
              state = STATE_LOG_STRING_START
              report(s"STATE=$state")

            // FOLLOWING COMMANDS ARE ONE BYTE SEQ
            case DO_CLEAR =>
              report(s"DO CLEAR")
              clearScreen()
            case DO_ORIGIN =>
              report(s"DO ORIGIN")
              y.set(0)
              x.set(0)
            case DO_CENTRE =>
              report(s"DO CENTRE")
              y.set(height / 2)
              x.set(width / 2)
            case DO_UP =>
              report(s"DO UP")
              y.decrementAndGet()
              y.set(Math.max(0, y.get()))
            case DO_DOWN =>
              report(s"DO DOWN")
              y.incrementAndGet()
              y.set(Math.min(height - 1, y.get()))
            case DO_LEFT =>
              report(s"DO LEFT")
              x.decrementAndGet()
              x.set(Math.max(0, x.get()))
            case DO_RIGHT =>
              report(s"DO RIGHT")
              x.incrementAndGet()
              if (x.get() >= width) {
                x.set(0)
                plot(DO_DOWN)
              }
          }
      }
    }
    catch {
      case ex: MatchError =>
        Dialog.showMessage(uiapp, s"PROTOCOL ERROR : current state : ${state},  this code int:${c.toInt}")
    }

  }

  private def drawPixel(x: Int, y: Int, bit: Char, flip: Boolean) = {

    /* do not draw off side of screen ...
    https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#dxyn-display
    */
    if (x < width) {

      if (bit != '0' && bit != '1') {
        sys.error("bit must be 0 or 1 but was '" + bit + "'")
      }

      val char = if (bit == '1') BLOCKCHAR else BLANKCHAR

      data(y % C8_SCREEN_HEIGHT)(x % C8_SCREEN_WIDTH) = char
    }

    doRepaint()
  }

  def fill(): mutable.Seq[mutable.Buffer[Char]] = {
    (0 until height).map {
      d =>
        //(BLANK * width).toBuffer
        (BACKGROUND_CHAR * width).toBuffer
    }.toBuffer
  }


}