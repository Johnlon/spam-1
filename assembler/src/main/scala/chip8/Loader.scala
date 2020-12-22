package chip8

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
  def BC_Test = new File("src/main/resources/chip8/roms/BC_test")
  def IBMLogo2 = new File("src/main/resources/chip8/roms/roms_programs_IBM_Logo.ch8")
  def rom(name: String)  = new File("src/main/resources/chip8/roms/", name)

  def read(file: File): List[U8] = {
    val dataIn = new FileInputStream(file)
    var eof = false

    val dataOut = ListBuffer.empty[U8]
    while (!eof) {

      val b = dataIn.read()
      eof = b == -1
      if (!eof) {
        dataOut.append( U8.valueOf(b) )
      }
    }
    dataOut.toList
  }

}