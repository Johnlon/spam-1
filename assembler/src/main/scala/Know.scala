import scala.collection.mutable
import scala.reflect.{ClassTag, classTag}

trait Knowing {

  sealed trait Know[T <: KnownValue] {
    def eval: Know[T] // none - means unknowable
    def getVal: Option[T]
  }

  sealed trait KnownValue
  case class KnownInt(v: Int) extends KnownValue {
    def toBinaryString: String = v.toBinaryString

    def +(knownInt: KnownInt) = KnownInt(v +  knownInt.v)
    def -(knownInt: KnownInt) = KnownInt(v -  knownInt.v)
    def /(knownInt: KnownInt) = KnownInt(v /  knownInt.v)
    def *(knownInt: KnownInt) = KnownInt(v *  knownInt.v)
    def %(knownInt: KnownInt) = KnownInt(v %  knownInt.v)
    def &(knownInt: KnownInt) = KnownInt(v &  knownInt.v)
    def |(knownInt: KnownInt) = KnownInt(v |  knownInt.v)

    override def toString = v.toString
  }
  case class KnownByteArray(v: Seq[Byte]) extends KnownValue

  val labels = mutable.Map.empty[String, Know[_]]

  def rememberValue[T<:KnownValue: ClassTag](name: String, v: T): Known[T] = {
    labels.get(name).map(e => sys.error(s"symbol '${name}' has already defined as ${e} can't assign new value ${v}"))
    val k = Known(name, v)
    rememberKnown(name,k)
  }

  def rememberKnown[K<:Know[_ <: KnownValue]](name: String, k: K): K = {
    labels.get(name).map(e => sys.error(s"symbol '${name}' has already defined as ${e} can't assign new value ${k}"))
    labels(name) = k
    k
  }

  def forwardReference[T <: KnownValue : ClassTag](name: String): Know[T] = {
    val maybeKnow = labels.get(name)
    maybeKnow match {
      case Some(Known(n, b:T)) => Known(n, b)
      case Some(Known(_, v)) => sys.error(s"asm error : value of ${name} is type ${v.getClass}(=${v.toString}) but require type ${classTag[T].runtimeClass.getClass}" )
      case _ => Knowable[T](name, () => recall[T](name))
    }
//    maybeKnow.getOrElse(Knowable[T](name, () => recall[T](name)))
  }

  def recall[T<:KnownValue: ClassTag](name: String): Option[T] = {
    val maybeKnow: Option[Know[_]] = labels.get(name)
    maybeKnow match {
      case Some(Known(_, v:T)) => Some(v)
      case Some(Known(_, v)) => sys.error(s"asm error : resolved value of ${name} is type ${v.getClass}(=${v.toString}) but require type ${classTag[T].runtimeClass.getClass}" )
      case _ => None
    }
  }

  case class Knowable[T<:KnownValue : ClassTag](name: String, a: () => Option[T]) extends Know[T] {
    def eval: Know[T] = {
      a().map(v => Known(name, v)).getOrElse(Unknown(name))
    }

    def getVal = a()

    override def toString(): String = {
      s"""${a().map(v => v.toString).getOrElse(s"unknown{${name}")})"""
    }
  }

  object Known {
    def apply(name: String, i: Int): Known[KnownInt] = Known(name, KnownInt(i))
    def apply(name: String, b: Seq[Byte]): Known[KnownByteArray] = Known(name, KnownByteArray(b))
  }
  case class Known[T<:KnownValue : ClassTag](name: String, knownVal: T) extends Know[T] {
    type KV = T

    def eval = {
      this
    }

    def getVal = Some(knownVal)

    override def toString(): String = s"""${knownVal}${if (name.length > 0) "{" + name + "}" else ""}"""
  }

  case class Irrelevant() extends Know[KnownInt] {
    def eval = {
      this
    }

    def getVal = Some(KnownInt(0))

    override def toString(): String = "Irrelevant"
  }

  case class Unknown[T<:KnownValue](name: String) extends Know[T] {
    def eval = this

    override def getVal = None
  }

  case class UniKnowable[T<:KnownValue : ClassTag](a: () => Know[T], op: T => T, name: String) extends Know[T] {
    def eval: Know[T] = {
      val eval1 = a().eval
      eval1 match {
        case Known(name, v) =>
          Known(name, op(v))
        case u =>
          u
      }
    }

    def getVal: Option[T] = a().getVal match {
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

  case class BiKnowable[T<:KnownValue : ClassTag, K1<:KnownValue : ClassTag,K2<:KnownValue : ClassTag](a: () => Know[K1], b: () => Know[K2], op: (K1, K2) => T, name: String) extends Know[T] {
    def eval: Know[T] = {
      val value = (a().eval, b().eval)
      value match {
        case (Known(a, av), Known(b, bv)) =>
          Known("{" + a + "," + b + "}", op(av, bv))
        case (Unknown(n), Known(_, _)) =>
          Unknown(n)
        case (Known(_, _), Unknown(n)) =>
          Unknown(n)
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