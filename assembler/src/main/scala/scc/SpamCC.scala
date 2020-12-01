package scc

import java.io.{File, PrintWriter}

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
      System.err.println("SpamCC ...")
      System.err.println("    usage:  file-name.asm ")
      sys.exit(1)
    }
  }

  private def compile(args: Array[String]) = {
    val fileName = args(0)

    val code = Source.fromFile(fileName).getLines().mkString("\n")

    val compiler = new SpamCC()

    val asm: List[String] = compiler.compile(code)

    val pw = new PrintWriter(new File(s"${fileName}.asm"))
    asm.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }
    pw.close()
  }
}

class SpamCC extends JavaTokenParsers {

  def SEMICOLON = ";"

  val SPACE = " ";

  var varLocn = -1

  //  val labels = mutable.TreeMap.empty[String, Int]
  val vars = mutable.TreeMap.empty[String, (String, Int)]

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
  def statementEqConst: Parser[Block] = "var" ~> name ~ "=" ~ constExpr <~ SEMICOLON ^^ {
    case target ~ _ ~ v =>
      new Block("statementVar", s"$target=$v") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val label = parent.assignVarLabel(target)
          List(
            s"[:$label] = $v",
          )
        }
      }
  }

  // optimisation of "var X=1 op Y"
  def statementEqConstOpVar: Parser[Block] = "var" ~> name ~ "=" ~ constExpr ~ op ~ name <~ SEMICOLON ^^ {
    case target ~ _ ~ e ~ op ~ v =>
      new Block("statementEqConstOpVar", s"$target=$e $op $v") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(v)
          val tLabel = parent.assignVarLabel(target)
          List(
            s"REGA = $e",
            s"REGA = REGA $op [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y op 1"
  def statementEqVarOpConst: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ constExpr <~ SEMICOLON ^^ {
    case target ~ _ ~ v ~ op ~ e =>
      new Block("statementEqVarOpConst", s"$target=$v $op $e") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(v)
          val tLabel = parent.assignVarLabel(target)
          List(
            s"REGA = [:$sLabel]",
            s"REGA = REGA $op $e",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y"
  def statementEqVar: Parser[Block] = "var" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
    case target ~ _ ~ v =>
      new Block("statementEqVar", s"$target=$v") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(v)
          val tLabel = parent.assignVarLabel(target)
          List(
            s"REGA = [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var X=Y op Z"
  def statementEqVarOpVar: Parser[Block] = "var" ~> name ~ "=" ~ name ~ op ~ name <~ SEMICOLON ^^ {
    case target ~ _ ~ s1 ~ op ~ s2 =>
      new Block("statementEqVarOpConst", s"$target=$s1 $op $s2") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val s1Label = parent.getVarLabel(s1)
          val s2Label = parent.getVarLabel(s2)
          val tLabel = parent.assignVarLabel(target)
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
      new Block("blkName", s"$n") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelSrcVar = parent.getVarLabel(n)
          List(
            s"REGA = [:$labelSrcVar]",
          )
        }
      }
  }

  def blkNExpr: Parser[Block] = constExpr ^^ {
    case i =>
      new Block("blkNExpr", s"$i") {
        override def gen(depth: Int, parent: Name): List[String] = {
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
      new Block("blkExprs", s"$str") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val temporaryVarLabel = parent.assignVarLabel("varExprs_d" + depth)

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


  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ blkExprs <~ SEMICOLON ^^ {
    case target ~ _ ~ v =>
      new Block("statementVarOp", s"$target = $v") {
        override def gen(depth: Int, parent: Name): List[String] = {

          // must do sources before calling assignVarLabel for target otherwise
          // we fail to spot undefined vars because assignVarLabel(target) causes the
          // defines vars before the source reference is considered so the source see it
          // as preexisting and the code fails to spot the error
          val stmts: List[String] = v.expr(depth + 1, parent)

          val labelTarget = parent.assignVarLabel(target)

          val assign = List(
            s"[:$labelTarget] = REGA",
          )
          stmts ++ assign
        }
      }
  }

  def statementReturn: Parser[Block] = "return" ~> constExpr ^^ {
    i: Int =>
      new Block("statementReturn", s"$i") {
        override def gen(depth: Int, parent: Name): List[String] = {
          List(
            "REGD = " + i,
          )
        }
      }
  }

  def statementReturnName: Parser[Block] = "return" ~> name ^^ {
    n: String =>
      new Block("statementReturnName", s"$n") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val label = parent.getVarLabel(n)
          List(
            s"REGD = [:$label]",
          )
        }
      }
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def stmtPutchar: Parser[Block] = "putchar" ~ "(" ~> constExpr <~ ")" ^^ {
    i: Int =>
      new Block("statementPutchar", s"$i", nestedName = "putcharI") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          split(
            s"""
               |$labelWait:
               |PCHITMP = <:$labelTransmit
               |PC = >:$labelTransmit _DO
               |PCHITMP = <:$labelWait
               |PC = <:$labelWait
               |$labelTransmit:
               |UART = $i
               |""")
        }
      }
  }


  def statementPutcharName: Parser[Block] = "putchar" ~ "(" ~> name <~ ")" ^^ {
    n: String =>
      new Block("statementPutcharName", s"$n", nestedName = "putcharN") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          val varLocn = parent.getVarLabel(n)
          split(
            s"""
               |$labelWait:
               |PCHITMP = <:$labelTransmit
               |PC = >:$labelTransmit _DO
               |PCHITMP = <:$labelWait
               |PC = <:$labelWait
               |$labelTransmit:
               |UART = [:$varLocn]
               |""")
        }
      }
  }


  def statement: Parser[Block] = (statementEqVarOpVar | statementEqVar | statementEqVarOpConst | statementEqConstOpVar | statementEqConst |
    statementReturn | statementReturnName | statementVarOp | stmtPutchar | statementPutcharName |
    whileTrue | whileCond | ifCond | breakOut)

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  def function: Parser[Block] = "def " ~> name ~ ("(" ~ ")" ~ ":" ~ "void" ~ "=" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ c =>
      // !!!NO SPACE IN THE NAME AS USED FOR LABELS
      new Block(s"function", s"_$fnName", nestedName = s"function_$fnName") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val stmts = c.flatMap {
            b => {
              //JL!              val newName = parent.blockName + NAME_SEPARATOR + fnName
              //   val newName = parent.fqn(fnName)
              b.expr(depth + 1, parent) //.pushName(newName = newName))
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

      new Block("whileTrue", s"${SPACE}true", nestedName = s"whileTrue${Name.nextInt}") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelBody = parent.toLabelPath("body")
          val labelAfter = parent.toLabelPath("after")

          val prefix = split(s"""$labelBody:""")

          val stmts = content.flatMap {
            b => {
              val nameName = parent.copy(endLabel = Some(labelAfter))
              b.expr(depth + 1, nameName) //JL1.pushName(newName = label))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelBody
               |PC = >:$labelBody
               |$labelAfter:
               |""")


          prefix ++ stmts ++ suffix
        }
      }
  }

  def whileCond: Parser[Block] = "while" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
    case cond ~ _ ~ _ ~ content =>

      new Block(s"whileCond", s"$SPACE($cond) with ${content.size} inner blocks", nestedName = s"whileCond${Name.nextInt}") {
        override def gen(depth: Int, parent: Name): List[String] = {
          //                    val labelBase = parent.fqnLocalUnique()

          val labelCheck = parent.toLabelPath("check") // s"${labelBase}_check"
          val labelBody = parent.toLabelPath("body")
          val labelBot = parent.toLabelPath("bot")

          val flagToCheck = cond._1
          val conditionBlock = cond._2

          val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

          val conditionalJump = {
            List(s"$labelCheck:") ++
              condStatements ++
              split(
                s"""
                   |PCHITMP = <:$labelBody
                   |PC = >:$labelBody $flagToCheck
                   |PCHITMP = <:$labelBot
                   |PC = >:$labelBot
                   |$labelBody:
               """)
          }

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent) //.pushName(newName = labelBase))
            }
          }

          val suffix = split(
            s"""
               |PCHITMP = <:$labelCheck
               |PC = >:$labelCheck
               |$labelBot:
               |""")

          conditionalJump ++ stmts ++ suffix
        }

      }
  }

  def ifCond: Parser[Block] = "if" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
    case cond ~ _ ~ _ ~ content =>

      new Block(s"ifCond", s"$SPACE($cond) with ${content.size} inner blocks", nestedName = s"ifCond${Name.nextInt}") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val labelCheck = parent.toLabelPath("check") // s"${labelBase}_check"
          val labelBody = parent.toLabelPath("body")
          val labelBot = parent.toLabelPath("bot")

          val flagToCheck = cond._1
          val conditionBlock = cond._2

          val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

          val conditionalJump = {
            List(s"$labelCheck:") ++
              condStatements ++
              split(
                s"""
                   |PCHITMP = <:$labelBody
                   |PC = >:$labelBody $flagToCheck
                   |PCHITMP = <:$labelBot
                   |PC = >:$labelBot
                   |$labelBody:
               """)
          }

          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, parent) //.pushName(newName = labelBase))
            }
          }

          val suffix = split(
            s"""
               |$labelBot:
               |""")

          conditionalJump ++ stmts ++ suffix
        }
      }
  }

  def breakOut: Parser[Block] = "break" ^^ {
    case _ =>
      new Block(s"break", "") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val breakToLabel = parent.getEndLabel().getOrElse {
            throw new RuntimeException("spamcc error: cannot use 'break' without surrounding 'while' block")
          }

          split(
            s"""
               |PCHITMP = <:$breakToLabel
               |PC = >:$breakToLabel
               """)
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
      override def gen(depth: Int, parent: Name): List[String] = {
        val label = parent.getVarLabel(n)

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

  trait Generator {
    def apply(depth: Int, blk: Block): List[String]

    override def toString() = "Code()"
  }


  abstract class Block(val typ: String, val context: String, nestedName: String = "") {
    //    private val compName = s"${typ}_$context"

    protected[this] def gen(depth: Int, parent: Name): List[String]

    final def expr(depth: Int, parent: Name): List[String] = {

      val newName = parent.pushScope(nestedName)

      val enter = s"${prefixComment(depth)}ENTER ${newName.blockName} @ $typ";
      val exit = s"${prefixComment(depth)}EXIT  ${newName.blockName} @ $typ";

      try {
        val value: List[String] = gen(depth, newName).map(l => {
          prefixOp(depth) + l
        })
        enter +: value :+ exit
      } catch {
        case ex: Exception =>
          // throw with updated message but existing stack trace
          val message = ex.getMessage
          val exception = new RuntimeException(s"$message @ \n '${parent.blockName}'", ex)
          exception.setStackTrace(ex.getStackTrace)
          throw exception
      }
    }

    override def toString() = s"Block($typ $context)"

    //    def fqn(child: String): String = {
    //      blockName + LABEL_NAME_SEPARATOR + child
    //    }
    //
    //    /* returns a globally unique name that is contextual by ibcluding the block name*/
    //    def fqnUnique(child: String): String = {
    //      Block.idx += 1
    //      blockName + LABEL_NAME_SEPARATOR + child + "_" + Block.idx
    //    }
    //
    //    def pushName(newName: String): Block = new Block(newName, this.context) {
    //      override def gen(depth: Int, parent: Block): List[String] = Block.this.expr(depth, parent)
    //    }

    private def prefixComment(depth: Int) = s"; ($depth) ${" " * depth}";

    private def prefixOp(depth: Int) = prefixComment(depth).replaceAll(".", " ");

  }

  object Name {
    lazy val RootName = Name(null, "root")

    private[this] var idx = 0

    def nextInt: Int = {
      idx += 1
      idx
    }
  }

  case class Name private(parent: Name, name: String, endLabel: Option[String] = None) {
    final val LABEL_NAME_SEPARATOR = "_"

    def blockName: String = {
      if (parent != null) {
        parent.blockName + "_" + name
      } else
        name
    }

    def getEndLabel(): Option[String] = endLabel.orElse(parent.getEndLabel())

    override def toString() = s"Name(path=$blockName, endLabel=$endLabel)"

    def toLabelPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "LABEL_" + child
    }

    def toVarPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "VAR_" + child
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnLabelPathUnique(child: String): String = {
      toLabelPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
      //      val labelBase = fqnLocalUnique()
      //      labelBase + LABEL_NAME_SEPARATOR + child
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnVarPathUnique(child: String): String = {
      toVarPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
      //      val labelBase = fqnLocalUnique()
      //      labelBase + LABEL_NAME_SEPARATOR + child
    }

    //
    //    def fqnLocalUnique(): String = {
    //      toLabelPath("") + LABEL_NAME_SEPARATOR + Name.nextInt
    //
    ////      val idx = Name.nextInt
    ////      blockName + LABEL_NAME_SEPARATOR + "_" + idx + "_"
    //    }

    def pushScope(newScopeName: String): Name = {
      if (newScopeName.size > 0) this.copy(parent = this, name = newScopeName)
      else this
    }

    def assignVarLabel(name: String): String = {
      val label = lookupVarLabel(name)
      label.getOrElse {

        val localName = toVarPath(name)

        // need a separate "var a" vs "let a" if want to distinguish definition from update!!
        //        vars.get(locaName).map { existing =>
        //          sys.error(s"scc error: $name is already defined as $existing")
        //        }

        def upd = {
          varLocn += 1
          (localName, varLocn)
        }

        vars.getOrElseUpdate(localName, upd)._1
      }
    }

    def lookupVarLabel(name: String): Option[String] = {
      val fqn1 = toVarPath(name)
      val maybeTuple: Option[(String, Int)] = vars.get(fqn1)
      val labelAndPos = maybeTuple.map(si => si._1).orElse {
        if (parent != null)
          parent.lookupVarLabel(name)
        else
          None
      }

      labelAndPos
    }

    def getVarLabel(name: String): String = {
      val label = lookupVarLabel(name)
      label.getOrElse {
        sys.error(s"scc error: $name has not been defined yet @ $this")
      }
    }
  }

  def program: Parser[List[String]] = (function +) ^^ {
    fns =>
      val Depth0 = 0

      val asm: List[String] = fns.flatMap {
        b => {
          b.expr(Depth0, Name.RootName)
        }
      }

      val varlist = vars.toList.sortBy(_._2._2).map { x => s"${x._1}: EQU ${x._2._2}" }
      varlist ++ asm :+ "root_end:" :+ "END"
  }
}

