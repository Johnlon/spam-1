package scc

import scala.collection.mutable
import scala.io.Source
import scala.language.postfixOps
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

  def EOL: Parser[Any] = ";"

  var varLocn = -1
  val labels = mutable.TreeMap.empty[String, Int]
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

  def op: Parser[String] = "+" | "-" | "*" | "/" ^^ {
    o => o
  }

  def nFactor: Parser[Int] = (char | dec | hex | bin | oct | "(" ~> nExpr <~ ")")

  def nExpr: Parser[Int] = nFactor ~ ((op ~ nFactor) *) ^^ {
    case x ~ list =>
      list.foldLeft(x)({
        case (acc, "+" ~ i) => acc + i
        case (acc, "*" ~ i) => acc * i
        case (acc, "/" ~ i) => acc / i
        case (acc, "-" ~ i) => acc - i
      })
  }

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ (a => a)

  // optimisation of "var X=1"
  def statementEqConst: Parser[Block] = "var" ~> name ~ "=" ~ nExpr <~ EOL ^^ {
    case target ~ _ ~ v =>
      Block(s"statementVar $target=$v",
        (depth, parent) => {
          val label = assignVar(parent, target)
          List(s"[:$label] = $v")
        }
      )
  }

  //  def varExprNE: Parser[Block] = name ~ op ~ expr ^^ {
  //    case name ~ op ~ expr =>
  //      Block(s"varExprNE $name $op $expr",
  //        parent => {
  //          val labelSrcVar = assignVar(parent, name)
  //          List(
  //            s"REGA = [:$labelSrcVar]",
  //            s"REGA = REGA $op $expr"
  //          )
  //        }
  //      )
  //  }

  //
  //  def varExprEN: Parser[Block] = expr ~ op ~ name ^^ {
  //    case expr ~ op ~ name =>
  //      Block(s"varExprEN $expr $op $name",
  //        parent => {
  //          val labelSrcVar = assignVar(parent, name)
  //          List(
  //            s"REGA = $expr",
  //            s"REGB = [:$labelSrcVar]",
  //            s"REGA = REGA $op REGB"
  //          )
  //        }
  //      )
  //  }

  // TODO - this looks suspiciously without context - should I load anything - or does it make sense as it's only used inside expressions?????
  def varExprName: Parser[Block] = name ^^ {
    case n =>
      Block(s"varExprName $n",
        (depth: Int, parent: Block) => {
          val labelSrcVar = assignVar(parent, n)
          List(
            s"; ($depth) ENTER varExprName $n",
            s"REGA = [:$labelSrcVar]",
            s"; ($depth) EXIT  varExprName $n"
          )
        }
      )
  }

  def varNExpr: Parser[Block] = nExpr ^^ {
    case n =>
      Block(s"varNExpr $n",
        (depth, parent) =>
          List(
            s"; ($depth) ENTER varNExpr $n",
            s"REGA = $n",
            s"; ($depth) EXIT  varNExpr $n",
          )
      )
  }

  //  def varExpr: Parser[Block] = varExprEN | varExprNE | varExprName | "(" ~> varExprs <~ ")"
  def varExpr: Parser[Block] = varNExpr | varExprName | "(" ~> varExprs <~ ")"

  def varExprs: Parser[Block] = varExpr ~ ((op ~ varExpr) *) ^^ {
    case x ~ list =>
      Block("varExprs",
        (depth, parent) => {
          val varTmo = assignVar(parent, "varExprs_d" + depth)
          val left: List[String] = x.expr(depth + 1, parent) :+ s"[:$varTmo] = REGA"
          val stmts: List[String] = list.reverse.flatMap { case op ~ b =>
            val bstmt = b.expr(depth + 1, parent)
            bstmt ++ List(s"REGB = [:$varTmo]", s"[:$varTmo] = REGB $op REGA")
          }

          val suffix = split(
            s"""REGA = [:$varTmo]
               |""".stripMargin)

          val commentEnter = Nil :+ s"; ($depth) ENTER varExprs $x ~ $list"
          val commentExit = Nil :+ s"; ($depth) EXIT  varExprs $x ~ $list"

          commentEnter ++ left ++ stmts ++ suffix ++ commentExit
        }
      )

  }


  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ varExprs <~ EOL ^^ {
    case target ~ _ ~ v =>
      Block(s"statementVarOp = $target = $v",
        (depth, parent) => {
          val labelTarget = assignVar(parent, target)

          val stmts: List[String] = v.expr(depth + 1, parent)

          val assign = List(
            s"[:$labelTarget] = REGA",
          )

          val enter = List(s"; ($depth) ENTER statementVarOp $target = $v")
          val exit = List(s"; ($depth) EXIT   statementVarOp $target = $v")

          enter ++ stmts ++ assign ++ exit
        }
      )
  }

  //  def statementVarEqOp2Var: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ name ^^ {
  //    case target ~ _ ~ s1 ~ op ~ s2 =>
  //      Block(s"statementVarEqOp2Var $target = $s1 $op $s2",
  //        parent => {
  //          val labelTarget = assignVar(parent, target)
  //          val labelS1 = assignVar(parent, s1)
  //          val labelS2 = assignVar(parent, s2)
  //          List(
  //            s"REGA = [:$labelS1]",
  //            s"REGB = [:$labelS2]",
  //            s"[:$labelTarget] = REGA $op REGB"
  //          )
  //        }
  //      )
  //  }

  def statementReturn: Parser[Block] = "return" ~> nExpr ^^ {
    a: Int =>
      Block(s"statementReturn $a",
        (depth, p) => {
          List("REGD = " + a)
        }
      )
  }

  def statementReturnName: Parser[Block] = "return" ~> name ^^ {
    n: String =>
      Block(s"statementReturnName $n",
        (depth, parent) => {
          val label = assignVar(parent, n)
          List(
            s"; ($depth) ENTER statementReturnName $name",
            s"REGD = [:$label]",
            s"; ($depth) EXIT  statementReturnName $name"
          )
        }
      )
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def statementPutchar: Parser[Block] = "putchar" ~ "(" ~> nExpr <~ ")" ^^ {
    n: Int =>
      Block(s"statementPutchar $n",
        (depth, parent) => {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          split(
            s"""
               |; ($depth) ENTER  putchar $n"
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = $n
               |; ($depth) EXIT   putchar $n"
               |""")
        }
      )
  }


  def statementPutcharName: Parser[Block] = "putchar" ~ "(" ~> name <~ ")" ^^ {
    n: String =>
      Block(s"statementPutcharName $n",
        (depth, parent) => {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          val varLocn = getVarLocn(parent, n)
          split(
            s"""
               |; ($depth) ENTER  putchar $n"
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = [:$varLocn]
               |; ($depth) EXIT   putchar $n"
               |""")
        }
      )
  }


  //  def blankLine: Parser[Block] = """\s*""".r <~ EOL ^^ {
  //    blank: String =>
  //      Block(s"blank",
  //        (depth, parent) => Nil
  //      )
  //  }


  //  def statement: Parser[Block] = statementReturn | statementReturnName | statementVarEqOp2Var | statementVarOp | statementVar | statementPutchar | whileBlock
  def statement: Parser[Block] = (statementEqConst | statementReturn | statementReturnName | statementVarOp | statementPutchar | statementPutcharName | whileBlock)

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  def function: Parser[Block] = "def " ~> name ~ ("(" ~ ")" ~ ":" ~ "void" ~ "=" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ c =>
      Block(fnName,
        (depth, parent) => {
          val stmts = c.flatMap {
            b => {
              val newName = parent.blockName + NAME_SEPARATOR + fnName
              b.expr(depth + 1, parent.copy(blockName = newName))
            }
          }

          val suffix = if (fnName == "main") {
            List(
              s"; ($depth) end MAIN",
              "PCHITMP = <:root_end",
              "PC = >:root_end"
            )
          } else Nil

          val enter = List(s"; ($depth) ENTER function $fnName")
          val exit = List(s"; ($depth) EXIT  function $fnName")
          enter ++ stmts ++ suffix ++ exit
        }
      )
  }

  def whileBlock: Parser[Block] = "while" ~ "(" ~ "true" ~ ")" ~ "{" ~> statements <~ "}" ^^ {
    case content =>

      Block("while",
        (depth, parent) => {
          val label = parent.fqnUnique("while")

          val labelTop = s"""${label}_top"""

          val prefix = split(
            s"""
               |$labelTop:
               |""".stripMargin)

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent.copy(blockName = label))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelTop
               |PC = >:$labelTop
               |${label}_bot:
               |""".stripMargin)


          val enter = List(s"; ($depth) ENTER while")
          val exit = List(s"; ($depth) EXIT  while")
          enter ++ prefix ++ stmts ++ suffix ++ exit
        }
      )
  }

  object Block {
    private var idx = 0
  }

  trait Generator {
    def apply(depth: Int, blk: Block): List[String]

    override def toString() = "Code()"
  }

  case class Block(blockName: String, expr: Generator) {
    override def toString() =
      s"Block($blockName, ${expr.toString})"

    def fqn(child: String): String = {
      blockName + NAME_SEPARATOR + child
    }

    def fqnUnique(child: String): String = {
      Block.idx += 1
      blockName + NAME_SEPARATOR + child + "_" + Block.idx
    }
  }

  def assignVar(label: String): String = {

    def upd = {
      varLocn += 1
      (label, varLocn)
    }

    vars.getOrElseUpdate(label, upd)._1
  }

  def assignVar(block: Block, name: String): String = {
    val fqn = block.fqn(name)
    val label = assignVar(fqn)
    assignVar(label)
  }

  //  def loopupVar(label: String): Option[String] = {
  //    vars.get(label).map(_._1)
  //  }

  def getVarLocn(block: Block, name: String): String = {
    val fqn = block.fqn(name)
    vars.get(fqn).getOrElse(sys.error(s"scc error: $name is not defined in ${block.toString()}"))._1
  }

  def program: Parser[List[String]] = (function +) ^^ {
    fns =>
      val Depth0 = 0
      val Root = Block("root", (_, _) => Nil)

      val asm: List[String] = fns.flatMap(b =>
        b.expr(Depth0, Root)
      )

      val varlist = vars.map(x => s"${x._1}: EQU ${x._2._2}").toList
      varlist ++ asm :+ "root_end:" :+ "END"
  }
}