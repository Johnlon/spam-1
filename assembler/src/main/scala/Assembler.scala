import java.io.{File, PrintWriter}

import Mode.Mode

import scala.io.Source

object Assembler {

  def main(args: Array[String]) = {
    if (args.size != 1) {
      System.err.println("missing argument : asm file name")
      sys.exit(1)
    }
    val fileName = args(0)

    val code = Source.fromFile(fileName).getLines().mkString("\n")

    val asm = new Assembler()
    val roms: List[List[String]] = asm.assemble(code)

    val pw = new PrintWriter(new File(s"${fileName}.rom"))
    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }
    pw.close()
  }
}

class Assembler extends InstructionParser with Knowing with Lines with Devices {


  def assemble(code: String): List[List[String]] = {

    parse(lines, code) match {
      case Success(matched, _) => {
        println("Statements:")
        matched.zipWithIndex.foreach(
          l => {
            val address = l._1.instructionAddress
            System.out.println(l._2.formatted("%03d") + " pc:" + address.formatted("%04x") + ":" + address.formatted("%05d") + ": " + l._1)
          }
        )
        val unresolvedStatements = matched.zipWithIndex.filter(_._1.unresolved)
        if (unresolvedStatements.size > 0) {
          System.err.println("Unresolved values:")
          unresolvedStatements.foreach(l => System.err.println(l._2.formatted("%03d") + " : " + l._1))
          sys.exit(1)
        }

        val instructions = matched.collect { case x: Instruction => x.roms }
        instructions.zipWithIndex.foreach(l => println("CODE : " + l))
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


  def decode[A <: Assembler](rom: List[String]): (AluOp, TDevice, ADevice, BDevice, Control, Mode, Int, Byte) = {
    val str = rom.mkString("");
    val sitr = str.iterator.buffered

    val op = fromBin(sitr, 5)
    val t = fromBin(sitr, 4)
    val a = fromBin(sitr, 3)
    val b = fromBin(sitr, 3)
    val cond = fromBin(sitr, 4)
    val f = fromBin(sitr, 1)
    sitr.take(3).mkString("")
    val m = if (fromBin(sitr, 1) == 1) Mode.DIRECT else Mode.REGISTER
    val addr = fromBin(sitr, 16)
    val immed = fromBin(sitr, 8)

    i(
      AluOp.valueOf(op),
      TDevice.valueOf(t),
      ADevice.valueOf(a),
      BDevice.valueOf(b),
      Control.valueOf(cond, f),
      m,
      addr,
      immed.toByte
    )
  }

  def i(passb: AluOp, ram: TDevice, nu: ADevice, immed: BDevice, a1: Control, direct: Mode.Value, addr: Int, byte: Byte) = {
    (passb, ram, nu, immed, a1, direct, addr, byte)
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }
}

