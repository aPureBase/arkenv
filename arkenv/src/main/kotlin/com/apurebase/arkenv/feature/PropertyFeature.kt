package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.putAll
import com.apurebase.arkenv.split
import com.apurebase.arkenv.toSnakeCase
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

/**
 * Loads configuration from property files.
 * @param file the name of the file to load from
 * @param locations a collection of locations in which to look for the file
 */
open class PropertyFeature(
    private val file: String = "application",
    locations: Collection<String> = listOf()
) : ArkenvFeature {

    protected open val extensions = listOf("properties")
    private val defaultLocations = locations + listOf("", "config/")
    private var extraLocations: List<String> = emptyList()

    override fun onLoad(arkenv: Arkenv) {
        extraLocations = arkenv.getOrNull("--arkenv-property-location")?.split() ?: extraLocations
        val fileNames = if (extensions.any { file.endsWith(".$it") }) listOf(file)
        else extensions.map(::makeFileName)

        fileNames
            .mapNotNull(::loadProperties)
            .firstOrNull()
            ?.let(arkenv::putAll)
    }

    private fun makeFileName(extension: String): String = "$file.$extension"

    private fun loadProperties(file: String): Map<String, String>? = getStream(file)?.use(::parse)

    protected open fun parse(stream: InputStream): Map<String, String> = parseProperties(stream)

    private fun getStream(name: String): InputStream? {
        (extraLocations + defaultLocations)
            .map { fixLocation(it) + name }
            .forEach {
                val stream = getFileStream(it) ?: getResourceStream(it)
                if (stream != null) return stream
            }
        return null
    }

    private fun fixLocation(location: String) =
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
