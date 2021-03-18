package scc

import java.io.{File, FileOutputStream}
import org.junit.jupiter.api.Assertions.{assertEquals, fail}
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.{Test, TestMethodOrder}
import verification.Checks._
import verification.HaltedException
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
  def varFromFile(): Unit = {

    val lines =
      """fun main() {
        | var s = [file("src/test/resources/SomeData.txt")];
        |}
        |""".stripMargin


    val actual = compile(lines, verbose = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_s: EQU   2
        |root_function_main___VAR_s: BYTES [65, 66, 10, 67]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varFromFileProp(): Unit = {

    val fileName = "src/test/resources/SomeData.txt"
    System.setProperty("FILENAME", fileName)
    val lines =
      """fun main() {
        | var s = [file(systemProp("FILENAME"))];
        |}
        |""".stripMargin

    val actual = compile(lines, verbose = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_s: EQU   2
        |root_function_main___VAR_s: BYTES [65, 66, 10, 67]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varFromData(): Unit = {

    val lines =
      """fun main() {
        | var s = [00 01 $ff];
        |}
        |""".stripMargin


    val actual = compile(lines, verbose = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_s: EQU   2
        |root_function_main___VAR_s: BYTES [0, 1, -1]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varFromLongFile(): Unit = {

    val lines =
      """fun main() {
        | var s = [file("src/test/resources/LotOfData.txt")];
        | uint16 a0 = s[0];
        | uint16 a255= s[255];
        | uint16 a256 = s[256];
        | uint16 a338 = s[337];
        | putchar(a0)
        | putchar(a255)
        | putchar(a256)
        | putchar(a338)
        |
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedChars('d', str, List('0', '5', '6', 'F'))
    })
  }

  @Test
  def varFromAll255File(): Unit = {

    val data = File.createTempFile(this.getClass.getName, ".dat")
    val os = new FileOutputStream(data)
    (0 to 255).foreach {
      x => os.write(x)
    }
    os.close()

    // backslash in names look like escapes
    val fileForwrdSlash = data.getAbsolutePath.replaceAll("\\\\", "/")

    val lines =
      s"""fun main() {
         | var s = [file("$fileForwrdSlash")];
         |}
         |""".stripMargin

    val actual = compile(lines, verbose = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_s: EQU   2
        |root_function_main___VAR_s: BYTES [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varFromMixedData(): Unit = {

    val data = File.createTempFile(this.getClass.getName, ".dat")
    val os = new FileOutputStream(data)
    ('a' to 'c').foreach {
      x => os.write(x)
    }
    os.close()

    // backslash in names look like escapes
    val fileForwardSlash = data.getAbsolutePath.replaceAll("\\\\", "/")

    val lines =
      s"""fun main() {
         | var s = [
         |    2: [file("$fileForwardSlash")]
         |    10: [ 'A' 'B' 'C' ]
         |    15: [ ]
         | ];
         |}
         |""".stripMargin

    val actual = compile(lines, verbose = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_s: EQU   2
        |root_function_main___VAR_s: BYTES [0, 0, 97, 98, 99, 0, 0, 0, 0, 0, 65, 66, 67, 0, 0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
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
  def varLSR(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        |
        | uint16 a = $4142;
        |
        | putchar(a)
        | putchar(a >> 0)
        | putchar(a >> 1)
        | putchar(a >> 8)
        | putchar(a >> 9)
        | putchar(a >> 16)
        |
        |}
        |
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>

        checkTransmittedL('d', str, List("66","66", "161", "65", "32", "0"))
    })
  }

  @Test
  def varLSL(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        |
        | uint16 b = %1001;

        | if ( (b<<1) == %00010010 ) {
        |   putchar('=')
        | }
        | if ( (b<<8) == %0000100100000000 ) {
        |   putchar('=')
        | }
        |
        | putchar( b << 0 )
        | putchar( b << 1 )
        | putchar( b << 2 )
        | putchar( b << 3 )
        | putchar( b << 4 )
        | putchar( b << 7 )
        | putchar( b << 8 )
        |
        | uint16 c = b << 12;
        | if ( (b<<12) == %1001000000000000 ) {
        |   putchar('=')
        | }
        |
        | // double check value was preserved
        | putchar( c >> 12 )
        |
        |}
        |
        |""".stripMargin

    val EqAsBin = "00111101"
    compile(lines, verbose = true, outputCheck = {
      str =>

        checkTransmittedL('b', str, List(EqAsBin,EqAsBin,
          "00001001", "00010010", "00100100", "01001000", "10010000", "10000000", "00000000", EqAsBin, "00001001"))
    })
  }

  @Test
  def varLogicalAnd(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        |
        | uint16 a = $ABCD;
        |
        | putchar(a & $f0)
        | putchar(a & $0f)
        | putchar( (a>> 8) & $f0)
        | putchar( a>> 12)
        | putchar( (a>> 8) & $0f)
        |
        | if ( (a & $f000) == $a000 ) {
        |   putchar( '=' )
        | }
        |
        | if ( (a & $f000) == $a001 ) {
        |   // not expected to go here
        |   putchar( '!' )
        | }
        |
        |}
        |
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>

        checkTransmittedL('h', str, List("c0","0d","a0","0a", "0b", '='.toInt.toHexString))
    })
  }

  @Test
  def varEqIfElse(): Unit = {

    val lines =
      """
        |
        |fun main(x) {
        | uint16 a = 2;
        |
        | if (a>2) {
        |   putchar(1)
        | } else {
        |   if (a<2) {
        |     putchar(2)
        |   }
        |   else { // ==
        |     putchar(3)
        |   }
        | }
        |
        | if (a>2) {
        |   putchar(1)
        | } else if (a<2) {
        |     putchar(2)
        | }
        | else { // ==
        |     putchar(3)
        | }
        |}
        |
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('d', str, List("3", "3"))
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
        | } else
        |   if (a<$1110) {
        |     putchar(2)
        |   }
        |   else { // ==
        |     putchar(3)
        |   }
        |
        |
        | if (a>$1010) {
        |   putchar(11)
        | } else
        |   if (a<$1010) {
        |     putchar(12)
        |   } else { // ==
        |     putchar(13)
        |   }
        |
        |
        | if (a>$1210) {
        |   putchar(21)
        | } else if (a<$1210) {
        |   putchar(22)
        | } else { // ==
        |   putchar(23)
        | }
        |
        |
        | // comparing lower byte
        | if (a>$1101) {
        |   putchar(31)
        | } else {
        |   if (a<$1101) {
        |     putchar(32)
        |   } else { // ==
        |     putchar(33)
        |   }
        | }
        |
        | if (a>$1120) {
        |   putchar(41)
        | } else {
        |   if (a<$1120) {
        |     putchar(42)
        |   } else { // ==
        |     putchar(43)
        |   }
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
        checkTransmittedL('d', str, List("3", "11", "22", "31", "42", '='.toInt.toString, '!'.toInt.toString))
    })
  }

  @Test
  def varScopeVarsWithIfBlock(): Unit = {

    val lines =
      """
        |fun main(x) {
        | uint16 a = 0;
        | if (a == 0) {
        |     uint16 thevar = 0;
        | }
        | else if (a == 1) {
        |     uint16 thevar = 1;
        | }
        |}
        |""".stripMargin

    compile(lines, verbose = true)
    // no other assertion yet
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
  def valEqVarAdd(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a='a';
        | uint16 c = a + 2;
        | putchar(c)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('c', str, List("c"))
    })
  }

  @Test
  def valEqVarMinus(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 c='c';
        | uint16 a = c - 2;
        | putchar(c)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('c', str, List("a"))
    })
  }

  @Test
  def valEqVarTimes(): Unit = {

    // TIMES NOT IMPLEMENTED YET
    val lines =
      """
        |fun main() {
        | uint16 s = 'a' / 2; // const expr
        | uint16 a = s * 2; // alu expr
        | putchar(c)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('c', str, List("a"))
    })
  }

  @Test
  def valEqVarDivide(): Unit = {

    // IMPLEMENTED BUT NOT WORKING
    val lines =
      """
        |fun main() {
        | uint16 a=8; // const expr
        |
        | uint16 res = a / 3; // alu expr
        | putchar(res)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        val expected = "2"
        checkTransmittedL('d', str, List(expected))
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
        |
        | while(true) {
        |   a = a + 1;
        |
        |   if (a>10) {
        |     break
        |   }
        |   putchar(a)
        |
        | }
        |
        | while(a < 100) {
        |   a = a - 1;
        |
        |   if (a==0) {
        |     break
        |   }
        |   putchar(a)
        |
        | }
        |}
        |""".stripMargin

    compile(lines, outputCheck = {
      lines =>
        checkTransmittedDecs(lines, List(2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1))
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
  def blockComment(): Unit = {

    // TODO USE "export" to allow sharing a var into static subroutines in call tree (not sure if will work for stack based calls)
    val lines =
      """
        |/* top comment */
        |//  export uint16 s = 'A';
        |
        |fun main() {
        | //putchar(a)
        | /*
        | commented
        | out
        | */
        |}
        |""".stripMargin

    val actual = compile(lines, timeout=5, quiet = true)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def referencesNOTIMPL(): Unit = {

    val lines =
      """
        |fun main() {
        |
        | // define string
        | var even = ["Even\0"];
        | var odd = ["Odd\0"];
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

    val data = (0 to 255) map { x =>
      f"$x%02x"
    } mkString ("")

    val lines =
      s"""
         |fun main() {
         |
         | // define string
         | var string = ["$data\\0"];
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
  def stringIndexingRead(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = ["ABCD\0"];
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
  def stringIndexingWrite(): Unit = {

    val lines =
      """
        |fun main() {
        | var string = ["ABCD\0"];
        | string[1] = '!';
        |
        | // define string
        | putchar(string[0])
        | putchar(string[1])
        | putchar(string[2])
        |
        | // this expression creates further temp vars in addition to those created on string[1] assign above - code gen is expected to generate unique names (INDEX_LO/HI)
        | string[1] = string[1] + 2;
        | putchar(string[1])
        |}
        |""".stripMargin

    compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChars(str, Seq("A", "!", "C", "#"))
    })
  }

  @Test
  def stringIteration(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = ["ABCD\0"];
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
        | var string = [ "ABCD\0" ];
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

  @Test
  def halt(): Unit = {

    val lines =
      """
        |fun main() {
        | halt(65432)
        |}
        |""".stripMargin

    try {
      compile(lines, timeout=5, quiet = true)
      fail("should have halted")
    } catch {
      case ex: HaltedException if ex.halt == 65432 =>
        println("halted ok")
      case ex: HaltedException  =>
        fail("halted with wrong code " + ex.halt)
      case ex =>
        fail("unexpected exception : " + ex.getMessage)
    }
  }

  @Test
  def haltVar(): Unit = {

    val lines =
      """
        |fun main() {
        | uint16 a = 65432;
        | halt(a)
        |}
        |""".stripMargin

    try {
      compile(lines, timeout=5, quiet = true)
      fail("should have halted")
    } catch {
      case ex: HaltedException if ex.halt == 65432 =>
        println("halted ok")
      case ex: HaltedException  =>
        fail("halted with wrong code " + ex.halt)
      case ex =>
        fail("unexpected exception : " + ex.getMessage)
    }
  }
}
