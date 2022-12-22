package conf

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.typesafe.config.{Config, ConfigRenderOptions}

object Configuration:
  private val options: ConfigRenderOptions = ConfigRenderOptions.concise()

class Configuration(private val hocon: Config,
                    private val json: JsonMapper & ClassTagExtensions,
                   ):

  import Configuration.*
