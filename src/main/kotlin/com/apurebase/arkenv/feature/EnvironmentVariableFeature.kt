package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import java.io.File

/**
 * @property envPrefix a common prefix for all environment variables
 * @property enableEnvSecrets whether to enable docker secrets parsing. Will attempt to parse any environment variable
 * @property dotEnvFilePath location of the dot env file to read variables from
 */
class EnvironmentVariableFeature(
    private val envPrefix: String = "",
    private val enableEnvSecrets: Boolean = false,
    private val dotEnvFilePath: String? = null
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) = loadEnvironmentVariables(arkenv)

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? =
        parseEnvironmentVariables(arkenv, delegate, enableEnvSecrets)

    override fun configure(argument: Argument<*>) {
        argument.withEnv = true
        argument.envPrefix = envPrefix
    }

    private fun parseEnvironmentVariables(
        arkenv: Arkenv,
        delegate: ArgumentDelegate<*>,
        enableEnvSecrets: Boolean
    ): String? = with(delegate) {
        if (argument.withEnv) getEnvValue(argument, arkenv.keyValue, enableEnvSecrets) else null
    }

    private fun getEnvValue(argument: Argument<*>, dotEnv: Map<String, String>, enableEnvSecrets: Boolean): String? {
        // If an envVariable is defined we'll pick this as highest order value
        argument.envVariable?.let {
            val definedEnvValue = getEnv(it, dotEnv, enableEnvSecrets)
            if (!definedEnvValue.isNullOrEmpty()) return definedEnvValue
        }

        // Loop over all argument names and pick the first one that matches
        return argument.names
            .filter(String::isAdvancedName)
            .map { argument.envPrefix + it.toSnakeCase() }
            .mapNotNull { getEnv(it, dotEnv, enableEnvSecrets) }
            .firstOrNull()
    }

    private fun getEnv(name: String, dotEnv: Map<String, String>, enableEnvSecrets: Boolean) =
        System.getenv(name) ?: dotEnv[name] ?: getEnvSecret(name, enableEnvSecrets)

    private fun getEnvSecret(lookup: String, enableEnvSecrets: Boolean): String? = when {
        enableEnvSecrets -> System.getenv("${lookup}_FILE")?.let(::File)?.readText()
        else -> null
    }

    private fun loadEnvironmentVariables(arkenv: Arkenv) {
        if (dotEnvFilePath != null) {
            parseDotEnv(dotEnvFilePath).let(arkenv.keyValue::putAll)
        }
    }

    private fun parseDotEnv(path: String): Map<String, String> =
        File(path).useLines { lines ->
            lines.map(String::trimStart)
                .filterNot { it.isBlank() || it.startsWith("#") }
                .map { it.split("=") }
                .associate { it[0].trimEnd() to it[1].substringBefore('#').trim() }
        }
}
