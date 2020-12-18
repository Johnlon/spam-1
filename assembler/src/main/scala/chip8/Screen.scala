package chip8

import chip8.Chip8Emulator.{BLANK, GAP, PIXEL}

case class Screen(
                   width: Int = Screen.WIDTH,
                   height: Int = Screen.HEIGHT,
                   buffer: List[String] = Screen.makeEmptyScreenBuffer(Screen.WIDTH, Screen.HEIGHT)
                 ) {
  def clear(): Screen = {
    this.copy(
      buffer = Screen.makeEmptyScreenBuffer(width, height)
    )
  }

  def setPixel(x: Int, y: Int): (Screen, Boolean) = {
    val xMod = x % width
    val wasSet = buffer(y)(xMod) != BLANK

    val row = buffer(y)

    /*
    We are setting a pixel by using XOR.
    If the pixel is already set then we clear it.
    If we clear a pixel then we record that a pixel was erased.
    */
    val erased = wasSet
    val updatedRow = if (wasSet) {
      row.set(xMod, BLANK)
    } else {
      row.set(xMod, PIXEL)
    }

    // convert to string so it prints nicer in debugger
    val str: String = updatedRow.mkString("")
    val newBuf: List[String] = buffer.set(y, str)
    (copy(buffer = newBuf), erased)
  }

}

object Screen {
  val HEIGHT = 32
  val WIDTH = 60

  def paintScreen(screen: Screen): Unit = {
    println("--------\n")
    (0 until screen.height).foreach { y =>

      val str = screen.buffer(y).mkString(GAP)
      val lStr = f"$y%02d| $str |"

      //        state.out.println(str)
      println(lStr)
    }
  }

  private def makeEmptyScreenBuffer(width: Int, height: Int): List[String] = {
    fillScreenBuffer(BLANK, width, height)
  }

  def fillScreenBuffer(c: Pixel, width: Int, height: Int): List[String] = {
    val value = (0 until height).map {
      _ =>
        c.toChar.toString * width
    }
    value.toList
  }
}
