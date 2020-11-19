import scala.collection.mutable

trait Knowing {
  val labels = mutable.Map.empty[String, Know]

  def remember(name: String, v: Int): Known = {
    labels.get(name).map(e => sys.error(s"symbol '${name}' has already defined as ${e} can't assign new value ${v}"))
    labels(name) = Known(name, v)
    Known(name, v)
  }

  def remember[T <: Know](name: String, k: T): T = {
    labels.get(name).map(e => sys.error(s"symbol '${name}' has already defined as ${e} can't assign new value ${k}"))
    labels(name) = k
    k
  }

  def knowable(name: String): Know = {
    val maybeKnow = labels.get(name)
    maybeKnow.getOrElse(Knowable(name, () => recall(name)))
  }

  def recall(name: String): Option[Int] = {
    val maybeKnow: Option[Know] = labels.get(name)
    maybeKnow match {
      case Some(Known(n, v)) => Some(v)
      case _ => None
    }
  }

  sealed trait Know {
    def eval: Know // none - means unknowable
    def getVal: Option[Int]
  }

  case class Knowable(name: String, a: () => Option[Int]) extends Know {
    def eval: Know = {
      a().map(v => Known(name, v)).getOrElse(Unknown(name))
    }

    def getVal = a()

    override def toString(): String = {
      s"""${a().map(v => v.toString).getOrElse(s"unknown{${name}")})"""
    }
  }

  case class Known(name: String, knownVal: Int) extends Know {
    def eval = {
      this
    }

    def getVal = Some(knownVal)

    override def toString(): String = s"""${knownVal}${if (name.length > 0) "{" + name + "}" else ""}"""
  }

  case class Irrelevant() extends Know {
    def eval = {
      this
    }

    def getVal = Some(0)

    override def toString(): String = "Irrelevant"
  }

  case class Unknown(name: String) extends Know {
    def eval = this

    override def getVal = None

  }

  case class UniKnowable(a: () => Know, op: Int => Int, name: String) extends Know {
    def eval: Know = {
      val eval1 = a().eval
      eval1 match {
        case Known(name, v) =>
          Known(name, op(v))
        case u =>
          u
      }
    }

    def getVal: Option[Int] = a().getVal match {
      case Some(v) =>
        Some(op(v))
      case None =>
        None
    }

    override def toString(): String = {
      val value = s"( ${name} ${a()} )"
      value
    }
  }

  case class BiKnowable(a: () => Know, b: () => Know, op: (Int, Int) => Int, name: String) extends Know {
    def eval: Know = {
      val value = (a().eval, b().eval)
      value match {
        case (Known(a, av), Known(b, bv)) =>
          Known("{" + a + "," + b + "}", op(av, bv))
        case (u, Known(_, _)) =>
          u
        case (Known(_, _), u) =>
          u
       case _ =>
          sys.error("UNMATCHED " + value )
      }
    }

    def getVal = (a().getVal, b().getVal) match {
      case (Some(av), Some(bv)) =>
        Some(op(av, bv))
      case _ =>
        None
    }

    override def toString(): String = {
      s"( ${a()} ${name} ${b()} )"
    }
  }

}