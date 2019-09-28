package com.apurebase.arkenv

/**
 * The configuration for this argument.
 * @param names a list of names that will be considered when parsing
 */
class Argument<T : Any?>(var names: List<String>) {
    /**
     * A general description of the purpose of this [Argument] that will be displayed on help
     */
    var description = ""

    /**
     * Determines whether this [Argument] is a help argument that will trigger the help output when parsed.
     */
    var isHelp: Boolean = false

    /**
     * A custom mapping to convert the input [String] argument to an instance of [T]
     */
    var mapping: ((String) -> T)? = null

    /**
     * Whether this [Argument] is a main argument
     */
    var isMainArg: Boolean = false

    /**
     * A default value that will be used when no other argument is found for parsing
     */
    var defaultValue: (() -> T)? = null

    /**
     * When true, this argument will ask for manual input via the command line on parse,
     * when no other argument is provided
     */
    var acceptsManualInput: Boolean = false

    internal val validation = mutableListOf<Validation<T>>()

    /**
     * Adds a validation criteria to this [Argument].
     * If the [assertion] returns false, the validation will throw an exception with the provided [message]
     */
    fun validate(message: String, assertion: (T) -> Boolean) = validation.add(Validation(message, assertion))

    internal class Validation<T>(val message: String, val assertion: (T) -> Boolean)
}
