package com.apurebase.arkenv

abstract class Arkenv(
    val programName: String = "Arkenv",
    val withEnv: Boolean = true,
    val envPrefix: String = ""
) {

    fun parseArguments(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
        delegates
            .sortedBy { it.argument.isMainArg }
            .forEach {
                it.reset()
                it.getValue(isParse = true)
            }
        onParse(args)
    }

    val argList = mutableListOf<String>()
    val delegates = mutableListOf<ArgumentDelegate<*>>()

    val help: Boolean by ArkenvLoader(listOf("-h", "--help"), false, { isHelp = true }, Boolean::class, this)

    inline fun <reified T : Any> argument(
        names: List<String>,
        isMainArg: Boolean = false,
        noinline block: Argument<T>.() -> Unit = {}
    ) = ArkenvLoader(names, isMainArg, block, T::class, this)

    fun isHelp(): Boolean = if (argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet) false else help

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

    /**
     * Main argument is used for the last argument,
     * which doesn't have a named property to it
     *
     * Main argument can't be passed through environment variables
     */
    inline fun <reified T : Any> mainArgument(noinline block: Argument<T>.() -> Unit = {}): ArkenvLoader<T> =
        argument(listOf(), true, block)

    inline fun <reified T : Any> argument(
        vararg names: String,
        noinline block: Argument<T>.() -> Unit = {}
    ): ArkenvLoader<T> = argument(names.toList(), false, block)

    open fun onParse(args: Array<String>) {

    }

    private fun ArgumentDelegate<*>.getValue(isParse: Boolean): Any? =
        getValue(this, property).also { value ->
            if (isParse && index != null) removeArgumentFromList(index!!, value)
        }

    private fun ArgumentDelegate<*>.removeArgumentFromList(index: Int, value: Any?) {
        if (index > -1) argList.removeAt(index)
        if (!isBoolean && value != null) argList.remove(value)
    }
}
