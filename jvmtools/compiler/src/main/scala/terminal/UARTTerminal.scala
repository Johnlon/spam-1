package terminal

import scala.swing._

/* includes button for controlling the sim - but also configures the verilog uart to transmit lots  */
abstract class UARTTerminal extends Chip8Terminal {

  def run(): Unit = {

    val buttons = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(brefresh, bpaint, bstop, bnext, breplay)
    }

    val label = new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(labelChar)
    }

    val head = new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(buttons, label, textArea, logLine, scrText)
    }

    uiapp.contents = head

    outputStream().print(f"t100000\n")
    outputStream().flush()
  }
}