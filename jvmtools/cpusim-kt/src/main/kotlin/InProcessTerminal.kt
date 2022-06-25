import scala.swing.BoxPanel
import scala.swing.Orientation
import terminal.UARTTerminal
import java.awt.Color
import java.awt.Paint
import java.io.OutputStream
import java.io.PrintStream
import javax.swing.BorderFactory
import javax.swing.border.Border
import javax.swing.border.TitledBorder

class InProcessTerminal(val gamepadHandler: (String) -> Unit) : UARTTerminal() {

    init {
        `stopped_$eq`(false)
    }

    class NullOS : OutputStream() {
        override fun write(b: Int) {
            //
        }
    }

    // write to the CPU via it's UART
    override fun outputStream(): PrintStream {
        // ignore
        return PrintStream(NullOS())
    }

    override fun doReplay() {
        // ignore
    }

    class GPStream(val handler: (String) -> Unit) : OutputStream() {
        var data: String = ""
        override fun write(b: Int) {
            if (b == '\n'.code) {
                handler(data)
                data = ""
            } else {
                data += b.toChar()
            }
        }
    }

    // write to the CPU gamepad port
    override fun gamepadStream(): PrintStream {
        return PrintStream(GPStream(gamepadHandler))
    }

    override fun run() {
        super.run()
        val c = super.uiapp().contents()

        val box = BoxPanel(Orientation.Horizontal())
        box.`border_$eq`(BorderFactory.createLineBorder(Color.RED, 20))
        box.contents().append(c)

        super.uiapp().`contents_$eq`( box)
    //        {
////            contents ++= Seq(brefresh, bpaint, bstop, bnext, breplay)
//        }
    }
}
