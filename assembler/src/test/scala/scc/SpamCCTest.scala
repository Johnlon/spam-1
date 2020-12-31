package scc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.{Test, TestMethodOrder}
import verification.Checks._
import verification.Verification._

@TestMethodOrder(classOf[MethodName])
class SpamCCTest {

  def split(s: String): List[String] = {
    val strings = s.split("\n")
    strings
      .map(_.stripTrailing().stripLeading())
      .filterNot(_.isBlank).toList
  }

  def assertSame(expected: List[String], actual: List[String]): Unit = {
    println("\nCOMPARING ASM:")
    if (expected != actual) {
      println("Expected: " + expected)
      println("Actual  : " + actual)

      val e = expected.map(_.stripTrailing().stripLeading()).mkString("\n")
      val a = actual.map(_.stripTrailing().stripLeading()).mkString("\n")
      assertEquals(e, a)
    }
  }

  @Test
  def varEq1(): Unit = {

    val lines =
      """fun main() {
        | uint16 a=1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0, 0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |REGA = > 1
        |REGD = < 1
        |[:root_function_main___VAR_a] = REGA
        |[:root_function_main___VAR_a+1] = REGD
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varEqHex4142(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        |
        | // $42 = 66 dev
        | // $41 = 65 dec
        | uint16 a = $4142;
        |
        | // $4142 >> 1 => 20A1
        | // $A1 = 161 dec
        | putchar(a >> 1)
        |
        | uint16 b = a >> 1;
        | putchar(b) // 161
        |
        | // $4142 & $ff => 42 => 66 dec
        | putchar(a) // should be 66
        | putchar(a & $ff )
        |
        |}
        |
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>

        checkTransmittedL('d', str, List("161", "161", "66", "66"))
    })
  }

  @Test
  def varEqIfCondition(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        |
        | // = 1110
        | uint16 a = $0100;
        | a = a + $1010;
        |
        | if (a>$1110) {
        |   putchar(1)
        | }
        | if (a<$1110) {
        |   putchar(2)
        | }
        | if (a==$1110) {
        |   putchar(3)
        | }

        | if (a>$1010) {
        |   putchar(11)
        | }
        | if (a<$1010) {
        |   putchar(12)
        | }
        | if (a==$1010) {
        |   putchar(13)
        | }
        |
        | if (a>$1210) {
        |   putchar(21)
        | }
        | if (a<$1210) {
        |   putchar(22)
        | }
        | if (a==$1210) {
        |   putchar(23)
        | }
        |
        | // comparing lower byte
        | if (a>$1101) {
        |   putchar(31)
        | }
        | if (a<$1101) {
        |   putchar(32)
        | }
        | if (a==$1101) {
        |   putchar(33)
        | }
        |
        | if (a>$1120) {
        |   putchar(41)
        | }
        | if (a<$1120) {
        |   putchar(42)
        | }
        | if (a==$1120) {
        |   putchar(43)
        | }
        |
        | // compare
        | uint16 b=$1000;
        |
        | if ( a == (b+$110) ) {
        |  putchar('=')
        | }
        |
        | if ( a != (b+$110) ) {
        |  putchar('~')
        | }
        |
        | if ( a != b ) {
        |  putchar('!')
        | }
        |
        |}
        |
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('d', str, List("3", "11", "22", "31", "42" , '='.toInt.toString, '!'.toInt.toString))
    })
  }

  @Test
  def varEqVar(): Unit = {

    val lines =
      """fun main() {
        | uint16 a=1;
        | uint16 b=a;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0, 0]
        |root_function_main___VAR_b: EQU   4
        |root_function_main___VAR_b: BYTES [0, 0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |REGA = > 1
        |REGD = < 1
        |[:root_function_main___VAR_a] = REGA
        |[:root_function_main___VAR_a+1] = REGD
        |REGA = [:root_function_main___VAR_a]
        |REGD = [:root_function_main___VAR_a + 1]
        |[:root_function_main___VAR_b] = REGA
        |[:root_function_main___VAR_b+1] = REGD
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varEqConstExpr(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a=1;
        | uint16 b=64+1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0, 0]
        |root_function_main___VAR_b: EQU   4
        |root_function_main___VAR_b: BYTES [0, 0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |REGA = > 1
        |REGD = < 1
        |[:root_function_main___VAR_a] = REGA
        |[:root_function_main___VAR_a+1] = REGD
        |REGA = > 65
        |REGD = < 65
        |[:root_function_main___VAR_b] = REGA
        |[:root_function_main___VAR_b+1] = REGD
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }


  @Test
  def twoFunctionsSameVarNames(): Unit = {

    val lines =
      """
        |fun other() {
        | uint16 a=100;
        | uint16 b=200;
        | putchar(a)
        | putchar(b)
        |}
        |fun main() {
        | uint16 a=1;
        | uint16 b=2;
        | other()
        | putchar(a)
        | putchar(b)
        |}
        |""".stripMargin


    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('d', str, List("100", "200", "1", "2"))
    })
  }

  @Test
  def varEqSimpleTwoArgExprNOTIMPL(): Unit = {

    val lines =
      """
        |fun main() {
        |  // a = 63 + 2 = 'A'
        |  uint16 a = 63 + 2;
        |
        |  // b = a + 1 = 'B'
        |  uint16 b = a + 1;
        |
        |  // c = 1 + b = 'C'
        |  uint16 c = 1 + b;
        |
        |  // d = c the d++
        |  uint16 d = c;
        |  d = d + 1;
        |
        |  // e = a + (b/2) = 'b'
        |  uint16 e = a + (b/2);
        |
        |  // should print 'A'
        |  putchar(a)
        |  // should print 'B'
        |  putchar(b)
        |  // should print 'C'
        |  putchar(c)
        |  // should print 'D'
        |  putchar(d)
        |  // should print 'b'
        |  putchar(e)
        |
        |  // should shift left twice to become the '@' char
        |  a = %00010000;
        |  b = 2;
        |  uint16 at = a A_LSL_B b;
        |  // should print '@'
        |  putchar(at)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
      checkTransmittedChar(str, 'b')
      checkTransmittedChar(str, '@')
    })
  }

  @Test
  def varEqNestedExpr(): Unit = {

    val lines =
      """fun main() {
        |  uint16 a = 64;
        |  uint16 b = 1 + (a + 3);
        |  putchar(b)
        |}
        |""".stripMargin

    compile(lines, outputCheck = str => {
      checkTransmittedChar(str, 'D')
    })

  }

  @Test
  def putchar(): Unit = {

    val lines =
      """
        |fun main() {
        |  putchar(65)
        |  putchar('B')
        |  uint16 c=67;
        |  putchar(c)
        |  putchar(c+1)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })

  }

  @Test
  def getchar(): Unit = {

    val lines =
      """
        |fun main() {
        |  uint16 g = getchar();
        |  putchar(g)
        |}
        |""".stripMargin

    compile(lines, verbose = true, timeout = 1000, dataIn = List("t1", "rA"), outputCheck = {
      str =>
        checkTransmittedChar(str, 'A')
    })
  }

  @Test
  def valEqVarLogicalNOTIMPL(): Unit = {

    // NOT IMPLEMENTED YET
    val lines =
      """
        |fun main() {
        | uint16 b=1024;
        |
        | uint16 a1 = b>1;
        | putchar(a1) // true
        |
        | uint16 a2= b==0;
        | putchar(a2) // false
        |
        | uint16 a3= b==1;
        | putchar(a3) // false
        |
        | uint16 a4= b==1024;
        | putchar(a4) // true
        |
        | // compare b==(a+24)
        | uint16 a5=1000;
        | uint16 a6= b==(a5+24);
        | putchar(a6) // true
        |
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('h', str, List("01", "00", "00", "01", "01"))
    })
  }

  @Test
  def valEqConstLogical(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a=1>0;
        | putchar(a)
        |
        | a=0>1;
        | putchar(a)
        |
        | a=0==1;
        | putchar(a)
        |
        | a=1==1;
        | putchar(a)
        |
        | a=%1010 & %1100;
        | putchar(a)

        | a=%1010 | %1100;
        | putchar(a)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('h', str, List("01", "00", "00", "01", "08", "0e"))
    })
  }

  @Test
  def whileLoopCond(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a=1260;
        | while(a>1250) {
        |   a=a-1;
        |   putchar(a-1250)
        | }
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      lines =>
//        checkTransmittedL('h', lines, List("01", "00", "00", "01", "08", "0e"))
        checkTransmittedDecs(lines, List(9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
    })
  }


  @Test
  def whileLoopTrueIfBreak(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a = 1;
        | while(true) {
        |   a = a + 1;
        |
        |   if (a>10) {
        |     break
        |   }
        |   putchar(a)
        |
        | }
        |}
        |""".stripMargin

    compile(lines, outputCheck = {
      lines =>
        checkTransmittedDecs(lines, List(2, 3, 4, 5, 6, 7, 8, 9, 10))
    })
}

  @Test
  def functionCalls(): Unit = {

    val lines =
      """
        |// START FN COMMAND
        |
        |fun print(a1 out, a2, a3, a4) {
        | // FN COMMENT
        | uint16 d = a1;
        | //d = a2;
        | putchar(d)
        | putchar(a2)
        | putchar(a3)
        | putchar(a4)
        |
        | // ascii 33 dec
        | a1 = '!';
        | // END FN COMMENT
        |}
        |
        |fun main() {
        | uint16 arg1 = 'A';
        | uint16 arg2 = 1;
        |
        | // CALLING PRINT - 63 is '?'
        | print(arg1, arg2+arg1, 63, arg1+4)
        |
        | // CALLING PUT CHAR OF OUT VALUE
        | putchar(arg1)
        |
        |}
        |
        |// END  COMMAND
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, '?')
      checkTransmittedChar(str, 'E')
      checkTransmittedChar(str, '!')
    })

  }

  @Test
  def functionCalls2Deep(): Unit = {

    val lines =
      """
        |fun depth2(b1 out) {
        | b1 = b1 + 1;
        |}
        |
        |fun depth1(a1 out) {
        | depth2(a1)
        |}
        |
        |fun main() {
        | uint16 arg1 = 'A';
        | depth1(arg1)
        | putchar(arg1)
        |}
        |
        |// END  COMMAND
        |""".stripMargin

    compile(lines, quiet = true, outputCheck = {
      lines =>
        checkTransmittedChars(lines, List("B"))
    })
  }

  @Test
  def referencesNOTIMPL(): Unit = {

    val lines =
      """
        |fun main() {
        |
        | // define string
        | var even = "Even\0";
        | var odd = "Odd\0";
        |
        | // value at 16 bit var ptr becomes address of array odd
        | ref ptr = odd;
        |
        | uint16 i = 10;
        | while (i>0) {
        |   i = i - 1;
        |   uint16 c = i % 2;
        |   if (c == 0) {
        |       // set pointer to point at even
        |       ptr = even;
        |   }
        |   if (c != 0) {
        |      ptr = odd;
        |   }
        |   puts(ptr)
        | }
        |}
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      val value: List[String] = "OddEvenOddEvenOddEvenOddEvenOddEven".toList.map(_.toString)
      checkTransmittedL('c', str, value)
    })
  }

  @Test
  def referenceLongNOTIMPL(): Unit = {

    val data = (0 to 255) map {x =>
      f"$x%02x"
    } mkString("")

    val lines =
      s"""
        |fun main() {
        |
        | // define string
        | var string = "$data\\0";
        |
        | // value at 16 bit var ptr becomes address of array odd
        | uint16 addr16 = string;
        | uint16 idx = 0;
        | uint16 c = string[idx];
        | ref ptr = string;
        |
        | uint16 i = 255;
        | while (i>0) {
        |   uint16 lo = ptr;
        |   ptr  = ptr + 1;
        |   uint16 hi = ptr;
        |   ptr  = ptr + 1;
        |
        |   i = i - 1;
        | }
        |}
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      val value: List[String] = "OddEvenOddEvenOddEvenOddEvenOddEven".toList.map(_.toString)
      checkTransmittedL('c', str, value)
    })
  }

  @Test
  def stringIndexing(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        |
        | // index by literal
        | uint16 ac = string[0];
        |
        | // index by variable
        | uint16 b = 1;
        | uint16 bc = string[b];
        |
        | // print values so we can test correct values selected
        | uint16 d = 3;
        | putchar(ac)
        | putchar(bc)
        | putchar(string[2])
        | putchar(string[d])
        |}
        |""".stripMargin

    compile(lines, verbose = false, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })
  }

  @Test
  def stringIteration(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        |
        | uint16 idx = 0;
        | uint16 c = string[idx];
        | while (c != 0) {
        |   putchar(c)
        |   idx = idx + 1;
        |   c = string[idx];
        | }
        |}
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })
  }

  @Test
  def snake(): Unit = {
    import terminal.UARTTerminal._

    val lines =
      s"""fun main() {
         | uint16 loop = 0;
         | while ( loop <= 2) {
         |  uint16 a = 33 + loop;
         |
         |  uint16 b = 2;
         |  while ( b > 0 ) {
         |   putchar(${DO_RIGHT.toInt})
         |   putchar( a )
         |   b = b - 1;
         |  }
         |  b = 2;
         |  while ( b > 0 ) {
         |   putchar(${DO_DOWN.toInt})
         |   putchar( a )
         |   b = b - 1;
         |  }
         |  b = 2;
         |  while ( b > 0 ) {
         |   putchar(${DO_LEFT.toInt})
         |   putchar( a )
         |   b = b - 1;
         |  }
         |
         |  b = 2;
         |  while ( b > 0 ) {
         |   putchar(${DO_UP.toInt})
         |   putchar( a )
         |   b = b - 1;
         |  }
         |  putchar(${DO_RIGHT.toInt})
         |  putchar(${DO_DOWN.toInt})
         |
         |  loop = loop + 1;
         | }
         |}
         |
         |// END  COMMAND
         |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedDec(str, DO_RIGHT)
      checkTransmittedDec(str, DO_DOWN)
      checkTransmittedDec(str, DO_LEFT)
      checkTransmittedDec(str, DO_UP)
    })

  }

  @Test
  def puts(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        | puts(string)
        |}
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })
 }
}
