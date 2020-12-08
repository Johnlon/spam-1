package scc

import org.junit.Assert.{assertEquals, fail}

object Checks {


  def checkTransmitted(selector: Char = 'c', actual: List[String], expected: List[String]): Unit = {
    val matches = extractTransmitted(selector, actual)
    assertEquals(expected, matches)
  }

  def checkTransmittedC(str: List[String], c: Char): Unit = {
    checkTransmittedCN('c', str, c)
  }

  def checkTransmittedCN(selector: Char = 'c', str: List[String], expectedValue: Char, requiredCount: Int = 1): Unit = {
    val actual = extractTransmitted(selector, str)
    val matches = actual.count(_ == expectedValue.toString)

    if (matches == 0) fail(s"did not transmit '$expectedValue' char")
    if (matches < requiredCount) fail(s"did not find $requiredCount transmits of value '$expectedValue' ; found only $matches")
  }

  def checkTransmittedL(selector: Char, str: List[String], expected: List[String]): Unit = {
    val actual = extractTransmitted(selector, str)
    assertEquals(expected, actual)
  }

  private def extractTransmitted(selector: Char, str: List[String]): List[String] = {
    str.filter(_.contains(s"TRANSMITTING")).map(s => s.replaceAll(s".*\\[$selector:", "").replaceAll("\\].*", ""))
  }

}
