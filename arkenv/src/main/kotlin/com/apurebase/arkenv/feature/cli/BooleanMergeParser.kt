package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument.ArkenvArgument
import com.apurebase.arkenv.util.isSimpleName

/**
 * Parses merged boolean arguments from the command line.
 */
internal class BooleanMergeParser {

    internal fun findRemaining(arkenv: Arkenv, argList: List<String>): List<Pair<String, List<ArkenvArgument<*>>>> =
        when {
            argList.isEmpty() -> listOf()
            else -> {
                val mergeCandidates = findBooleanDelegates(arkenv, arkenv.delegates).let(::findMergeCandidates)
                argList
                    .prepareNames()
                    .map { arg -> arg to resolveMatch(arg, mergeCandidates, listOf(), 0, 0) }
                    .filter { it.second.isNotEmpty() }
            }
        }

    private fun findMergeCandidates(delegates: List<ArkenvArgument<*>>) = delegates
        .map { delegate ->
            val simpleNames = delegate.argument.names.prepareNames()
            MergeCandidate(delegate, simpleNames)
        }
        .filter(MergeCandidate::hasNames)

    private fun findBooleanDelegates(arkenv: Arkenv, delegates: List<ArkenvArgument<*>>): List<ArkenvArgument<*>> =
        delegates
            .filter { it.isBoolean }
            .filterNot { it.getValue(arkenv, it.property) as Boolean }

    private class MergeCandidate(val delegate: ArkenvArgument<*>, val names: List<String>) {
        fun filterNames(arg: String) = MergeCandidate(delegate, names.filter(arg::startsWith))

        fun hasNames() = names.isNotEmpty()
        override fun equals(other: Any?): Boolean = other is MergeCandidate && other.delegate == delegate
        override fun hashCode(): Int = delegate.hashCode()
    }

    private fun resolveMatch(
        arg: String, candidates: List<MergeCandidate>, results: List<ArkenvArgument<*>>, index: Int, nameIndex: Int
    ): List<ArkenvArgument<*>> {
        val options = candidates.findCandidates(arg)
        val chosen = options.getOrNull(index) ?: return listOf()
        val remaining = candidates - chosen
        val reducedArgument = arg.removePrefix(chosen.names[nameIndex])
        return if (reducedArgument.isBlank()) results + chosen.delegate
        else {
            val matches = resolveMatch(reducedArgument, remaining, results + chosen.delegate, 0, 0)
            when {
                shouldContinueSearch(matches, options, index) -> resolveMatch(arg, candidates, results, index + 1, 0)
                shouldContinueSearch(matches, chosen.names, nameIndex) ->
                    resolveMatch(arg, candidates, results, index, nameIndex + 1)
                else -> matches
            }
        }
    }

    /**
     * Continue search if there are no matches and there are more items in the list.
     */
    private fun shouldContinueSearch(matches: List<ArkenvArgument<*>>, list: List<*>, index: Int) =
        matches.isEmpty() && list.size > index + 1

    private fun List<MergeCandidate>.findCandidates(arg: String): List<MergeCandidate> =
        map { it.filterNames(arg) }.filter(MergeCandidate::hasNames)

    private fun List<String>.prepareNames(): List<String> =
        filter(String::isSimpleName).map { it.removePrefix("-") }
}
