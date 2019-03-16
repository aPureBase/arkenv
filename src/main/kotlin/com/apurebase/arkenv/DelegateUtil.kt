package com.apurebase.arkenv

import kotlin.reflect.KProperty

internal fun parsePlaceholders(value: String, arkenv: Arkenv): String {
    var i = 0
    var final = value
    while (i < final.length - 2) {
        if (final[i] == '$' && final[i + 1] == '{') {
            final = findEndAndReplace(i, final, arkenv)
        }
        i++
    }
    return final
}

private fun findEndAndReplace(i: Int, final: String, arkenv: Arkenv): String {
    var start = i + 2
    while (start < final.length) {
        if (final[start] == '}') {
            val placeholder = final.substring(i + 2, start).toUpperCase()
            val replaceWith = findPlaceholderReplacement(arkenv, placeholder)
            return final.replaceRange(i, start + 1, replaceWith)
        } else start++
    }
    return final
}

private fun findPlaceholderReplacement(arkenv: Arkenv, placeholder: String): String =
    findReplacementInDelegates(arkenv.delegates, placeholder)
            ?: findReplacementInArgs(arkenv.argList, placeholder)
            ?: findReplacementInKeyValue(arkenv.keyValue, placeholder)
            ?: findReplacementInEnv(placeholder)
            ?: throw IllegalArgumentException("Cannot find value for placeholder $placeholder")

private fun findReplacementInDelegates(delegates: Collection<ArgumentDelegate<*>>, placeholder: String): String? =
    delegates
        .find { del -> del.argument.names.any { it.toSnakeCase() == placeholder } }
        ?.value?.toString()

private fun findReplacementInArgs(args: List<String>, placeholder: String): String? {
    var i = 0
    while (i < args.size - 1) {
        if (args[i].toSnakeCase() == placeholder) {
            return args[i + 1]
        } else i++
    }
    return null
}

private fun findReplacementInKeyValue(keyValue: Map<String, String>, placeholder: String): String? {
    return keyValue[placeholder]
}

private fun findReplacementInEnv(placeholder: String): String? {
    return System.getenv(placeholder)
}

internal fun <T> checkValidation(validation: List<Argument.Validation<T>>, value: T, property: KProperty<*>) =
    validation
        .filterNot { it.assertion(value) }
        .map { it.message }
        .let {
            if (it.isNotEmpty()) it
                .reduce { acc, s -> "$acc. $s" }
                .run { throw ValidationException(property, value, this) }
        }
