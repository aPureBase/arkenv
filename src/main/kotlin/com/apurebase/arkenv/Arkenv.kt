package com.apurebase.arkenv

abstract class Arkenv(
    args: Array<String>,
    val programName: String = "Arkenv",
    val withEnv: Boolean = true,
    val envPrefix: String = ""
) {

    /**
     * Manually parse the arguments, clearing all previously set ones
     */
    fun parse(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
    }

    private val argList = args.toMutableList()
    private val delegates = mutableListOf<ArgumentDelegate<*>>()

    open val help: Boolean by ArkenvLoader(
        listOf("-h", "--help"), false, { isHelp = true },
        withEnv, envPrefix, argList, false, delegates
    )

    fun <T : Any> argument(
        names: List<String>,
        isMainArg: Boolean = false,
        block: Argument<T>.() -> Unit = {}
    ) = ArkenvLoader(names, isMainArg, block, withEnv, envPrefix, argList, help, delegates)

    override fun toString(): String {
        val sb = StringBuilder()
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
                .append(delegate.getValue(this, delegate.property))
                .appendln()
        }
        return sb.toString()
    }

    fun <T : Any> argument(
        vararg names: String,
        block: Argument<T>.() -> Unit = {}
    ): ArkenvLoader<T> = argument(names.toList(), false, block)

    /**
     * Main argument is used for the last argument,
     * which doesn't have a named property to it
     *
     * Main argument can't be passed through environment variables
     */
    fun <T : Any> mainArgument(block: Argument<T>.() -> Unit = {}): ArkenvLoader<T> =
        argument(listOf(), true, block)

    private fun StringBuilder.append(value: String, times: Int): StringBuilder = apply {
        repeat(times) { append(value) }
    }

}
