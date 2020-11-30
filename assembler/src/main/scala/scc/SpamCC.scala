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

  val SPACE = " ";

  var varLocn = -1
  //  val labels = mutable.TreeMap.empty[String, Int]
  val vars = mutable.TreeMap.empty[String, (String, Int)]

  final val LABEL_NAME_SEPARATOR = "_"
  final val LABEL_PATH_SEPARATOR = "/"

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
      new Block("statementVar", s"$SPACE$target=$v") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val label = assignVarLabel(parent, target)
          List(
            s"[:$label] = $v",
          )
        }
      }
  }

  // optimisation of "var X=1 op Y"
  def statementEqConstOpVar: Parser[Block] = "var" ~> name ~ "=" ~ constExpr ~ op ~ name <~ EOL ^^ {
    case target ~ _ ~ e ~ op ~ v =>
      new Block("statementEqConstOpVar", s"$SPACE$target=$e $op $v") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val sLabel = getVarLabel(parent, v)
          val tLabel = assignVarLabel(parent, target)
          List(
            s"REGA = $e",
            s"REGA = REGA $op [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y op 1"
  def statementEqVarOpConst: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ constExpr <~ EOL ^^ {
    case target ~ _ ~ v ~ op ~ e =>
      new Block("statementEqVarOpConst", s"$SPACE$target=$v $op $e") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val sLabel = getVarLabel(parent, v)
          val tLabel = assignVarLabel(parent, target)
          List(
            s"REGA = [:$sLabel]",
            s"REGA = REGA $op $e",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y"
  def statementEqVar: Parser[Block] = "var" ~> name ~ "=" ~ name <~ EOL ^^ {
    case target ~ _ ~ v =>
      new Block("statementEqVar", s"$SPACE$target=$v") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val sLabel = getVarLabel(parent, v)
          val tLabel = assignVarLabel(parent, target)
          List(
            s"REGA = [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y op Z"
  def statementEqVarOpVar: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ name <~ EOL ^^ {
    case target ~ _ ~ s1 ~ op ~ s2 =>
      new Block("statementEqVarOpConst", s"$SPACE$target=$s1 $op $s2") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val s1Label = getVarLabel(parent, s1)
          val s2Label = getVarLabel(parent, s2)
          val tLabel = assignVarLabel(parent, target)
          List(
            s"REGA = [:$s1Label]",
            s"REGA = [:$s2Label]",
            s"[:$tLabel] = REGA $op REGB",
          )
        }
      }
  }

  // TODO - this looks suspiciously without context - should I load anything - or does it make sense as it's only used inside expressions?????
  def blkName: Parser[Block] = name ^^ {
    case n =>
      new Block("blkName", s"$SPACE$n") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val labelSrcVar = getVarLabel(parent, n)
          List(
            s"REGA = [:$labelSrcVar]",
          )
        }
      }
  }

  def blkNExpr: Parser[Block] = constExpr ^^ {
    case i =>
      new Block("blkNExpr", s"$SPACE$i") {
        override def gen(depth: Int, parent: Block): List[String] = {
          List(
            s"REGA = $i",
          )
        }
      }
  }

  def allBlkExprs: Parser[Block] = blkNExpr | blkName | "(" ~> blkExprs <~ ")"

  def blkExprs: Parser[Block] = allBlkExprs ~ ((op ~ allBlkExprs) *) ^^ {
    case x ~ list =>
      val inner = list.foldLeft(x.toString()) {
        case (acc, b) => s"$acc ${b._1} ( ${b._2} )"
      }

      val str = s" $inner  "
      new Block("blkExprs", s"$SPACE$str") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val temporaryVarLabel = assignVarLabel(parent, "varExprs_d" + depth)

          val left: List[String] = x.expr(depth + 1, parent) :+ s"[:$temporaryVarLabel] = REGA"

          val rightStatments: List[String] = list.reverse.flatMap { case op ~ b =>
            val bstmt = b.expr(depth + 1, parent)
            bstmt ++ List(s"REGB = [:$temporaryVarLabel]", s"[:$temporaryVarLabel] = REGB $op REGA")
          }

          val suffix = split(s"""REGA = [:$temporaryVarLabel]""")

          left ++ rightStatments ++ suffix
        }
      }
  }


  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ blkExprs <~ EOL ^^ {
    case target ~ _ ~ v =>
      new Block("statementVarOp", s"$SPACE$target = $v") {
        override def gen(depth: Int, parent: Block): List[String] = {

          // must do sources before calling assignVarLabel for target otherwise
          // we fail to spot undefined vars because assignVarLabel(target) causes the
          // defines vars before the source reference is considered so the source see it
          // as preexisting and the code fails to spot the error
          val stmts: List[String] = v.expr(depth + 1, parent)

          val labelTarget = assignVarLabel(parent, target)

          val assign = List(
            s"[:$labelTarget] = REGA",
          )
          stmts ++ assign
        }
      }
  }

  def statementReturn: Parser[Block] = "return" ~> constExpr ^^ {
    i: Int =>
      new Block("statementReturn", s"$SPACE$i") {
        override def gen(depth: Int, parent: Block): List[String] = {
          List(
            "REGD = " + i,
          )
        }
      }
  }

  def statementReturnName: Parser[Block] = "return" ~> name ^^ {
    n: String =>
      new Block("statementReturnName", s"$SPACE$n") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val label = getVarLabel(parent, n)
          List(
            s"REGD = [:$label]",
          )
        }
      }
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def statementPutchar: Parser[Block] = "putchar" ~ "(" ~> constExpr <~ ")" ^^ {
    i: Int =>
      new Block("statementPutchar", s"$SPACE$i") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          split(
            s"""
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = $i
               |""")
        }
      }
  }


  def statementPutcharName: Parser[Block] = "putchar" ~ "(" ~> name <~ ")" ^^ {
    n: String =>
      new Block("statementPutcharName", s"$SPACE$n") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val labelW = parent.fqnUnique("putchar_wait")
          val labelT = parent.fqnUnique("putchar_transmit")
          val varLocn = getVarLabel(parent, n)
          split(
            s"""
               |$labelW:
               |PCHITMP = <:$labelT
               |PC = >:$labelT _DO
               |PCHITMP = <:$labelW
               |PC = <:$labelW
               |$labelT:
               |UART = [:$varLocn]
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
      // !!!NO SPACE IN THE NAME AS USED FOR LABELS
      new Block("function", s"_$fnName") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val stmts = c.flatMap {
            b => {
              //JL!              val newName = parent.blockName + NAME_SEPARATOR + fnName
              val newName = parent.fqn(fnName)
              b.expr(depth + 1, parent.pushName(newName = newName))
            }
          }

          val suffix = if (fnName == "main") {
            List(
              "PCHITMP = <:root_end",
              "PC = >:root_end"
            )
          } else Nil

          stmts ++ suffix
        }
      }
  }

  def whileTrue: Parser[Block] = "while" ~ "(" ~ "true" ~ ")" ~ "{" ~> statements <~ "}" ^^ {
    case content =>

      new Block("while", s"${SPACE}true") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val label = parent.fqnUnique("while")
          val labelTop = s"""${label}_top"""
          val labelBot = s"""${label}_bot"""

          val prefix = split(s"""$labelTop:""")

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent.pushName(newName = label))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelTop
               |PC = >:$labelTop
               |$labelBot:
               |""")


          prefix ++ stmts ++ suffix
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
    val b = new Block("condition", s"$SPACE$n $op $v") {
      override def gen(depth: Int, parent: Block): List[String] = {
        val label = getVarLabel(parent, n)

        op match {
          case ">" | "<" | "==" | "!=" =>
            List(
              s"REGA = [:$label]",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
          case ">=" =>
            List(
              s"REGA = [:$label]",
              s"REGA = REGA + 1 _S",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
          case "<=" =>
            List(
              s"REGA = [:$label]",
              s"REGA = REGA - 1 _S",
              s"REGA = REGA PASS_A $v _S" // this op is unimportant
            )
        }

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

      new Block(s"while", s"$SPACE($cond) with ${content.size} inner blocks") {
        override def gen(depth: Int, parent: Block): List[String] = {
          val labelBase = parent.fqnUnique("while")

          val labelTop = s"${labelBase}_top"
          val labelBot = s"${labelBase}_bot"

          val flagToCheck = cond._1
          val conditionBlock = cond._2

          val prefix = split(s"$labelTop:")

          //JL1  .rename(newName = label))
          val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

          val conditionalJump = condStatements ++ split(
            s"""
               |PCHITMP = <:$labelBot
               |PC = >:$labelBot $flagToCheck
               """)

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent.pushName(newName = labelBase))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelTop
               |PC = >:$labelTop
               |$labelBot:
               |""")

          prefix ++ conditionalJump ++ stmts ++ suffix
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


  abstract class Block(val typ: String, val context: String) {
    def blockName = s"${typ}$context"

    protected[this] def gen(depth: Int, parent: Block): List[String]

    final def expr(depth: Int, parent: Block): List[String] = {
      try {
        val value: List[String] = gen(depth, parent).map(l => {
          prefixOp(depth) + l
        })
        enter(depth) +: value :+ exit(depth)
      } catch {
        case ex: Exception =>
          // throw with updated message but existing stack trace
          val message = ex.getMessage
          val exception = new RuntimeException(s"$message @ \n '$blockName'", ex)
          exception.setStackTrace(ex.getStackTrace)
          throw exception
      }
    }

    override def toString() = s"Block($blockName)"

    def fqn(child: String): String = {
      blockName + LABEL_NAME_SEPARATOR + child
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnUnique(child: String): String = {
      Block.idx += 1
      blockName + LABEL_NAME_SEPARATOR + child + "_" + Block.idx
    }

    def pushName(newName: String): Block = new Block(newName, this.context) {
      override def gen(depth: Int, parent: Block): List[String] = Block.this.expr(depth, parent)
    }

    private def prefixComment(depth: Int) = s"; ($depth) ${" " * depth}";

    private def prefixOp(depth: Int) = prefixComment(depth).replaceAll(".", " ");

    private def enter(depth: Int) = s"${prefixComment(depth)}ENTER $blockName";

    private def exit(depth: Int) = s"${prefixComment(depth)}EXIT  $blockName";

  }

  def assignVar(label: String): String = {

    def upd = {
      varLocn += 1
      (label, varLocn)
    }

    vars.getOrElseUpdate(label, upd)._1
  }

  def assignVarLabel(block: Block, name: String): String = {
    val fqn = block.fqn(name)
    vars.get(fqn).map { existing =>
      sys.error(s"scc error: $name is already defined as ${existing}")
    }
    val label = assignVar(fqn)
    assignVar(label)
  }

  def getVarLabel(block: Block, name: String): String = {
    val fqn = block.fqn(name)
    val blockStr = block.toString()
    vars.get(fqn).
      getOrElse(sys.error(s"scc error: $name has not been defined yet in $blockStr"))._1
  }

  def program: Parser[List[String]] = (function +) ^^ {
    fns =>
      val Depth0 = 0
      val Root = new Block("root", "") {
        override def gen(depth: Int, parent: Block): List[String] = Nil
      }

      val asm: List[String] = fns.flatMap(b =>
        b.expr(Depth0, Root)
      )

      val varlist = vars.toList.sortBy(_._2._2).map { x => s"${x._1}: EQU ${x._2._2}" }
      varlist ++ asm :+ "root_end:" :+ "END"
  }
}