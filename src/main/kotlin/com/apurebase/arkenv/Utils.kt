package com.apurebase.arkenv

/**
 * Parses the [args] and returns the [Arkenv] instance.
 */
fun <T : Arkenv> T.parse(args: Array<String>) = apply { parseArguments(args) }

/**
 * Main argument is used for the last argument,
 * which doesn't have a named property to it
 *
 * Main argument can't be passed through environment variables
 */
inline fun <reified T : Any> Arkenv.mainArgument(noinline block: Argument<T>.() -> Unit = {}): ArkenvDelegateLoader<T> =
    argument(listOf(), true, block)

/**
 * Defines an argument that can be parsed.
 * @param names the names that the argument can be called with
 * @param configuration optional configuration of the argument's properties
 */
inline fun <reified T : Any> Arkenv.argument(
    vararg names: String,
    noinline configuration: Argument<T>.() -> Unit = {}
): ArkenvDelegateLoader<T> = argument(names.toList(), false, configuration)

internal fun <T> ArgumentDelegate<T>.readInput(mapping: (String) -> T): T? {
    println("Accepting input for ${property.name}: ")
    val input = readLine()
    return if (input == null) null
    else mapping(input)
}

fun Arkenv.install(feature: ArkenvFeature) {
    features.add(feature)
}

fun Arkenv.uninstall(feature: ArkenvFeature) {
    features.remove(feature)
}
