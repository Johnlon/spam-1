import org.apache.commons.io.input.Tailer
import org.apache.commons.io.input.TailerListener
import java.io.File
import java.util.function.Consumer

class GamepadFileReader {
    private var tailer: Tailer? = null

    val gamepadControl = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\gamepad.control"

    fun startTailer(gotoEnd: Boolean, handler: (String) -> Unit) {
        val t = tailer
        if (t != null) t.stop()

        val listener = FileListener(handler)
        tailer = Tailer.create(File(gamepadControl), listener, 0, gotoEnd, false, 1000)
    }

    class FileListener(val fn: Consumer<String>) : TailerListener {
        var count = 0;

        override fun init(tailer: Tailer) {
        }

        override fun fileNotFound() {
            println("file not found")
            Thread.sleep(100)
        }

        override fun fileRotated() {
            println("file rotated")
        }

        override fun handle(ex: Exception) {
            println(ex)
        }

        override fun handle(line: String) {
            this.fn.accept(line)
        }
    }
}