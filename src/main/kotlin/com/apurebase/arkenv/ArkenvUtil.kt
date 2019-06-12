package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import kotlin.reflect.jvm.jvmName

/**
 * Parses the arguments contained in this instance using the installed features.
 * Validation will be run and potentially throw an Exception if not passed.
 *
 * Be aware, that it is not recommended to call parse in the init block of your [Arkenv] class.
 * If you still want to use it that way,
 * make sure to put it after all arguments have been declared.
 * @param args The command line arguments passed to the program via its main method
 * @return the [Arkenv] instance that was parsed
 * @throws ValidationException if any of the declared argument validation did not pass
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
 * The main argument is used for the last argument,
 * which doesn't have a named property to it.
 * The main argument can't be passed through environment variables.
 * @param block the configuration that will be applied to the Argument
 */
inline fun <reified T : Any> Arkenv.mainArgument(noinline block: Argument<T>.() -> Unit = {}): ArkenvDelegateLoader<T> =
    argument(listOf(), true, block)

internal fun ArkenvFeature.getKeyValPair() = this::class.jvmName to this

internal fun Arkenv.isHelp(): Boolean = when {
    argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet -> false
    else -> help
}
