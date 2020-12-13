package terminal

import verification.Checks.checkTransmittedChar
import verification.Verification

object Echo extends App {

  val lines =
    """
      |fun main() {
      |  while(true) {
      |   var g = getchar();
      |   putchar(g)
      |  }
      |}
      |""".stripMargin

  Verification.compile(lines, verbose = true, timeout = 1000, dataIn = List("t1", "rA" ), outputCheck = {
    str =>
      checkTransmittedChar(str, 'A')
  })
}
