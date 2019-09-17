package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import kotlin.reflect.KClass
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
    argList.isEmpty() && !delegates.first { it.argument.isHelp }.isSet -> false
    else -> help
}

internal inline fun <reified T : ArkenvFeature> Arkenv.findFeature(): T? {
    return configuration.features.find { it is T } as T?
}

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
 * @throws IllegalArgumentException when the key can not be found
 */
operator fun Arkenv.get(key: String): String =
    getOrNull(key) ?: throw IllegalArgumentException("Arkenv does not contain a value for key '$key'")

/**
 * Maps the input [value] to an instance of [T] using [clazz] as a reference.
 * @throws IllegalArgumentException if the mapping is not supported or didn't succeed
 */
@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY", "ComplexMethod", "LongMethod", "TooGenericExceptionCaught")
internal fun <T> mapDefault(key: String, value: String, clazz: KClass<*>): T = try {
    with(value) {
        when (clazz) {
            Int::class -> toIntOrNull()
            Long::class -> toLongOrNull()
            String::class -> value
            IntArray::class -> split().map(String::toInt).toIntArray()
            ShortArray::class -> split().map(String::toShort).toShortArray()
            CharArray::class -> toCharArray()
            LongArray::class -> split().map(String::toLong).toLongArray()
            FloatArray::class -> split().map(String::toFloat).toFloatArray()
            DoubleArray::class -> split().map(String::toDouble).toDoubleArray()
            BooleanArray::class -> split().map(String::toBoolean).toBooleanArray()
            ByteArray::class -> split().map(String::toByte).toByteArray()
            else -> throw IllegalArgumentException("$key ($clazz) is not supported. Define a custom mapping.")
        } as T
    }
} catch (ex: RuntimeException) {
    throw IllegalArgumentException("Could not parse $key - $value as $clazz", ex)
}
