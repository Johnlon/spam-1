import java.io.{File, FileInputStream}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/*
* BC_Text can be found here ...
*  https://github.com/stianeklund/chip8/tree/master/roms
*
* IBM Logo ...
* https://github.com/loktar00/chip8/blob/master/roms/IBM%20Logo.ch8
* */
object Disasm extends App {

  def IBMLogo = new File("src/main/resources/chip8/roms/IBM_Logo.ch8")
  def BC_Test = new File("src/main/resources/chip8/roms/BC_test.ch8")
  def IBMLogo2 = new File("src/main/resources/chip8/roms/roms_programs_IBM_Logo.ch8")

  def read(file: File): List[String] = {
    val dataIn = new FileInputStream(file)
    var eof = false
    var idx = 0x200
    val dataOut = ListBuffer.empty[String]
    while (!eof) {

      val hi = dataIn.read()
      eof = hi == -1
      if (!eof) {
        val lo = dataIn.read()
        if (lo == -1) {
          sys.error("odd number of bytes in file")
        }

        val instr = (hi << 8) + lo

        dataOut.append(f"$idx%04X: $instr%04X")
        idx += 2 // 2 bytes per op
      }
    }
    dataOut.toList
  }

}