package chip8

case class WritePixelEvent(x: Int, y: Int, set: Boolean) extends scala.swing.event.Event

