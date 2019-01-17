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
        checkRemaining()
    }

    private fun checkRemaining() {
        val availableDelegates = delegates
            .filter { it.isBoolean }
            .filterNot { it.getValue(this, it.property) as Boolean }
        if (argList.isNotEmpty() && availableDelegates.isNotEmpty()) {
            val validDelegateMap = availableDelegates
                .map { delegate ->
                    val simpleNames = delegate.argument.names.filter { it.isSimpleName() }
                    delegate to simpleNames
                }
                .filter { it.second.isNotEmpty() }
            argList
                .filter { it.isSimpleName() }
                .map { it.removePrefix("-") }
                .map { arg -> arg to resolveMatch(arg, validDelegateMap, listOf()) }
                .filter { it.second.isNotEmpty() }
                .forEach { (arg, delegates) ->
                    argList.remove("-$arg")
                    delegates.forEach { it.setTrue() }
                    println("$arg: ${delegates.map { it.property.name }}")
                }
        }
    }

    private fun resolveMatch(
        arg: String,
        candidates: Candidates,
        results: List<ArgumentDelegate<*>>
    ): List<ArgumentDelegate<*>> {
        println("Resolving: $arg, ${candidates.map { it.first.property.name }}")
        if (arg.isBlank()) return results
        val options = candidates.findCandidates(arg)
        if (options.isEmpty()) return listOf()
        val chosen = options.first() // TODO for all
        val remaining = candidates.filterNot { it == chosen }
        val reducedArgument = arg.removePrefix(chosen.second.first().removePrefix("-")) // TODO for all
        return resolveMatch(
            arg = reducedArgument,
            candidates = remaining,
            results = results + chosen.first
        )
    }

    private fun Candidates.findCandidates(arg: String): Candidates =
        map { (delegate, names) -> delegate to names.filter { arg.startsWith(it.removePrefix("-")) } }
            .filter { it.second.isNotEmpty() }

    private fun String.isSimpleName() = startsWith("-") && !startsWith("--")

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
