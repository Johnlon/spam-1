package asm

import org.junit.jupiter.api.Assertions.{assertEquals, fail}
import org.junit.jupiter.api.Test
import verification.HaltCode
import verification.Verification.verifyRoms

import java.io.File
import java.nio.file.{Files, Path}

class VbccAndAssemblerTest {
  @Test
  def vbccTestC1(): Unit = {

    val c =
      """
        void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";
        
        int main() {
        
            int value = 666;
        
            if (value!=666) {
              halt(1);
            }
        
            halt(0);
        }
        
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC2(): Unit = {

    val c =
      """
        void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

        int main() {

            int status = 666;

            if (status!=666) {
              halt(1);
            }

            if (status==666) {
              status=999;
            } else {
              halt(2);
            }

            if (status==666) {
              halt(3);
            }

            if (status!=999) {
              halt(4);
            }

            halt(0);
        }
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC3(): Unit = {

    val c =
      """

void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int main() {

    int countUp = 0;

    while (countUp < 3) {
      countUp += 2;
    }


    if (countUp != 4) {
      halt(1);
    }

    halt(0);
}
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC4(): Unit = {

    val c =
      """

void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int adder(int a, int b) {
  return a+b;
}

int main() {

    int sum = adder(1,2);

    if (sum == 3) {
      halt(0);
    }

    halt(sum);
}
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC5(): Unit = {

    val c =
      """
void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int main() {

    int value = 666;

    int* pValue = &value;

    int other = *pValue;

    if (other != 666) {
      halt(1);
    }
    if (other == 666) {
      halt(0);
    }

    halt(2);
}
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC6(): Unit = {

    val c =
      """
void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int main() {

    int value = 1234567890;

    int* pValue = &value;

    int other = *pValue;

    if (other == 1234567890) {
      halt(0);
    }

    halt(1);
}
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  private def vbcc(c: String): List[String] = {
    import java.io.PrintWriter
    import scala.language.postfixOps
    import scala.sys.process._

    val tmp: Path = Files.createTempFile("testcase", "")
    val cfile = tmp.toAbsolutePath.toString + ".c"
    val afile = tmp.toAbsolutePath.toString + ".asm"

    new PrintWriter(cfile) {
      val code: String = c.stripMargin
      write(code);
      close
    }

    val exit = new File(cfile) #> "bash /home/john/work/vbcc/demo/vbccTest" #> new File(afile) !

    val asm = Files.readString(Path.of(afile))
    val console = asm.split("[\n\r]")
    val lines = console.filter(_.startsWith("ASM:")).map(_.replaceAll("ASM:", ""))

    if (exit != 0) {
      println(asm)
      fail("non zero exit code from vbcc")
    }
    lines.foreach(println)
    lines.toList
  }


  private def runTest(c: String, someCode: Some[HaltCode]) = {
    val assembly = vbcc(c)

    val assembler = new Assembler()

    val roms = assemble(assembly, assembler)
    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + assembler.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = someCode,
      timeout = 200,
      roms = roms);
  }

  private def instructions(code: Seq[String], asm: Assembler): Seq[(AluOp, Any, Any, Any, Control, AddressMode, ConditionMode, Int, Byte)] = {
    val roms: Seq[List[String]] = assemble(code, asm)
    decode(roms, asm)
  }

  private def decode(roms: Seq[List[String]], asm: Assembler) = {
    roms.map(r =>
      asm.decode(r)
    )
  }

  private def assemble(code: Seq[String], asm: Assembler) = {
    // comments run to end of line
    asm.assemble(code.mkString("\n"))
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    assertEquals(i.map(asm.KnownInt), asm.labels(s).getVal)
  }

  def assertEqualsList[T](expected: IterableOnce[T], actual: IterableOnce[T]): Unit = {
    assertEquals(expected.iterator.mkString("\n"), actual.iterator.mkString("\n"))
  }
}