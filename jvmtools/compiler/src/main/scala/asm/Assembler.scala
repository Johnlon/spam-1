package asm

import asm.Ports.{ReadPort, WritePort}

import java.io.{BufferedOutputStream, File, FileOutputStream, PrintWriter}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
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

  case class AsmMacro(name: String, args: List[String], text: List[String])

  def includeFile(newIncludedLines: ListBuffer[String], name: String): Unit = {
    val src = Source.fromFile(name)
    src.getLines().foreach(l =>
      newIncludedLines.append(l)
    )
    src.close()
  }

  def preprocessInclude(split: Array[String]): ListBuffer[String] = {
    val includedLines = ListBuffer[String]()
    includedLines.addAll(split)

    var included = false
    do {
      included = false
      val newIncludedLines = ListBuffer[String]()
      includedLines.foreach {
        l =>
          val trimmed = l.trim

          if (trimmed.startsWith(".include")) {
            included = true
            val words = trimmed.split("\\s+").toBuffer
            if (words.size < 2) {
              throw new RuntimeException("found .include without file name")
            }
            words.remove(0)
            val fileName = words(0)

            includeFile(newIncludedLines, fileName)
          } else {
            newIncludedLines.append(l)
          }
      }

      includedLines.clear()
      includedLines.addAll(newIncludedLines)

    } while (included)
    includedLines
  }

  def preprocessMacros(includedLines: ListBuffer[String]): ListBuffer[String] = {
    val macros = mutable.HashMap[String, AsmMacro]()

    var name = ""
    var args = List[String]()

    val macroLines = ListBuffer[String]()
    val nonMacroLines = ListBuffer[String]()

    includedLines.foreach {
      l =>
        val trimmed = l.trim

        if (trimmed.startsWith(".macro")) {
          val words = trimmed.split("\\s+").toBuffer
          if (words.size < 2) {
            throw new RuntimeException("found .macro without name")
          }
          words.remove(0)
          name = words(0)
          words.remove(0)
          args = words.toList
        } else if (trimmed.startsWith(".endmacro")) {
          macros.put(name, AsmMacro(name, args, macroLines.toList))
          name = ""
          args = List[String]()
          macroLines.clear()
        } else {
          if (name.isEmpty) {
            nonMacroLines.append(l)
          } else {
            macroLines.append(l)
          }
        }
    }
    val productLines = ListBuffer[String]()

    var macroUsage = 0

    nonMacroLines.foreach { l =>

      val words = l.trim.split("\\s+")
      if (words.nonEmpty) {
        val firstWord = words(0)
        val m = macros.get(firstWord)

        if (m.isDefined) {
          macroUsage += 1

          // macro match
          val matchedMacro = m.get
          if (words.size - 1 != matchedMacro.args.size) {
            throw new AssertionError(s"macro ${matchedMacro.name} expected ${matchedMacro.args.size} args but got ${words.size - 1}")
          }

          val argVals = words.takeRight(words.size - 1)
          val argsMap = matchedMacro.args.zip(argVals).map {
            nv =>
              val aKey = nv._1
              val aVal = nv._2
              (aKey, aVal)
          }.toBuffer

          argsMap.addOne("__#__" -> macroUsage.toString)

          matchedMacro.text.foreach {
            mLine =>
              var l = mLine
              argsMap.foreach { kv =>
                val k = kv._1
                val v = kv._2
                l = l.replace(k, v)
              }
              productLines.append(l)
          }

        } else {
          productLines.append(l)
        }
      }
    }
    productLines.zipWithIndex.map(
      l =>
          l._1.replaceAll("__LINE__", l._2.toString )
    )
  }

  def preprocess(lines: String): Seq[String] = {

    val split = lines.split("(\n|\r\n)")

    val includedLines = preprocessInclude(split)

    val macrodLines = preprocessMacros(includedLines)

    macrodLines.toSeq.map {
          // eliminate empty comments as the wrap lines and cause havoc
      l => l.replaceFirst(";\\s*$","")
    }
  }

  def assemble(raw: String, stripComments: Boolean = false): Seq[List[String]] = {

    val code = preprocess(raw)

    //val code = cpp(raw)

    val constantsRd = ReadPort.values.map(
      p => s"${p.asmPortName}: EQU ${p.id}"
    ).toList

    val constantsWr = WritePort.values.map(
      p => s"${p.asmPortName}: EQU ${p.id}"
    ).toList

    val product = constantsWr.mkString("\n", "\n", "\n") + constantsRd.mkString("\n", "\n", "\n") + code.mkString("\n")


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

        var pc = 0;
        filtered.foreach(l => {
          l match {
            case l: Instruction =>
              l.pc = Some(pc)
              pc += 1
            case l: Label =>
              l.value = Some(pc)
            case l: EquInstruction =>
            // value is known at write time
            case l: Debug =>
            // no value
            case l: Comment =>
            // no value
          }
        })

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
    val widIdx = math.log10(filtered.size).toInt + 1
    val instructions = filtered.collect { case l: Instruction => l }
    val widPc = math.log10(instructions.map(_.pc.getOrElse(0)).maxOption.getOrElse(0) + 1).toInt + 1

    filtered.zipWithIndex.foreach(
      l => {
        val line: Line = l._1

        val address = line match {
          case i: Instruction =>
            val pc = i.pc.getOrElse(sys.error("pc not yet assigned to " + i))
            s"pc %04x %${widPc}d".format(pc, pc)
          case _ =>
            " ".*(3 + 4 + 1 + widPc)
        }
        val index: Int = l._2
        System.out.println(s"""${index.formatted("%03d")} src=${line.sourceLineNumber.formatted(s"%-${widIdx}d")} $address : $line""")
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

  def decode[A <: Assembler](rom: List[String]): (AluOp, TDevice, ADevice, BDevice, Control, AddressMode, ConditionMode, Int, Byte) = {
    val str = rom.mkString(" ");
    decode(str)
  }

  /* spaces permitted in input value for formatting reasons */
  private def decode[A <: Assembler](strIn: String) = {

    val str = strIn.replaceAll(" ", "")

    val sitr = str.iterator.buffered

    val op = fromBin(sitr, 5)
    val tdev_3_0 = fromBin(sitr, 4)
    val a = fromBin(sitr, 3)
    val bdev_2_0 = fromBin(sitr, 3)
    val cond = fromBin(sitr, 4)
    val f = fromBin(sitr, 1)
    val condmode = if (fromBin(sitr, 1) == 1) ConditionMode.INVERT else ConditionMode.STANDARD
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
    //println( i)
    i
  }

  def inst(passb: AluOp, ram: TDevice, nu: ADevice, immed: BDevice, a1: Control, direct: AddressMode, cm: ConditionMode, addr: Int, byte: Byte) = {
    (passb, ram, nu, immed, a1, direct, cm, addr, byte)
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }
}

