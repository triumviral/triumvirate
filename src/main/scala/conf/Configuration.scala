package conf

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions}

import scala.reflect.ClassTag

object Configuration:
  def apply(): Configuration = new Configuration(
    hocon = ConfigFactory
      .defaultApplication()
      .getConfig(config),
    json = JsonMapper
      .builder()
      .addModule(DefaultScalaModule)
      .build() :: ClassTagExtensions,
  )

  given[Cfg: Configurable]: Conversion[Configuration, Cfg] with
    def apply(configuration: Configuration): Cfg =
      configuration.configure[Cfg]

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
