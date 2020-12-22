package chip8

case class WritePixelEvent(x: Int, y: Int, set: Boolean) extends scala.swing.event.Event {
  if (x < 0 || x >= Screen.WIDTH) sys.error(s"x=$x is out of bounds")
  if (y < 0 || y >= Screen.HEIGHT) sys.error(s"y=$y is out of bounds")
}

