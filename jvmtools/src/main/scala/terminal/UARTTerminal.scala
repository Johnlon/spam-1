package terminal

import org.apache.commons.io.input.{Tailer, TailerListener}

import java.awt.Color
import java.io.{File, FileOutputStream, PrintStream}
import java.lang.Thread.sleep
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import javax.swing.BorderFactory
import scala.collection.mutable
import scala.swing.ScrollPane.BarPolicy
import scala.swing._
import scala.swing.event._


sealed trait TerminalIOState

object STATE_INIT extends TerminalIOState // next is a direct op like "RIGHT", or a signal to start a two byte binary value op like DRAW

object STATE_SETX extends TerminalIOState

object STATE_SETY extends TerminalIOState

object STATE_DRAW_PIXEL extends TerminalIOState

object STATE_DRAW_BYTE extends TerminalIOState

object STATE_LOG_CHAR extends TerminalIOState

object STATE_LOG_BYTE extends TerminalIOState

object UARTTerminal extends SimpleSwingApplication {
  var uiapp = new MainFrame()

  val replay = true

  @volatile var stopped = true
  val step = new AtomicLong(0);

  val uartOut = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"
  val uartControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.control"

  val C8_SCREEN_HEIGHT = 32
  val C8_SCREEN_WIDTH = 64

  val width = C8_SCREEN_WIDTH; //50
  val height = C8_SCREEN_HEIGHT; // 40

  val BLANKCHAR = '.'
  val BLANK = BLANKCHAR.toString
  //val BLOCKCHAR = '#'
  val BLOCKCHAR = 0x2588.toChar // '#'

  var x = new AtomicInteger(0)
  var y = new AtomicInteger(0)

  val K_UP: Char = 'U'
  val K_DOWN: Char = 'D'
  val K_LEFT: Char = 'L'
  val K_RIGHT: Char = 'R'

  // valid in NONE_STATE
  val GOTO_INIT_STATE: Char = 0
  val GOTO_SETX_STATE: Char = 1 // switch state
  val GOTO_SETY_STATE: Char = 2 // switch state
  val GOTO_DRAW_PIXEL_STATE: Char = 3 // switch state
  val GOTO_DRAW_BYTE_STATE: Char = 4 // switch state
  val GOTO_LOG_CHAR_STATE: Char = 5 // switch state
  val GOTO_LOG_BYTE_STATE: Char = 6 // switch state
  val DO_CLEAR: Char = 10
  val DO_UP: Char = 11
  val DO_DOWN: Char = 12
  val DO_LEFT: Char = 13
  val DO_RIGHT: Char = 14
  val DO_ORIGIN: Char = 15
  val DO_CENTRE: Char = 16

  var state: TerminalIOState = STATE_INIT

  var data: mutable.Seq[mutable.Buffer[Char]] = fill()

  val text = new TextArea("Java Version: " + util.Properties.javaVersion + "\n" + "john")
  text.border = BorderFactory.createLineBorder(Color.BLUE)
  text.preferredSize = new Dimension(900, 450)
  text.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)

  val logLine = new TextArea(1, 100)
  logLine.border = BorderFactory.createLineBorder(Color.GREEN)
  logLine.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)

  val logPanel = new TextArea(20, 100)
  logPanel.border = BorderFactory.createLineBorder(Color.GREEN)
  logPanel.font = new Font("Courier New", scala.swing.Font.Plain.id, 10)
  val scrText = new scala.swing.ScrollPane(logPanel)
  scrText.verticalScrollBarPolicy = BarPolicy.Always

  var tailer: Tailer = null

  private def run(): Unit = {
    val gotoEnd = !replay
    startTailer(gotoEnd)
  }

  private def startTailer(gotoEnd: Boolean) = {
    if (tailer != null) tailer.stop()

    val listener = new FileListener
    tailer = Tailer.create(new File(uartOut), listener, 0, gotoEnd, false, 1000)
  }

  class FileListener extends TailerListener {
    var count = 0;

    def handle(line: String): Unit = {

      //      plot(GOTO_SETX_STATE)
      //      plot(0)
      //      plot(GOTO_SETY_STATE)
      //      plot(0)
      //      plot(GOTO_DRAW_BYTE_STATE)
      //      plot(0xaa)

      if (count == 3118) {
        println("==")
      }
      try {
        var wasStopped = false
        while (stopped && step.get() == 0) {
          wasStopped = true
          Thread.sleep(10)
        }
        if (step.get() > 0) step.decrementAndGet()

        if (line.trim.nonEmpty) {
          count += 1
          println("" + count + " ! " + line)
          val c = Integer.parseInt(line, 16)
          val char = c.toChar
          plot(char)
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

  val labelChar = new Label("")

  def top: Frame = {

    //bounds = new Rectangle(50, 100, 300, 30)

    val brefresh = new Button("Reset")
    val bpaint = new Button("Paint")
    val bstop = new Button("Stop")
    val bstep = new Button("Step")
    val breplay = new Button("Replay")

    plot(DO_CENTRE)

    listenTo(text.keys, brefresh, bpaint, bstop, bstep, breplay)

    val uartCtrl = new PrintStream(new FileOutputStream(uartControl, true))

    uartCtrl.print(f"t100000\n")
    uartCtrl.flush()

    def send(c: Char): Unit = {
      uartCtrl.print(f"x${c.toInt}%02X\n")
      uartCtrl.flush()
      println("sent " + c)
    }

    def updateStopButton() = {
      if (stopped) {
        bstop.background = Color.RED
      } else {
        bstop.background = Color.WHITE
      }
    }

    updateStopButton()

    reactions += {
      case ButtonClicked(b) if b == brefresh =>
        data = fill()
        doRepaint()

      case ButtonClicked(b) if b == bpaint =>
        doRepaint()

      case ButtonClicked(b) if b == bstop =>
        stopped = !stopped
        updateStopButton()
      case ButtonClicked(b) if b == breplay =>
        startTailer(false)

      case ButtonClicked(b) if b == bstep =>
        step.incrementAndGet()

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

    uiapp.contents = new BoxPanel(Orientation.Vertical) {
      val buttons = new BoxPanel(Orientation.Horizontal) {
        contents ++= Seq(brefresh, bpaint, bstop, bstep, breplay)
      }
      val label = new BoxPanel(Orientation.Horizontal) {
        contents ++= Seq(labelChar)
      }

      contents ++= Seq(buttons, label, text, logLine, scrText)
    }

    run()

    uiapp
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
            drawPixel(w, h, '0', false)
        }
    }
  }

  def log(c: Char): Unit = {
    if (c == '\n') {
      // insert at top : https://stackoverflow.com/questions/12565358/how-to-insert-or-append-new-line-on-top-of-the-jtextarea-in-java-swing
      logPanel.peer.getDocument.insertString(0, logLine.text + "\n", null);
      //logPanel.text = logLine.text +  "\n" + logPanel.text
      logLine.text = ""
    } else {
      logLine.text += c
    }
  }

  def logByte(c: Char): Unit = {
    logLine.text += f"$c%02x"
  }

  def report(s: String, c: Char) = {
    labelChar.text = s + " D[" + c.toInt + "] " + "H[" + c.toHexString + "] " + "C[" + c + "] B[" + c.toBinaryString + "]"
  }

  def report(s: String) = {
    labelChar.text = s
  }

  def plot(c: Char): Unit = synchronized {
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

        report(s"DRAW BYTE AT X=$xi Y=$yi Bits=$bits", c)
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
          case GOTO_LOG_BYTE_STATE =>
            state = STATE_LOG_BYTE
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

    doRepaint()
  }

  var countChar = 'a';

  private def drawPixel(x: Int, y: Int, bit: Char, flip: Boolean) = {

    /* do not draw off side of screen ...
    https://tobiasvl.github.io/blog/write-a-chip-8-emulator/#dxyn-display
    */
    if (x < width) {

      if (bit != '0' && bit != '1') {
        sys.error("bit must be 0 or 1 but was '" + bit + "'")
      }

      //      val existing = data(y % C8_SCREEN_HEIGHT)(x % C8_SCREEN_WIDTH)

      //    val newval = if (flip) {
      //      existing == BLANKCHAR
      //    } else {
      //      existing.equals('1')
      //    }

      //val char = if (bit == '1') BLOCKCHAR else BLANKCHAR
      val char = if (bit == '1') {
        countChar = (countChar + 1).asInstanceOf[Char]
        countChar
      }
      else BLANKCHAR

      if (countChar > 'z') countChar = '0'
      //    val pixelImg = char // if (newval) BLOCKCHAR else BLANKCHAR

      data(y % C8_SCREEN_HEIGHT)(x % C8_SCREEN_WIDTH) = char
    }
  }

  def fill(): mutable.Seq[mutable.Buffer[Char]] = {
    (0 until height).map {
      d =>
        //(BLANK * width).toBuffer
        ("-" * width).toBuffer
    }.toBuffer
  }


}