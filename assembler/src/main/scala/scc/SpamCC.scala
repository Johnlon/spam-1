package scc

import java.io.{File, PrintWriter}

import asm.{E, EnumParserOps}
import scc.SpamCC.{posToAddress, split}

import scala.collection.mutable
import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers


object SpamCC {
  val ZERO = 0.toByte

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

  def posToAddress(pos: Int): List[Byte] = {
    List((pos >> 8) toByte, pos.toByte)
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

}

class SpamCC extends Naming with SubBlocks with ConstExpr with Condition with EnumParserOps with JavaTokenParsers {
  val SPACE = " "
  val variables = mutable.ListBuffer.empty[Variable]
  private val MAIN_LABEL = "ROOT________main_start"

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

  def statement: Parser[Block] = {
    comment |
      statementVarEqVarOpVar | statementVarEqVar | statementVarEqVarOpConst | statementVarEqConstOpVar | statementVarEqConst | statementVarOp | statementRef |
      statementLetEqConst | statementLetVarEqVar |
      statementPutcharVarOptimisation | statementPutcharConstOptimisation | stmtPutcharGeneral |
      stmtPutsName |
      whileTrue | whileCond | ifCond | breakOut | functionCall |
      statementVarString ^^ {
        s =>
          s
      }
  }

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  // optimisation of "var VARIABLE=CONST"
  def statementVarEqConst: Parser[Block] = "var" ~> name ~ "=" ~ constExpr <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ konst =>
      new Block("statementVarEqConst", s"$targetVar=$konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val label = parent.assignVarLabel(targetVar, IsVar).fqn
          List(
            s"[:$label] = $konst",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=CONST"
  def statementLetEqConst: Parser[Block] = "let" ~> name ~ "=" ~ constExpr <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ konst =>
      new Block("statementLetEqConst", s"$targetVar=$konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val variable = parent.getVarLabel(targetVar)
          val fqn = variable.fqn

          variable.typ match {
            case IsVar | IsData =>
              List(
                s"; let var $targetVar = $konst",
                s"[:$fqn] = $konst",
              )
            case IsRef =>
              List(
                s"; let ref $name = $konst",
                s"[:$fqn] = <$konst",
                s"[:$fqn+1] = >$konst "
              )
          }
        }
      }
  }

  // optimisation of "var VARIABLE=CONST op VARIABLE"
  def statementVarEqConstOpVar: Parser[Block] = "var" ~> name ~ "=" ~ constExpr ~ aluOp ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ konst ~ oper ~ srcVar =>
      new Block("statementEqConstOpVar", s"$targetVar=$konst $oper $srcVar") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srcVar).fqn
          val tLabel = parent.assignVarLabel(targetVar, IsVar).fqn
          List(
            s"REGA = $konst",
            s"REGA = REGA $oper [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=VARIABLE op CONST"
  def statementVarEqVarOpConst: Parser[Block] = "var" ~> name ~ "=" ~ name ~ aluOp ~ constExpr <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srcVar ~ op ~ konst =>
      new Block("statementEqVarOpConst", s"$targetVar=$srcVar $op $konst") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srcVar).fqn
          val tLabel = parent.assignVarLabel(targetVar, IsVar).fqn
          List(
            s"REGA = [:$sLabel]",
            s"REGA = REGA $op $konst",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=VARIABLE"
  def statementVarEqVar: Parser[Block] = "var" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srcVar =>
      new Block("statementEqVar", s"$targetVar=$srcVar") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val sLabel = parent.getVarLabel(srcVar).fqn
          val tLabel = parent.assignVarLabel(targetVar, IsVar).fqn
          List(
            s"REGA = [:$sLabel]",
            s"[:$tLabel] = REGA",
          )
        }
      }
  }

  // optimisation of "var VARIABLE=VARIABLE op VARIABLE"
  def statementVarEqVarOpVar: Parser[Block] = "var" ~> name ~ "=" ~ name ~ aluOp ~ name <~ SEMICOLON ^^ {
    case targetVar ~ _ ~ srcVar1 ~ op ~ srcVar2 =>
      new Block("statementEqVarOpVar", s"$targetVar=$srcVar1 $op $srcVar2") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val s1Label = parent.getVarLabel(srcVar1).fqn
          val s2Label = parent.getVarLabel(srcVar2).fqn
          val tLabel = parent.assignVarLabel(targetVar, IsVar).fqn
          List(
            s"REGA = [:$s1Label]",
            s"REGB = [:$s2Label]",
            s"[:$tLabel] = REGA $op REGB",
          )
        }
      }
  }

  // general purpose
  def statementVarOp: Parser[Block] = "var" ~> name ~ "=" ~ compoundBlkExpr <~ SEMICOLON ^^ {
    case target ~ _ ~ v =>
      new Block("statementVarOp", s"$target = $v") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val stmts: List[String] = v.expr(depth + 1, parent)

          val labelTarget = parent.assignVarLabel(target, IsVar).fqn

          val assign = List(
            s"[:$labelTarget] = REGA",
          )
          stmts ++ assign
        }
      }
  }

  def statementLetVarEqVar: Parser[Block] = "let" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
    case targetVarName ~ _ ~ srcVarName =>
      new Block("statementLetVarEqVar", s"$targetVarName = $srcVarName") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val srcVar = parent.getVarLabel(srcVarName)
          val targVar = parent.getVarLabel(targetVarName)

          targVar.typ match {
            case IsVar | IsData =>
              List(
                s"REGA = [:${srcVar.fqn}]",
                s"[:${targVar.fqn}] = REGA",
              )
            case IsRef =>
              List(
                s"REGA = <:${srcVar.fqn} ",
                s"[:${targVar.fqn}] = REGA",
                s"REGA = >:${srcVar.fqn} ",
                s"[:${targVar.fqn} + 1] = REGA"
              )
          }
        }
      }
  }

  /*
  STRING:     STR     "ABC\n\0\u0000"
  BYTE_ARR:   BYTES   [ 'A', 65 ,$41, %10101010 ] ; parse as hex bytes and then treat as per STR
  */
  def statementVarString: Parser[Block] = "var" ~> name ~ "=" ~ quotedString <~ SEMICOLON ^^ {
    case target ~ _ ~ str =>
      new Block("statementVarEqString", s"$target = $str") {
        override def gen(depth: Int, parent: Name): List[String] = {
          // nothing to do but record the data with current scope - data will be laid out later
          parent.assignVarLabel(target, IsData, str.getBytes("UTF-8").toList).fqn
          List(
            s"""; var $target = "$str""""
          )
        }
      }
  }

  def statementRef: Parser[Block] = "ref" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
    case refName ~ _ ~ target =>
      new Block("statementVRef", s"$refName = $target") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val targetLabelPos = parent.getVarLabel(target).pos
          val addr = posToAddress(targetLabelPos)
          val refLabel = parent.assignVarLabel(refName, IsRef, addr).fqn

          List(
            s"""; ref $refName = "$target"     ($refLabel = ${
              addr.mkString("[Hi:", ",LO:", "]")
            })"""
          )
        }
      }
  }

  def stmtPutsName: Parser[Block] = "puts" ~ "(" ~> name <~ ")" ^^ {
    varName =>
      new Block("stmtPutsName", s"$varName", nestedName = "puts") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelStartLoop = parent.fqnLabelPathUnique("startloop")
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          val labelEndLoop = parent.fqnLabelPathUnique("endloop")

          val variable = parent.getVarLabel(varName)

          val marSetup = variable.typ match {
            case IsVar | IsData =>
              val varLabel = variable.fqn
              List(
                s"MARLO = >:$varLabel",
                s"MARHI = <:$varLabel"
              )
            case IsRef => variable.fqn
              val varLabel = variable.fqn
              List(
                s"MARHI = [:$varLabel]",
                s"MARLO = [:$varLabel+1]"
              )
          }

          marSetup ++
            split(
              s"""
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
                 |; done break from loop
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
      new Block("statementPutcharVarOptimisation", s"$varName", nestedName = s"putcharVar_${
        varName
      }_") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelWait = parent.fqnLabelPathUnique("wait")
          val labelTransmit = parent.fqnLabelPathUnique("transmit")
          val varLocn = parent.getVarLabel(varName).fqn
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
      new Block("statementPutcharConstOptimisation", s"$varName", nestedName = s"putcharI_${
        varName
      }_") {
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

  def functionDef: Parser[Block] = "fun " ~> name ~ "(" ~ repsep((name ~ ("out" ?)), ",") ~ (")" ~ "{") ~ statements <~ "}" ^^ {
    case fnName ~ _ ~ args ~ _ ~ content =>

      // !!!NO SPACE IN THE NAME AS USED FOR LABELS
      val fn = new Block(s"function", s"_$fnName(${
        args.mkString(",")
      })", nestedName = s"function_$fnName") with IsFunction {

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

      new Block("whileTrue", s"${
        SPACE
      }true", nestedName = s"whileTrue${
        Name.nextInt
      }") {
        override def gen(depth: Int, parent: Name): List[String] = {
          val labelBody = parent.toFqLabelPath("BODY")
          val labelAfter = parent.toFqLabelPath("AFTER")

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

      new Block(s"whileCond", s"$SPACE($cond) with ${
        content.size
      } inner blocks", nestedName = s"whileCond${
        Name.nextInt
      }") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val labelCheck = parent.toFqLabelPath("CHECK")
          val labelBody = parent.toFqLabelPath("BODY")
          val labelBot = parent.toFqLabelPath("AFTER")

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

  def ifCond: Parser[Block] = "if" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
    case cond ~ _ ~ _ ~ content =>

      new Block(s"ifCond", s"$SPACE($cond) with ${
        content.size
      } inner blocks", nestedName = s"ifCond${
        Name.nextInt
      }") {
        override def gen(depth: Int, parent: Name): List[String] = {

          val labelCheck = parent.toFqLabelPath("CHECK")
          val labelBody = parent.toFqLabelPath("BODY")
          val labelBot = parent.toFqLabelPath("AFTER")

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

  def functionCall: Parser[Block] = name ~ "(" ~ repsep(compoundBlkExpr, ",") ~ ")" ^^ {
    case fnName ~ _ ~ argExpr ~ _ =>
      new Block(s"functionCall",
        s"""$fnName(${
          argExpr.mkString(", ")
        })""") {

        override def gen(depth: Int, parent: Name): List[String] = {
          val fns = parent.lookupFunction(fnName)
          val (functionScope: Name, fn: Block with IsFunction) = fns.getOrElse(sys.error(s"no such function '$fnName''"))

          val FunctionDef(startLabel, returnHiLabel, returnLoLabel, argsLabelsAndDir) = fn.scopedArgLabels(functionScope)

          val argNamesAndDir: List[FunctionArgName] = fn.functionArgs

          if (argExpr.length != argNamesAndDir.size) {
            val argsNames = argNamesAndDir.map(_.name)
            sys.error(
              s"""call to function "$fnName" has wrong number of arguments for ; expected $fnName(${
                argsNames.mkString(",")
              }) but got $fnName(${
                argExpr.mkString(",")
              })""")
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
                        }.fqn

                        // recover ourput value from the function and assign back to the local variable
                        List(
                          s"REGA = [:$argLabel]",
                          s"[:$localVarLabel] = REGA"
                        )
                      case _ =>
                        sys.error(
                          s"""output parameter '${
                            nameAndOutput.name
                          }' in call to function "$fnName" is not a pure variable reference, but is '$v'""")
                    }
                  case somethingElse =>
                    sys.error(
                      s"""output parameter '${
                        nameAndOutput.name
                      }' in call to function "$fnName" is not a pure variable reference, but is '$somethingElse'""")
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

  def comment: Parser[Block] = "//.*".r ^^ {
    c =>
      val withoutLeading = c.replace("//", "")
      new Block("comment", s"${
        SPACE
      }$withoutLeading", logEntryExit = false) {
        override def gen(depth: Int, parent: Name): List[String] = {
          List(s"; $withoutLeading")
        }
      }
  }

  def program: Parser[List[String]] = "program" ~ "{" ~> ((comment | functionDef) +) <~ "}" ^^ {
    fns =>
      val Depth0 = 0

      val asm: List[String] = fns.flatMap {
        b => {
          b.expr(Depth0, Name.RootName)
        }
      }

      val varlist = variables.flatMap {
        case Variable(name, fqn, pos, bytes, typ) =>
          Seq(
            s"; $typ : $name : $fqn",
            s"$fqn: EQU   $pos",
            s"$fqn: BYTES [${
              bytes.map(_.toInt).mkString(", ")
            }]"
          )
      }.toList

      val jumpToMain = split(
        s"""
           |PCHITMP = < :$MAIN_LABEL
           |PC = > :$MAIN_LABEL
           |""")

      varlist ++ jumpToMain ++ asm :+ "root_end:" :+ "END"
  }

  //  trait IsVariable

  //  trait IsConst

  trait IsFunction {
    self: Block =>

    def functionName: String

    def functionArgs: List[FunctionArgName]

    def scopedArgLabels(scope: Name): FunctionDef = {
      val returnHiLabel = scope.assignVarLabel("RETURN_HI", IsVar).fqn
      val returnLoLabel = scope.assignVarLabel("RETURN_LO", IsVar).fqn
      // These locations where we write the input parameters into the function.
      // Also, read from these locations to fetch "out" values.
      val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.map {
        argName =>
          FunctionArgNameAndLabel(scope.assignVarLabel(argName.name, IsVar).fqn, FunctionArgName(argName.name, argName.output))
      }
      val fnStart = scope.toFqLabelPath("START")

      FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
    }
  }


  abstract class Block(val typ: String, val context: String, nestedName: String = "", logEntryExit: Boolean = true) {

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

        if (logEntryExit) enter +: value :+ exit
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

    private def prefixComment(depth: Int) = s"; ($depth) ${" " * depth}"

    private def prefixOp(depth: Int) = prefixComment(depth).replaceAll(".", " ")

    override def toString = s"Block($typ $context)"

    protected[this] def gen(depth: Int, parent: Name): List[String]

  }

}
