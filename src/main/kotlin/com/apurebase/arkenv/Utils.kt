package com.apurebase.arkenv

internal fun String.toSnakeCase() = this
    .replace("([a-z])([A-Z]+)".toRegex(), "$1_$2")
    .replace(".", "_").replace("-", "_")
    .toUpperCase()
    .removePrefixes("_")

internal fun String.removePrefixes(prefix: CharSequence): String = this
    .removePrefix(prefix)
    .let {
        if (it.startsWith(prefix)) it.removePrefixes(prefix)
        else it
    }

internal fun String.endsWith(list: Iterable<String>): Boolean = list.any { endsWith(it) }

internal fun String.startsWith(list: Iterable<String>): Boolean = list.any { startsWith(it) }

internal fun String.contains(list: Iterable<String>): Boolean = list.any { contains(it) }

internal fun String.removeSurrounding(list: Iterable<String>): String =
    list.fold(this) { acc, s -> acc.removeSurrounding(s) }

fun <T : Arkenv> T.parse(args: Array<String>) = apply { parseArguments(args) }

/**
 * Main argument is used for the last argument,
 * which doesn't have a named property to it
 *
 * Main argument can't be passed through environment variables
 */
inline fun <reified T : Any> Arkenv.mainArgument(noinline block: Argument<T>.() -> Unit = {}): ArkenvLoader<T> =
    argument(listOf(), true, block)

inline fun <reified T : Any> Arkenv.argument(
    vararg names: String,
    noinline block: Argument<T>.() -> Unit = {}
): ArkenvLoader<T> = argument(names.toList(), false, block)

internal fun <T> ArgumentDelegate<T>.readInput(mapping: (String) -> T): T? =
    if (argument.acceptsManualInput) {
        println("Accepting input for ${property.name}: ")
        val input = readLine()
        if (input == null) null
        else mapping(input)
    } else null
