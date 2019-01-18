package com.apurebase.arkenv

import java.io.File

internal fun getEnvValue(argument: Argument<*>, enableEnvSecrets: Boolean): String? {
    // If an envVariable is defined we'll pick this as highest order value
    argument.envVariable?.let {
        val definedEnvValue = System.getenv(it) ?: getEnvSecret(it, enableEnvSecrets)
        if (!definedEnvValue.isNullOrEmpty()) return definedEnvValue
    }

    // Loop over all argument names and pick the first one that matches
    return argument.names
        .filter(String::isAdvancedName)
        .mapNotNull {
            val lookup = argument.envPrefix + it.toSnakeCase()
            System.getenv(lookup) ?: getEnvSecret(lookup, enableEnvSecrets)
        }
        .firstOrNull()
}

private fun getEnvSecret(lookup: String, enableEnvSecrets: Boolean): String? = when {
    enableEnvSecrets -> System.getenv("${lookup}_FILE")?.let(::File)?.readText()
    else -> null
}
