package conf

import scala.reflect.{ClassTag, classTag}

trait Configurable[Cfg]:
  val cfg: ClassTag[Cfg]

  override def toString: String = this.getClass.getPackageName

object Configurable:
  transparent inline given derived[Cfg: ClassTag]: Configurable[Cfg] =
    new Configurable[Cfg]:
      val cfg: ClassTag[Cfg] = classTag[Cfg]
