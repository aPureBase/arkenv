package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.toSnakeCase
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class YamlFeature(
    file: String = "application.yml",
    locations: Collection<String> = listOf()
) : PropertyFeature(file, locations) {

    override fun parse(stream: InputStream): Map<String, String> {
        Yaml().load<Map<String, Any?>>(stream)?.map { (key, value) -> parse(key, value) }
        return keyValue
    }

    override fun finally(arkenv: Arkenv) = clearInput()

    @Suppress("UNCHECKED_CAST")
    private fun parse(key: String, value: Any?) {
        when (value) {
            is Map<*, *> -> (value as? Map<String, Any?>)?.forEach { (k, v) -> parse("${key}_$k", v) }
            else -> keyValue[key.toSnakeCase()] = value.toString()
        }
    }
}
