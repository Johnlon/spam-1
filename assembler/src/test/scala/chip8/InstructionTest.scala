package chip8

import chip8.Chip8Compiler.State.{INITIAL_PC, emptyMemory, emptyRegisters}
import chip8.Chip8Compiler.{STATUS_REGISTER_VF, State}
import chip8.Chip8Emulator.PIXEL
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.{Test, TestMethodOrder}

import scala.collection.mutable

@TestMethodOrder(classOf[MethodName])
class InstructionTest {

  @Test
  def tesGoSub(): Unit = {
    val sut = GoSub("thsOp", 123)

    val initialState = State()
    val actual = sut.exec(initialState)

    val expectedState = State(
      stack = mutable.Stack(INITIAL_PC).toList,
      pc = 123
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testJump(): Unit = {
    val sut = Jump("thsOp", 123)

    val initialState = State()
    val actual = sut.exec(initialState)

    val expectedState = State().copy(
      pc = 123
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testReturnSub(): Unit = {
    val sut = ReturnSub("thsOp")

    val initialState = State(
      stack = List(100, 200)
    )
    val actual = sut.exec(initialState)

    val expectedState = initialState.copy(
      stack = List(200),
      pc = 102
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testClearScreen(): Unit = {
    val sut = ClearScreen("thsOp")

    val screen = Screen().setPixel(1, 1)

    val initialState = State(
      screen = screen._1
    )
    val actual = sut.exec(initialState)

    val expectedState = State(pc = INITIAL_PC + 2)

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXEqN_Eq(): Unit = {
    val sut = SkipIfXEqN("op", 0.toByte, 123.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXEqN_Neq(): Unit = {
    val sut = SkipIfXEqN("op", 0.toByte, 123.toByte)

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeN_Eq(): Unit = {
    val sut = SkipIfXNeN("op", 0.toByte, 123.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeN_Neq(): Unit = {
    val sut = SkipIfXNeN("op", 0.toByte, 123.toByte)

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSkipIfXEqY_Eq(): Unit = {
    val sut = SkipIfXEqY("op", 0.toByte, 1.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte).set(1, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte).set(1, 123.toByte),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXEqY_Neq(): Unit = {
    val sut = SkipIfXEqY("op", 0.toByte, 1.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSkipIfXNeY_Eq(): Unit = {
    val sut = SkipIfXNeY("op", 0.toByte, 1.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte).set(1, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte).set(1, 123.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeY_Neq(): Unit = {
    val sut = SkipIfXNeY("op", 0.toByte, 1.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 123.toByte)
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 123.toByte),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSetX(): Unit = {
    val sut = SetX("op", 0.toByte, 1.toByte)

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 1.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testAddX(): Unit = {
    val sut = AddX("op", 0.toByte, 1.toByte)

    val initialState = State(
      register = emptyRegisters.set(0, 10.toByte),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, 11.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSetIndex(): Unit = {
    val sut = SetIndex("op", 1)

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      index = 1,
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testDisplay(): Unit = {
    val sut = Display("op", 1.toByte, 2.toByte, 2.toByte)

    val locationOfFont = 100
    val memory = emptyMemory.set(locationOfFont, 0xaa.toByte).set(locationOfFont + 1, 0xff.toByte)
    val initialState = State(
      register = emptyRegisters.set(1, 3.toByte).set(2, 0.toByte),
      memory = memory,
      index = locationOfFont
    )

    // above
    // X=3 Y=0 and 2 rows high
    // index points to font

    val actual = sut.exec(initialState)

    var screenData = Screen().buffer
    val row0 = screenData(0).set(3, PIXEL).set(5, PIXEL).set(7, PIXEL).set(9, PIXEL).mkString("")
    val row1 = screenData(1).set(3, PIXEL).set(4, PIXEL).set(5, PIXEL).set(6, PIXEL).set(7, PIXEL).set(8, PIXEL).set(9, PIXEL).set(10, PIXEL).mkString("")

    screenData = screenData.set(0, row0)
    screenData = screenData.set(1, row1)

    val expectedState = State(
      memory = memory,
      register = emptyRegisters.set(1, 3.toByte).set(2, 0.toByte),
      screen = Screen(buffer = screenData),
      index = locationOfFont,
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)

    // apply the write a second time and it should clear everything and collision should be detected
    val actual2 = sut.exec(actual)

    val expectedState2 = State(
        memory = memory,
        register = emptyRegisters.set(1, 3.toByte).set(2, 0.toByte).set(STATUS_REGISTER_VF, 1.toByte),
        screen = Screen(),
        index = locationOfFont,
        pc = INITIAL_PC + 4
    )
    assertEquals(expectedState2, actual2)

  }

  @Test
  def testSetXEqY(): Unit = {
    val sut = SetXEqY("op", 1 ,2)

    val initialState = State(
      register = emptyRegisters.set(1, 3.toByte).set(2, 42.toByte),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(1, 42.toByte).set(2, 42.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqXMinusY_NotGt(): Unit = {
    val sut = XEqXMinusY("op", 1 ,2)

    val initialState = State(
      register = emptyRegisters.set(1, 30.toByte).set(2, 100.toByte),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(1, (-70.toByte).toByte).set(2, 100.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqXMinusY_Gt(): Unit = {
    val sut = XEqXMinusY("op", 1 ,2)

    val initialState = State(
      register = emptyRegisters.set(1, 100.toByte).set(2, 30.toByte),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(1, 70.toByte).set(2, 30.toByte).set(STATUS_REGISTER_VF, 1.toByte),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  def assertEquals(p1: Product, p2: Product): Unit = {
    org.junit.jupiter.api.Assertions.assertEquals(p1.productIterator.mkString("\n"), p2.productIterator.mkString("\n"))
  }
}