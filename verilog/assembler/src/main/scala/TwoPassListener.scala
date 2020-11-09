import SPAM1Parser.LabelContext
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import org.apache.logging.log4j.LogManager

import scala.collection.convert.WrapAsScala._
import scala.collection.mutable.Map

final class TwoPassListener extends SPAM1Listener {
  val StringDecode = """"(.*)"""".r
  val log = LogManager.getLogger(this.getClass)
  val symbols = Map[String, Int]("**" -> 0)
  var memoryStart = 0
  var memorySize = 50; //0x8000
  var memory = Array.fill(memorySize)(0xff)

  // allows one to set a value on a node
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

  def stringToInt(s: String) = s.charAt(0) match {
    case '$' => Integer.parseInt(s.tail, 16)
    case '@' => Integer.parseInt(s.tail, 8)
    case '%' => Integer.parseInt(s.tail, 2)
    case '\'' => s.charAt(1).toInt
    case _ => s.toInt
  }

  def pc = symbols.getOrElse("**", 0)

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

//  override def enterLabelInstruction(ctx: SPAM1Parser.LabelInstructionContext): Unit = {
//    val label = ctx.label()
//    log.debug(s"${label.getText} := ${pc}")
//    symbols(label.getText) = pc
//  }

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

//  override def exitLabelInstruction(ctx: SPAM1Parser.LabelInstructionContext): Unit = ???

  override def enterEquInstruction(ctx: SPAM1Parser.EquInstructionContext): Unit = ???

  override def exitEquInstruction(ctx: SPAM1Parser.EquInstructionContext): Unit = ???

  override def enterAssignABInstruction(ctx: SPAM1Parser.AssignABInstructionContext): Unit = ???

  override def exitAssignABInstruction(ctx: SPAM1Parser.AssignABInstructionContext): Unit = ???

  override def enterAssignAInstruction(ctx: SPAM1Parser.AssignAInstructionContext): Unit = ???

  override def exitAssignAInstruction(ctx: SPAM1Parser.AssignAInstructionContext): Unit = ???

  override def enterAssignBInstruction(ctx: SPAM1Parser.AssignBInstructionContext): Unit = ???

  override def exitAssignBInstruction(ctx: SPAM1Parser.AssignBInstructionContext): Unit = ???

  override def enterAdev(ctx: SPAM1Parser.AdevContext): Unit = ???

  override def exitAdev(ctx: SPAM1Parser.AdevContext): Unit = ???

  override def enterBDevDevice(ctx: SPAM1Parser.BDevDeviceContext): Unit = ???

  override def exitBDevDevice(ctx: SPAM1Parser.BDevDeviceContext): Unit = ???

  override def enterBDevRAMDirect(ctx: SPAM1Parser.BDevRAMDirectContext): Unit = ???

  override def exitBDevRAMDirect(ctx: SPAM1Parser.BDevRAMDirectContext): Unit = ???

  override def enterBDevExpr(ctx: SPAM1Parser.BDevExprContext): Unit = ???

  override def exitBDevExpr(ctx: SPAM1Parser.BDevExprContext): Unit = ???

  override def enterBDevOnlyRAMRegister(ctx: SPAM1Parser.BDevOnlyRAMRegisterContext): Unit = ???

  override def exitBDevOnlyRAMRegister(ctx: SPAM1Parser.BDevOnlyRAMRegisterContext): Unit = ???

  override def enterBDevOnlyRAMDirect(ctx: SPAM1Parser.BDevOnlyRAMDirectContext): Unit = ???

  override def exitBDevOnlyRAMDirect(ctx: SPAM1Parser.BDevOnlyRAMDirectContext): Unit = ???

  override def enterBDevOnlyExpr(ctx: SPAM1Parser.BDevOnlyExprContext): Unit = ???

  override def exitBDevOnlyExpr(ctx: SPAM1Parser.BDevOnlyExprContext): Unit = ???

  override def enterBdevDevices(ctx: SPAM1Parser.BdevDevicesContext): Unit = ???

  override def exitBdevDevices(ctx: SPAM1Parser.BdevDevicesContext): Unit = ???

  override def enterTargetDevice(ctx: SPAM1Parser.TargetDeviceContext): Unit = ???

  override def exitTargetDevice(ctx: SPAM1Parser.TargetDeviceContext): Unit = ???

  override def enterTargetRamDirect(ctx: SPAM1Parser.TargetRamDirectContext): Unit = ???

  override def exitTargetRamDirect(ctx: SPAM1Parser.TargetRamDirectContext): Unit = ???

  override def enterPC(ctx: SPAM1Parser.PCContext): Unit = ???

  override def exitPC(ctx: SPAM1Parser.PCContext): Unit = ???

  override def enterHiHyte(ctx: SPAM1Parser.HiHyteContext): Unit = ???

  override def exitHiHyte(ctx: SPAM1Parser.HiHyteContext): Unit = ???


  override def enterNum(ctx: SPAM1Parser.NumContext): Unit = ???

  override def exitNum(ctx: SPAM1Parser.NumContext): Unit = ???

  override def enterParens(ctx: SPAM1Parser.ParensContext): Unit = ???

  override def exitParens(ctx: SPAM1Parser.ParensContext): Unit = ???

  override def enterLoByte(ctx: SPAM1Parser.LoByteContext): Unit = ???

  override def exitLoByte(ctx: SPAM1Parser.LoByteContext): Unit = ???

  override def enterPlus(ctx: SPAM1Parser.PlusContext): Unit = ???

  override def exitPlus(ctx: SPAM1Parser.PlusContext): Unit = ???


  override def enterLabel(ctx: LabelContext): Unit = ???

  override def exitLabel(ctx: LabelContext): Unit = ???

  override def enterRamDirect(ctx: SPAM1Parser.RamDirectContext): Unit = ???

  override def exitRamDirect(ctx: SPAM1Parser.RamDirectContext): Unit = ???

  override def enterNumber(ctx: SPAM1Parser.NumberContext): Unit = ???

  override def exitNumber(ctx: SPAM1Parser.NumberContext): Unit = ???

  override def visitTerminal(node: TerminalNode): Unit = {
  }

  override def visitErrorNode(node: ErrorNode): Unit = {
  }

  override def enterEveryRule(ctx: ParserRuleContext): Unit = {
  }

  override def exitEveryRule(ctx: ParserRuleContext): Unit = {
  }

  override def enterLabelInstruction(ctx: SPAM1Parser.LabelInstructionContext): Unit = ???

  override def exitLabelInstruction(ctx: SPAM1Parser.LabelInstructionContext): Unit = ???

  override def enterVar(ctx: SPAM1Parser.VarContext): Unit = ???

  override def exitVar(ctx: SPAM1Parser.VarContext): Unit = ???

  override def enterName(ctx: SPAM1Parser.NameContext): Unit = ???

  override def exitName(ctx: SPAM1Parser.NameContext): Unit = ???
}