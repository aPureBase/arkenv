package com.apurebase.arkenv

import java.io.File

internal fun parseDotEnv(path: String?): Map<String, String> = when (path) {
    null -> mapOf()
    else -> File(path)
        .readLines()
        .filter(String::isNotBlank)
        .map { it.split("=") }
        .associate { it[0] to it[1] }
}

internal fun getEnvValue(argument: Argument<*>, dotEnv: Map<String, String>, enableEnvSecrets: Boolean): String? {
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
