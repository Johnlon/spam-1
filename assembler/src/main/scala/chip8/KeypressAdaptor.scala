package chip8

import scala.swing.event.{Key, KeyEvent, KeyPressed, KeyReleased}

object KeypressAdaptor {

  @volatile
  private var keys = Set.empty[Key.Value]

  def pressedKeys: Set[Key.Value] = {
    keys
  }

  def registerKeypress(ke: KeyEvent): Unit = {
    ke match {
      case KeyPressed(_, k, _, _) =>
        val effK: Key.Value = keyAlternatives(k)
        keys = keys + effK
      case KeyReleased(_, k, _, _) =>
        val effK: Key.Value = keyAlternatives(k)
        keys = keys - effK
      case _ => // ignore
    }
  }

  // seek http://www.sunrise-ev.com/photos/1802/Chip8interpreter.pdf
  private def keyAlternatives(k: Key.Value): Key.Value = {
    if (k == Key.Up || k == Key.I) Key.Key2
    else if (k == Key.Left || k == Key.J) Key.Key4
    else if (k == Key.Right || k == Key.K) Key.Key6
    else if (k == Key.Down || k == Key.M) Key.Key8
    else if (k == Key.Space) Key.Key5
    else k
  }
}

