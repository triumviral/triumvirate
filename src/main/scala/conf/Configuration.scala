package conf

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.ClassTagExtensions
import com.typesafe.config.Config

object Configuration:

class Configuration(private val hocon: Config,
                    private val json: JsonMapper & ClassTagExtensions,
                   ):
