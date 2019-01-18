package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

class ArgumentDelegate<T : Any?>(
    private val arkenv: Arkenv,
    val argument: Argument<T>,
    val property: KProperty<*>,
    val isBoolean: Boolean,
    private val mapping: (String) -> T
) : ReadOnlyProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    private var value: T = null as T
    var isSet: Boolean = false
        private set

    fun reset() {
        isSet = false
    }

    /**
     * Points to the index in [parsedArgs] where [Argument.names] is placed.
     */
    var index: Int? = null
        private set

    private var parsedArgs: List<String> = listOf()

    private fun parseArguments() {
        val list = mutableListOf<String>()
        var isReading = false
        arkenv.argList.forEach {
            if (isReading) {
                list[list.lastIndex] = "${list.last()} $it"
            } else {
                list.add(it)
            }

            if (isReading && it.endsWith(allowedSurroundings)) {
                list[list.lastIndex] = list.last().removeSurrounding(allowedSurroundings)
                isReading = false
            } else if (!isReading && it.startsWith(allowedSurroundings)) {
                isReading = true
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

    private fun checkValidation() = argument
        .validation
        .filterNot { it.assertion(value) }
        .map { it.message }
        .let {
            if (it.isNotEmpty()) it
                .reduce { acc, s -> "$acc. $s" }
                .run { throw IllegalArgumentException("Argument ${property.name} did not pass validation: $this") }
        }

    @Suppress("UNCHECKED_CAST")
    private fun setValue(property: KProperty<*>): T {
        val envVal = if (argument.withEnv) getEnvValue() else null
        return when {
            isBoolean -> (index != null || envVal != null) as T
            envVal == null && cliValue == null -> {
                if (argument.acceptsManualInput) readInput() ?: argument.defaultValue
                else argument.defaultValue
            }
            else -> {
                val rawValue = cliValue ?: envVal!!
                mapping(rawValue)
            }
        }
    }

    private fun getEnvValue(): String? {
        // If an envVariable is defined we'll pick this as highest order value
        if (argument.envVariable != null) {
            val definedEnvValue = System.getenv(argument.envVariable)
            if (!definedEnvValue.isNullOrEmpty()) return definedEnvValue
        }

        // Loop over all argument names and pick the first one that matches
        return argument.names.mapNotNull {
            if (it.startsWith("--")) {
                System.getenv(argument.envPrefix + it.toSnakeCase())
            } else null
        }.firstOrNull()
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

    private fun readInput(): T? = if (argument.acceptsManualInput) {
        println("Accepting input for ${property.name}: ")
        val input = readLine()
        if (input == null) null
        else mapping(input)
    } else null

    private val allowedSurroundings = listOf("'", "\"")

    private fun valuesAreNull(): Boolean = value == null && argument.defaultValue == null
}
