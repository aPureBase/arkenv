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
            val replaceWith = arkenv.delegates
                .find { del -> del.argument.names.any { it.toSnakeCase() == placeholder } }
                ?.value?.toString()
                    ?: throw IllegalArgumentException("Cannot find value for placeholder $placeholder")
            return final.replaceRange(i, start + 1, replaceWith)
        } else start++
    }
    return final
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
