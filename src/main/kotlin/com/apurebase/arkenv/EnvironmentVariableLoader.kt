package com.apurebase.arkenv

import java.io.File

fun loadEnvironmentVariables(arkenv: Arkenv) {
    parseDotEnv(arkenv.dotEnvFilePath).let(arkenv.dotEnv::putAll)
}

private fun parseDotEnv(path: String?): Map<String, String> = when (path) {
    null -> mapOf()
    else -> File(path).useLines { lines ->
        lines.map(String::trimStart)
            .filterNot { it.isBlank() || it.startsWith("#") }
            .map { it.split("=") }
            .associate { it[0].trimEnd() to it[1].substringBefore('#').trim() }
    }
}
