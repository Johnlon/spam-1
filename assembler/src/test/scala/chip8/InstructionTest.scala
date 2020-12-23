package chip8

import chip8.Screen.BLOCK_CHAR
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.{Test, TestMethodOrder}

import scala.collection.mutable

@TestMethodOrder(classOf[MethodName])
class InstructionTest {

  val U100 = U8(100)
  val U70 = U8(70)
  val U30 = U8(30)

  private val THE_OP = "MYOP"

  @Test
  def tesGoSub(): Unit = {
    val sut = GoSub(THE_OP, 123)

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
    val sut = Jump(THE_OP, 123)

    val initialState = State()
    val actual = sut.exec(initialState)

    val expectedState = State().copy(
      pc = 123
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testReturnSub(): Unit = {
    val sut = ReturnSub(THE_OP)

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
    val sut = ClearScreen(THE_OP)

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
    val sut = SkipIfXEqN(THE_OP, U8(0), U8(123))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXEqN_Neq(): Unit = {
    val sut = SkipIfXEqN(THE_OP, U8(0), U8(123))

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeN_Eq(): Unit = {
    val sut = SkipIfXNeN(THE_OP, U8(0), U8(123))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeN_Neq(): Unit = {
    val sut = SkipIfXNeN(THE_OP, U8(0), U8(123))

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSkipIfXEqY_Eq(): Unit = {
    val sut = SkipIfXEqY(THE_OP, U8(0), U8(1))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123)).set(1, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)).set(1, U8(123)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXEqY_Neq(): Unit = {
    val sut = SkipIfXEqY(THE_OP, U8(0), U8(1))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSkipIfXNeY_Eq(): Unit = {
    val sut = SkipIfXNeY(THE_OP, U8(0), U8(1))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123)).set(1, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)).set(1, U8(123)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSkipIfXNeY_Neq(): Unit = {
    val sut = SkipIfXNeY(THE_OP, U8(0), U8(1))

    val initialState = State(
      register = emptyRegisters.set(0, U8(123))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(123)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testSetX(): Unit = {
    val sut = SetX(THE_OP, U8(0), U8(1))

    val initialState = State()

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(1)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testAddX(): Unit = {
    val sut = AddX(THE_OP, U8(0), U8(1))

    val initialState = State(
      register = emptyRegisters.set(0, U8(100)),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(0, U8(101)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testSetIndex(): Unit = {
    val sut = SetIndex(THE_OP, 1)

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
    val sut = Display(THE_OP, U8(1), U8(2), U8(2))

    val locationOfFont = 100
    val memory = emptyMemory.set(locationOfFont, U8(0xaa)).set(locationOfFont + 1, U8(0xff))
    val initialState = State(
      register = emptyRegisters.set(1, U8(3)).set(2, U8(0)),
      memory = memory,
      index = locationOfFont
    )

    // above
    // X=3 Y=0 and 2 rows high
    // index points to font

    val actual = sut.exec(initialState)

    var screenData = Screen().buffer
    val row0 = screenData(0).set(3, BLOCK_CHAR).set(5, BLOCK_CHAR).set(7, BLOCK_CHAR).set(9, BLOCK_CHAR).mkString("")
    val row1 = screenData(1).set(3, BLOCK_CHAR).set(4, BLOCK_CHAR).set(5, BLOCK_CHAR).set(6, BLOCK_CHAR).set(7, BLOCK_CHAR).set(8, BLOCK_CHAR).set(9, BLOCK_CHAR).set(10, BLOCK_CHAR).mkString("")

    screenData = screenData.set(0, row0)
    screenData = screenData.set(1, row1)

    val expectedState = State(
      memory = memory,
      register = emptyRegisters.set(1, U8(3)).set(2, U8(0)),
      screen = Screen(buffer = screenData),
      index = locationOfFont,
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)

    // apply the write a second time and it should clear everything and collision should be detected
    val actual2 = sut.exec(actual)

    val expectedState2 = State(
      memory = memory,
      register = emptyRegisters.set(1, U8(3)).set(2, U8(0)).set(STATUS_REGISTER_ID, U8(1)),
      screen = Screen(),
      index = locationOfFont,
      pc = INITIAL_PC + 4
    )
    assertEquals(expectedState2, actual2)

  }

  @Test
  def testSetXEqY(): Unit = {
    val sut = SetXEqY(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.set(1, U8(3)).set(2, U8(42)),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.set(1, U8(42)).set(2, U8(42)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqXMinusY_Carry(): Unit = {
    val sut = XEqXMinusY(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.set(1, U8(0)).set(2, U8(1))
        .set(STATUS_REGISTER_ID, U8(1)), // this unsets the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf(255 /*== -1*/)).
        set(2, U8(1)).
        set(STATUS_REGISTER_ID, U8(0)), // expect carry
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqXMinusY_NoCarryEq(): Unit = {
    val sut = XEqXMinusY(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters
        .set(1, U8(1))
        .set(2, U8(1))
        .set(STATUS_REGISTER_ID, U8(0)), // this sets the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8(0)).
        set(2, U8(1)).
        set(STATUS_REGISTER_ID, U8(1)), // should be unset
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqXMinusY_NoCarry(): Unit = {
    val sut = XEqXMinusY(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.set(1, U8(100))
        .set(2, U8(30))
        .set(STATUS_REGISTER_ID, U8(0)), // this sets the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8(70)).
        set(2, U8(30)).
        set(STATUS_REGISTER_ID, U8(1)), // should be unset
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqYMinusX_NoCarry(): Unit = {
    val sut = XEqYMinusX(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8(1)).
        set(2, U8(4)).
        set(STATUS_REGISTER_ID, U8(0)), // this sets the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8(3)).
        set(2, U8(4)).
        set(STATUS_REGISTER_ID, U8(1)), // should clear the bit
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqYMinusX_NoCarryEq(): Unit = {
    val sut = XEqYMinusX(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8(1)).
        set(2, U8(1)).
        set(STATUS_REGISTER_ID, U8(0)), // this sets the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8(0)).
        set(2, U8(1)).
        set(STATUS_REGISTER_ID, U8(1)), // should clear the bit
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqYMinusX_Carry(): Unit = {
    val sut = XEqYMinusX(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8(4)).
        set(2, U8(1)).
        set(STATUS_REGISTER_ID, U8(1)), // this clears the carry bit
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8(253)).
        set(2, U8(1))
        set(STATUS_REGISTER_ID, U8(0)), // this sets the carry bit
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqLogicalOr(): Unit = {
    val sut = XEqLogicalOr(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(2, U8.valueOf("00001111", 2))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01011111", 2)).
        set(2, U8.valueOf("00001111", 2)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXEqLogicalAnd(): Unit = {
    val sut = XEqLogicalAnd(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(2, U8.valueOf("00001111", 2))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("00000101", 2)).
        set(2, U8.valueOf("00001111", 2)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }


  @Test
  def testXEqLogicalXor(): Unit = {
    val sut = XEqLogicalXor(THE_OP, U8(1), U8(2))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(2, U8.valueOf("00001111", 2))
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01011010", 2)).
        set(2, U8.valueOf("00001111", 2)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)
  }

  @Test
  def testXShiftRight(): Unit = {
    val sut = XShiftRight(THE_OP, U8(1))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(STATUS_REGISTER_ID, U8(0)),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("00101010", 2)).
        set(STATUS_REGISTER_ID, U8(1)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)

    val actual2 = sut.exec(expectedState)

    val expectedState2 = State(
      register = emptyRegisters.
        set(1, U8.valueOf("00010101", 2)).
        set(STATUS_REGISTER_ID, U8(0)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState2, actual2)
  }

  @Test
  def testXShiftLeft(): Unit = {
    val sut = XShiftLeft(THE_OP, U8(1))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(STATUS_REGISTER_ID, U8(1)),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("10101010", 2)).
        set(STATUS_REGISTER_ID, U8(0)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)

    val actual2 = sut.exec(expectedState)

    val expectedState2 = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010100", 2)).
        set(STATUS_REGISTER_ID, U8(1)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState2, actual2)
  }

  @Test
  def testStoreRegisters(): Unit = {
    val sut = StoreRegisters(THE_OP, U8(1))

    val initialState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010101", 2)).
        set(STATUS_REGISTER_ID, U8(1)),
    )

    val actual = sut.exec(initialState)

    val expectedState = State(
      register = emptyRegisters.
        set(1, U8.valueOf("10101010", 2)).
        set(STATUS_REGISTER_ID, U8(0)),
      pc = INITIAL_PC + 2
    )

    assertEquals(expectedState, actual)

    val actual2 = sut.exec(expectedState)

    val expectedState2 = State(
      register = emptyRegisters.
        set(1, U8.valueOf("01010100", 2)).
        set(STATUS_REGISTER_ID, U8(1)),
      pc = INITIAL_PC + 4
    )

    assertEquals(expectedState2, actual2)
  }

  def assertEquals(p1: Product, p2: Product): Unit = {
    org.junit.jupiter.api.Assertions.assertEquals(p1.productIterator.mkString("\n"), p2.productIterator.mkString("\n"))
  }
}