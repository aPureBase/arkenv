package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import kotlin.reflect.jvm.jvmName

/**
 * Parses the [args] and returns the [Arkenv] instance.
 */
fun <T : Arkenv> T.parse(args: Array<String>) = apply { parseArguments(args) }

/**
 * Defines an argument that can be parsed.
 * @param names the names that the argument can be called with
 * @param isMainArg whether this argument is a main argument, meaning it doesn't use names,
 * but the last supplied argument
 * @param configuration optional configuration of the argument's properties
 */
inline fun <reified T : Any> Arkenv.argument(
    names: List<String>,
    isMainArg: Boolean = false,
    noinline configuration: Argument<T>.() -> Unit = {}
) = ArkenvDelegateLoader(names, isMainArg, configuration, T::class, this)


/**
 * Defines an argument that can be parsed.
 * @param names the names that the argument can be called with
 * @param configuration optional configuration of the argument's properties
 */
inline fun <reified T : Any> Arkenv.argument(
    vararg names: String,
    noinline configuration: Argument<T>.() -> Unit = {}
): ArkenvDelegateLoader<T> = argument(names.toList(), false, configuration)

/**
 * Main argument is used for the last argument,
 * which doesn't have a named property to it
 *
 * Main argument can't be passed through environment variables
 */
inline fun <reified T : Any> Arkenv.mainArgument(noinline block: Argument<T>.() -> Unit = {}): ArkenvDelegateLoader<T> =
    argument(listOf(), true, block)

internal fun <T> ArgumentDelegate<T>.readInput(mapping: (String) -> T): T? {
    println("Accepting input for ${property.name}: ")
    val input = readLine()
    return if (input == null) null
    else mapping(input)
}

internal fun ArkenvFeature.getKeyValPair() = this::class.jvmName to this

fun Arkenv.install(feature: ArkenvFeature) {
    val (key, value) = feature.getKeyValPair()
    features[key] = value
}

fun Arkenv.uninstall(feature: ArkenvFeature) {
    features.remove(feature.getKeyValPair().first)
}

internal fun Arkenv.isHelp(): Boolean = when {
    argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet -> false
    else -> help
}
