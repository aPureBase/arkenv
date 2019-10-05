package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.*
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.mapRelaxed
import kotlin.collections.set

/**
 * Provides command line argument support.
 * Loads and parses the arguments that were passed to Arkenv via the parse function.
 */
class CliFeature : ArkenvFeature {

    private var args = mutableListOf<String>()

    override fun onLoad(arkenv: Arkenv) {
        args = arkenv.argList
        args.replaceAll(String::mapRelaxed)
        loadCliAssignments().let(arkenv::putAll)
        val parsed = parseArguments(args)
        args.clear()
        args.addAll(parsed)
    }

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? =
        findIndex(delegate.argument, arkenv.argList)?.let {
            val value = parseCli(it) ?: if (delegate.isBoolean) parseCli(it - 1) else null
            removeArgumentFromList(delegate, it, value)
            value
        }

    override fun finally(arkenv: Arkenv) {
        BooleanMergeParser().checkRemaining(arkenv, args).forEach { (arg, boolDelegates) ->
            args.remove("-$arg")
            boolDelegates.forEach { it.setTrue() }
        }
    }

    /**
     * Responsible for loading arguments that use the assignment syntax, e.g. key=value
     */
    private fun loadCliAssignments(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        var i = 0
        while (i < args.size) {
            val value = args[i]
            val split = value.split('=')
            val key = split.first().toSnakeCase()
            if (split.size == 2) {
                args.removeAt(i)
                map[key] = split.getOrNull(1) ?: ""
            } else if (split.size == 1 && i < args.size - 1) {
                val nextValue = args[i + 1]
                if (!nextValue.startsWith('-')) map[key] = args[i + 1] //args.removeAt(i)
                i++
            } else i++
        }
        return map
    }

    private fun parseCli(index: Int): String? = args.getOrNull(index + 1)

    private fun parseArguments(arguments: List<String>): List<String> {
        val list = mutableListOf<String>()
        var isReading = false
        arguments.forEach { value ->
            when {
                isReading -> list[list.lastIndex] = "${list.last()} $value"
                else -> list.add(value)
            }
            when {
                isReading && value.endsWith(allowedSurroundings) -> {
                    list[list.lastIndex] = list.last().removeSurrounding(allowedSurroundings)
                    isReading = false
                }
                !isReading && value.startsWith(allowedSurroundings) -> isReading = true
            }
        }
        return list
    }

    private fun findIndex(argument: Argument<*>, arguments: List<String>): Int? = when {
        argument.isMainArg -> arguments.size - 2
        else -> argument
            .names
            .map(arguments::indexOf)
            .find { it >= 0 }
    }

    private fun removeArgumentFromList(delegate: ArgumentDelegate<*>, index: Int, value: Any?) {
        removeValueArgument(index, delegate.isBoolean, value, delegate.isDefault)
        removeNameArgument(index, delegate.argument.isMainArg)
    }

    private fun removeNameArgument(index: Int, isMainArg: Boolean) {
        if (index > -1 && !isMainArg) args.removeAt(index)
    }

    private fun removeValueArgument(index: Int, isBoolean: Boolean, value: Any?, default: Boolean) {
        if (!isBoolean && !default && value != null) args.removeAt(index + 1)
    }

    private val allowedSurroundings = listOf("'", "\"")
}
