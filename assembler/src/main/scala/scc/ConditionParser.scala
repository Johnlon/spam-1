package scc

import asm.AluOp

trait ConditionParser {
  self: SpamCC =>

  def comparison: Parser[String] =  ">=" | "<=" | ">" | "<" | "==" | "!="

  // return the block of code and the name of the flag to add to the jump operation
  def condition: Parser[(String, Block)] = name ~ comparison ~ constExpression ^^ {
    case varName ~ compOp ~ konst =>
      val b = Condition(varName, compOp, konst)

      val cpuFlag = compOp match {
        case ">" => "_GT"
        case "<" => "_LT"
        case ">=" => "_Z"
        case "<=" => "_Z"
        case "==" => "_EQ"
        case "!=" => "_NE"
      }
      (cpuFlag, b)
  }

}

case class Condition(varName: String, compOp: String, konst: Int) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val label = parent.getVarLabel(varName).fqn

    //      val l: Char = (konst & 0xff).toChar
    //      if (l != konst)
    //        println(l)

    // TODO: work out how to designate use of Signed of Unsigned comparison!!
    // record vars as signed or unsigned?    unsigned byte a = 1    // ditch var
    // do it with the op?     a <:s
    // what about signed vs unsigned const?     a < -1:s   or is -1 automatically signed and 255 is automatically unsigned?
    // and if using octal or hex then are they signed or unsigned?
    // maybe restrict signs to the ops??

    val UseSigned = true
    val op = (if (UseSigned)
      AluOp.A_MINUS_B_SIGNEDMAG
    else
      AluOp.A_MINUS_B).preferredName

    compOp match {
      case ">" | "<" | "==" | "!=" =>
        List(
          s"; condition :  $varName $compOp $konst",
          s"REGA = [:$label]",
          s"REGA = REGA $op $konst _S" // this op is unimportant
        )
      case ">=" =>
        List(
          s"; condition :  $varName $compOp $konst",
          s"REGA = [:$label]",
          s"REGA = REGA $op $konst _S", // this op is unimportant as we are dong magnitude
          s"REGA = 1",
          s"REGA = 0 _GT", // set REGA=0 if was GT
          s"REGA = 0 _EQ", // set REGA=0 if was EQ
          s"REGA = REGA _S", // set REGA=0 if was EQ
        )
      case "<=" =>
        List(
          s"; condition :  $varName $compOp $konst",
          s"REGA = [:$label]",
          s"REGA = REGA $op $konst _S", // this op is unimportant as we are dong magnitude
          s"REGA = 1",
          s"REGA = 0 _LT", // set REGA=0 if was LT
          s"REGA = 0 _EQ", // set REGA=0 if was EQ
          s"REGA = REGA _S", // set REGA=0 if was EQ
        )
    }
  }
}
