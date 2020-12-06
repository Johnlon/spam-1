package scc

import java.io.{File, PrintWriter}

import asm.{AluOp, EnumParserOps}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers

object SpamCC {

  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      compile(args)
    }
    else {
      System.err.println("SpamCC ...")
      System.err.println("    usage:  file-name.asm ")
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
}

class SpamCC extends EnumParserOps with JavaTokenParsers {
  private val MAIN_LABEL = "ROOT________main_start"

  private def SEMICOLON = ";"

  private val SPACE = " "

  private val variables = mutable.TreeMap.empty[String, List[Byte]]

  def compile(code: String): List[String] = {

    parse(program, code) match {
      case Success(matched, _) =>
        matched
      case msg: Failure =>
        sys.error(s"FAILURE: $msg ")
      case msg: Error =>
        sys.error(s"ERROR: $msg")
    }
  }

  def decToken: Parser[Int] =
    """-?\d+""".r ^^ { v =>
      v.toInt
    }

  def charToken: Parser[Int] = "'" ~> ".".r <~ "'" ^^ { v =>
    val i = v.codePointAt(0)
    if (i > 127) throw new RuntimeException(s"asm error: character '$v' codepoint $i is outside the 0-127 range")
    i.toByte
  }

  def hexToken: Parser[Int] = "$" ~ "[0-9a-hA-H]+".r ^^ { case _ ~ v => Integer.valueOf(v, 16) }

  def binToken: Parser[Int] = "%" ~ "[01]+".r ^^ { case _ ~ v => Integer.valueOf(v, 2) }

  def octToken: Parser[Int] = "@" ~ "[0-7]+".r ^^ { case _ ~ v => Integer.valueOf(v, 8) }

  def aluOp: Parser[String] = {
    val shortAluOps = {
      // reverse sorted to put longer operators ahead of shorter ones otherwise shorter ones gobble
      val reverseSorted = AluOp.values.filter(_.isAbbreviated).sortBy(x => x.abbrev).reverse.toList
      reverseSorted map { m =>
        literal(m.abbrev) ^^^ m
      } reduceLeft {
        _ | _
      }
    }
    val longAluOps = enumToParser(AluOp.values)
    (longAluOps | shortAluOps).map(_.preferredName)
  }

  def nFactor: Parser[Int] = charToken | decToken | hexToken | binToken | octToken | "(" ~> constExpr <~ ")"

  def contOperations: Parser[String] = "+" | "-" | "*" | "/"

  //  def constExpr: Parser[Int] = nFactor ~ ((aluOp ~ nFactor) *) ^^ {
  def constExpr: Parser[Int] = nFactor ~ ((contOperations ~ nFactor) *) ^^ {
    case x ~ list =>
      list.foldLeft(x)({
        case (acc, "+" ~ i) => acc + i
        case (acc, "*" ~ i) => acc * i
        case (acc, "/" ~ i) => acc / i
        case (acc, "-" ~ i) => acc - i
      })
  }

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ (a => a)

  // optimisation of "var VARIABLE=CONST"
  def statementEqConst: Parser[Block] = "var" ~> name ~ "=" ~ constExpr <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ konst =>
      new Block("statementEqConst", s"$targetVar=$konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val label = parent.assignVarLabel(targetVar)
          List(
            s"[:$label] = $konst",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=CONST op VARIABLE"
  def statementEqConstOpVar: Parser[Block] = "var" ~> name ~ "=" ~ constExpr ~ aluOp ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ konst ~ oper ~ srcVar =>
      new Block("statementEqConstOpVar", s"$targetVar=$konst $oper $srcVar") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srcVar)
          val tLabel = parent.assignVarLabel(targetVar)
          List(
            s"REGA = $konst",
            s"REGA = REGA $oper [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=VARIABLE op CONST"
  def statementEqVarOpConst: Parser[Block] = "var" ~> name ~ "=" ~ name ~ aluOp ~ constExpr <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srcVar ~ op ~ konst =>
      new Block("statementEqVarOpConst", s"$targetVar=$srcVar $op $konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srcVar)
          val tLabel = parent.assignVarLabel(targetVar)
          List(
            s"REGA = [:$sLabel]",
            s"REGA = REGA $op $konst",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  trait IsVariable

  trait IsConst

  // optimisation of "var VARIABLE=VARIABLE"
  def statementEqVar: Parser[Block] = "var" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srvVar =>
      new Block("statementEqVar", s"$targetVar=$srvVar") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srvVar)
          val tLabel = parent.assignVarLabel(targetVar)
          List(
            s"REGA = [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=VARIABLE op VARIABLE"
  def statementEqVarOpVar: Parser[Block] = "var" ~> name ~ "=" ~ name ~ aluOp ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srcVar1 ~ op ~ srcVar2 =>
      new Block("statementEqVarOpVar", s"$targetVar=$srcVar1 $op $srcVar2") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val s1Label = parent.getVarLabel(srcVar1)
          val s2Label = parent.getVarLabel(srcVar2)
          val tLabel = parent.assignVarLabel(targetVar)
          List(
            s"REGA = [:$s1Label]",
            s"REGB = [:$s2Label]",
            s"[:$tLabel] = REGA $op REGB",
          )
        }
      }
  }

  trait IsStandaloneVarExpr {
    def variableName: String
  }

  def blkVarExpr: Parser[Block] = name ^^ {
    n =>
      new Block("blkVar", s"$n") with IsStandaloneVarExpr {
        override def toString = s"$n"

        override def gen(depth: Int, parent: Name): List[String] = {
          val labelSrcVar = parent.getVarLabel(n)
          List(
            s"REGA = [:$labelSrcVar]",
          )
        }

        override def variableName: String = n
      }
  }

  def blkArrayElementExpr: Parser[Block] = name ~ "[" ~ compoundBlkExpr ~ "]" ^^ {
    case arrayName ~ _ ~ blkExpr ~ _ =>
      new Block("blkArrayElement", s"$arrayName[$blkExpr]") with IsStandaloneVarExpr {
        override def toString = s"$arrayName"

        override def gen(depth: Int, parent: Name): List[String] = {

          // drops result into A
          val stmts: List[String] = blkExpr.expr(depth + 1, parent)

          val labelSrcVar = parent.getVarLabel(arrayName)

          stmts ++ List(
            s"MARLO = REGA + (>:$labelSrcVar) _S",
            s"MARHI = <:$labelSrcVar",
            s"MARHI = NU B_PLUS_1 <:$labelSrcVar _C",
            s"REGA = RAM",
          )
        }

        override def variableName: String = arrayName
      }
  }

  def blkNExpr: Parser[Block] = constExpr ^^ {
    i =>
      new Block("blkNExpr", s"$i") {
        override def toString = s"$i"

        override def gen(depth: Int, parent: Name): List[String] = {
          List(
            s"REGA = $i",
          )
        }

      }
  }

  def blkSingleExpr: Parser[Block] = blkArrayElementExpr | blkNExpr | blkVarExpr
//
//  def blkBiExpr: Parser[Block] = blkSingleExpr ~ aluOp ~ blkSingleExpr ^^ {
//    case leftExpr ~ oper ~ rightExpr =>
//      val description = s"$leftExpr $oper $rightExpr"
//
//      new Block("blkBiExpr", s" $description") with IsCompoundExpressionBlock {
//
//        override def toString = s"$description"
//
//        override def gen(depth: Int, parent: Name): List[String] = {
//          val temporaryVarLabel = parent.assignVarLabel("blkBiExpr" + depth)
//
//          val leftStatement: List[String] = leftExpr.expr(depth + 1, parent)
//          val rightStatement: List[String] = rightExpr.expr(depth + 1, parent)
//
//
//          List(s"; load right expression") ++
//            rightStatement ++
//            List(s"[:$temporaryVarLabel] = REGA") ++
//            List(s"; load left expression") ++
//            leftStatement ++
//            List(s"; perform op") ++
//            List(s"REGA = REGA $oper [:$temporaryVarLabel]")
//        }
//
//        /* populated only if this block evaluates to a standalone variable reference*/
//        override def variableName: Option[String] = {
//          None
//        }
//      }
//  }

  def blkExpr: Parser[Block] = blkSingleExpr | "(" ~> compoundBlkExpr <~ ")"
//  def blkExpr: Parser[Block] = blkBiExpr | blkSingleExpr | "(" ~> compoundBlkExpr <~ ")"

  sealed trait IsCompoundExpressionBlock {
    def variableName: Option[String]
  }

  def compoundBlkExpr: Parser[Block] = blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
    case leftExpr ~ otherExpr =>
      val description = otherExpr.foldLeft(leftExpr.toString()) {
        case (acc, b) =>
          s"$acc ${b._1} (${b._2})"
      }

      new Block("compoundBlkExpr", s" $description") with IsCompoundExpressionBlock {

        override def toString = s"$description"

        override def gen(depth: Int, parent: Name): List[String] = {

          val leftStatement: List[String] = leftExpr.expr(depth + 1, parent)

          // if there is no right side then no need for temporary variables or merge logic
          val optionalExtraForRight = if (otherExpr.size > 0) {
            val temporaryVarLabel = parent.assignVarLabel("compoundBlkExpr" + depth)

            val assignLeftToTemp =
              List(
                s"; assign clause 1 result to [:$temporaryVarLabel] = ${leftExpr.context} ",
                s"[:$temporaryVarLabel] = REGA"
              )

            // In an expression the result of the previous step is accumulated in the assigned temporaryVarLabel.
            // It is somewhat inefficient that I has to shove the value into RAM and back out on each step.
            var x = 1

            val otherStatements: List[String] = otherExpr.reverse.flatMap { case op ~ b =>
              // clause must drop it's result into REGC
              val expressionClause = b.expr(depth + 1, parent)

              x += 1
              expressionClause ++
                List(
                  s"; concatenate clause $x to [:$temporaryVarLabel] <= $op ${b.context}",
                  s"REGC = [:$temporaryVarLabel]",
                  s"[:$temporaryVarLabel] = REGC $op REGA"
                )
            }

            val suffix = split(
              s"""
                 |; assigning result back to REGA
                 |REGA = [:$temporaryVarLabel]
                 |""")

            assignLeftToTemp ++ otherStatements ++ suffix
          } else Nil

          leftStatement ++ optionalExtraForRight
        }

        /* populated only if this block evaluates to a standalone variable reference*/
        override def variableName: Option[String] = {
          // return a variable name ONLY if this expression is simply a standalone variable name
          if (otherExpr.isEmpty) {
            // is a single clause
            leftExpr match {
              case v: IsStandaloneVarExpr =>
                // clause is a variable name
                Some(v.variableName)
              case _ =>
                None
            }
          } else None
        }
      }
  }


  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ compoundBlkExpr <~ SEMICOLON ^^ {
    case target ~ _ ~ v =>
      new Block("statementVarOp", s"$target = $v") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val stmts: List[String] = v.expr(depth + 1, parent)

          val labelTarget = parent.assignVarLabel(target)

          val assign = List(
            s"[:$labelTarget] = REGA",
          )
          stmts ++ assign
        }
      }
  }

  // permits \0 null char
  def quotedString: Parser[String] = ("\"" + """([^"\x01-\x1F\x7F\\]|\\[\\'"0bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\"").r ^^ {
    s =>
      val withoutQuotes = s.stripPrefix("\"").stripSuffix("\"")
      val str = org.apache.commons.text.StringEscapeUtils.unescapeJava(withoutQuotes)
      str
  }

  /*
  STRING:     STR     "ABC\n\0\u0000"
  BYTE_ARR:   BYTES   [ 'A', 65 ,$41, %10101010 ] ; parse as hex bytes and then treat as per STR
  */
  def statementVarString: Parser[Block] = "var" ~> name ~ "=" ~ quotedString <~ SEMICOLON ^^ {
    case target ~ _ ~ str =>
      new Block("statementVarEqString", s"$target = $str") {
        override def gen(depth: Int, parent: Name): List[String] = {
          // nothing to but record the data with current scope - data will be laid out later
          parent.assignVarLabel(target, str.getBytes("UTF-8").toList)
          List(
            s"""; $target = "$str""""
          )
        }
      }
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def stmtPutsName: Parser[Block] = "puts" ~ "(" ~> name <~ ")" ^^ {
    varName =>
      new Block("stmtPuts", s"$varName", nestedName = "puts") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelStartLoop = parent.fqnLabelPathUnique("startloop")
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          val labelEndLoop = parent.fqnLabelPathUnique("endloop")

          val varLocn = parent.getVarLabel(varName)

          split(
            s"""
               |MARLO = >:$varLocn
               |MARHI = <:$varLocn
               |$labelStartLoop:
               |; break if NULL
               |NOOP = RAM _S
               |PCHITMP = <:$labelEndLoop
               |PC = >:$labelEndLoop _Z
               |; wait for tx ready
               |$labelWait:
               |PCHITMP = <:$labelTransmit
               |PC = >:$labelTransmit _DO
               |PCHITMP = <:$labelWait
               |PC = <:$labelWait
               |; do transmit
               |$labelTransmit:
               |UART = RAM
               |; goto next char
               |MARLO = MARLO + 1 _S
               |MARHI = MARHI + 1 _C
               |PCHITMP = <:$labelStartLoop
               |PC = >:$labelStartLoop
               |$labelEndLoop:
               """)
        }
      }
  }

  def stmtPutcharGeneral: Parser[Block] = "putchar" ~ "(" ~> compoundBlkExpr <~ ")" ^^ {
    bex =>
      new Block("stmtPutcharGeneral", s"$bex", nestedName = "putcharGeneral") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")

          // leaves result in REGA
          val stmts: List[String] = bex.expr(depth + 1, parent)

          stmts ++ split(
            s"""
               |$labelWait:
               |PCHITMP = <:$labelTransmit
               |PC = >:$labelTransmit _DO
               |PCHITMP = <:$labelWait
               |PC = <:$labelWait
               |$labelTransmit:
               |UART = REGA
               |""")
        }
      }
  }

  def statementPutcharVarOptimisation: Parser[Block] = "putchar" ~ "(" ~> name <~ ")" ^^ {
    varName: String =>
      new Block("statementPutcharVarOptimisation", s"$varName", nestedName = s"putcharVar_${varName}_") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          val varLocn = parent.getVarLabel(varName)
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

  def statementPutcharConstOptimisation: Parser[Block] = "putchar" ~ "(" ~> constExpr <~ ")" ^^ {
    varName =>
      new Block("statementPutcharConstOptimisation", s"$varName", nestedName = s"putcharI_${varName}_") {
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
               |UART = $varName
               |""")
        }
      }
  }

  //
  //  def stmtPutsGeneral: Parser[Block] = "puts" ~ "(" ~> blkExpr <~ ")" ^^ {
  //    bex =>
  //      new Block("stmtPutsGeneral", s"$bex", nestedName = "putsGeneral") {
  //        override def gen(depth: Int, parent: Name): List[String] = {
  //          val labelWait = parent.fqnLabelPathUnique("wait")
  //          val labelTransmit = parent.fqnLabelPathUnique("transmit")
  //
  //          // leaves result in REGA
  //          val stmts: List[String] = bex.expr(depth + 1, parent)
  //
  //          stmts ++ split(
  //            s"""
  //               |$labelWait:
  //               |PCHITMP = <:$labelTransmit
  //               |PC = >:$labelTransmit _DO
  //               |PCHITMP = <:$labelWait
  //               |PC = <:$labelWait
  //               |$labelTransmit:
  //               |UART = REGA
  //               |""")
  //        }
  //      }
  //  }


  def statement: Parser[Block] =
    statementEqVarOpVar | statementEqVar | statementEqVarOpConst | statementEqConstOpVar | statementEqConst |
      statementVarOp |
      statementPutcharVarOptimisation | statementPutcharConstOptimisation | stmtPutcharGeneral |
      stmtPutsName |
      whileTrue | whileCond | ifCond | breakOut | functionCall | comment |
      statementVarString ^^ {
        s =>
          s
      }

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  case class FunctionArgName(name: String, output: Boolean)

  case class FunctionArgNameAndLabel(labelName: String, argName: FunctionArgName)

  case class FunctionDef(startLabel: String, returnHiLabel: String, returnLoLabel: String, args: List[FunctionArgNameAndLabel])

  trait IsFunction {
    self: Block =>

    def functionName: String

    def functionArgs: List[FunctionArgName]

    def scopedArgLabels(scope: Name): FunctionDef = {
      val returnHiLabel = scope.assignVarLabel("RETURN_HI")
      val returnLoLabel = scope.assignVarLabel("RETURN_LO")
      // These locations where we write the input parameters into the function.
      // Also, read from these locations to fetch "out" values.
      val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.map {
        argName =>
          FunctionArgNameAndLabel(scope.assignVarLabel(argName.name), FunctionArgName(argName.name, argName.output))
      }
      val fnStart = scope.toLabelPath("START")

      FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
    }
  }


  def functionDef: Parser[Block] = "fun " ~> name ~ "(" ~ repsep((name ~ ("out" ?)), ",") ~ (")" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ args ~ _ ~ content =>

      // !!!NO SPACE IN THE NAME AS USED FOR LABELS
      val fn = new Block(s"function", s"_$fnName(${args.mkString(",")})", nestedName = s"function_$fnName") with IsFunction {

        override def functionName: String = fnName

        override def functionArgs: List[FunctionArgName] = args.map {
          case n ~ io =>
            FunctionArgName(n, io.isDefined)
        }

        override def gen(depth: Int, scope: Name): List[String] = {

          val FunctionDef(startLabel, returnHi, returnLo, argsLabels) = scopedArgLabels(scope)

          val prefix = if (fnName == "main") {
            List(
              s"$MAIN_LABEL:",
              s"$startLabel:"
            )
          } else
            List(s"$startLabel:")


          // eval the code that uses these vars
          val stmts = content.flatMap {
            b => {
              b.expr(depth + 1, scope)
            }
          }

          val suffix = if (fnName == "main") {
            List(
              "PCHITMP = <:root_end",
              "PC = >:root_end"
            )
          } else {
            List(
              s"PCHITMP = [:$returnHi]",
              s"PC = [:$returnLo]"
            )
          }

          prefix ++ stmts ++ suffix
        }
      }


      fn
  }

  def whileTrue: Parser[Block] = "while" ~ "(" ~ "true" ~ ")" ~ "{" ~> statements <~ "}" ^^ {
    content =>

      new Block("whileTrue", s"${SPACE}true", nestedName = s"whileTrue${Name.nextInt}") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelBody = parent.toLabelPath("BODY")
          val labelAfter = parent.toLabelPath("AFTER")

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

          val labelCheck = parent.toLabelPath("CHECK")
          val labelBody = parent.toLabelPath("BODY")
          val labelBot = parent.toLabelPath("AFTER")

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

          val labelCheck = parent.toLabelPath("CHECK")
          val labelBody = parent.toLabelPath("BODY")
          val labelBot = parent.toLabelPath("AFTER")

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
    _ =>
      new Block(s"break", "") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val breakToLabel = parent.getEndLabel.getOrElse {
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


  trait isFunctionCall

  // DODO - NEED T HANDLE OUT PARAMS !!!!!!!!!!!!!!!!!!!!!!!!!!!!

  def functionCall: Parser[Block] = name ~ "(" ~ repsep(compoundBlkExpr, ",") ~ ")" ^^ {
    case fnName ~ _ ~ argExpr ~ _ =>
      new Block(s"functionCall", s"""$fnName(${argExpr.mkString(", ")})""") with isFunctionCall {

        override def gen(depth: Int, parent: Name): List[String] = {
          val fns = parent.lookupFunction(fnName)
          val (functionScope: Name, fn: Block with IsFunction) = fns.getOrElse(sys.error(s"no such function '$fnName''"))

          val FunctionDef(startLabel, returnHiLabel, returnLoLabel, argsLabelsAndDir) = fn.scopedArgLabels(functionScope)

          val argNamesAndDir: List[FunctionArgName] = fn.functionArgs

          if (argExpr.length != argNamesAndDir.size) {
            val argsNames = argNamesAndDir.map(_.name)
            sys.error(s"""call to function "$fnName" has wrong number of arguments for ; expected $fnName(${argsNames.mkString(",")}) but got $fnName(${argExpr.mkString(",")})""")
          }

          val argDefinitionVsExpression = argsLabelsAndDir.zip(argExpr)

          // instructions needed to evaluate parameter clauses and set the values of the function input variables
          val setupCallParams: List[String] = argDefinitionVsExpression.flatMap {
            case (FunctionArgNameAndLabel(argLabel, _), argBlk) =>
              // evaluate the arg expression
              val stmts: List[String] = argBlk.expr(depth + 1, parent)
              // put the result into the input var locations
              stmts :+ s"[:$argLabel] = REGA"
          }

          // instructions needed to capture the output of the function into local vars within the caller's scope
          val setupOutParams: List[String] = argDefinitionVsExpression.flatMap {
            case (FunctionArgNameAndLabel(argLabel, nameAndOutput), argBlk) =>
              if (nameAndOutput.output) {
                // if this arg is defined as an output then ...
                argBlk match {
                  case v: IsCompoundExpressionBlock =>
                    // is this expression a standalone variable name reference?
                    val vn = v.variableName
                    vn match {
                      case Some(name) =>
                        val localVarLabel = parent.lookupVarLabel(name).getOrElse {
                          sys.error(s"""output parameter variable '$name' in call to function "$fnName" is not defined""")
                        }

                        // recover ourput value from the function and assign back to the local variable
                        List(
                          s"REGA = [:$argLabel]",
                          s"[:$localVarLabel] = REGA"
                        )
                      case _ =>
                        sys.error(s"""output parameter '${nameAndOutput.name}' in call to function "$fnName" is not a pure variable reference, but is '$v'""")
                    }
                  case somethingElse =>
                    sys.error(s"""output parameter '${nameAndOutput.name}' in call to function "$fnName" is not a pure variable reference, but is '$somethingElse'""")
                }
              }
              else // not an output param
                Nil
          }

          val labelReturn = parent.fqnLabelPathUnique("RETURN_LOCATION")

          val setupReturnJumpParams: List[String] = List(
            s"; set return address variables of function",
            s"[:$returnHiLabel] = < :$labelReturn",
            s"[:$returnLoLabel] = > :$labelReturn"
          )

          val setupJumpToFn = List(
            s"; do jump to function '$fnName''",
            s"PCHITMP = < :$startLabel",
            s"PC = > :$startLabel",
            s"; will return to this next location",
            s"$labelReturn:"
          )

          setupCallParams ++ setupReturnJumpParams ++ setupJumpToFn ++ setupOutParams
        }
      }
  }

  def comparison: Parser[String] = ">" | "<" | ">=" | "<" | "<=" | "==" | "!="

  // return the block of code and the name of the flag to add to the jump operation
  def condition: Parser[(String, Block)] = name ~ comparison ~ constExpr ^^ {
    case varName ~ compOp ~ konst =>
      val b = new Block("condition", s"$SPACE$varName $compOp $konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val label = parent.getVarLabel(varName)

          compOp match {
            case ">" | "<" | "==" | "!=" =>
              List(
                s"REGA = [:$label]",
                s"REGA = REGA PASS_A $konst _S" // this op is unimportant
              )
            case ">=" =>
              List(
                s"REGA = [:$label]",
                s"REGA = REGA + 1 _S",
                s"REGA = REGA PASS_A $konst _S" // this op is unimportant
              )
            case "<=" =>
              List(
                s"REGA = [:$label]",
                s"REGA = REGA - 1 _S",
                s"REGA = REGA PASS_A $konst _S" // this op is unimportant
              )
          }

        }
      }

      val cpuFlag = compOp match {
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

    override def toString = "Code()"
  }


  def comment: Parser[Block] = "//.*".r ^^ {
    c =>
      new Block("comment", s"${SPACE}$c") {
        override def gen(depth: Int, parent: Name): List[String] = {
          List(s"; $c")
        }
      }
  }

  abstract class Block(val typ: String, val context: String, nestedName: String = "", logEntryExit: Boolean = true) {

    protected[this] def gen(depth: Int, parent: Name): List[String]

    final def expr(depth: Int, parentScope: Name): List[String] = {

      val thisScope = localize(parentScope)

      val enter = s"${prefixComment(depth)}ENTER ${thisScope.blockName} @ $typ"
      val exit = s"${prefixComment(depth)}EXIT  ${thisScope.blockName} @ $typ"

      try {
        val value: List[String] = this match {
          case bf: Block with IsFunction =>
            parentScope.addFunction(thisScope, bf)

            gen(depth, thisScope).map(l => {
              prefixOp(depth) + l
            })
          case _ =>
            gen(depth, thisScope).map(l => {
              prefixOp(depth) + l
            })
        }

        if (logEntryExit)enter +: value :+ exit
        else value

      } catch {
        case ex: Exception =>
          // throw with updated message but existing stack trace
          val message = ex.getMessage
          val cause = if (ex.getCause == null) ex else ex.getCause
          val exception = new RuntimeException(s"$message @ \n '${parentScope.blockName}'", cause)
          exception.setStackTrace(ex.getStackTrace)
          throw exception
      }
    }

    def localize(parent: Name): Name = {
      parent.pushScope(nestedName)
    }

    override def toString = s"Block($typ $context)"

    private def prefixComment(depth: Int) = s"; ($depth) ${" " * depth}"

    private def prefixOp(depth: Int) = prefixComment(depth).replaceAll(".", " ")

  }

  object Name {
    lazy val RootName: Name = Name(null, "root")

    private[this] var idx = 0

    def nextInt: Int = {
      idx += 1
      idx
    }
  }

  case class Name private(parent: Name, name: String, endLabel: Option[String] = None, functions: ListBuffer[(Name, Block with IsFunction)] = ListBuffer.empty) {

    def lookupFunction(name: String): Option[(Name, Block with IsFunction)] = {
      val maybeBlock = functions.find(f => f._2.functionName == name)
      maybeBlock.orElse {
        Option(parent).flatMap(_.lookupFunction(name))
      }
    }

    final val LABEL_NAME_SEPARATOR = "_"

    def blockName: String = {
      if (parent != null) {
        parent.blockName + "_" + name
      } else
        name
    }

    def getEndLabel: Option[String] = endLabel.orElse(Option(parent).flatMap(_.getEndLabel))

    override def toString = s"Name(path=$blockName, endLabel=$endLabel)"

    def toVarPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "VAR_" + child
    }

    def toLabelPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "LABEL_" + child
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnLabelPathUnique(child: String): String = {
      toLabelPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnVarPathUnique(child: String): String = {
      toVarPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
    }

    def pushScope(newScopeName: String): Name = {
      if (newScopeName.length > 0) this.copy(parent = this, name = newScopeName)
      else this
    }

    def addFunction(functionScope: Name, newFunction: Block with IsFunction): Unit = {
      val newReg = (functionScope, newFunction)
      functions.append(newReg)
    }

    def assignVarLabel(name: String, data: List[Byte] = List(0)): String = {
      val label = lookupVarLabel(name)
      label.getOrElse {

        val localName = toVarPath(name)

        // need a separate "var a" vs "let a" if want to distinguish definition from update!!
        //        vars.get(locaName).map { existing =>
        //          sys.error(s"scc error: $name is already defined as $existing")
        //        }

        variables.getOrElseUpdate(localName, data)
        localName
      }
    }

    def lookupVarLabel(name: String): Option[String] = {
      val fqn1 = toVarPath(name)
      if (variables.contains(fqn1)) Some(fqn1)
      else {
        if (parent != null)
          parent.lookupVarLabel(name)
        else
          None
      }
    }

    def getVarLabel(name: String): String = {
      val label = lookupVarLabel(name)
      label.getOrElse {
        val str = s"scc error: $name has not been defined yet @ $this"
        sys.error(str)
      }
    }
  }

  def program: Parser[List[String]] = "program" ~ "{" ~> ((comment | functionDef) +) <~ "}" ^^ {
    //  def program: Parser[List[String]] =  (functionWithArgs +) <~ "}" ^^ {
    fns =>
      val Depth0 = 0

      val asm: List[String] = fns.flatMap {
        b => {
          b.expr(Depth0, Name.RootName)
        }
      }

      val varlist = variables.toList.sortBy(_._1).map {
        x =>
          s"${x._1}: BYTES [${x._2.map(_.toInt).mkString(", ")}]"
      }

      val jumpToMain = split(
        s"""
           |PCHITMP = < :$MAIN_LABEL
           |PC = > :$MAIN_LABEL
           |""")

      varlist ++ jumpToMain ++ asm :+ "root_end:" :+ "END"
  }
}

