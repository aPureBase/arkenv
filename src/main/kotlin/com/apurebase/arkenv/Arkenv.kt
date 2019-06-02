package com.apurebase.arkenv

import com.apurebase.arkenv.feature.CliFeature
import com.apurebase.arkenv.feature.EnvironmentVariableFeature

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 * @param programName
 * @param configuration
 */
abstract class Arkenv(
    val programName: String = "Arkenv",
    configuration: (ArkenvBuilder.() -> Unit)? = null
) {

    internal val builder = ArkenvBuilder()
    internal val argList = mutableListOf<String>()
    internal val keyValue = mutableMapOf<String, String>()
    internal val delegates = mutableListOf<ArgumentDelegate<*>>()
    val help: Boolean by ArkenvDelegateLoader(listOf("-h", "--help"), false, { isHelp = true }, Boolean::class, this)

    init {
        builder.install(CliFeature())
        builder.install(EnvironmentVariableFeature())
        configuration?.invoke(builder)
    }

    internal fun parseArguments(args: Array<out String>) {
        if (builder.clearInputBeforeParse) clearInput()
        argList.addAll(args)
        onParse(args)
        builder.features.forEach { it.onLoad(this) }
        parse()
        if (builder.clearInputAfterParse) clearInput()
    }

    private fun clearInput() {
        argList.clear()
        keyValue.clear()
    }

    open fun onParse(args: Array<out String>) {}

    open fun onParseArgument(name: String, argument: Argument<*>, value: Any?) {}

    override fun toString(): String = StringBuilder().apply {
        val indent = "    "
        val doubleIndent = indent + indent
        append("$programName: \n")
        delegates.forEach { delegate ->
            append(indent)
                .append(delegate.argument.names)
                .append(doubleIndent)
                .append(delegate.argument.description)
                .appendln()
                .append(doubleIndent)
                .append(delegate.property.name)
                .append(doubleIndent)
                .append(delegate.getValue())
                .appendln()
        }
    }.toString()

    private fun parse() {
        delegates
            .sortedBy { it.argument.isMainArg }
            .forEach {
                builder.features.forEach { feature ->
                    feature.configure(it.argument)
                }
                it.reset()
                val value = it.getValue()
                onParseArgument(it.property.name, it.argument, value)
            }
        parseBooleanMerge()
    }

    private fun parseBooleanMerge() = checkRemaining(delegates, argList).forEach { (arg, delegates) ->
        argList.remove("-$arg")
        delegates.forEach { it.setTrue() }
    }
}
