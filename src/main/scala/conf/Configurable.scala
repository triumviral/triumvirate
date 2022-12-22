package conf

import scala.reflect.ClassTag

trait Configurable[Cfg]:
  val cfg: ClassTag[Cfg]
