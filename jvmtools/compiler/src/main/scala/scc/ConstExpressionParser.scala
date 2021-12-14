package scc

import scala.language.postfixOps

trait ConstExpressionParser {
  self: SpamCC =>

  def SEMICOLON = ";"

  def name: Parser[String] = "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ (a => a)

  // permits \0 null char
  def quotedString: Parser[String] = ("\"" + """([^"\x01-\x1F\x7F\\]|\\[\\'"0bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\"").r ^^ {
    s =>
      val withoutQuotes = s.stripPrefix("\"").stripSuffix("\"")
      val str = org.apache.commons.text.StringEscapeUtils.unescapeJava(withoutQuotes)
      str
  }

  def decToken: Parser[Int] =
    """-?\d+""".r ^^ { v =>
      v.toInt
    }

  // any single char or \n is permitted
  def charToken: Parser[Int] = "'" ~> """\\?.""".r <~ "'" ^^ {
    v =>
      val c = org.apache.commons.text.StringEscapeUtils.unescapeJava(v)
//      val c = if (v == "\\n") {
//        //"\n"
//      } else {
//        v
//      }
      val i = c.codePointAt(0)
      if (i > 127) throw new RuntimeException(s"asm error: character '$v' (code:${v.toInt}) codepoint $i is outside the 0-127 range")
      i.toByte
  }

  def hexToken: Parser[Int] = "$" ~ "[0-9a-hA-H]+".r ^^ { case _ ~ v => Integer.valueOf(v, 16) }

  def binToken: Parser[Int] = "%" ~ "[01]+".r ^^ { case _ ~ v => Integer.valueOf(v, 2) }

  def octToken: Parser[Int] = "@" ~ "[0-7]+".r ^^ { case _ ~ v => Integer.valueOf(v, 8) }

  def constFactor: Parser[Int] = (charToken | decToken | hexToken | binToken | octToken | "(" ~> constExpression <~ ")")

  def constOperation: Parser[String] = "*" | "/" | "%" | "+" | "-" | ">" | "<" | "&" | "|" | "^" | "==" | "!="

  def constExpression: Parser[Int] = constFactor ~ ((constOperation ~ constFactor) *) ^^ {
    case x ~ list =>
      list.foldLeft(x)({
        case (acc, "*" ~ i) => acc * i
        case (acc, "/" ~ i) => acc / i
        case (acc, "%" ~ i) => acc % i
        case (acc, "+" ~ i) => acc + i
        case (acc, "-" ~ i) => acc - i
        case (acc, ">" ~ i) => if (acc > i) 1 else 0
        case (acc, "<" ~ i) => if (acc < i) 1 else 0
        case (acc, "&" ~ i) => acc & i
        case (acc, "|" ~ i) => acc | i
        case (acc, "^" ~ i) => acc ^ i
        case (acc, "==" ~ i) => if (acc == i) 1 else 0
        case (acc, "!=" ~ i) => if (acc != i) 1 else 0
      })
  }
}
