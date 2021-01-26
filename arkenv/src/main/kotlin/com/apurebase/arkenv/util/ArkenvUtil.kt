package com.apurebase.arkenv.util

import com.apurebase.arkenv.*
import com.apurebase.arkenv.FeatureNotFoundException
import com.apurebase.arkenv.argument.Argument
import com.apurebase.arkenv.argument.ArkenvArgument
import com.apurebase.arkenv.argument.ArkenvDelegateLoader
import com.apurebase.arkenv.argument.ArkenvSimpleArgument
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.parse.ArkenvParser
import kotlin.reflect.jvm.jvmName

/**
 * Parses the [configuration] class using the provided [args].
 * @param configuration the configuration class to parse.
 * @param args the command line arguments.
 * @param configureArkenv additional arkenv configuration.
 * @since 3.2.0
 */
inline fun <reified T : Any> Arkenv.Arkenv.parse(
    configuration: T, args: Array<String>, configureArkenv: ArkenvBuilder.() -> Unit = {}
) {
    val builder = ArkenvBuilder().apply(configureArkenv)
    ArkenvParser(T::class, args, builder).parse(configuration)
}

/**
 * Parses a configuration class of type [T] using the provided [args].
 * @param args the command line arguments.
 * @param configureArkenv additional arkenv configuration.
 * @return an instance of the parsed class.
 * @since 3.2.0
 */
inline fun <reified T : Any> Arkenv.Arkenv.parse(
    args: Array<String>, configureArkenv: ArkenvBuilder.() -> Unit = {}
): T {
    val builder = ArkenvBuilder().apply(configureArkenv)
    return ArkenvParser(T::class, args, builder).parseClass()
}

/**
 * Defines an argument that can be parsed in the current class.
 * @param names additional names to consider when parsing.
 * @param configuration optional configuration of the argument's properties
 * @since 3.2.0
 */
fun <T : Any> argument(vararg names: String, configuration: Argument<T>.() -> Unit = {}): ArkenvArgument<T> =
    ArkenvSimpleArgument(
        Argument<T>(names.toList()).apply(configuration)
    )

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
inline fun <T : Any> Arkenv.argument(
    names: List<String>,
    isMainArg: Boolean = false,
    configuration: Argument<T>.() -> Unit = {}
): ArkenvDelegateLoader<T> {
    val argument = Argument<T>(names).apply(configuration)
    argument.isMainArg = isMainArg
    return ArkenvDelegateLoader(argument, this)
}

/**
 * Defines an argument that can be parsed.
 * @param names the names that the argument can be called with
 * @param configuration optional configuration of the argument's properties
 */
inline fun <T : Any> Arkenv.argument(
    vararg names: String,
    configuration: Argument<T>.() -> Unit = {}
): ArkenvDelegateLoader<T> = argument(names.toList(), false, configuration)

/**
 * The main argument is used for the last argument,
 * which doesn't have a named property to it.
 * The main argument can't be passed through environment variables.
 * @param block the configuration that will be applied to the Argument
 */
inline fun <T : Any> Arkenv.mainArgument(block: Argument<T>.() -> Unit = {}): ArkenvDelegateLoader<T> =
    argument(listOf(), true, block)

internal val ArkenvFeature.key get() = this::class.jvmName

internal fun Arkenv.isHelp(): Boolean = when {
    argList.isEmpty() && delegates.first { it.argument.isHelp }.value == false -> false
    else -> help
}

internal inline fun <reified T : ArkenvFeature> Arkenv.findFeature(): T? {
    return configuration.features.find { it is T } as T?
}

internal inline fun <reified T : ArkenvFeature> Arkenv.getFeature(): T =
    findFeature() ?: throw FeatureNotFoundException(T::class.simpleName)

/**
 * Inserts all key-value pairs in [from] to [Arkenv], overwriting already existing keys.
 * Applies all [ProcessorFeature]s to the value.
 */
fun Arkenv.putAll(from: Map<out String, String>) = from.forEach { (k, v) -> set(k, v) }

/**
 * Retrieves the parsed value for the given [key].
 * All parsed but not declared arguments are available.
 * @param key the non-case-sensitive name of the argument
 * @return The value for the [key]
 * @throws MissingArgumentException when the key can not be found
 */
operator fun Arkenv.get(key: String): String =
    getOrNull(key) ?: throw MissingArgumentException("Arkenv does not contain a value for key '$key'", "", programName)
