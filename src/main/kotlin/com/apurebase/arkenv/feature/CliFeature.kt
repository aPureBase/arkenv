package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import com.apurebase.arkenv.mapRelaxed
import kotlin.collections.set

class CliFeature : ArkenvFeature {

    private val args = mutableListOf<String>()

    override fun onLoad(arkenv: Arkenv) {
        arkenv.argList.replaceAll(String::mapRelaxed)
        loadCliAssignments(arkenv)
        args.clear()
        args.addAll(parseArguments(arkenv))
    }

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? {
        return findIndex(delegate.argument, args)?.let {
            val value = parseCli(it) ?: if (delegate.isBoolean) parseCli(it - 1) else null
            delegate.removeArgumentFromList(it, value)
            value
        }
    }

    /**
     * Responsible for loading arguments that use the assignment syntax, e.g. key=value
     */
    private fun loadCliAssignments(arkenv: Arkenv) {
        val names = arkenv.delegates.flatMap { it.argument.names }.map { it.trimStart('-') }
        var i = 0
        while (i < arkenv.argList.size) {
            val value = arkenv.argList[i]
            val spl = value.split('=')
            val key = spl.first().toSnakeCase()
            if (spl.size == 2 && names.contains(key)) {
                arkenv.argList.removeAt(i)
                arkenv.keyValue[key] = spl.getOrNull(1) ?: ""
            } else i++
        }
    }

    private fun parseCli(index: Int): String? = args.getOrNull(index + 1)

    private fun parseArguments(arkenv: Arkenv): List<String> {
        val list = mutableListOf<String>()
        var isReading = false
        arkenv.argList.forEach {
            when {
                isReading -> list[list.lastIndex] = "${list.last()} $it"
                else -> list.add(it)
            }
            when {
                isReading && it.endsWith(allowedSurroundings) -> {
                    list[list.lastIndex] = list.last().removeSurrounding(allowedSurroundings)
                    isReading = false
                }
                !isReading && it.startsWith(allowedSurroundings) -> isReading = true
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

    private fun ArgumentDelegate<*>.removeArgumentFromList(index: Int, value: Any?) {
        removeValueArgument(index, isBoolean, value, isDefault)
        removeNameArgument(index, argument.isMainArg)
    }

    private fun removeNameArgument(index: Int, isMainArg: Boolean) {
        if (index > -1 && !isMainArg) args.removeAt(index)
    }

    private fun removeValueArgument(index: Int, isBoolean: Boolean, value: Any?, default: Boolean) {
        if (!isBoolean && !default && value != null) args.removeAt(index + 1)
    }

    private val allowedSurroundings = listOf("'", "\"")
}
