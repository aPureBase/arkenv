package com.apurebase.arkenv

import java.io.File
import java.util.*

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 * @property programName the name of your program
 * @property withEnv whether to enable environment variable parsing. Defaults to true
 * @property envPrefix a common prefix for all environment variables
 * @property enableEnvSecrets whether to enable docker secrets parsing. Will attempt to parse any environment variable
 * with the _FILE suffix and read the value from the specified path.
 */
abstract class Arkenv(
    open val programName: String = "Arkenv",
    open val withEnv: Boolean = true,
    open val envPrefix: String = "",
    open val enableEnvSecrets: Boolean = false,
    open val dotEnvFilePath: String? = null,
    open val propertiesFilePath: String? = null
) {

    /**
     * Parses the [args] and resets all previously parsed state.
     */
    fun parseArguments(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
        onParse(args)
        dotEnv.clear()
        parseDotEnv(dotEnvFilePath).let(dotEnv::putAll)
        parseProperties(propertiesFilePath).let(dotEnv::putAll)
        delegates
            .sortedBy { it.argument.isMainArg }
            .forEach {
                it.reset()
                val value = it.getValue(isParse = true)
                onParseArgument(it.property.name, it.argument, value)
            }
        checkRemaining(delegates, argList).forEach { (arg, delegates) ->
            argList.remove("-$arg")
            delegates.forEach { it.setTrue() }
        }
    }

    open fun onParse(args: Array<String>) {}

    open fun onParseArgument(name: String, argument: Argument<*>, value: Any?) {}

    internal val argList = mutableListOf<String>()
    internal val delegates = mutableListOf<ArgumentDelegate<*>>()
    internal val dotEnv = mutableMapOf<String, String>()

    val help: Boolean by ArkenvLoader(listOf("-h", "--help"), false, { isHelp = true }, Boolean::class, this)

    /**
     * Defines an argument that can be parsed.
     * @param names the names that the argument can be called with
     * @param isMainArg whether this argument is a main argument, meaning it doesn't use names,
     * but the last supplied argument
     * @param configuration optional configuration of the argument's properties
     */
    inline fun <reified T : Any> argument(
        names: List<String>,
        isMainArg: Boolean = false,
        noinline configuration: Argument<T>.() -> Unit = {}
    ) = ArkenvLoader(names, isMainArg, configuration, T::class, this)

    internal fun isHelp(): Boolean = when {
        argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet -> false
        else -> help
    }

    override fun toString(): String = StringBuilder().let { sb ->
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

    private fun ArgumentDelegate<*>.getValue(isParse: Boolean): Any? =
        getValue(this, property).also { value ->
            if (isParse && index != null) removeArgumentFromList(index!!, value)
        }

    private fun ArgumentDelegate<*>.removeArgumentFromList(index: Int, value: Any?) {
        removeValueArgument(index, isBoolean, value)
        removeNameArgument(index, argument.isMainArg)
    }

    private fun removeNameArgument(index: Int, isMainArg: Boolean) {
        if (index > -1 && !isMainArg) argList.removeAt(index)
    }

    private fun removeValueArgument(index: Int, isBoolean: Boolean, value: Any?) {
        if (!isBoolean && value != null) argList.removeAt(index + 1)
    }
}
