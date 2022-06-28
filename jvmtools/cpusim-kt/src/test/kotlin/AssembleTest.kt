import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AssembleTest {
    @Test
    fun testAsm() {
        val code = Instruction(
            Op.A_MINUS_B_BCD,
            TDev.halt,
            ADev.marhi,
            BDev.immed,
            Cond.DI,
            Flag.Set,
            CInv.Std,
            AMode.Dir,
            0xaaaa,
            0xff
        )

        val (rom, inst) = assemble(code)

        val expected = "11111 1000 101 110 1001 1 0 0 0 1 10101010 10101010 11111111"
            .replace("\\s".toRegex(), "")
        assertEquals(expected.toLong(2), rom[0])
        assertEquals(code, inst[0])
    }
}