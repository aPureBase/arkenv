package com.apurebase.arkenv

import java.io.File

internal fun getEnvValue(argument: Argument<*>, enableEnvSecrets: Boolean): String? {
    // If an envVariable is defined we'll pick this as highest order value
    argument.envVariable?.let {
        val definedEnvValue = getEnv(it, enableEnvSecrets)
        if (!definedEnvValue.isNullOrEmpty()) return definedEnvValue
    }

    // Loop over all argument names and pick the first one that matches
    return argument.names
        .filter(String::isAdvancedName)
        .map { argument.envPrefix + it.toSnakeCase() }
        .mapNotNull { getEnv(it, enableEnvSecrets) }
        .firstOrNull()
}

private fun getEnv(name: String, enableEnvSecrets: Boolean) =
    System.getenv(name) ?: getEnvSecret(name, enableEnvSecrets)

private fun getEnvSecret(lookup: String, enableEnvSecrets: Boolean): String? = when {
    enableEnvSecrets -> System.getenv("${lookup}_FILE")?.let(::File)?.readText()
    else -> null
}
