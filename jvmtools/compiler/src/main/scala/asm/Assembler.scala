package asm

import asm.AddressMode.Mode
import asm.Ports.{ReadPort, WritePort}
//import org.anarres.cpp.{CppReader, DefaultPreprocessorListener}
import org.apache.commons.io.IOUtils

import java.io.{BufferedOutputStream, File, FileOutputStream, PrintWriter, StringReader}
import scala.io.Source

object Assembler {

  def main(args: Array[String]) = {
    if (args.size == 1) {
      asmFile(args)
    }
    else if (args.size == 2 && args(0) == "-d") {
      disAsm(args(1))
    }
    else {
      System.err.println("Assemble ...")
      System.err.println("    usage:  file-name.asm ")
      System.err.println("Disassemble ...")
      System.err.println("    usage:  -d '48 bit binary string' ")
      sys.exit(1)
    }

  }


  def disAsm(code: String) = {
    val asm = new Assembler()
    val codes = code.split("\\s")
    codes.zipWithIndex.foreach {
      c => {
        println(c._2.toString + " : " + c)
        println("\t " + asm.decode(c._1))
      }
    }
  }

  def binToByte(str: String): Char = {
    Integer.parseInt(str, 2).toChar
  }

  private def asmFile(args: Array[String]) = {
    val fileName = args(0)

    println("reading : " + fileName)
    val code = Source.fromFile(fileName).getLines().mkString("\n")
    println("assembling : " + fileName)

    val asm = new Assembler()

    val roms: Seq[List[String]] = asm.assemble(code)

    println("writing roms")

    val singleRom = new File(s"${fileName}.rom")
    singleRom.delete()
    val rom = new PrintWriter(singleRom)

    val files = (1 to 6).map(n => new File(s"${fileName}.rom" + n))
    files.foreach(_.delete())

    // massive write speed up by using buffered writer
    val romStreams = files.map(f => new BufferedOutputStream(new FileOutputStream(f)))
    val rom1 = romStreams(0)
    val rom2 = romStreams(1)
    val rom3 = romStreams(2)
    val rom4 = romStreams(3)
    val rom5 = romStreams(4)
    val rom6 = romStreams(5)


    var i = 0;
    roms.foreach { line =>
      line.foreach { romLine =>
        rom.write(romLine)
      }
      rom.write("\n")

      // rom6 is high byte / left most
      rom6.write(binToByte(line(0)))
      rom5.write(binToByte(line(1)))
      rom4.write(binToByte(line(2)))
      rom3.write(binToByte(line(3)))
      rom2.write(binToByte(line(4)))
      rom1.write(binToByte(line(5)))

      /*
      printf("ROM PC %6d:  ", i)
      line.foreach {
        b =>
          printf(" %2s (%8s) ", binToByte(b).toHexString, b)
      }
      printf("\n")
       */


      i += 1
      if (i % 1000 == 0)
        println("written : " + i)

    }

    List(rom, rom1, rom2, rom3, rom4, rom5, rom6).foreach(_.flush())
    List(rom, rom1, rom2, rom3, rom4, rom5, rom6).foreach(_.close())

    println("completed roms")
  }
}

class Assembler extends InstructionParser with Knowing with Lines with Devices {


  def assemble(raw: String, stripComments: Boolean = false): Seq[List[String]] = {

    //val code = cpp(raw)
    val code = raw

    val constantsRd = ReadPort.values.map(
      p => s"${p.asmPortName}: EQU ${p.id}"
    ).toList

    val constantsWr = WritePort.values.map(
      p => s"${p.asmPortName}: EQU ${p.id}"
    ).toList

    val product = constantsWr.mkString("\n", "\n","\n") + constantsRd.mkString("\n", "\n","\n") + code

    parse(lines, product) match {
      case Success(theCode, _) =>
        println("Statements:")

        // move any Ram init's to the front of the program so that ram is configured before regular program code

        val filtered = theCode.filter { l =>
          l match {
            case Comment(_) if stripComments =>
              false
            case _ =>
              true
          }
        }

        logInstructions(filtered)

        assertAllResolved(theCode)

        val instructions = filtered.collect { case x: Instruction => x.encode }
        //instructions.zipWithIndex.foreach(l => println("CODE : " + l))
        println("Assembled: " + instructions.size + " instructions")
        instructions

      case msg: Failure => {
        sys.error(s"FAILURE: $msg ")

      }
      case msg: Error => {
        sys.error(s"ERROR: $msg")
      }
    }
  }

  /*
  def cpp(rawCode: String): String = {
    val code = "#define jmp(label) \\\n PCHITMP = < :label \\\n PC      = > :label ; jmp label\n" + rawCode

    val r = new CppReader(new StringReader(code))
    r.getPreprocessor.setListener(new DefaultPreprocessorListener())
    IOUtils.toString(r)
  }
  */

  private def logInstructions(filtered: Seq[Line]) = {
    filtered.zipWithIndex.foreach(
      l => {
        val line: Line = l._1
        val address = line.instructionAddress
        val index: Int = l._2
        System.out.println(index.formatted("%03d") + " pc:" + address.formatted("%04x") + ":" + address.formatted("%05d") + ": " + line)
      }
    )
  }

  private def assertAllResolved(theCode: Seq[Line]) = {
    val unresolvedStatements = theCode.zipWithIndex.filter(s =>
      s._1.unresolved
    )
    if (unresolvedStatements.nonEmpty) {
      //          System.err.println("Unresolved values:")
      //          unresolvedStatements.foreach(l => System.err.println(l._2.formatted("%03d") + " : " + l._1))
      sys.error("Unresolved values: \n" + unresolvedStatements.map(l => l._2.formatted("%03d") + " : " + l._1).mkString("\n"))
    }
  }

  def decode[A <: Assembler](rom: List[String]): (AluOp, TDevice, ADevice, BDevice, Control, Mode, ConditionMode, Int, Byte) = {
    val str = rom.mkString(" ");
    decode(str)
  }

  /* spaces permitted in input value for formatting reasons */
  private def decode[A <: Assembler](strIn: String) = {

    val str = strIn.replaceAll(" ","")

    val sitr = str.iterator.buffered

    val op = fromBin(sitr, 5)
    val tdev_3_0 = fromBin(sitr, 4)
    val a = fromBin(sitr, 3)
    val bdev_2_0 = fromBin(sitr, 3)
    val cond = fromBin(sitr, 4)
    val f = fromBin(sitr, 1)
    val condmode = if (fromBin(sitr, 1) == 1) ConditionMode.Invert else ConditionMode.Standard
    val bdev_3 = fromBin(sitr, 1)
    val tdev_4 = fromBin(sitr, 1)
    val m = if (fromBin(sitr, 1) == 1) AddressMode.DIRECT else AddressMode.REGISTER
    val addr = fromBin(sitr, 16)
    val immed = fromBin(sitr, 8)

    val b = (bdev_3 << 3) + bdev_2_0
    val t = (tdev_4 << 4) + tdev_3_0

    val i = inst(
      AluOp.valueOf(op),
      TDevice.valueOf(t),
      ADevice.valueOf(a),
      BDevice.valueOf(b),
      Control.valueOf(cond, FlagControl.fromBit(f)),
      m,
      condmode,
      addr,
      immed.toByte
    )
    println(i)
    i
  }

  def inst(passb: AluOp, ram: TDevice, nu: ADevice, immed: BDevice, a1: Control, direct: AddressMode.Value, cm: ConditionMode, addr: Int, byte: Byte) = {
    (passb, ram, nu, immed, a1, direct, cm, addr, byte)
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }
}

