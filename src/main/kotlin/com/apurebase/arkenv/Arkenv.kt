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
                println("${it.property.name} - $argList")
                it.getValue(isParse = true)
            }
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
        sb.append("$programName: \n")
        delegates.forEach { delegate ->
            sb
                .append(indent)
                .append(delegate.argument.names)
                .append(indent, 2)
                .append(delegate.argument.description)
                .appendln()
                .append(indent, 2)
                .append(delegate.property.name)
                .append(indent, 2)
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

    private fun ArgumentDelegate<*>.getValue(isParse: Boolean): Any? {
        val value = getValue(this, property)
        if (isParse) index?.let {
            println("${property.name} $parsedArgs")
            if (it > -1) argList.removeAt(it)
            if (!isBoolean && value != null) argList.remove(value)
        }
        return value
    }

    private fun StringBuilder.append(value: String, times: Int): StringBuilder = apply {
        repeat(times) { append(value) }
    }
}
