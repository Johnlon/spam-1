package scc

object MathsWorkingOutScratchpad extends App {

  def timeHi(x: Int, y: Int): Int = {
    ((x * y) >> 8) & 0xff
  }

  def timeLo(x: Int, y: Int): Int = {
    (x * y) & 0xff
  }

  case class HL(hl: Char) {

    val h: Int = hl.toInt >> 8
    val l: Int = hl.toInt & 0xff

    val v = (h << 8) + l

    def *(r: HL) = v * r.v
  }

//  val a = HL(0x00ff)
//  val b = HL(0x0101)

  val a = HL(503)
  val b = HL(2)

  val c = a.v * b.v
  if (c > 65535) println(f"overlfow : ${c}%04X")

  println(f"${a.v}%04X * ${b.v}%04X = ${c.toChar}%04X")
  println(f"dec= ${c.toChar.toInt}")
  println(f"dec= ${(c.toChar& 0xff).toInt}")

  val res = ((timeHi(a.l, b.l) + timeLo(a.l, b.h) + timeLo(a.h, b.l)) << 8) + timeLo(a.l, b.l)

  //  val res = (timeHi(a.l, b.l) << 8) + timeLo(a.l, b.l) +
  //    (timeLo(a.l, b.h) << 8) +
  //    (timeLo(a.h, b.l) << 8)

  //  val res = (timeHi(a.l, b.l) << 8) + timeLo(a.l, b.l) +
  //    (((timeHi(a.l, b.h) << 8) + timeLo(a.l, b.h)) << 8) +
  //    (((timeHi(a.h, b.l) << 8) + timeLo(a.h, b.l)) << 8)

  //  (((timeHi(a.h, b.h) << 8) + timeLo(a.h, b.h)) << 16)

  if (res > 65535) println(f"overlfow : ${c}%04X")
  println(f"${a.v}%04X * ${b.v}%04X = ${res.toChar}%04X")
}
