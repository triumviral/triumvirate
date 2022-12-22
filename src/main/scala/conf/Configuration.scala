package conf

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.typesafe.config.{Config, ConfigRenderOptions}

import scala.reflect.ClassTag

object Configuration:
  private val options: ConfigRenderOptions = ConfigRenderOptions.concise()

class Configuration(private val hocon: Config,
                    private val json: JsonMapper & ClassTagExtensions,
                   ):

  import Configuration.*

  def configure[Cfg](using configured: Configurable[Cfg]): Cfg =
    given ClassTag[Cfg] = configured.cfg

    json.readValue[Cfg]:
      hocon
        .getObject(configured.toString)
        .render(options)
