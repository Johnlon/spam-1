package cpp

import scala.util.parsing.combinator.JavaTokenParsers

class Cpp extends JavaTokenParsers {

  def token: Parser[_] = keyword | identifier | constant | string_literal | punctuator

  def keyword: Parser[String] = "auto" | "break" | "case" | "char" | "const" | "continue" | "default" | "do" | "double" | "else" |
    "enum" | "extern" | "float" | "for" | "goto" | "if" | "inline" | "int" | "long" | "register" | "restrict" | "return" |
    "short" | "signed" | "sizeof" | "static" | "struct" | "switch" | "typedef" | "union" | "unsigned" | "void" | "volatile" | "while"

  // IDENTIFIERS

  def identifier: Parser[_] = identifier_nondigit |
    (identifier ~ identifier_nondigit) |
    (identifier ~ digit)

  def identifier_nondigit = nondigit | universal_character_name

  def nondigit: Parser[String] = "[a-zA-Z_]".r

  def digit: Parser[String] = "[0-9]".r

  def universal_character_name = "\\u" ~ hex_quad | "\\U" ~ hex_quad ~ hex_quad

  def hex_quad = hexadecimal_digit ~ hexadecimal_digit ~ hexadecimal_digit ~ hexadecimal_digit

  def hexadecimal_digit: Parser[String] = "[a-fA-F0-9]".r

  // CONSTANTS
  // https://eel.is/c++draft/lex.icon

  def constant = integer_constant | floating_constant | enumeration_constant | character_constant

  def integer_constant: Parser[Int] =
    decimal_constant ~ opt(integer_suffix) |
      binary_constant ~ opt(integer_suffix) |
      octal_constant ~ opt(integer_suffix) |
      hexadecimal_constant ~ opt(integer_suffix)

  def decimal_constant = nonzero_digit | decimal_constant ~ digit

  def binary_constant = binary_prefix ~ binary_digit | binary_constant ~ binary_digit

  def binary_prefix: Parser[String] = "0[bB]".r

  def binary_digit: Parser[String] = "[01]".r

  def octal_constant: Parser[String] = "0" | octal_constant ~ octal_digit

  def hexadecimal_constant: Parser[String] = hexadecimal_prefix ~ hexadecimal_digit | hexadecimal_constant ~ hexadecimal_digit

  def hexadecimal_prefix: Parser[String] = "0[xX]".r

  def nonzero_digit: Parser[String] = "[1-9]".r

  def octal_digit: Parser[String] = "[0-7]".r

  def hexadecimal_digit: Parser[String] = "[a-fA-F0-9]".r

  def integer_suffix =
    unsigned_suffix ~ opt(long_suffix) |
      unsigned_suffix ~ opt(long_long_suffix) |
      long_suffix ~ opt(unsigned_suffix) |
      long_long_suffix ~ opt(unsigned_suffix)

  def unsigned_suffix: Parser[String] = "[uU]".r

  def long_suffix: Parser[String] = "[lL]".r

  def long_long_suffix: Parser[String] = "ll" | "LL"

  def floating_constant: Parser = decimal_floating_constant | hexadecimal_floating_constant

  def decimal_floating_constant: Parser =
    fractional_constant ~ opt(exponent_part) ~ opt(floating_suffix) |
      digit_sequence ~ exponent_part ~ opt(floating_suffix)

  def hexadecimal_floating_constant: Parser =
    hexadecimal_prefix ~ hexadecimal_fractional_constant ~ opt(binary_exponent_part) ~ opt(floating_suffix) |
      hexadecimal_prefix ~ hexadecimal_digit_sequence ~ binary_exponent_part ~ opt(floating_suffix)

  def fractional_constant: Parser = opt(digit_sequence) ~ "." ~ digit_sequence | digit_sequence

  def exponent_part: Parser = "[eE]".r ~ opt(sign) ~ digit_sequence

  def sign: Parser[String] = "+" | "-"

  def digit_sequence: Parser = digit | digit_sequence ~ digit

  def hexadecimal_fractional_constant: Parser = (opt(hexadecimal_digit_sequence) ~ "." ~ hexadecimal_digit_sequence) | (hexadecimal_digit_sequence ~ ".")

  def binary_exponent_part: Parser = "[pP]".r ~ opt(sign) ~ digit_sequence

  def hexadecimal_digit_sequence: Parser = hexadecimal_digit | hexadecimal_digit_sequence ~ hexadecimal_digit

  def floating_suffix: Parser[String] = "[flFL]".r

  def enumeration_constant: Parser = identifier

  def character_constant: Parser = "'" ~ c_char_sequence ~ "'" | "L'" ~ c_char_sequence ~ "'"

  def c_char_sequence: Parser = c_char | c_char_sequence ~ c_char

  // Any member of the source character set except the single quotation mark ('), backslash (\), or newline character
  def c_char: Parser[String] = "[^'\\\n\r]".r | escape_sequence

  def escape_sequence: Parser[String] = simple_escape_sequence | octal_escape_sequence | hexadecimal_escape_sequence | universal_character_name

  def simple_escape_sequence: Parser[String] = """\\[abfnrtv'"?]""".r

  def octal_escape_sequence: Parser[String] = "\\" ~ octal_digit | "\\" ~ octal_digit ~ octal_digit | "\\" ~ octal_digit ~ octal_digit ~ octal_digit

  def hexadecimal_escape_sequence: Parser = "\\x" ~ hexadecimal_digit | hexadecimal_escape_sequence ~ hexadecimal_digit

  // STRING LITERALS
  // https://eel.is/c++draft/lex.string
  // https://docs.microsoft.com/en-us/cpp/c-language/lexical-grammar?view=msvc-170

  def string_literal: Parser = opt(encoding_prefix) ~ "\"" ~ opt(s_char_sequence) ~ "\""

  def encoding_prefix: Parser[String] = "u8" | "[uUL]".r

  def s_char_sequence: Parser[String] = s_char | s_char_sequence ~ s_char

  // any member of the source character set except the double-quotation mark ("), backslash (\), or newline character
  def s_char: Parser[String] = """[^"\\\n\r]""".r | escape_sequence


  // PUNCTUATORS

  def punctuator: Parser[String] =
    "[" | "]" | "(" | ")" | "{" | "}" | "." | "->" |
      "++" | "--" | "&" | "*" | "+" | "-" | "|" | "!" |
      "/" | "%" | "<<" | ">>" | "<" | ">" | "<=" | ">=" | "==" |
      "!=" | "^" | "|" | "&&" | "||" | "?" | ":" | ";" | "..." |
      "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" |
      "&=" | "^=" | "|=" | "" | "" | "#" | "##" |
      "<:" | ":>" | "<%" | "%>" | "%:" | "%:%:"


  // HEADER NAMES https://eel.is/c++draft/lex.header

  def header_name: Parser = "<" ~ h_char_sequence ~ ">" | "\"" ~ q_char_sequence ~ "\""

  def h_char_sequence: Parser = h_char | h_char_sequence ~ h_char

  // any member of the source character set except the new_line character and >
  def h_char: Parser[String] = """[^>\n\r]""".r

  def q_char_sequence: Parser[_] = q_char | q_char_sequence ~ q_char

  def q_char: Parser[String] = """[^"\n\r]""".r



  /*

  def lines: Parser[List[Line]] = line ~ (line *) <~ "END" ^^ {
    case a ~ b =>
      a ++ b.flatten
  }

  def assemble(raw: String, quiet: Boolean = false): List[List[String]] = {

    //val code = cpp(raw)
    val code = raw

    parse(lines, product) match {
      case Success(theCode, _) => {
        println("Statements:")

        val filtered = theCode.filter { l =>
          l match {
            case Comment(_) if quiet =>
              false
            case _ =>
              true
          }
        }

        logInstructions(filtered)

        assertAllResolved(theCode)

        val instructions = filtered.collect { case x: Instruction => x.encode }
        //instructions.zipWithIndex.foreach(l => println("CODE : " + l))
        println("Assembled: " + instructions.size + " instructions")
        instructions
      }

      case msg: Failure => {
        sys.error(s"FAILURE: $msg ")

      }
      case msg: Error => {
        sys.error(s"ERROR: $msg")
      }
    }
  }
*/
}
