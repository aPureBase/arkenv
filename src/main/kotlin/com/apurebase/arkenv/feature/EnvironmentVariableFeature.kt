package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import java.io.File

/**
 * Provides environment variable support.
 * Loads and parses environment variables that are declared by arguments.
 * @property envPrefix a common prefix for all environment variables
 * @property enableEnvSecrets whether to enable docker secrets parsing. Will attempt to parse any environment variable
 * @property dotEnvFilePath location of the dot env file to read variables from
 */
class EnvironmentVariableFeature(
    private val envPrefix: String? = null,
    private val enableEnvSecrets: Boolean = false,
    private val dotEnvFilePath: String? = null
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) = loadEnvironmentVariables(arkenv.keyValue)

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? =
        parseEnvironmentVariables(delegate, enableEnvSecrets)

    override fun configure(argument: Argument<*>) {
        argument.withEnv = true
        argument.envPrefix = envPrefix
    }

    private fun parseEnvironmentVariables(
        delegate: ArgumentDelegate<*>,
        enableEnvSecrets: Boolean
    ): String? = with(delegate) {
        if (argument.withEnv) getEnvValue(argument, enableEnvSecrets) else null
    }

    private fun getEnvValue(argument: Argument<*>, enableEnvSecrets: Boolean): String? {
        // If an envVariable is defined we'll pick this as highest order value
        argument.envVariable?.let {
            val definedEnvValue = getEnv(it, enableEnvSecrets)
            if (!definedEnvValue.isNullOrEmpty()) return definedEnvValue
        }

        // Loop over all argument names and pick the first one that matches
        return argument.names
            .asSequence()
            .filter(String::isAdvancedName)
            .map { parseArgumentName(argument, it) }
            .mapNotNull { getEnv(it, enableEnvSecrets) }
            .firstOrNull()
    }

    private fun parseArgumentName(argument: Argument<*>, name: String): String =
        (argument.envPrefix?.toSnakeCase()?.ensureEndsWith('_') ?: "") + name.toSnakeCase()

    private fun loadEnvironmentVariables(keyValue: MutableMap<String, String>) {
        if (dotEnvFilePath != null) {
            parseDotEnv(dotEnvFilePath).let(keyValue::putAll)
        }
    }

    companion object {
        internal fun getEnv(name: String, enableEnvSecrets: Boolean): String? =
            System.getenv(name)
                    ?: getEnvSecret(name, enableEnvSecrets)
                    ?: getKebabCase(name)
                    ?: getCamelCase(name)

        private fun getKebabCase(name: String) = System.getenv(name.replace('_', '-').toLowerCase())

        private fun getCamelCase(name: String): String? = System.getenv(
            name.toLowerCase().split('_').joinToString("") { it.capitalize() }.decapitalize()
        )

        private fun getEnvSecret(lookup: String, enableEnvSecrets: Boolean): String? = when {
            enableEnvSecrets -> System.getenv("${lookup}_FILE")?.let(::File)?.readText()
            else -> null
        }

        private fun parseDotEnv(path: String): Map<String, String> = File(path).useLines { lines ->
            lines.map(String::trimStart)
                .filterNot { it.isBlank() || it.startsWith("#") }
                .map { it.split("=") }
                .associate { it[0].trimEnd() to it[1].substringBefore('#').trim() }
        }
    }
}
