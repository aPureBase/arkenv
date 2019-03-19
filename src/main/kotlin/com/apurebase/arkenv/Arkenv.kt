package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.CliFeature
import com.apurebase.arkenv.feature.EnvironmentVariableFeature

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 */
abstract class Arkenv {

    var programName: String = "Arkenv"
    internal val features: MutableMap<String, ArkenvFeature> = mutableMapOf()
    internal val argList = mutableListOf<String>()
    internal val delegates = mutableListOf<ArgumentDelegate<*>>()
    val keyValue = mutableMapOf<String, String>()
    val help: Boolean by ArkenvDelegateLoader(listOf("-h", "--help"), false, { isHelp = true }, Boolean::class, this)

    init {
        install(CliFeature())
        install(EnvironmentVariableFeature())
    }

    /**
     * Parses the [args] and resets all previously parsed state.
     */
    fun parseArguments(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
        onParse(args)

        keyValue.clear()
        features.values.forEach { it.onLoad(this) }
        parse()
        parseBooleanMerge()
    }

    open fun onParse(args: Array<String>) {}

    open fun onParseArgument(name: String, argument: Argument<*>, value: Any?) {}

    override fun toString(): String = StringBuilder().also { sb ->
        val indent = "    "
        val doubleIndent = indent + indent
        sb.append("$programName: \n")
        delegates.forEach { delegate ->
            sb
                .append(indent)
                .append(delegate.argument.names)
                .append(doubleIndent)
                .append(delegate.argument.description)
                .appendln()
                .append(doubleIndent)
                .append(delegate.property.name)
                .append(doubleIndent)
                .append(delegate.getValue(isParse = false))
                .appendln()
        }
    }.toString()

    private fun parse() = delegates
        .sortedBy { it.argument.isMainArg }
        .forEach {
            features.values.forEach { feature ->
                feature.configure(it.argument)
            }
            it.reset()
            val value = it.getValue(isParse = true)
            onParseArgument(it.property.name, it.argument, value)
        }

    private fun parseBooleanMerge() =
        checkRemaining(delegates, argList).forEach { (arg, delegates) ->
            argList.remove("-$arg")
            delegates.forEach { it.setTrue() }
        }

    private fun ArgumentDelegate<*>.getValue(isParse: Boolean): Any? =
        getValue(this, property).also { value ->
            if (isParse && index != null) removeArgumentFromList(index!!, value)
        }

    private fun ArgumentDelegate<*>.removeArgumentFromList(index: Int, value: Any?) {
        removeValueArgument(index, isBoolean, value, isDefault)
        removeNameArgument(index, argument.isMainArg)
    }

    private fun removeNameArgument(index: Int, isMainArg: Boolean) {
        if (index > -1 && !isMainArg) argList.removeAt(index)
    }

    private fun removeValueArgument(
        index: Int, isBoolean: Boolean, value: Any?, default: Boolean
    ) {
        if (!isBoolean && !default && value != null) argList.removeAt(index + 1)
    }
}
