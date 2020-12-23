package chip8

case class DrawScreenEvent(buf: Seq[Seq[Boolean]]) extends scala.swing.event.Event {
  if (buf.length != SCREEN_HEIGHT)
    sys.error(s"screen length ${buf.length} is out of bounds")
  buf.foreach(line =>
    if (line.length != SCREEN_WIDTH)
      sys.error(s"screen width ${line.length} is out of bounds")
  )
}

