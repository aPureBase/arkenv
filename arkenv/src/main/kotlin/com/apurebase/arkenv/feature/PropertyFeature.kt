package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

/**
 * Loads configuration from property files.
 */
open class PropertyFeature(
    protected val file: String = "application.properties",
    locations: Collection<String> = listOf()
) : ArkenvFeature, Arkenv("PropertyFeature") {

    private val defaultLocations = listOf("", "config/")
    private val locations: Collection<String> by argument("--arkenv-property-location") {
        val combined = locations + defaultLocations
        mapping = { it.split(',') + combined }
        defaultValue = { combined }
    }

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
        loadProperties(file)?.let(arkenv::putAll)
    }

    private fun loadProperties(file: String): Map<String, String>? = getStream(file)?.use(::parse)

    protected open fun parse(stream: InputStream): Map<String, String> = parseProperties(stream)

    private fun getStream(name: String): InputStream? {
        locations
            .map { fixLocation(it) + name }
            .forEach {
                val stream = getFileStream(it) ?: getResourceStream(it)
                if (stream != null) return stream
            }
        return null
    }

    private fun fixLocation(location: String) = // TODO test this
        if (location.isNotBlank() && !location.endsWith('/')) "$location/"
        else location

    private fun getFileStream(name: String): FileInputStream? = File(name).takeIf(File::exists)?.inputStream()

    private fun getResourceStream(name: String): InputStream? = Arkenv::class.java.classLoader.getResourceAsStream(name)

    companion object {
        internal fun parseProperties(stream: InputStream): Map<String, String> =
            Properties()
                .apply { load(stream) }
                .map { (key, value) -> key.toString().toSnakeCase() to value.toString() }
                .toMap()
    }
}
