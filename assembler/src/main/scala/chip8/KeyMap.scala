package chip8

import scala.swing.event.Key

object KeyMap {
  val keyMap = Map(
    U8(0) -> Key.Key0,
    U8(1) -> Key.Key1,
    U8(2) -> Key.Key2,
    U8(3) -> Key.Key3,
    U8(4) -> Key.Key4,
    U8(5) -> Key.Key5,
    U8(6) -> Key.Key6,
    U8(7) -> Key.Key7,
    U8(8) -> Key.Key8,
    U8(9) -> Key.Key9,
    U8(10) -> Key.A,
    U8(11) -> Key.B,
    U8(12) -> Key.C,
    U8(13) -> Key.D,
    U8(14) -> Key.E,
    U8(15) -> Key.F
  )

  def isKeyPressed(state: State, key: U8): Boolean = {
    keyMap.get(key).exists {
      c8Key => state.pressedKeys.contains(c8Key)
    }
  }
}
