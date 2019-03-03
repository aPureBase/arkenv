package com.apurebase.arkenv

import java.io.File
import java.util.*

interface ArkenvLoader {

    fun load(arkenv: Arkenv)

}

class EnvironmentVariableLoader : ArkenvLoader {
    override fun load(arkenv: Arkenv) {
        parseDotEnv(arkenv.dotEnvFilePath).let(arkenv.dotEnv::putAll)
    }

    private fun parseDotEnv(path: String?): Map<String, String> = when (path) {
        null -> mapOf()
        else -> File(path).useLines { lines ->
            lines.map(String::trimStart)
                .filterNot { it.isBlank() || it.startsWith("#") }
                .map { it.split("=") }
                .associate { it[0].trimEnd() to it[1].substringBefore('#').trim() }
        }
    }
}

class PropertiesLoader : ArkenvLoader {
    override fun load(arkenv: Arkenv) {
        parseProperties(arkenv.propertiesFile).let(arkenv.dotEnv::putAll)
    }

    private fun parseProperties(propertiesFile: String?): Map<String, String> = when {
        propertiesFile != null -> Properties()
            .apply {
                Arkenv::class.java.classLoader
                    .getResourceAsStream(propertiesFile)
                    .use(::load)
            }
            .map { (key, value) -> key.toString().toUpperCase() to value.toString() }
            .toMap()
        else -> mapOf()
    }
}
