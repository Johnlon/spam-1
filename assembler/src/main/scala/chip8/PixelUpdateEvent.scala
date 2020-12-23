package chip8

case class PixelUpdateEvent(x: Int, y: Int, set: Boolean) extends scala.swing.event.Event {
  if (x < 0 || x >= SCREEN_WIDTH) sys.error(s"x=$x is out of bounds")
  if (y < 0 || y >= SCREEN_HEIGHT) sys.error(s"y=$y is out of bounds")
}

case class DrawScreenEvent(buf: Seq[Seq[Boolean]]) extends scala.swing.event.Event {
  if (buf.length != SCREEN_HEIGHT)
    sys.error(s"screen length ${buf.length} is out of bounds")
  buf.foreach(line =>
    if (line.length != SCREEN_WIDTH)
      sys.error(s"screen width ${line.length} is out of bounds")
  )
}

