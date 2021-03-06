package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import com.apurebase.arkenv.argument.Argument
import com.apurebase.arkenv.argument.ArkenvArgument
import com.apurebase.arkenv.util.ensureEndsWith
import com.apurebase.arkenv.util.isAdvancedName
import com.apurebase.arkenv.util.putAll
import com.apurebase.arkenv.util.toSnakeCase
import java.io.File

/**
 * Provides environment variable support.
 * Loads and parses environment variables that are declared by arguments.
 * @param envPrefix a common prefix for all environment variables.
 * Can be set via the *ARKENV_ENV_PREFIX* argument.
 * @param enableEnvSecrets whether to enable docker secrets parsing. Will attempt to parse any environment variable.
 * Can be set via the *ARKENV_ENV_SECRETS* argument.
 * @param dotEnvFilePath location of the dot env file to read variables from. Defaults to *.env*
 * Can be set via the *ARKENV_DOT_ENV_FILE* argument.
 */
class EnvironmentVariableFeature(
    private val envPrefix: String? = null,
    private val enableEnvSecrets: Boolean = false,
    private val dotEnvFilePath: String? = null
) : ArkenvFeature {

    private var isLoaded = false

    override fun onLoad(arkenv: Arkenv) {
        val loadedEnvVars = loadEnvironmentVariables(arkenv.getOrNull("ARKENV_DOT_ENV_FILE"))
        loadedEnvVars?.let(arkenv::putAll)
        isLoaded = loadedEnvVars != null // reset in case of re-parse
    }

    override fun onParse(arkenv: Arkenv, delegate: ArkenvArgument<*>): String? = with(delegate) {
        val envSecrets = enableEnvSecrets || arkenv.getOrNull("ARKENV_ENV_SECRETS") != null
        val setEnvPrefix = envPrefix ?: arkenv.getOrNull("ARKENV_ENV_PREFIX") ?: ""
        getEnvValue(argument, envSecrets, setEnvPrefix)
    }

    override fun postLoad(arkenv: Arkenv) {
        if (!isLoaded) onLoad(arkenv)
    }

    /**
     * Loop over all argument names and pick the first one that matches
     */
    private fun getEnvValue(argument: Argument<*>, enableEnvSecrets: Boolean, prefix: String): String? =
        argument.names
            .asSequence()
            .filter(String::isAdvancedName)
            .map { parseArgumentName(it, prefix) }
            .mapNotNull { getEnv(it, enableEnvSecrets) }
            .firstOrNull()

    private fun parseArgumentName(name: String, prefix: String): String =
        prefix.toSnakeCase().ensureEndsWith('_') + name.toSnakeCase()

    private fun loadEnvironmentVariables(parsedDotEnvFilePath: String?): Map<String, String>? =
        (parsedDotEnvFilePath ?: dotEnvFilePath)?.let(::parseDotEnv)

    private fun parseDotEnv(path: String): Map<String, String> =
        File(path)
            .inputStream()
            .use(PropertyFeature.Companion::parseProperties)

    internal fun getEnv(name: String, enableEnvSecrets: Boolean): String? =
        System.getenv(name)
                ?: getEnvSecret(name, enableEnvSecrets)
                ?: getKebabCase(name)
                ?: getCamelCase(name)

    private fun getKebabCase(name: String) = System.getenv(name.replace('_', '-').toLowerCase())

    private fun getCamelCase(name: String): String? = System.getenv(
        name.toLowerCase().split('_').joinToString("", transform = String::capitalize).decapitalize()
    )

    private fun getEnvSecret(lookup: String, enableEnvSecrets: Boolean): String? = when {
        enableEnvSecrets -> System.getenv("${lookup}_FILE")?.let(::File)?.readText()
        else -> null
    }
}
