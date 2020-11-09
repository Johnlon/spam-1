
import java.io.{FileInputStream, FileOutputStream}

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.{Level, LogManager, Logger}

import scala.collection.JavaConversions.asScalaBuffer

object Asm extends App {
  //  if (System.getProperty("asm.debug") != null) {
  Configurator.setRootLevel(Level.DEBUG)
  //  }
  val log = LogManager.getLogger(this.getClass)
  val iFile = "operations.txt"

  val is = new FileInputStream(iFile)
  //val is = if (args.size > 0) new FileInputStream(iFile) else System.in
  val outputStream = if (args.size > 1) new FileOutputStream(args(1)) else System.out
  val input = CharStreams.fromStream(is)

  val lexer = new SPAM1Lexer(input)
  lexer.removeErrorListeners()
  lexer.addErrorListener(new ConsoleErrorListener(log))
  lexer.getAllTokens.foreach(t => println("TOKEN : " + t))

  val tokens = new CommonTokenStream(lexer)
  tokens.getTokens.foreach(t => println("TOKEN : " + t))

  val parser = new SPAM1Parser(tokens)
  parser.addErrorListener(new ConsoleErrorListener(log))

  val tree = parser.prog()
  parser.setTrace(true)
  val str: String = tree.toStringTree(parser)
  log.debug("TREE: " + str)

  val walker = new ParseTreeWalker()
  val listener = new TwoPassListener()

  walker.walk(listener, tree)
  log.debug("SYMBOLS1: " + listener.symbols)

  Configurator.setRootLevel(Level.INFO)
  walker.walk(listener, tree)
  //log.debug("SYMBOLS2: " + listener.symbols)
  //log.debug(listener.memory.toList.take(200).map(_.toHexString).grouped(10).toList.map(_.mkString(", ")).mkString("\n"))
  //  if (listener.memory.size > 16374) {
  if (listener.memory.size > 10) {
    //log.debug("BYTES: " + listener.memory.toList.drop(10).map(_.toHexString).grouped(10).toList.map(_.mkString(", ")).mkString("\n"))
    //    log.debug(listener.memory.toList.drop(16374).map(_.toHexString).grouped(10).toList.map(_.mkString(", ")).mkString("\n"))
  }

  //outputStream.write(listener.memory.map(_.toByte))
  println("END")


}

//
//  private val stream = CharStreams.fromString(
//    "start: ;comment\n" +
//      "VAL1: EQU #1 + #$ff + VAL2\n" +
//      "VAL2: EQU #$ff\n" +
//      "VAL3: EQU #$abcd\n" +
//      "VAL4: EQU VAL1\n" +
//      "start1: ;comment\n" +
//      "\n" +
//      "label: REGA=#1\n" +
//      "\n" +
//      "; assign ram\n" +
//      "[#:label]=REGB\n" +
//      "RAM=REGB\n" +
//      "[#:label]=REGB'S\n" +
//      "RAM=REGB'S\n" +
//      "\n" +
//      "REGA=REGB\n" +
//      "REGA=REGA PLUS [#:label]\n" +
//      "REGA=REGB PLUS'S REGC\n" +
//      "REGA=REGB PLUS REGC\n" +
//      "\n" +
//      "\n" +
//      "; imediate values\n" +
//      "REGA=#12\n" +
//      "REGA=#12'S\n" +
//      "REGA=#$ff\n" +
//      "REGA=#$ff'S\n" +
//      "; imediate values by labels\n" +
//      "REGA=#:label\n" +
//      "REGA=#:label'S\n" +
//      "\n" +
//      "; ram access by register\n" +
//      "REGA=RAM\n" +
//      "REGA=RAM'S\n" +
//      "; ram access direct\n" +
//      "REGA=[#12]\n" +
//      "REGA=[#12]'S\n" +
//      "REGA=[#$ff]\n" +
//      "REGA=[#$ff]'S\n" +
//      "REGA=[#:label]\n" +
//      "REGA=[#:label]'S\n" +
//      ";ops\n" +
//      "\n"
//  )

class ConsoleErrorListener(log: Logger) extends BaseErrorListener {

  override def syntaxError(recognizer: Recognizer[_, _], offendingSymbol: Any, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException): Unit = {
    log.error("line " + line + ":" + charPositionInLine + " " + msg)
  }
}
