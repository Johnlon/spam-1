package scc

import asm.E

sealed trait VarType extends E

object IsData extends VarType

object IsVar8 extends VarType
object IsVar16 extends VarType

object IsRef extends VarType

case class Variable(name: String, fqn: String, address: Int, bytes: List[Byte], typ: VarType)

case class FunctionArg(argName: String, isOutput: Boolean) {
  def dump(depth: Int): List[(Int, String)] = {
    val o = argName + " " + (if (isOutput) "out" else "")
    List((depth + 1, o))
  }
}

case class FunctionArgNameAndLabel(labelName: String, argName: FunctionArg)

case class FunctionDef(startLabel: String, returnHiLabel: String, returnLoLabel: String, args: List[FunctionArgNameAndLabel])

case class Address(hi: Byte, lo: Byte) {
  def bytes: Array[Byte] = {
    List(hi, lo).toArray
  }
}
