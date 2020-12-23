package chip8

import chip8.Screen.{BLANK_CHAR, NullListener, BLOCK_CHAR}


case class Screen(
                   buffer: List[String] = Screen.makeEmptyScreenbuffer1(SCREEN_WIDTH,SCREEN_HEIGHT),
                   publishDrawEvent: WritePixelEvent => Unit = NullListener
                 ) {
  def clear(): Screen = {
    this.copy(
      buffer = Screen.makeEmptyScreenbuffer1(SCREEN_WIDTH, SCREEN_HEIGHT)
    )
  }

//  def buffer : List[String] = buffer1

  def setPixel(x: Int, y: Int): (Screen, Boolean) = {
    val xMod = x % SCREEN_WIDTH
    val yMod = y % SCREEN_HEIGHT

    val wasSet = buffer(yMod)(xMod) != BLANK_CHAR

    val row = buffer(yMod)

    /*
    We are setting a pixel by using XOR.
    If the pixel is already set then we clear it.
    If we clear a pixel then we record that a pixel was erased.
    */
    val erased = wasSet
    val updatedRow = if (wasSet) {
      publishDrawEvent(WritePixelEvent(xMod, yMod, false))
      row.set(xMod, BLANK_CHAR)
    } else {
      publishDrawEvent(WritePixelEvent(xMod, yMod, true))
      row.set(xMod, BLOCK_CHAR)
    }

    // convert to string so it prints nicer in debugger
    val str: String = updatedRow.mkString("")
    val newBuf: List[String] = buffer.set(yMod, str)
    (copy(buffer = newBuf), erased)
  }

}

object Screen {
  val BLANK_CHAR: PixelType = 32.toChar
  val BLOCK_CHAR: PixelType = 0x2588.toChar

  def GAP = ""

  def NullListener(writePixelEvent: WritePixelEvent): Unit = {}

  def paintScreen(screen: Screen): Unit = {
    println("--------\n")
    (0 until SCREEN_HEIGHT).foreach { y =>

      val str = screen.buffer(y).mkString(GAP)
      val lStr = f"$y%02d| $str |"

      //        state.out.println(str)
      println(lStr)
    }
  }

  private def makeEmptyScreenbuffer1(width: Int, height: Int): List[String] = {
    fillScreenbuffer1(BLANK_CHAR, width, height)
  }

  def fillScreenbuffer1(c: PixelType, width: Int, height: Int): List[String] = {
    val value = (0 until height).map {
      _ =>
        c.toChar.toString * width
    }
    value.toList
  }
}
