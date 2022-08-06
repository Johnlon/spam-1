package asm

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source


object Preprocessor {

  case class AsmMacro(name: String, args: List[String], text: List[String])

  private def includeFile(newIncludedLines: ListBuffer[String], name: String): Unit = {
    val src = Source.fromFile(name)
    src.getLines().foreach(l =>
      newIncludedLines.append(l)
    )
    src.close()
  }

  private def preprocessInclude(split: Array[String]): ListBuffer[String] = {
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

  private def extractMacros(includedLines: ListBuffer[String]) = {
    val macros = mutable.HashMap[String, AsmMacro]()
    var args = List[String]()

    val macroLines = ListBuffer[String]()
    val nonMacroLines = ListBuffer[String]()

    var name = ""
    includedLines.foreach {
      l =>
        val trimmed = l.trim

        if (trimmed.startsWith(".macro")) {
          val words = trimmed.split("\\s+").toBuffer
          if (words.size < 1) {
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
    (macros, nonMacroLines)
  }

  private def applyMacros(nonMacroLines: ListBuffer[String], macros: mutable.HashMap[String, AsmMacro]) = {
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
          argsMap.addOne("__NAME__" -> m.get.name)

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
    productLines
  }

  private def preprocessMacros(includedLines: ListBuffer[String]): ListBuffer[String] = {
    val (macros, nonMacroLines) = extractMacros(includedLines)
    val productLines = applyMacros(nonMacroLines, macros)

    productLines.zipWithIndex.map(
      l =>
        l._1.replaceAll("__LINE__", l._2.toString)
    )
  }

  def preprocess(lines: String): Seq[String] = {

    val split = lines.split("(\n|\r\n)")

    val includedLines = preprocessInclude(split)

    val macrodLines = preprocessMacros(includedLines)

    macrodLines.toSeq.map {
      // eliminate empty comments as the wrap lines and cause havoc
      l => l.replaceFirst(";\\s*$", "")
    }
  }
}