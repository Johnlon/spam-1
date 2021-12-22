import terminal.UARTTerminal
import java.io.OutputStream
import java.io.PrintStream

class InProcessTerminal(val gamepadHandler: (String) -> Unit) : UARTTerminal() {
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
}
