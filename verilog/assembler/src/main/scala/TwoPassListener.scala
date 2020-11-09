import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import org.apache.logging.log4j.LogManager

import scala.collection.mutable.Map
import scala.collection.convert.WrapAsScala._
import AddrMode._

class TwoPassListener extends SPAM1BaseListener {
  val StringDecode = """"(.*)"""".r
  val log = LogManager.getLogger(this.getClass)
  val symbols = Map[String, Int]("**" -> 0)
  var memoryStart = 0
  var memorySize = 50; //0x8000
  var memory = Array.fill(memorySize)(0xff)
  var op: Operation.Value = _
  var addrMode: AddrMode.Value = _
  val exprValues = new ParseTreeProperty[Int] {
    def apply(node: ParseTree) = {
      get(node)
    }

    def update(node: ParseTree, value: Int) = {
      put(node, value)
    }

    def clear() = {
      annotations.clear()
    }

    override def toString = annotations.keySet().map(key => s"${key.getText} => ${annotations.get(key)}").mkString("{", ", ", "}")
  }
  val argValues = new ParseTreeProperty[List[Int]] {
    def apply(node: ParseTree) = {
      get(node)
    }

    def update(node: ParseTree, value: List[Int]) = {
      put(node, value)
    }

    def clear() = {
      annotations.clear()
    }

    override def toString = annotations.keySet().map(key => s"${key.getText} => ${annotations.get(key)}").mkString("{", ", ", "}")
  }

  implicit def stringToInt(s: String) = s.charAt(0) match {
    case '$' => Integer.parseInt(s.tail, 16)
    case '@' => Integer.parseInt(s.tail, 8)
    case '%' => Integer.parseInt(s.tail, 2)
    case '\'' => s.charAt(1).toInt
    case _ => s.toInt
  }

  def pc = symbols.getOrElse("**", 0xffff)

  def store(byte: Int) {
    val address = symbols("**")
    val offset = address - memoryStart
    if (offset < 0 || offset >= memorySize) {
      log.error(s"Address out of bounds: ${address} (expecting in range ${memoryStart} - ${memoryStart + memorySize - 1})")
    } else {
      memory(offset) = byte & 0xff
      symbols("**") += 1
    }
  }

  var nIndent = 0

  def indent: String = {
    var r = ""
    (1 to nIndent).foreach(_ => r += " ")
    r
  }

  override def enterProg(ctx: SPAM1Parser.ProgContext) {}

  override def exitProg(ctx: SPAM1Parser.ProgContext) {}

  override def enterLine(ctx: SPAM1Parser.LineContext): Unit = {
    log.debug(indent + s"LINE : ${ctx.getText} := ${pc}")
  }

  override def exitLine(ctx: SPAM1Parser.LineContext) {
    nIndent -= 1;
    exprValues.clear()
  }
//
//  override def enterEquInstruction(ctx: SPAM1Parser.EquInstructionContext) {
//
//    //    val label = ctx.label()
//    //    val labelPart = if (label == null) "" else s"${label.getText} "
//
//    //    val instruction: SPAM1Parser.InstructionContext = ctx.instruction()
//        log.error(indent + s"equ: ${ctx.expr().getText}")
//      //        val args = equInstruction.expr() match {
//      //          case v: SPAM1Parser.NumContext =>
//      //            log.info(indent + s"CVT: ${v.number().getText} -> ${stringToInt(v.number().getText)}")
//      //            v.number().getText
//      //          case _ =>
//      //            "OTHER"
//      //        }
//      //        log.debug(indent + s"EQU  : ${labelPart}${instruction} ${args}")
//      //      case v: SPAM1Parser.AssignABInstructionContext =>
//      //        log.debug(indent + s"OPER : ${labelPart}${v.adev().getText} ${v.ALUOP().getText} ${getFlags(v.FLAG())}")
//      //      case v: SPAM1Parser.AssignAInstructionContext =>
//      //        log.debug(indent + s"PASSA: ${labelPart}${v.adev().getText} ${getFlags(v.FLAG())}")
//      //      case v: SPAM1Parser.AssicgnBInstructionContext =>
//      //        val bdevOnlyContext = v.bdevOnly()
//      //        log.debug(indent + s"PASSB: ${labelPart}${bdevOnlyContext.getText} ${getFlags(v.FLAG())}")
////      case x =>
////        log.error(indent + s"Unknown command type: ${x.getClass.getName}")
////    }
//    //    if (label != null) {
//    //      log.debug(indent + s"LABEL: ${labelPart} := ${pc}")
//    //      symbols(labelPart) = pc
//    //    }
//    nIndent += 1;
//  }
//
//  override def exitEquInstruction(ctx: SPAM1Parser.EquInstructionContext) {
//    nIndent -= 1;
//  }

  private def getFlags(vr: TerminalNode): String = {
    if (vr == null) "" else "SET"
  }

  private def getText(vr: TerminalNode, deflt: String): String = {
    if (vr == null) deflt else vr.getText()
  }

  //  override def exitLabeledCommand(ctx: SPAM1Parser.LabeledCommandContext) { }
  //  override def enterOpCommand(ctx: SPAM1Parser.OpCommandContext) {
  //    addrMode = Implied
  //    val opName = ctx.OPCODE().getFlags
  //    op = Operation.withName(opName)
  //  }
  //  override def exitOpCommand(ctx: SPAM1Parser.OpCommandContext) {
  //    val opName = ctx.OPCODE().getFlags
  //    val operand = ctx.operand()
  //    val addr = if (operand == null) -1 else exprValues(operand)
  //    val opCode = OpCodes(op, addrMode)
  //    log.debug(s"${opName} -> ${op} addr: ${addr}")
  //    store(opCode.opCode)
  //    if (opCode.bytes > 0) {
  //      store(addr)
  //      if (opCode.bytes > 1) {
  //        store(addr / 256)
  //      }
  //    }
  //  }
  //  override def enterDirectiveCommand(ctx: SPAM1Parser.DirectiveCommandContext) { }
  //  override def exitDirectiveCommand(ctx: SPAM1Parser.DirectiveCommandContext) {
  //    val directive = ctx.DIRECTIVE().getFlags
  //    val args = argValues(ctx.args())
  //    log.debug(s"Directive ${directive} ${args}")
  //    directive match {
  //      case ".OUTPUT" =>
  //        if (args.size != 2) {
  //          log.error(s"OUTPUT expected 2 args, got ${args.size}")
  //        } else {
  //          val newStart = args(0)
  //          val newSize = args(1)-memoryStart+1
  //          if (newStart != memoryStart || newSize != memorySize) {
  //            memoryStart = newStart
  //            memorySize = newSize
  //            memory = Array.fill(memorySize)(0xff)
  //          }
  //        }
  //      case ".BYTE" =>
  //        args.foreach { byte => store(byte) }
  //      case ".DBYTE" =>
  //        // Store in high byte, low byte order
  //        args.foreach { word =>
  //          store(word / 256)
  //          store(word % 256)
  //        }
  //      case ".WORD" =>
  //        // Store in low byte, high byte order
  //        args.foreach { word =>
  //          store(word % 256)
  //          store(word / 256)
  //        }
  //      case _ =>
  //        log.error(s"Unknown directive: ${directive}")
  //    }
  //  }
  //  override def enterOperand(ctx: SPAM1Parser.OperandContext) { }
  //  override def exitOperand(ctx: SPAM1Parser.OperandContext) {
  //    exprValues(ctx) = exprValues(ctx.getChild(0))
  //  }
  //  override def enterImmediateAddr(ctx: SPAM1Parser.ImmediateAddrContext) { }
  //  override def exitImmediateAddr(ctx: SPAM1Parser.ImmediateAddrContext) {
  //    addrMode = Immediate
  //    exprValues(ctx) = exprValues(ctx.expr())
  //  }
  //  override def enterDirectAddr(ctx: SPAM1Parser.DirectAddrContext) { }
  //  override def exitDirectAddr(ctx: SPAM1Parser.DirectAddrContext) {
  //    val target = exprValues(ctx.expr())
  //    exprValues(ctx) = target
  //    addrMode = if (Operation.relativeAddrOps.contains(op)) {
  //      val currentAddr = pc
  //      val relAddr = target - (currentAddr + 2)
  //      exprValues(ctx) = if (relAddr >= 0) relAddr else relAddr+256
  //      Relative
  //    } else if (target >= 256 || !OpCodes.has(op, ZeroPage)) {
  //      Absolute
  //    } else {
  //      ZeroPage
  //    }
  //  }
  //  override def enterIndexedDirectAddrX(ctx: SPAM1Parser.IndexedDirectAddrXContext) { }
  //  override def exitIndexedDirectAddrX(ctx: SPAM1Parser.IndexedDirectAddrXContext) {
  //    val value = exprValues(ctx.expr())
  //    exprValues(ctx) = value
  //    addrMode = if (value >= 256) AbsoluteIndexedX else ZeroPageIndexedX
  //  }
  //  override def enterIndexedDirectAddrY(ctx: SPAM1Parser.IndexedDirectAddrYContext) { }
  //  override def exitIndexedDirectAddrY(ctx: SPAM1Parser.IndexedDirectAddrYContext) {
  //    val value = exprValues(ctx.expr())
  //    exprValues(ctx) = value
  //    addrMode = if (value >= 256) AbsoluteIndexedY else ZeroPageIndexedY
  //  }
  //  override def enterIndirectAddr(ctx: SPAM1Parser.IndirectAddrContext) { }
  //  override def exitIndirectAddr(ctx: SPAM1Parser.IndirectAddrContext) {
  //    val value = exprValues(ctx.expr())
  //    exprValues(ctx) = value
  //    addrMode = if (value >= 256) AbsoluteIndirect else ZeroPageIndirect
  //  }
  //  override def enterPreIndexedIndirectAddr(ctx: SPAM1Parser.PreIndexedIndirectAddrContext) { }
  //  override def exitPreIndexedIndirectAddr(ctx: SPAM1Parser.PreIndexedIndirectAddrContext) {
  //    val value = exprValues(ctx.expr())
  //    exprValues(ctx) = value
  //    addrMode = ZeroPagePreIndexedIndirect
  //  }
  //  override def enterPostIndexedIndirectAddr(ctx: SPAM1Parser.PostIndexedIndirectAddrContext) { }
  //  override def exitPostIndexedIndirectAddr(ctx: SPAM1Parser.PostIndexedIndirectAddrContext) {
  //    val value = exprValues(ctx.expr())
  //    exprValues(ctx) = value
  //    addrMode = ZeroPagePostIndexedIndirect
  //  }
  //  override def enterStringArg(ctx: SPAM1Parser.StringArgContext) { }
  //  override def exitStringArg(ctx: SPAM1Parser.StringArgContext) {
  //    ctx.STRING().getFlags match {
  //      case StringDecode(rawString) =>
  //        val values = rawString.getBytes.map(_.toInt).toList
  //        log.debug(s"${values}")
  //        argValues(ctx) = values
  //      case other =>
  //        log.error(s"Unexpected string format: ${other}")
  //    }
  //  }
  //  override def enterListArg(ctx: SPAM1Parser.ListArgContext) { }
  //  override def exitListArg(ctx: SPAM1Parser.ListArgContext) {
  //    val values = ctx.expr().map(exprValues(_)).toList
  //    log.debug(s"${values}")
  //    argValues(ctx) = values
  //  }
  //  override def enterVarAssign(ctx: SPAM1Parser.VarAssignContext) {
  //    log.debug(s"${ctx.label().getFlags} = ${ctx.expr().getFlags}")
  //  }
  //  override def exitVarAssign(ctx: SPAM1Parser.VarAssignContext) {
  //    log.debug(s"${ctx.label().getFlags} := ${exprValues(ctx.expr())}")
  //    symbols(ctx.label().getFlags) = exprValues(ctx.expr())
  //  }
  //  override def enterPcAssign(ctx: SPAM1Parser.PcAssignContext) {
  //    log.debug(s"** = ${ctx.expr().getFlags}")
  //  }
  //  override def exitPcAssign(ctx: SPAM1Parser.PcAssignContext) {
  //    log.debug(s"** := ${exprValues(ctx.expr())}")
  //    symbols("**") = exprValues(ctx.expr())
  //  }
  //  override def enterDiv(ctx: SPAM1Parser.DivContext) { }
  //  override def exitDiv(ctx: SPAM1Parser.DivContext) {
  //    log.debug(s"${exprValues(ctx.expr(0))} / ${exprValues(ctx.expr(1))}")
  //    exprValues(ctx) = exprValues(ctx.expr(0)) / exprValues(ctx.expr(1))
  //  }
  //  override def enterAdd(ctx: SPAM1Parser.AddContext) { }
  //  override def exitAdd(ctx: SPAM1Parser.AddContext) {
  //    log.debug(s"${exprValues(ctx.expr(0))} + ${exprValues(ctx.expr(1))}")
  //    exprValues(ctx) = exprValues(ctx.expr(0)) + exprValues(ctx.expr(1))
  //  }
  //  override def enterSub(ctx: SPAM1Parser.SubContext) { }
  //  override def exitSub(ctx: SPAM1Parser.SubContext) {
  //    log.debug(s"${exprValues(ctx.expr(0))} - ${exprValues(ctx.expr(1))}")
  //    exprValues(ctx) = exprValues(ctx.expr(0)) - exprValues(ctx.expr(1))
  //  }
  //  override def enterPc(ctx: SPAM1Parser.PcContext) { }
  //  override def exitPc(ctx: SPAM1Parser.PcContext) {
  //    log.debug(s"** -> ${pc}")
  //    exprValues(ctx) = pc
  //  }
  //  override def enterMult(ctx: SPAM1Parser.MultContext) { }
  //  override def exitMult(ctx: SPAM1Parser.MultContext) {
  //    log.debug(s"${exprValues(ctx.expr(0))} * ${exprValues(ctx.expr(1))}")
  //    exprValues(ctx) = exprValues(ctx.expr(0)) * exprValues(ctx.expr(1))
  //  }
  //  override def enterHighByte(ctx: SPAM1Parser.HighByteContext) { }
  //  override def exitHighByte(ctx: SPAM1Parser.HighByteContext) {
  //    log.debug(s">${exprValues(ctx.expr())}")
  //    exprValues(ctx) = exprValues(ctx.expr()) / 256
  //  }
  //  override def enterVar(ctx: SPAM1Parser.VarContext) { }
  //  override def exitVar(ctx: SPAM1Parser.VarContext) {
  //    log.debug(s"${ctx.label().getFlags} -> ${symbols.getOrElse(ctx.label().getFlags, 0xffff)}")
  //    exprValues(ctx) = symbols.getOrElse(ctx.label().getFlags, 0xffff)
  //  }
  //  override def enterChar(ctx: SPAM1Parser.CharContext) { }
  //  override def exitChar(ctx: SPAM1Parser.CharContext) {
  //    log.debug(s"Char ${ctx.CHAR().getFlags} = ${ctx.CHAR().getFlags.charAt(1).toInt}")
  //    exprValues(ctx) = ctx.CHAR().getFlags
  //  }
  //  override def enterParens(ctx: SPAM1Parser.ParensContext) { }
  //  override def exitParens(ctx: SPAM1Parser.ParensContext) {
  //    log.debug(s"[${exprValues(ctx.expr())}]")
  //    exprValues(ctx) = exprValues(ctx.expr())
  //  }
  //  override def enterNum(ctx: SPAM1Parser.NumContext) { }
  //  override def exitNum(ctx: SPAM1Parser.NumContext) {
  //    log.debug(s"Num ${exprValues(ctx.getChild(0))}")
  //    exprValues(ctx) = exprValues(ctx.getChild(0))
  //  }
  //  override def enterRem(ctx: SPAM1Parser.RemContext) { }
  //  override def exitRem(ctx: SPAM1Parser.RemContext) {
  //    log.debug(s"${exprValues(ctx.expr(0))} % ${exprValues(ctx.expr(1))}")
  //    exprValues(ctx) = exprValues(ctx.expr(0)) % exprValues(ctx.expr(1))
  //  }
  //  override def enterLowByte(ctx: SPAM1Parser.LowByteContext) { }
  //  override def exitLowByte(ctx: SPAM1Parser.LowByteContext) {
  //    log.debug(s"<${exprValues(ctx.expr())}")
  //    exprValues(ctx) = exprValues(ctx.expr()) % 256
  //  }
  //  override def enterNumber(ctx: SPAM1Parser.NumberContext) { }
  //  override def exitNumber(ctx: SPAM1Parser.NumberContext) {
  //    log.debug(s"number ${ctx.getChild(0).getFlags}")
  //    exprValues(ctx) = ctx.getChild(0).getFlags
  //  }
  override def enterEveryRule(ctx: ParserRuleContext) {}

  override def exitEveryRule(ctx: ParserRuleContext) {}

  override def visitTerminal(node: TerminalNode) {}

  override def visitErrorNode(node: ErrorNode) {}
}