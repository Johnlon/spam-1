
sealed class Control private(val cond: Int, val setflag: Int) extends E {
  override def toString = s"Condition=${enumName} Setflag=${setflag}"
}

object Control {
  object _C_S extends Control(1, 0)
  object _Z_S extends Control(2, 0)
  object _O_S extends Control(3, 0)
  object _N_S extends Control(4, 0)
  object _GT_S extends Control(5, 0)
  object _LT_S extends Control(6, 0)
  object _EQ_S extends Control(7, 0)
  object _NE_S extends Control(8, 0)
  object _DI_S extends Control(9, 0)
  object _DO_S extends Control(10, 0)
  object _A_S extends Control(0, 0)
  object _S extends Control(0, 0)
  object _A extends Control(0, 1)
  object _C extends Control(1, 1)
  object _Z extends Control(2, 1)
  object _O extends Control(3, 1)
  object _N extends Control(4, 1)
  object _GT extends Control(5, 1)
  object _LT extends Control(6, 1)
  object _EQ extends Control(7, 1)
  object _NE extends Control(8, 1)
  object _DI extends Control(9, 1)
  object _DO extends Control(10, 1)

  def values = Seq(_C_S, _Z_S, _O_S, _N_S, _GT_S, _LT_S, _EQ_S, _NE_S, _DI_S, _DO_S, _A_S, _S, _A, _C, _Z, _O, _N, _GT, _LT, _EQ, _NE, _DI, _DO)

  def valueOf(cond : Int, flagBit : Int):  Control = {
    values.find(o => o.cond == cond && o.setflag == flagBit).head
  }
}
