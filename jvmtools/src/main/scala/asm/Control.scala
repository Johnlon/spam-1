package asm

import asm.ConditionMode.Standard
import asm.FlagControl.{NoSetFlag, SetFlag}

sealed class ConditionMode(val bit: String)

object ConditionMode {
  case object Standard extends ConditionMode("0")

  case object Invert extends ConditionMode("1")
}

case class Condition private(mode: ConditionMode, cond: Control) extends E {
  override def toString = s"Control=${cond} Mode=${mode}"
}

object Condition {
  val Default = Condition(Standard, Control._A)
}

sealed class FlagControl(val bit: Int)

object FlagControl {
  def fromBit(bit: Int): FlagControl = {
    if (SetFlag.bit == bit) SetFlag
    else if (NoSetFlag.bit == bit) SetFlag
    else sys.error("invalid flag control bit" + bit)
  }

  case object SetFlag extends FlagControl(1)

  case object NoSetFlag extends FlagControl(0)

}

sealed class Control private(val cond: Int, val setflag: FlagControl) extends E {
  override def toString = s"Condition=${enumName} ${setflag.getClass.getSimpleName}"
}

object Control {
  val (a, c, z, o, n, eq, ne, gt, lt, dI, dO) = (0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  object _C_S extends Control(c, SetFlag)

  object _Z_S extends Control(z, SetFlag)

  object _O_S extends Control(o, SetFlag)

  object _N_S extends Control(n, SetFlag)

  object _EQ_S extends Control(eq, SetFlag)

  object _NE_S extends Control(ne, SetFlag)

  object _GT_S extends Control(gt, SetFlag)

  object _LT_S extends Control(lt, SetFlag)

  object _DI_S extends Control(dI, SetFlag)

  object _DO_S extends Control(dO, SetFlag)

  object _A_S extends Control(a, SetFlag)

  object _S extends Control(a, SetFlag)

  object _A extends Control(a, NoSetFlag)

  object _C extends Control(c, NoSetFlag)

  object _Z extends Control(z, NoSetFlag)

  object _O extends Control(o, NoSetFlag)

  object _N extends Control(n, NoSetFlag)

  object _EQ extends Control(eq, NoSetFlag)

  object _NE extends Control(ne, NoSetFlag)

  object _GT extends Control(gt, NoSetFlag)

  object _LT extends Control(lt, NoSetFlag)

  object _DI extends Control(dI, NoSetFlag)

  object _DO extends Control(dO, NoSetFlag)

  def values = Seq(_C_S, _Z_S, _O_S, _N_S, _EQ_S, _NE_S, _GT_S, _LT_S, _DI_S, _DO_S, _A_S, _S, _A, _C, _Z, _O, _N, _EQ, _NE, _GT, _LT, _DI, _DO)

  def valueOf(cond: Int, flagBit: FlagControl): Control = {
    values.find(o => o.cond == cond && o.setflag == flagBit).head
  }
}
