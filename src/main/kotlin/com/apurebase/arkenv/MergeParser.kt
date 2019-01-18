package com.apurebase.arkenv

internal fun Arkenv.checkRemaining(
    delegates: List<ArgumentDelegate<*>>,
    argList: List<String>
): List<Pair<String, List<ArgumentDelegate<*>>>> {
    val availableDelegates = findBooleanDelegates(this, delegates)
    if (argList.isEmpty() || availableDelegates.isEmpty()) return listOf()
    val mergeCandidates = findMergeCandidates(availableDelegates)
    return argList
        .prepareNames()
        .map { arg -> arg to resolveMatch(arg, mergeCandidates, listOf(), 0) }
        .filter { it.second.isNotEmpty() }
}

private fun findMergeCandidates(delegates: List<ArgumentDelegate<*>>) = delegates
    .map { delegate ->
        val simpleNames = delegate.argument.names.prepareNames()
        MergeCandidate(delegate, simpleNames)
    }
    .filter(MergeCandidate::hasNames)

private fun findBooleanDelegates(arkenv: Arkenv, delegates: List<ArgumentDelegate<*>>): List<ArgumentDelegate<*>> =
    delegates
        .filter { it.isBoolean }
        .filterNot { it.getValue(arkenv, it.property) as Boolean }

private class MergeCandidate(val delegate: ArgumentDelegate<*>, val names: List<String>) {
    fun filterNames(arg: String) = MergeCandidate(delegate, names.filter { arg.startsWith(it) })
    fun hasNames() = names.isNotEmpty()
    override fun equals(other: Any?): Boolean = other is MergeCandidate && other.delegate == delegate
    override fun hashCode(): Int = delegate.hashCode()
}

private fun resolveMatch(
    arg: String,
    candidates: List<MergeCandidate>,
    results: List<ArgumentDelegate<*>>,
    index: Int
): List<ArgumentDelegate<*>> {
    val options = candidates.findCandidates(arg)
    return when {
        arg.isBlank() -> results
        options.isEmpty() -> listOf()
        else -> findNextMatch(arg, candidates, options, results, index)
    }
}

private fun findNextMatch(
    arg: String,
    candidates: List<MergeCandidate>,
    options: List<MergeCandidate>,
    results: List<ArgumentDelegate<*>>,
    index: Int
): List<ArgumentDelegate<*>> {
    val chosen = options[index]
    val remaining = candidates.filterNot { it == chosen }
    val reducedArgument = arg.removePrefix(chosen.names.first()) // TODO for all
    val matches = resolveMatch(
        arg = reducedArgument,
        candidates = remaining,
        results = results + chosen.delegate,
        index = 0
    )
    return when {
        matches.isEmpty() && options.size > index + 1 -> resolveMatch(arg, candidates, results, index + 1)
        else -> matches
    }
}

private fun List<MergeCandidate>.findCandidates(arg: String): List<MergeCandidate> =
    map { it.filterNames(arg) }.filter(MergeCandidate::hasNames)

private fun List<String>.prepareNames(): List<String> =
    filter { it.isSimpleName() }.map { it.removePrefix("-") }

private fun String.isSimpleName() = startsWith("-") && !startsWith("--")
