package com.apurebase.arkenv

fun String.toSnakeCase() = this
    .replace("([a-z])([A-Z]+)".toRegex(), "$1_$2")
    .replace(".", "_").replace("-", "_")
    .toUpperCase()
    .removePrefixes("_")

fun String.removePrefixes(prefix: CharSequence): String = this
    .removePrefix(prefix)
    .let {
        if (it.startsWith(prefix)) it.removePrefixes(prefix)
        else it
    }
