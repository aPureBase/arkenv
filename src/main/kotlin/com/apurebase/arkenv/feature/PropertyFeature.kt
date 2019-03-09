package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import java.util.*

class PropertyFeature(private val file: String) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        loadProperties(file, arkenv)
    }

    private fun loadProperties(file: String, arkenv: Arkenv) = parseProperties(file).let(arkenv.dotEnv::putAll)

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
