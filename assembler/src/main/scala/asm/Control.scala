package asm

sealed class Control private(val cond: Int, val setflag: Int) extends E {
  override def toString = s"Condition=${enumName} Setflag=${setflag}"
}

object Control {
  val (a,c,z,o,n,gt,lt,eq,ne,dI,dO) = (0,1,2,3,4,5,6,7,8,9,10)

  object _C_S extends Control(c, 0)
  object _Z_S extends Control(z, 0)
  object _O_S extends Control(o, 0)
  object _N_S extends Control(n, 0)
  object _GT_S extends Control(gt, 0)
  object _LT_S extends Control(lt, 0)
  object _EQ_S extends Control(eq, 0)
  object _NE_S extends Control(ne, 0)
  object _DI_S extends Control(dI, 0)
  object _DO_S extends Control(dO, 0)
  object _A_S extends Control(a, 0)
  object _S extends Control(a, 0)
  object _A extends Control(a, 1)
  object _C extends Control(c, 1)
  object _Z extends Control(z, 1)
  object _O extends Control(o, 1)
  object _N extends Control(n, 1)
  object _GT extends Control(gt, 1)
  object _LT extends Control(lt, 1)
  object _EQ extends Control(eq, 1)
  object _NE extends Control(ne, 1)
  object _DI extends Control(dI, 1)
  object _DO extends Control(dO, 1)

  def values = Seq(_C_S, _Z_S, _O_S, _N_S, _GT_S, _LT_S, _EQ_S, _NE_S, _DI_S, _DO_S, _A_S, _S, _A, _C, _Z, _O, _N, _GT, _LT, _EQ, _NE, _DI, _DO)

  def valueOf(cond : Int, flagBit : Int):  Control = {
    values.find(o => o.cond == cond && o.setflag == flagBit).head
  }
}
