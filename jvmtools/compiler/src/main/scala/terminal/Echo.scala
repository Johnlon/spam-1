package terminal

import verification.Verification

object Echo extends App {

  val lines =
    """
      |fun main() {
      |  while(true) {
      |   var g = waituart();
      |   putuart(g)
      |  }
      |}
      |""".stripMargin

  Verification.compile(lines, verbose = true, timeout = 1000, dataIn = List("t100000000"))
}
