package com.apurebase.arkenv

internal fun Arkenv.checkRemaining(
    delegates: List<ArgumentDelegate<*>>,
    argList: List<String>
): List<Pair<String, List<ArgumentDelegate<*>>>> {
    val availableDelegates = delegates
        .filter { it.isBoolean }
        .filterNot { it.getValue(this, it.property) as Boolean }
    if (argList.isEmpty() || availableDelegates.isEmpty()) return listOf()
    val validDelegateMap = availableDelegates
        .map { delegate ->
            val simpleNames = delegate.argument.names.filter { it.isSimpleName() }
            delegate to simpleNames
        }
        .filter { it.second.isNotEmpty() }
    return argList
        .filter { it.isSimpleName() }
        .map { it.removePrefix("-") }
        .map { arg -> arg to resolveMatch(arg, validDelegateMap, listOf()) }
        .filter { it.second.isNotEmpty() }
}

private fun resolveMatch(
    arg: String,
    candidates: Candidates,
    results: List<ArgumentDelegate<*>>
): List<ArgumentDelegate<*>> {
    val options = candidates.findCandidates(arg)
    return when {
        arg.isBlank() -> results
        options.isEmpty() -> listOf()
        else -> {
            val chosen = options.first() // TODO for all
            val remaining = candidates.filterNot { it == chosen }
            val reducedArgument = arg.removePrefix(chosen.second.first().removePrefix("-")) // TODO for all
            resolveMatch(
                arg = reducedArgument,
                candidates = remaining,
                results = results + chosen.first
            )
        }
    }
}

private fun Candidates.findCandidates(arg: String): Candidates =
    map { (delegate, names) -> delegate to names.filter { arg.startsWith(it.removePrefix("-")) } }
        .filter { it.second.isNotEmpty() }

private fun String.isSimpleName() = startsWith("-") && !startsWith("--")
