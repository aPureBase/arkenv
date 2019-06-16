package com.apurebase.arkenv

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 * @param programName
 * @param configuration
 */
abstract class Arkenv(
    programName: String = "Arkenv",
    internal val configuration: ArkenvBuilder = ArkenvBuilder()
) {

    @Deprecated("Will be removed in future major version")
    constructor(programName: String = "Arkenv", configuration: (ArkenvBuilder.() -> Unit))
            : this(programName, configureArkenv(configuration))

    internal val argList = mutableListOf<String>()
    internal val keyValue = mutableMapOf<String, String>()
    internal val delegates = mutableListOf<ArgumentDelegate<*>>()

    val help: Boolean by argument("-h", "--help") { isHelp = true }

    val programName: String by argument("--arkenv-application-name") {
        defaultValue = { programName }
    }

    internal fun parseArguments(args: Array<out String>) {
        if (configuration.clearInputBeforeParse) clearInput()
        argList.addAll(args)
        onParse(args)
        configuration.features.forEach { it.onLoad(this) }
        parse()
        if (configuration.clearInputAfterParse) clearInput()
    }

    internal fun clearInput() {
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
                configuration.features.forEach { feature ->
                    feature.configure(it.argument)
                }
                it.reset()
                val value = it.getValue()
                onParseArgument(it.property.name, it.argument, value)
            }
        configuration.features.forEach { it.finally(this) }
    }
}
