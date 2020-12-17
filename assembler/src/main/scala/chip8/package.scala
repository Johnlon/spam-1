

package object chip8 {
  type UByte = Int
  val UByte0 : UByte = 0.toChar
  val UByte1 : UByte = 1.toChar


  implicit class StringOps(value: String) {
    val charArray: Array[Char] = value.toCharArray

    def b32: UByte = Integer.valueOf(value.substring(0, 2), 16).toByte

    def b10: UByte = Integer.valueOf(value.substring(2, 4), 16).toByte

    def b210: Int = Integer.valueOf(value.substring(1, 4), 16)

    def b3210: Int = Integer.valueOf(value, 16)

    def hexToByte: UByte = Integer.valueOf(value, 16)

//    def hexToInt: Int = Integer.valueOf(value, 16)

    def set(position: Int, t: UByte): String = {
      val buf = value.toBuffer
      buf(position) = t.toChar
      buf.mkString("")
    }
  }


  implicit class SeqOps[T](list: Seq[T]) {
    def set(position: Int, t: T): Seq[T] = {
      val buf = list.toBuffer
      buf(position) = t
      buf.toSeq
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


}
