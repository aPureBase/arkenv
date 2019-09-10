package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

/**
 * Adds support for loading configuration from yaml files.
 * Nested keys will be concatenated using an underscore (_).
 * Arrays will be parsed as a comma-separated string.
 * @param file the name of the file to load from
 * @param locations a collection of locations in which to look for the file
 * @since 2.1.0
 */
class YamlFeature(
    file: String = "application",
    locations: Collection<String> = listOf()
) : PropertyFeature(file, locations) {

    override val extensions: List<String> = listOf("yml", "yaml")

    override fun parse(stream: InputStream): Map<String, String> {
        Yaml().load<Map<String, Any?>>(stream)?.map { (key, value) -> parse(key, value) }
        return getAll()
    }

    override fun finally(arkenv: Arkenv) = clear()

    @Suppress("UNCHECKED_CAST")
    private fun parse(key: String, value: Any?) {
        when (value) {
            is Map<*, *> -> (value as? Map<String, Any?>)?.forEach { (k, v) -> parse("${key}_$k", v) }
            is ArrayList<*> -> this[key] = value.joinToString(",")
            else -> this[key] = value.toString()
        }
    }
}
