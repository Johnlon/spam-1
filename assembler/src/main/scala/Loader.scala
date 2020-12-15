import java.io.{File, FileInputStream}

import scala.collection.mutable.ListBuffer

/*
* BC_Text can be found here ...
*  https://github.com/stianeklund/chip8/tree/master/roms
*
* IBM Logo ...
* https://github.com/loktar00/chip8/blob/master/roms/IBM%20Logo.ch8
* */
// load bytes into an array of ints with msb to the left
object Loader extends App {

  def IBMLogo = new File("src/main/resources/chip8/roms/IBM_Logo.ch8")
  def BC_Test = new File("src/main/resources/chip8/roms/BC_test.ch8")
  def IBMLogo2 = new File("src/main/resources/chip8/roms/roms_programs_IBM_Logo.ch8")

  def read(file: File): List[Short] = {
    val dataIn = new FileInputStream(file)
    var eof = false

    val dataOut = ListBuffer.empty[Short]
    while (!eof) {

      val hi = dataIn.read()
      eof = hi == -1
      if (!eof) {
        val lo = dataIn.read()
        if (lo == -1) {
          sys.error("odd number of bytes in file")
        }

        val instr: Short = ((hi << 8) + lo).toShort

        dataOut.append( instr )
      }
    }
    dataOut.toList
  }

}