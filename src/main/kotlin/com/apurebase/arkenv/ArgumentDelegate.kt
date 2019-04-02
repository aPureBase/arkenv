package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ArgumentDelegate<T : Any?>(
    private val arkenv: Arkenv,
    val argument: Argument<T>,
    val property: KProperty<*>,
    val isBoolean: Boolean,
    private val mapping: (String) -> T
) : ReadOnlyProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    private var value: T = null as T

    internal var isSet: Boolean = false
        private set

    var isDefault: Boolean = false
        private set

    private val defaultValue: T? by lazy {
        isDefault = true
        argument.defaultValue?.invoke()
    }

    internal fun reset() {
        isSet = false
    }

    @Suppress("UNCHECKED_CAST")
    internal fun setTrue() = when {
        isBoolean -> value = true as T
        else -> throw IllegalStateException("Attempted to set value to true but ${property.name} is not boolean")
    }

    /**
     * Points to the index in [parsedArgs] where [Argument.names] is placed.
     */
    internal var index: Int? = null
        private set

    private var parsedArgs: List<String> = listOf()

    private fun parseArguments() {
        val list = mutableListOf<String>()
        var isReading = false
        arkenv.argList.forEach {
            when {
                isReading -> list[list.lastIndex] = "${list.last()} $it"
                else -> list.add(it)
            }
            when {
                isReading && it.endsWith(allowedSurroundings) -> {
                    list[list.lastIndex] = list.last().removeSurrounding(allowedSurroundings)
                    isReading = false
                }
                !isReading && it.startsWith(allowedSurroundings) -> isReading = true
            }
        }
        parsedArgs = list
    }

    private fun findIndex() {
        index = if (argument.isMainArg) parsedArgs.size - 2
        else argument
            .names
            .asSequence()
            .map(parsedArgs::indexOf)
            .find { it >= 0 }
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isSet) {
            parseArguments()
            findIndex()
            value = setValue(property)
            checkNullable(property)
            checkValidation()
            isSet = true
        }
        return value
    }

    private fun checkValidation() {
        if (value == null) return
        argument
            .validation
            .filterNot { it.assertion(value) }
            .map { it.message }
            .let {
                if (it.isNotEmpty()) it
                    .reduce { acc, s -> "$acc. $s" }
                    .run { throw ValidationException(property, value, this) }
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setValue(property: KProperty<*>): T {
        val envVal = if (argument.withEnv) getEnvValue(argument, arkenv.dotEnv, arkenv.enableEnvSecrets) else null
        return when {
            isBoolean -> (index != null || envVal != null) as T
            envVal == null && cliValue == null -> {
                if (argument.acceptsManualInput) readInput(mapping) ?: defaultValue as T
                else defaultValue as T
            }
            else -> {
                val rawValue = cliValue ?: envVal!!
                mapping(rawValue)
            }
        }
    }

    private val cliValue: String?
        get() = index?.let {
            parsedArgs.getOrNull(it + 1)
        }

    @Suppress("NO_REFLECTION_IN_CLASS_PATH")
    private fun checkNullable(property: KProperty<*>) {
        if (argument.isHelp) return
        if (!arkenv.isHelp() && !property.returnType.isMarkedNullable && valuesAreNull()) {
            val nameInfo = if (argument.isMainArg) "Main argument" else argument.names.joinToString()
            throw IllegalArgumentException("No value passed for property ${property.name} ($nameInfo)")
        }
    }

    private val allowedSurroundings = listOf("'", "\"")

    private fun valuesAreNull(): Boolean = value == null && defaultValue == null
}
