package scc

import scala.language.postfixOps
import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.io.Source
import scala.util.parsing.combinator.JavaTokenParsers

object SpamCC {

  def main(args: Array[String]) = {
    if (args.size == 1) {
      compile(args)
    }
    else {
      System.err.println("Assemble ...")
      System.err.println("    usage:  file-name.scc ")
      sys.exit(1)
    }
  }

  private def compile(args: Array[String]) = {
    val fileName = args(0)

    val code = Source.fromFile(fileName).getLines().mkString("\n")

    val scc = new SpamCC()

    val roms = scc.compile(code)
    println("roms : " + roms)
    //
    //    val pw = new PrintWriter(new File(s"${fileName}.rom"))
    //    roms.foreach { line =>
    //      line.foreach { rom =>
    //        pw.write(rom)
    //      }
    //      pw.write("\n")
    //    }
    //    pw.close()

  }
}

class SpamCC extends JavaTokenParsers {
  var varLocn = -1
  val vars = mutable.TreeMap.empty[String, (String, Int)]

  final val NAME_SEPARATOR = "_"

  def compile(code: String): List[String] = {

    parse(program, code) match {
      case Success(matched, _) => {
        //        matched.zipWithIndex.foreach(
        //          l => {
        //            println(">>" + l)
        //          }
        //        )
        matched
      }
      case msg: Failure => {
        sys.error(s"FAILURE: $msg ")

      }
      case msg: Error => {
        sys.error(s"ERROR: $msg")
      }
    }
  }

  case class Statement() {

  }

  case class Context(parent: Option[Context]) {

  }

  def dec: Parser[Int] =
    """-?\d+""".r ^^ { v =>
      v.toInt
    }

  def char: Parser[Int] = "'" ~> ".".r <~ "'" ^^ { v =>
    val i = v.codePointAt(0)
    if (i > 127) throw new RuntimeException(s"asm error: character '$v' codepoint $i is outside the 0-127 range")
    i.toByte
  }

  def hex: Parser[Int] = "$" ~ "[0-9a-hA-H]+".r ^^ { case _ ~ v => Integer.valueOf(v, 16) }

  def bin: Parser[Int] = "%" ~ "[01]+".r ^^ { case _ ~ v => Integer.valueOf(v, 2) }

  def oct: Parser[Int] = "@" ~ "[0-7]+".r ^^ { case _ ~ v => Integer.valueOf(v, 8) }

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ (a => a)

  def assignVar(name: String): String = {

    def upd = {
      varLocn += 1
      (name, varLocn)
    }

    vars.getOrElseUpdate(name, upd)._1
  }

  def statementVar: Parser[Block] = "var" ~> name ~ "=" ~ dec ^^ {
    case n ~ _ ~ v =>
      Block("",
        parent => {
          val fqn = parent.fqn(n)
          val label = assignVar(fqn)
          List(s"[:$label] = $v")
        }
      )
  }

  def statementReturn: Parser[Block] = "return" ~> dec ^^ {
    a =>
      Block("",
        _ => {
          List("REGD = " + a)
        }
      )
  }

  def statement: Parser[Block] = statementReturn | statementVar

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  def function: Parser[Block] = "def " ~> name ~ ("(" ~ ")" ~ ":" ~ "void" ~ "=" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ c =>
      Block(fnName,
        p => {
          val stmts = c.flatMap {
            b => {
              val newName = p.blockName + NAME_SEPARATOR + fnName
              b.expr(p.copy(blockName = newName))
            }
          }

          val suffix = if (fnName == "main") {
            List("PCHITMP = <:root_end", "PC = >:root_end")
          } else Nil

          stmts ++ suffix
        }
      )
  }

  case class Block(blockName: String, expr: Block => List[String]) {

    def fqn(child: String): String = {
      blockName + NAME_SEPARATOR + child
    }

    //
    //    def fqn(child: Block): String = {
    //      blockName + "/" + child
    //    }
  }

  def program: Parser[List[String]] = (function +) ^^ {
    fns =>
      val root = Block("root", _ => Nil)
      val asm: List[String] = fns.flatMap(b =>
        b.expr(root)
      )

      val varlist = vars.map(x => s"${x._1}: EQU ${x._2._2}").toList
      varlist ++ asm :+ ":root_end" :+ "END"
  }
}