package com.apurebase.arkenv.feature.cli

/**
 * Parses the command line arguments.
 */
internal class CliArgumentParser {
    private val allowedSurroundings = listOf("'", "\"")
    private val list = mutableListOf<String>()
    private var isReading = false

    /**
     * Parses the provided [arguments] and returns the accumulated results.
     * @param arguments List of raw command line string arguments to parse.
     */
    fun parseArguments(arguments: List<String>): List<String> {
        arguments.forEach(::parse)
        return list
    }

    private fun parse(value: String) {
        when {
            isReading -> list[list.lastIndex] = "${list.last()} $value"
            else -> list.add(value)
        }

        when {
            isReading && value.endsWith(allowedSurroundings) -> {
                list[list.lastIndex] = list.last().removeSurrounding(allowedSurroundings)
                isReading = false
            }
            !isReading && value.startsWith(allowedSurroundings) -> isReading = true
        }
    }

    private fun String.removeSurrounding(list: Iterable<CharSequence>): String =
        list.fold(this, String::removeSurrounding)

    private fun String.startsWith(list: Iterable<String>): Boolean = list.any(::startsWith)

    private fun String.endsWith(list: Iterable<CharSequence>): Boolean = list.any(::endsWith)
}
