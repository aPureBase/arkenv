package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.parse
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class PropertyFeature(
    private val file: String,
    locations: Collection<String> = listOf()
) : ArkenvFeature, Arkenv() {

    private val defaultLocations = listOf("", "config/")
    private val locations: Collection<String> by argument("--arkenv-property-location") {
        val combined = locations + defaultLocations
        mapping = { it.split(',') + combined }
        defaultValue = { combined }
    }

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
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

    private fun getStream(name: String): InputStream? {
        locations
            .map { fixLocation(it) + name }
            .forEach {
                val stream = getFileStream(it) ?: getResourceStream(it)
                if (stream != null) return stream
            }
        return null // throw exception ??
    }

    private fun fixLocation(location: String) = // TODO test this
        if (location.isNotBlank() && !location.endsWith('/')) "$location/"
        else location

    private fun getFileStream(name: String): FileInputStream? =
        File(name).takeIf { it.exists() }?.inputStream()

    private fun getResourceStream(name: String): InputStream? =
        Arkenv::class.java.classLoader.getResourceAsStream(name)
}
