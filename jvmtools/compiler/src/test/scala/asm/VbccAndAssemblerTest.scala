package asm

import org.junit.jupiter.api.Assertions.fail
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
        
            return 0;
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

            return 0;
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

    return 0;
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
      return 0;;
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
      return 0;
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
      return 0;
    }

    halt(1);
}
        """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }


  @Test
  def vbccTestC7(): Unit = {

    val c =
      """
void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int main() {

    // not using inline initialiser works ok
    int value[2];
    value[0] = 1234567890;
    value[1] = 987654321;

    int value1 = value[0];
    int value2 = value[1];

    if (value1 != 1234567890) {
      halt(1);
    }

    if (value2 != 987654321) {
      halt(2);
    }

    ////////////

    if (*value != 1234567890) {
      return 1;
    }

    if (*(value+1) != 987654321) {
      return 1;
    }

    ////////////

    int dref1 = *value;
    if (dref1 != 1234567890) {
      halt(3);
    }

    int dref2 = *(value+1);
    if (dref2 != 987654321) {
      halt(3);
    }

    ////////////

    int * iPtr = value;
    int idptr = *iPtr;
    if (idptr !=  1234567890) {
      halt(3);
    }

    iPtr ++;
    idptr = *iPtr;
    if (idptr !=  987654321) {
      halt(3);
    }

    ////////////

    if (value[0] != 1234567890) {
      halt(4);
    }
    if (value[1] != 987654321) {
      halt(5);
    }

    ////////////

    int* ptr = value;
    if (ptr[0] != 1234567890) {
      halt(6);
    }
    ptr+=1;
    if (*ptr != 987654321) {
      halt(7);
    }

    return 0;
}
        """
    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  @Test
  def vbccTestC8(): Unit = {

    val c =
      """
void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";

int main() {

    // array initialiser
    int value[] = { 1234567890, 987654321 };

    int value1 = value[0];
    int value2 = value[1];

    if (value1 != 1234567890) {
      halt(1);
    }

    if (value2 != 987654321) {
      halt(2);
    }

    return 0;
}
        """
    runTest(c, Some(HaltCode(0xffff, 0)))
  }


  @Test
  def vbccTestC9(): Unit = {

    val c =
      """
int main() {

    // not using inline initialiser works ok
    int value[] = { 1234567890, 987654321 };

    if (*value != 1234567890) {
      return 1;
    }
    if (*value == 1234567890) {
      return 0;
    }

    return 2;
}
        """
    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  /*
    kitchen sink of call stack, passing, reg preservation and so on.
    */
  @Test
  def vbccCall(): Unit = {

    val c =
      """
// cannot use \n in the asm as this terminates it !!
void halt2(__reg("ghalt1") char marlo, __reg("ghalt2") char alu) = "\tMARHI=0\tMARLO=[:ghalt1]\tHALT=[:ghalt2]\n";

int more(int moreParam) {
  return moreParam;
}

int sub(int subParamA, int subParamB) {
  int subLocalA = subParamA;

  int sum = subLocalA + subParamB;
  int ret = more(sum);

  return ret;
}

int main() {
  int localA = 10;
  int localB = 11;

  // call a function - does subroutine trample local var?
  int subRet = sub(3, 4);

  if (subRet != 7)  {
    halt2(subRet, 7);
  }

  subRet = sub(localA, localB);

  if (subRet != 21)  {
    halt2(subRet, 21);
  }

  if (localA != 10)  {
    halt2(localA, 10);
  }

  if (localB != 11)  {
    halt2(localB, 11);
  }

  return 66;
}
 """

    runTest(c, Some(HaltCode(0xffff, 66)))
  }


  /*
    I found this function halted with MAR=4 ALU=88 because main and sub both used gpr6 and the
    value of "check" was in gpr6 so got overwritten. By passing a as '3' in the 'a != 3'.
    And then there was a bug in jumping across pages cos I was assigning to PCLO instead of PC so jump didn't load pchitmp

   */
  @Test
  def arrayAccess(): Unit = {

    val c =
      """
// cannot use \n in the asm as this terminates it !!
void halt2(__reg("ghalt1") char marlo, __reg("ghalt2") char alu) = "\tMARHI=0\tMARLO=[:ghalt1]\tHALT=[:ghalt2]\n";

int main() {

  int a = 0x01020304;
  char * c = (char*)(&a);

  int i = c[0];

  if (i != 1)  {
    halt2(i, 1);
  }

  return 66;
}
        """

    runTest(c, Some(HaltCode(0xffff, 66)))
  }

  @Test
  def primes(): Unit = {

    val c =
      """
// cannot use \n in the asm as this terminates it !!
void halt2(__reg("ghalt1") char marlo, __reg("ghalt2") char alu) = "\tMARHI=0\tMARLO=[:ghalt1]\tHALT=[:ghalt2]\n";

int main()
{
  int i, a = 1, count;


  while(a <= 100)
  {
    count = 0;
    i = 2;
    while(i <= a/2)
    {
      if(a%i == 0)
      {
        count++;
	break;
      }
      i++;
    }
    if(count == 0 && a != 1 )
    {
	printf(" %d ", a);
    }
    a++;
  }
  return 0;
}
        """

    runTest(c, Some(HaltCode(0xffff, 66)))
  }

  /*
    I found this function halted with MAR=4 ALU=88 because main and sub both used gpr6 and the
    value of "check" was in gpr6 so got overwritten. By passing a as '3' in the 'a != 3'.
    And then there was a bug in jumping across pages cos I was assigning to PCLO instead of PC so jump didn't load pchitmp

   */
  @Test
  def jumpAcrossPages(): Unit = {

    val c =
      """
// cannot use \n in the asm as this terminates it !!
void halt2(__reg("ghalt1") char marlo, __reg("ghalt2") char alu) = "\tMARHI=0\tMARLO=[:ghalt1]\tHALT=[:ghalt2]\n";

int more(int unused) {
  return unused;
}

int sub(int a, int b) {
  int subA = a;
  int subB = b;

  if (a != 3)  {
    more(a);
  }

  if (a != 3)  {
    halt2(a, 13);
  }
  if (b != 4)  {
    halt2(17, 14);
  }
  if (subA != 3)  {
    halt2(subA, 23);
  }
  if (subB != 4)  {
    halt2(subB, 24);
  }
  return 99;
}

int main() {
  int localA = 10;

  int check = 88;
  // call a function - does subroutine trample local var r?
  sub(3, 4);

  if (localA != 10)  {
    halt2(localA, 10);
  }

  if (check != 88)  {
    halt2(check, 88);
  }

  return 66;
}
        """

    runTest(c, Some(HaltCode(0xffff, 66)))
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

    val roms = assembler.assemble(assembly.mkString("\n"))

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

}