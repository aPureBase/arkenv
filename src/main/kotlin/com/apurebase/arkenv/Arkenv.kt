package com.apurebase.arkenv

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 * @property programName the name of your program
 * @property withEnv whether to enable environment variable parsing. Defaults to true
 * @property envPrefix a common prefix for all environment variables
 * @property enableEnvSecrets whether to enable docker secrets parsing. Will attempt to parse any environment variable
 * with the _FILE suffix and read the value from the specified path.
 * @property dotEnvFilePath location of the dot env file to read variables from
 * @property propertiesFile name of the properties file to load variables from
 */
abstract class Arkenv(
    open val programName: String = "Arkenv",
    open val withEnv: Boolean = true,
    open val envPrefix: String = "",
    open val enableEnvSecrets: Boolean = false,
    open val dotEnvFilePath: String? = null,
    val propertiesFile: String? = null
) {

    open val loaders: MutableList<(Arkenv) -> Unit> = listOfNotNull(
        ::loadEnvironmentVariables,
        if (propertiesFile != null) ::loadProperties else null
    ).toMutableList()

    open val parsers: MutableList<(Arkenv, ArgumentDelegate<*>) -> String?> = mutableListOf(
        ::parseCli,
        ::parseEnvironmentVariables
    )

    /**
     * Parses the [args] and resets all previously parsed state.
     */
    fun parseArguments(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
        onParse(args)

        dotEnv.clear()
        loaders.forEach { it(this) }

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
    val dotEnv = mutableMapOf<String, String>()

    val help: Boolean by ArkenvDelegateLoader(listOf("-h", "--help"), false, { isHelp = true }, Boolean::class, this)

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
    ) = ArkenvDelegateLoader(names, isMainArg, configuration, T::class, this)

    internal fun isHelp(): Boolean = when {
        argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet -> false
        else -> help
    }

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
        index: Int,
        isBoolean: Boolean,
        value: Any?,
        default: Boolean
    ) {
        if (!isBoolean && !default && value != null) argList.removeAt(index + 1)
    }
}
