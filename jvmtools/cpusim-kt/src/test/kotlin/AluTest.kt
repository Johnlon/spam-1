import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

internal class AluTest {

    var romFile = File("../../verilog/alu/roms/alu-hex.rom")

    @Test
    fun compare() {
        val aluRom = loadAlu(romFile)

        (Op.values()).forEach { op ->
            listOf(true, false).forEach { cin ->
                (0..255).forEach { a ->
                    (0..255).forEach { b ->
                        try {
                            val outRom = alu(aluRom, op, cin, a, b)
                            val outCode = alu(null, op, cin, a, b)
                            assertEquals(outRom, outCode, "$op, $cin, \$a, $b")
                        } catch ( e: NotImplementedError) {

                        }
                    }
                }
            }
        }
    }
}