package com.apurebase.arkenv.feature

import com.apurebase.arkenv.ArgumentDelegate
import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.feature.EnvironmentVariableFeature.Companion.getEnv
import com.apurebase.arkenv.toSnakeCase

/**
 * Evaluates placeholders in parsed configuration data.
 * @since 2.1.0
 */
internal class PlaceholderParser : ProcessorFeature {

    override lateinit var arkenv: Arkenv

    override fun process(key: String, value: String): String {
        return parsePlaceholders(value)
    }

    private fun parsePlaceholders(value: String): String {
        var i = 0
        var final = value
        while (i < final.length - 2) {
            if (final[i] == '$' && final[i + 1] == '{') {
                final = findEndAndReplace(i, final)
            }
            i++
        }
        return final
    }

    private fun findEndAndReplace(i: Int, final: String): String {
        var start = i + 2
        while (start < final.length) {
            if (final[start] == '}') {
                val placeholder = final.substring(i + 2, start).toUpperCase()
                val replaceWith = findPlaceholderReplacement(placeholder)
                return final.replaceRange(i, start + 1, replaceWith)
            } else start++
        }
        return final
    }

    private fun findPlaceholderReplacement(placeholder: String): String =
        findReplacementInDelegates(arkenv.delegates, placeholder)
                ?: arkenv.getOrNull(placeholder)
                ?: getEnv(placeholder, false)
                ?: findReplacementInArgs(arkenv.argList, placeholder)
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
}
