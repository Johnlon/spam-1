package verification

import org.junit.jupiter.api.Assertions._


object Checks {

  def checkTransmittedHex(actual: List[String], expected: String): Unit = {
    val matches = extractTransmitted('h', actual)
    assertEquals(List(expected), matches)
  }

  def checkTransmittedChars(actual: Seq[String], expected: Seq[String]): Unit = {
    val matches = extractTransmitted('c', actual)
    assertEquals(expected, matches)
  }

  def checkTransmittedDecs(actual: List[String], expected: List[Int]): Unit = {
    val matches = extractTransmitted('d', actual)
    assertEquals(expected.map(_.toString), matches)
  }

  def checkTransmittedDec(actual: List[String], expected: Int): Unit = {
    checkTransmittedCN('d', actual, expected.toString)
  }

  def checkTransmittedChar(str: List[String], c: Char): Unit = {
    checkTransmittedCN('c', str, c.toString)
  }

  def checkTransmittedCN(selector: Char = 'c', str: List[String], expectedValue: String, requiredCount: Int = 1): Unit = {
    val actual = extractTransmitted(selector, str)
    val matches = actual.count(_ == expectedValue)

    if (matches == 0) fail(s"did not transmit '$expectedValue' char")
    if (matches < requiredCount) fail(s"did not find $requiredCount transmits of value '$expectedValue' ; found only $matches")
  }

  def checkTransmittedL(selector: Char, str: List[String], expected: List[String]): Unit = {
    val actual = extractTransmitted(selector, str)
    println("TRANSMITTED : '" + actual + "'")
    assertEquals(expected, actual)
  }

  def checkTransmittedChars(selector: Char, str: List[String], expectedChar: List[Char]): Unit = {
    val expected = expectedChar.map(_.toInt.toString)
    val actual = extractTransmitted(selector, str)
    assertEquals(expected, actual)
  }

  private def extractTransmitted(selector: Char, str: Seq[String]): Seq[String] = {
    val transmitted = str.filter(_.contains(s"TRANSMITTING"))
    transmitted.map(s => s.replaceAll(s".*\\[$selector:", "").replaceAll("].*", ""))
  }

}
