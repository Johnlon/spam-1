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

  def EOL: Parser[String] = ";"

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

  def nFactor: Parser[Int] = (char | dec | hex | bin | oct | "(" ~> constExpr <~ ")")

  def constExpr: Parser[Int] = nFactor ~ ((op ~ nFactor) *) ^^ {
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
  def statementEqConst: Parser[Block] = "var" ~> name ~ "=" ~ constExpr <~ EOL ^^ {
    case target ~ _ ~ v =>
      new Block(s"statementVar $target=$v") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val label = assignVar(parent, target)
          List(
            enter(depth),
            s"[:$label] = $v",
            enter(depth)
          )
        }
      }
  }

  // optimisation of "var X=1 op Y"
  def statementEqConstOpVar: Parser[Block] = "var" ~> name ~ "=" ~ constExpr ~ op ~ name <~ EOL ^^ {
    case target ~ _ ~ e ~ op ~ v =>
      new Block(s"statementEqConstOpVar $target=$e $op $v") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val tLabel = assignVar(parent, target)
          val sLabel = assignVar(parent, v)
          List(
            enter(depth),
            s"REGA = $e",
            s"REGA = REGA $op [:$sLabel]",
            s"[:$tLabel] = REGA",
            exit(depth)
          )
        }
      }
  }

  // optimisation of "var X=Y op 1"
  def statementEqVarOpConst: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ constExpr <~ EOL ^^ {
    case target ~ _ ~ v ~ op ~ e =>
      new Block(s"statementEqVarOpConst $target=$v $op $e") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val tLabel = assignVar(parent, target)
          val sLabel = assignVar(parent, v)
          List(
            enter(depth),
            s"REGA = [:$sLabel]",
            s"REGA = REGA $op $e",
            s"[:$tLabel] = REGA",
            exit(depth)
          )
        }
      }
  }

  // optimisation of "var X=Y"
  def statementEqVar: Parser[Block] = "var" ~> name ~ "=" ~ name <~ EOL ^^ {
    case target ~ _ ~ v =>
      new Block(s"statementEqVar $target=$v") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val tLabel = assignVar(parent, target)
          val sLabel = assignVar(parent, v)
          List(
            enter(depth),
            s"REGA = [:$sLabel]",
            s"[:$tLabel] = REGA",
            exit(depth)
          )
        }
      }
  }

  // optimisation of "var X=Y op Z"
  def statementEqVarOpVar: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ name <~ EOL ^^ {
    case target ~ _ ~ s1 ~ op ~ s2 =>
      new Block(s"statementEqVarOpConst $target=$s1 $op $s2") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val tLabel = assignVar(parent, target)
          val s1Label = assignVar(parent, s1)
          val s2Label = assignVar(parent, s2)
          List(
            enter(depth),
            s"REGA = [:$s1Label]",
            s"REGA = [:$s2Label]",
            s"[:$tLabel] = REGA $op REGB",
            exit(depth),
          )
        }
      }
  }

  // TODO - this looks suspiciously without context - should I load anything - or does it make sense as it's only used inside expressions?????
  def blkName: Parser[Block] = name ^^ {
    case n =>
      new Block(s"blkName $n") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val labelSrcVar = assignVar(parent, n)
          List(
            enter(depth),
            s"REGA = [:$labelSrcVar]",
            exit(depth)
          )
        }
      }
  }

  def blkNExpr: Parser[Block] = constExpr ^^ {
    case n =>
      new Block(s"blkNExpr $n") {
        override def expr(depth: Int, parent: Block): List[String] = {
          List(
            enter(depth),
            s"REGA = $n",
            exit(depth),
          )
        }
      }
  }

  def allBlkExprs: Parser[Block] = blkNExpr | blkName | "(" ~> blkExprs <~ ")"

  def blkExprs: Parser[Block] = allBlkExprs ~ ((op ~ allBlkExprs) *) ^^ {
    case x ~ list =>
      new Block("varExprs") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val varTmo = assignVar(parent, "varExprs_d" + depth)
          val left: List[String] = x.expr(depth + 1, parent) :+ s"[:$varTmo] = REGA"
          val stmts: List[String] = list.reverse.flatMap { case op ~ b =>
            val bstmt = b.expr(depth + 1, parent)
            bstmt ++ List(s"REGB = [:$varTmo]", s"[:$varTmo] = REGB $op REGA")
          }

          val suffix = split(
            s"""REGA = [:$varTmo]
               |""".stripMargin)

          enter(depth) +: (left ++ stmts ++ suffix) :+ exit(depth)
        }
      }
  }


  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ blkExprs <~ EOL ^^ {
    case target ~ _ ~ v =>
      new Block(s"statementVarOp = $target = $v") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val labelTarget = assignVar(parent, target)

          val stmts: List[String] = v.expr(depth + 1, parent)

          val assign = List(
            s"[:$labelTarget] = REGA",
          )

          enter(depth) +: (stmts ++ assign) :+ exit(depth)
        }
      }
  }

  def statementReturn: Parser[Block] = "return" ~> constExpr ^^ {
    a: Int =>
      new Block(s"statementReturn $a") {
        override def expr(depth: Int, parent: Block): List[String] = {
          List(
            enter(depth),
            "REGD = " + a,
            exit(depth),
          )
        }
      }
  }

  def statementReturnName: Parser[Block] = "return" ~> name ^^ {
    n: String =>
      new Block(s"statementReturnName $n") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val label = assignVar(parent, n)
          List(
            enter(depth),
            s"REGD = [:$label]",
            exit(depth),
          )
        }
      }
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def statementPutchar: Parser[Block] = "putchar" ~ "(" ~> constExpr <~ ")" ^^ {
    n: Int =>
      new Block(s"statementPutchar $n") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          split(
            s"""
               |${enter(depth)}
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = $n
               |${exit(depth)}
               |""")
        }
      }
  }


  def statementPutcharName: Parser[Block] = "putchar" ~ "(" ~> name <~ ")" ^^ {
    n: String =>
      new Block(s"statementPutcharName $n") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          val varLocn = getVarLocn(parent, n)
          split(
            s"""
               ||${enter(depth)}
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = [:$varLocn]
               ||${exit(depth)}
               |""")
        }
      }
  }


  def statement: Parser[Block] = (statementEqVarOpVar | statementEqVar | statementEqVarOpConst | statementEqConstOpVar | statementEqConst |
    statementReturn | statementReturnName | statementVarOp | statementPutchar | statementPutcharName |
    whileTrue | whileCond)

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  def function: Parser[Block] = "def " ~> name ~ ("(" ~ ")" ~ ":" ~ "void" ~ "=" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ c =>
      new Block(fnName) {
        override def expr(depth: Int, parent: Block): List[String] = {
          val stmts = c.flatMap {
            b => {
              val newName = parent.blockName + NAME_SEPARATOR + fnName
              b.expr(depth + 1, parent.rename(newName= newName))
            }
          }

          val suffix = if (fnName == "main") {
            List(
              "PCHITMP = <:root_end",
              "PC = >:root_end"
            )
          } else Nil

          enter(depth) +: (stmts ++ suffix) :+ enter(depth)
        }
      }
  }

  def whileTrue: Parser[Block] = "while" ~ "(" ~ "true" ~ ")" ~ "{" ~> statements <~ "}" ^^ {
    case content =>

      new Block("while (true)") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val label = parent.fqnUnique("while")

          val labelTop = s"""${label}_top"""

          val prefix = split(
            s"""
               |$labelTop:
               |""".stripMargin)

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent.rename(newName = label))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelTop
               |PC = >:$labelTop
               |${label}_bot:
               |""".stripMargin)


          enter(depth) +:( prefix ++ stmts ++ suffix) :+ exit(depth)
        }
      }
  }

  def comparison: Parser[String] = ">" | "<" | ">=" | "<" | "<=" | "==" | "!=" ^^ {
    case op =>
      op
  }

  // return te block of code and the name of the flag to add to the jump operation
  def condition: Parser[(String, Block)] = name ~ comparison ~ constExpr ^^ {
    case n ~ c ~ v =>
      c match {
        case ">" => condBLock(n, c, v, ">")
        case "<" => condBLock(n, c, v, "<")
        case "<=" => condBLock(n, c, v, "<=")
        case ">=" => condBLock(n, c, v, ">=")
        case "==" => condBLock(n, c, v, "==")
        case "!=" => condBLock(n, c, v, "!=")
      }
  }

  private def condBLock(n: String, c: String, v: Int, op: String) = {
    val b = new Block(s"condition $n") {
      override def expr(depth: Int, parent: Block): List[String] = {

        val b = op match {
          case ">" | "<" | "==" | "!=" =>
            val label = assignVar(parent, n)
            List(
              s"REGA = [:$label]",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
          case ">=" =>
            val label = assignVar(parent, n)
            List(
              s"REGA = [:$label]",
              s"REGA = REGA + 1 _S",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
          case "<=" =>
            val label = assignVar(parent, n)
            List(
              s"REGA = [:$label]",
              s"REGA = REGA - 1 _S",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
        }

        enter(depth) +: b :+ exit(depth)

      }
    }

    val cpuFlag = op match {
      case ">" => "_GT"
      case "<" => "_LT"
      case ">=" => "_GT"
      case "<=" => "_LT"
      case "==" => "_EQ"
      case "!=" => "_NE"
    }
    (cpuFlag, b)
  }

  def whileCond: Parser[Block] = "while" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
    case cond ~ _ ~ _ ~ content =>

      new Block(s"while ($cond)") {
        override def expr(depth: Int, parent: Block): List[String] = {
          val label = parent.fqnUnique("while")

          val labelTop = s"${label}_top"
          val labelBot = s"${label}_bot"

          val flagToCheck = cond._1
          val conditionBlock = cond._2

          val prefix = split(s"$labelTop:")

          val condStatements = conditionBlock.expr(depth + 1, parent.rename(newName = label))

          val conditionalJump = condStatements ++ split(
            s"""
               |PCHITMP = <:$labelBot
               |PC = >:$labelBot $flagToCheck
               """.stripMargin)

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent.rename(newName = label))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelTop
               |PC = >:$labelTop
               |$labelBot:
               |""".stripMargin)

          enter(depth) +: ( prefix ++ conditionalJump ++ stmts ++ suffix) :+ exit(depth)
        }

      }
  }

  object Block {
    private var idx = 0
  }

  trait Generator {
    def apply(depth: Int, blk: Block): List[String]

    override def toString() = "Code()"
  }


  abstract class Block(val blockName: String)  {
    def expr(depth: Int, parent: Block): List[String]

    override def toString() = s"Block($blockName)"

    def fqn(child: String): String = {
      blockName + NAME_SEPARATOR + child
    }

    def fqnUnique(child: String): String = {
      Block.idx += 1
      blockName + NAME_SEPARATOR + child + "_" + Block.idx
    }

    def enter(depth: Int) = s"; ($depth) ENTER $blockName";

    def exit(depth: Int) = s"; ($depth) EXIT  $blockName";

    def rename(newName: String): Block = new Block(newName) {
      override def expr(depth: Int, parent: Block): List[String] = Block.this.expr(depth, parent)
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
    vars.get(fqn).getOrElse(sys.error(s"scc error: $name is not defined in ${
      block.toString()
    }"))._1
  }

  def program: Parser[List[String]] = (function +) ^^ {
    fns =>
      val Depth0 = 0
      val Root = new Block("root") {
        override def expr(depth: Int, parent: Block): List[String] = Nil
      }
      
      val asm: List[String] = fns.flatMap(b =>
        b.expr(Depth0, Root)
      )

      val varlist = vars.map(x => s"${
        x._1
      }: EQU ${
        x._2._2
      }").toList
      varlist ++ asm :+ "root_end:" :+ "END"
  }
}