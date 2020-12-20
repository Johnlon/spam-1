package chip8

import chip8.Screen.{BLANK, NullListener, PIXEL}

trait PixelListener {
  def apply(x: Int, y: Int, set: Boolean): Unit
}

case class Screen(
                   width: Int = Screen.WIDTH,
                   height: Int = Screen.HEIGHT,
                   buffer: List[String] = Screen.makeEmptyScreenBuffer(Screen.WIDTH, Screen.HEIGHT),
                   pixelListener: PixelListener = NullListener
                 ) {
  def clear(): Screen = {
    this.copy(
      buffer = Screen.makeEmptyScreenBuffer(width, height)
    )
  }

  def setPixel(x: Int, y: Int): (Screen, Boolean) = {
    val xMod = x % width
    val yMod = y % height

    val wasSet = buffer(yMod)(xMod) != BLANK

    val row = buffer(yMod)

    /*
    We are setting a pixel by using XOR.
    If the pixel is already set then we clear it.
    If we clear a pixel then we record that a pixel was erased.
    */
    val erased = wasSet
    val updatedRow = if (wasSet) {
      pixelListener(xMod, yMod, false)
      row.set(xMod, BLANK)
    } else {
      pixelListener(xMod, yMod, true)
      row.set(xMod, PIXEL)
    }

    // convert to string so it prints nicer in debugger
    val str: String = updatedRow.mkString("")
    val newBuf: List[String] = buffer.set(yMod, str)
    (copy(buffer = newBuf), erased)
  }

}

object Screen {
  private val BLANK: Pixel = ' '
  val PIXEL: Pixel = 0x2588.toChar

  val HEIGHT = 32
  val WIDTH = 60

  def GAP = ""

  val NullListener = new PixelListener {
    override def apply(x: Int, y: Int, set: Boolean): Unit = {}
  }

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
