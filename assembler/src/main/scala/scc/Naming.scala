package scc

import scala.collection.mutable.ListBuffer

trait Naming {

  self: SpamCC =>

  final val LABEL_NAME_SEPARATOR = "_"

  case class Name private(parent: Name, name: String, endLabel: Option[String] = None, functions: ListBuffer[(Name, Block with IsFunction)] = ListBuffer.empty) {

    def lookupFunction(name: String): Option[(Name, Block with IsFunction)] = {
      val maybeBlock = functions.find(f => f._2.functionName == name)
      maybeBlock.orElse {
        Option(parent).flatMap(_.lookupFunction(name))
      }
    }

    def blockName: String = {
      if (parent != null) {
        parent.blockName + "_" + name
      } else
        name
    }

    def getEndLabel: Option[String] = endLabel.orElse(Option(parent).flatMap(_.getEndLabel))

    override def toString = s"Name(path=$blockName, endLabel=$endLabel)"

    def toFqVarPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "VAR_" + child
    }

    def toFqLabelPath(child: String): String = {
      blockName + (LABEL_NAME_SEPARATOR * 3) + "LABEL_" + child
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnLabelPathUnique(child: String): String = {
      toFqLabelPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
    }

    /* returns a globally unique name that is contextual by ibcluding the block name*/
    def fqnVarPathUnique(child: String): String = {
      toFqVarPath(child) + LABEL_NAME_SEPARATOR + Name.nextInt
    }

    def pushScope(newScopeName: String): Name = {
      if (newScopeName.length > 0) this.copy(parent = this, name = newScopeName)
      else this
    }

    def addFunction(functionScope: Name, newFunction: Block with IsFunction): Unit = {
      val newReg = (functionScope, newFunction)
      functions.append(newReg)
    }

    def assignVarLabel(name: String, typ: VarType, data: List[Byte] = List(0)): Variable = {
      val label = lookupVarLabel(name)

      label.map { existing =>
        if (existing.typ != typ) sys.error(s"cannot redefine '$name' as $typ; it is already defined as a ${existing.typ}' with label ${existing.fqn}")
        sys.error(s"cannot redefine '$name' it is already defined with label ${existing.fqn} with initial value 0x${existing.pos.toHexString}(${existing.pos} dec)")
      }

      label.getOrElse {
        val fqn = toFqVarPath(name)
        val pos = variables.lastOption.map(v => v.pos + v.bytes.length).getOrElse(0)
        val v = Variable(name, fqn, pos, data, typ)
        variables.append(v)
        v
      }
    }

    def lookupVarLabel(name: String): Option[Variable] = {
      val fqn = toFqVarPath(name)
      val maybeExists = lookupVarLabelByFqn(fqn)

      maybeExists match {
        case s@Some(_) => s
        case _ if parent != null => parent.lookupVarLabel(name)
        case _ => None
      }
    }

    def lookupVarLabelByFqn(fqn: String): Option[Variable] = {
      variables.map(l => (l.fqn, l)).toMap.get(fqn)
    }

    def getVarLabel(name: String): Variable = {
      val label = lookupVarLabel(name)
      label.getOrElse {
        val str = s"scc error: $name has not been defined yet @ $this"
        sys.error(str)
      }
    }
    def getVarLabel(name: String, typ: VarType): Variable = {
      val label = lookupVarLabel(name)
      val v = label.getOrElse {
        val str = s"scc error: $name has not been defined yet @ $this"
        sys.error(str)
      }
      if (v.typ!=typ) {
        sys.error(s"cannot locate '$name' as $typ; but found it defined as a ${v.typ}' with label ${v.fqn}")
      }
      v
    }
  }

  object Name {
    lazy val RootName: Name = Name(null, "root")

    private[this] var idx = 0

    def nextInt: Int = {
      idx += 1
      idx
    }
  }
}

