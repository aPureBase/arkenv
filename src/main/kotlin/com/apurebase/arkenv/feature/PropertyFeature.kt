package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class PropertyFeature(private val file: String) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        loadProperties(file, arkenv)
    }

    private fun loadProperties(file: String, arkenv: Arkenv) {
        parseProperties(file).let(arkenv.dotEnv::putAll)
    }

    private fun parseProperties(propertiesFile: String): Map<String, String> =
        Properties()
            .apply { getStream(propertiesFile).use(::load) }
            .map { (key, value) -> key.toString().toUpperCase() to value.toString() }
            .toMap()

    private fun getStream(name: String) =
        getFileStream(name) ?: getResourceStream(name)

    private fun getFileStream(name: String): FileInputStream? =
        File(name).takeIf { it.exists() }?.inputStream()

    private fun getResourceStream(name: String): InputStream? =
        Arkenv::class.java.classLoader.getResourceAsStream(name)
}
