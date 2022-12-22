package conf

import scala.reflect.ClassTag

trait Configurable[Cfg]:
  val cfg: ClassTag[Cfg]

  override def toString: String = this.getClass.getPackageName
