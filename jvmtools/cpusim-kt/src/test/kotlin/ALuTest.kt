import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ALuTest {

    @Test
    fun compare() {
        val aluRom = mutableListOf<Int>()

        CPU.loadAlu(aluRom)

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