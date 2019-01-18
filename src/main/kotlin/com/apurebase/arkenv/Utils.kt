package com.apurebase.arkenv

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
