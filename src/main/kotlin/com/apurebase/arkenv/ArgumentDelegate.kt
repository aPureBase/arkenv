package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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

    /**
     * Points to the index in [parsedArgs] where [Argument.names] is placed.
     */
    val index: Int? by lazy {
        if (argument.isMainArg) parsedArgs.size - 2
        else argument
            .names
            .asSequence()
            .map(parsedArgs::indexOf)
            .find { it >= 0 }
    }

    val parsedArgs: List<String> by lazy {
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
        list
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isSet) {
            value = setValue(property)
            checkNullable(property)
            isSet = true
        }
        return value
    }

    @Suppress("UNCHECKED_CAST")
    private fun setValue(property: KProperty<*>): T {
        val envVal = if (argument.withEnv) getEnvValue() else null
        return when {
            isBoolean -> (index != null || envVal != null) as T
            envVal == null && cliValue == null -> argument.defaultValue
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

    private val allowedSurroundings = listOf("'", "\"")

    private fun valuesAreNull(): Boolean = value == null && argument.defaultValue == null
}
