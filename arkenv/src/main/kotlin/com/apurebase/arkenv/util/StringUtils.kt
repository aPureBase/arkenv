package com.apurebase.arkenv.util

internal fun String.toSnakeCase() = this
    .replace("([a-z])([A-Z]+)".toRegex(), "$1_$2")
    .replace(".", "_").replace("-", "_")
    .toUpperCase()
    .removePrefixes("_")

private fun String.removePrefixes(prefix: CharSequence): String = this
    .removePrefix(prefix)
    .let {
        if (it.startsWith(prefix)) it.removePrefixes(prefix)
        else it
    }

internal fun String.isAdvancedName() = startsWith("--")

internal fun String.isSimpleName() = startsWith("-") && !isAdvancedName()

/**
 * Append [char] to the end of this, unless it already ends with [char] or is empty.
 */
internal fun String.ensureEndsWith(char: Char): String =
    if (endsWith(char) || isBlank()) this else this + char

internal fun String.mapRelaxed(): String =
    if (isAdvancedName()) "--" + toSnakeCase()
    else this

internal fun String.split() = split(',')
