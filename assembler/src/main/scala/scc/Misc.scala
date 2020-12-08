package scc

import asm.E

sealed trait VarType extends E

object IsData extends VarType

object IsVar extends VarType

object IsRef extends VarType

case class Variable(name: String, fqn: String, pos: Int, bytes: List[Byte], typ: VarType)

case class FunctionArgName(name: String, output: Boolean)

case class FunctionArgNameAndLabel(labelName: String, argName: FunctionArgName)

case class FunctionDef(startLabel: String, returnHiLabel: String, returnLoLabel: String, args: List[FunctionArgNameAndLabel])

case class Address(hi: Byte, lo: Byte) {
  def bytes: Array[Byte] = {
    List(hi, lo).toArray
  }
}
