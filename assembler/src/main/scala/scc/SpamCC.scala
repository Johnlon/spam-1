/*
todo:
  block comment
  peek/poke of const or expr
  16 bits maths / 32?
  chip 8
  basic compiler?
  "else"


   TODO: work out how to designate use of Signed of Unsigned comparison!!
   record vars as signed or unsigned?

   unsigned int8 e a = 1   // ditch var
   unsigned int16 e a = 1  // ditch var
   var                     // short for unsigned int8

   do it with the op?     "a <:s b"
   what about signed vs unsigned const?     a < -1:s   or is -1 automatically signed and 255 is automatically unsigned?
   and if using octal or hex then are they signed or unsigned?
   maybe restrict signs to the ops??

 */
// TODO: Add some traps for stupid errors like putchar("...") which I wasted loads of time on
// TODO USE "export" to allow sharing a var into static subroutines in call tree (not sure if will work for stack based calls) - could be used for "globals?"


package scc

import java.io.{File, PrintWriter}

import asm.EnumParserOps

import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers


object SpamCC {
  val ZERO = 0.toByte
  val MAIN_LABEL = "ROOT________main_start"
  val ONE_BYTE_STORAGE = List(0.toByte)
  val TWO_BYTE_STORAGE = List(0.toByte, 0.toByte)

  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      compile(args)
    }
    else {
      System.err.println("SpamCC ...")
      System.err.println("    usage:  file-name.scc ")
      sys.exit(1)
    }
  }

  private def compile(args: Array[String]): Unit = {
    val fileName = args(0)

    val code = readAllLines(fileName)

    val compiler = new SpamCC()

    val asm: List[String] = compiler.compile(code)

    val pw = new PrintWriter(new File(s"$fileName.asm"))
    asm.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }
    pw.close()
  }

  private def readAllLines(fileName: String): String = {
    val src = Source.fromFile(fileName)
    try {
      src.getLines().mkString("\n")
    }
    catch {
      case ex: Throwable =>
        src.close()
        throw ex
    }
  }

  // convert in to storage format
  def intTo2xBytes(addr: Int): List[Byte] = {
    List((addr >> 8) toByte, addr.toByte)
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }
}

class SpamCC extends StatementParser with ExpressionParser with ConstExpressionParser with ConditionParser with EnumParserOps with JavaTokenParsers {

  def compile(code: String): List[String] = {

    parse(program, code) match {
      case Success(matched, _) =>
        println("SpamCC parsed : ")
        println(matched.dump(1))

        matched.compile()
      case msg: Failure =>
        sys.error(s"FAILURE: $msg ")
      case msg: Error =>
        sys.error(s"ERROR: $msg")
    }
  }

}