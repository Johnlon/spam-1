package asm

import asm.AddressMode.Mode

import java.io.{File, FileOutputStream, PrintWriter}
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

  def binToByte(str: String): Byte = {
    Integer.parseInt(str, 2).toByte
  }

  private def asmFile(args: Array[String]) = {
    val fileName = args(0)

    println("reading : " + fileName)
    val code = Source.fromFile(fileName).getLines().mkString("\n")
    println("assembling : " + fileName)

    val asm = new Assembler()

    val roms: List[List[String]] = asm.assemble(code)

    println("writing roms")

    val singleRom = new File(s"${fileName}.rom")
    singleRom.delete()
    val rom = new PrintWriter(singleRom)

    val files = (1 to 6).map(n => new File(s"${fileName}.rom" + n))
    files.foreach(_.delete())

    val romStreams = files.map(new FileOutputStream(_))
    val rom1 =romStreams(0)
    val rom2 =romStreams(1)
    val rom3 =romStreams(2)
    val rom4 =romStreams(3)
    val rom5 =romStreams(4)
    val rom6 =romStreams(5)

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
    }

    List(rom, rom1, rom2, rom3, rom4, rom5, rom6).foreach(_.flush())
    List(rom, rom1, rom2, rom3, rom4, rom5, rom6).foreach(_.close())

    println("completed roms")
  }
}

class Assembler extends InstructionParser with Knowing with Lines with Devices {


  def assemble(code: String, quiet: Boolean = false): List[List[String]] = {

    parse(lines, code) match {
      case Success(theCode, _) => {
        println("Statements:")

        val filtered = theCode.filter { l =>
          l match {
            case Comment(_) if quiet =>
              false
            case _ =>
              true
          }
        }

        filtered.zipWithIndex.foreach(
          l => {
            val line: Line = l._1
            val address = line.instructionAddress
            val index: Int = l._2
            System.out.println(index.formatted("%03d") + " pc:" + address.formatted("%04x") + ":" + address.formatted("%05d") + ": " + line)
          }
        )

        assertAllResolved(theCode)

        val instructions = filtered.collect { case x: Instruction => x.roms }
        //instructions.zipWithIndex.foreach(l => println("CODE : " + l))
        println("Assembled: " + instructions.size + " instructions")
        instructions
      }

      case msg: Failure => {
        sys.error(s"FAILURE: $msg ")

      }
      case msg: Error => {
        sys.error(s"ERROR: $msg")
      }
    }
  }


  private def assertAllResolved(theCode: List[Line]) = {
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
    val str = rom.mkString("");
    decode(str)
  }

  private def decode[A <: Assembler](str: String) = {
    val sitr = str.iterator.buffered

    val op = fromBin(sitr, 5)
    val t = fromBin(sitr, 4)
    val a = fromBin(sitr, 3)
    val bLo = fromBin(sitr, 3)
    val cond = fromBin(sitr, 4)
    val f = fromBin(sitr, 1)
    val condmode = if (fromBin(sitr, 1) == 1) ConditionMode.Invert else ConditionMode.Standard
    sitr.take(1).mkString("")
    val bHi = fromBin(sitr, 1)
    val m = if (fromBin(sitr, 1) == 1) AddressMode.DIRECT else AddressMode.REGISTER
    val addr = fromBin(sitr, 16)
    val immed = fromBin(sitr, 8)

    inst(
      AluOp.valueOf(op),
      TDevice.valueOf(t),
      ADevice.valueOf(a),
      BDevice.valueOf(bHi << 3 + bLo),
      Control.valueOf(cond, FlagControl.fromBit(f)),
      m,
      condmode,
      addr,
      immed.toByte
    )
  }

  def inst(passb: AluOp, ram: TDevice, nu: ADevice, immed: BDevice, a1: Control, direct: AddressMode.Value, cm: ConditionMode, addr: Int, byte: Byte) = {
    (passb, ram, nu, immed, a1, direct, cm, addr, byte)
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }
}

