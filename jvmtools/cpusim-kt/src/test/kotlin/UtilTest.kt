import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UtilTest {
    @Test
    fun testPadding() {
        assertEquals("00001" , tobin(1, 5))
        assertEquals("00011" , tobin(3, 5))
        assertEquals("1" , tobin(1, 1))
        assertEquals("1" , tobin(255, 1))
        assertEquals("0" , tobin(0, 1))
    }

    @Test
    fun testLeftSlice() {
        assertEquals("0" , tobin(10, 8, 0))
        assertEquals("1" , tobin(10, 8, 1))
        assertEquals("0" , tobin(10, 8, 2))
        assertEquals("1" , tobin(10, 8, 3))
    }

    @Test
    fun testLeftRightSlice() {
        assertEquals("1" , tobin(1, 8, 0, 0))
        assertEquals("001" , tobin(1, 8, 2, 0))
        assertEquals("11010" , tobin(0xfa, 8, 4, 0))
        assertEquals("1101" , tobin(0xfa, 8, 4, 1))
    }
}