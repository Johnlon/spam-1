import java.lang.Integer.parseInt

package object chip8 {
  type PixelType = Char

  val STATUS_REGISTER_ID: Int = 0xF

  val SCREEN_HEIGHT = 32
  val SCREEN_WIDTH = 64

  val MEM_SIZE = 4096

  val OLD_INTERPRETER_BOT = 0x000
  val OLD_INTERPRETER_TOP = 0x1FF

  val INTERNAL_BOT = 0xEA0
  val INTERNAL_TOP = 0xEFF

  val SCREEN_BUF_BOT = 0xF00
  val SCREEN_BUF_TOP = 0xFFF

  val INITIAL_PC = 0x200

  val emptyRegisters: List[U8] = List.fill(16)(U8(0))
  val emptyMemory: List[U8] = List.fill(MEM_SIZE)(U8(0))

  assert((SCREEN_HEIGHT*SCREEN_WIDTH)/8 == (1 + SCREEN_BUF_TOP - SCREEN_BUF_BOT))

  case class U8(ubyte: Char) {
    override def toString() =  s"${ubyte.toHexString}(${ubyte.toInt & 0xff})"

    def toInt = ubyte.toInt

    def isZero: Boolean = ubyte == 0
    def isNotZero: Boolean = ubyte != 0
    def asOneZero: U8 = if (ubyte != 0) U8(1) else U8(0)

    def >(yVal: U8): Boolean = (ubyte > yVal.ubyte)
//    def >(yVal: Int): Boolean = (ubyte > U8.valueOf(yVal))

    def <(yVal: U8): Boolean = (ubyte < yVal.ubyte)
    def ==(other: Int): Boolean = (ubyte == other)

    def |(yVal: U8): chip8.U8 = U8.valueOf(ubyte | yVal.ubyte)
    def &(yVal: U8): chip8.U8 = U8.valueOf(ubyte & yVal.ubyte)
    def ^(yVal: U8): chip8.U8 = U8.valueOf(ubyte ^ yVal.ubyte)

    def |(yVal: Int): chip8.U8 = U8.valueOf(ubyte | yVal)
    def &(yVal: Int): chip8.U8 = U8.valueOf(ubyte & yVal)
    def >>(yVal: Int): chip8.U8 = U8.valueOf(ubyte >> yVal )
    def <<(yVal: Int): chip8.U8 = U8.valueOf((ubyte << yVal) & 0xff)

    def +(x: U8): U8 = {
      val u = (ubyte + x.ubyte) & 0xff
      U8(u.toChar)
    }
    def -(x: U8): U8 = {
      val u = (ubyte - x.ubyte) & 0xff
      U8(u.toChar)
    }
    def +(x: Int): U8 = {
      this + U8.valueOf(x)
    }
    def -(x: Int): U8 = {
      this - U8.valueOf(x)
    }
  }

  object U8 {
    var MAX_INT = 255


    def valueOf(b: Boolean) : U8 = if (b) U8(1) else U8(0)

    def valueOf(x: Int): U8 = {
      if (x < 0 || x > 255)
        sys.error("out of range " + x)
      U8((x & 0xff).toChar)
    }

    def valueOf(s: String, radix: Int): U8 = {
      U8.valueOf(parseInt(s, radix))
    }
  }

  implicit class StringOps(value: String) {
    val charArray: Array[Char] = value.toCharArray

    def b32: U8 = U8.valueOf(value.substring(0, 2), 16)

    def b10: U8 = U8.valueOf(value.substring(2, 4), 16)

    def b210: Int = Integer.valueOf(value.substring(1, 4), 16)

    def b3210: Int = Integer.valueOf(value, 16)

    def hexToByte: U8 = U8.valueOf(value, 16)
    def hexToInt: Int = Integer.valueOf(value, 16)

//    def hexToInt: Int = Integer.valueOf(value, 16)

    def set(position: Int, t: Char): String = {
      val buf = value.toBuffer
      buf(position) = t
      buf.mkString("")
    }

  }


  implicit class SeqOps[T](list: Seq[T]) {
    def set(position: U8, t: T): Seq[T] = {
      val buf = list.toBuffer
      buf(position.ubyte) = t
      buf.toSeq
    }

    def set(position: Int, t: T): Seq[T] = {
      val buf = list.toBuffer
      buf(position) = t
      buf.toSeq
    }

    def apply(position: U8): T = {
      list(position.ubyte.toInt)
    }
  }

  implicit class ListOps[T](list: List[T]) {
    def set(position: Int, t: T): List[T] = {
      val buf = list.toBuffer
      buf(position) = t
      buf.toList
    }
  }
//
//  implicit class ArrayOps[T](list: Array[T]) {
//    def set(position: Int, t: T): Array[T] = {
//      list(position) = t
//      list
//    }
//  }


  def intTo8Bits(spriteRow: Char): Seq[Boolean] = {
    f"${spriteRow.toBinaryString}%8s".replace(' ', '0').map(x => x == '1')
  }

}
