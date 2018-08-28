package com.apurebase.arkenv

import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

class ArgumentDelegate<T : Any?>(
    private val isHelp: Boolean,
    private val args: List<String>,
    val argument: Argument<T>,
    private val argumentPrefix: String
) {

    @Suppress("UNCHECKED_CAST")
    private var value: T = null as T
    private var isSet: Boolean = false

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isSet) {
            value = setValue(property)
            checkNullable(property)
            isSet = true
        }
        return value
    }

    @Suppress("UNCHECKED_CAST")
    private fun setValue(property: KProperty<*>): T {
        val type = property.returnType
        val envVal = if (argument.withEnv) getEnvValue() else null
        return when {
            type == Boolean::class.starProjectedType -> (index != null || envVal != null) as T
            envVal == null && cliValue == null -> argument.defaultValue
            else -> {
                val rawValue = cliValue ?: envVal!!
                mapType(rawValue, property)
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
            if (it.startsWith("--")) System.getenv(argument.envPrefix + it.toSnakeCase())
            else null
        }.firstOrNull()
    }

    /** TODO: Comment what this index means and is used for. Maybe examples also */
    private val index get() = if (argument.isMainArg) -1 else argument.names.map(args::indexOf).find { it >= 0 }
    private val cliValue: String?
        get() = index?.let { i ->
            val list = args.subList(i + 1, args.size)
            if (list.isEmpty()) null
            else list.takeWhile { !it.startsWith(argumentPrefix) }.joinToString(" ")
        }

    private fun checkNullable(property: KProperty<*>) {
        if (!isHelp && !property.returnType.isMarkedNullable && valuesAreNull()) {
            val nameInfo = if (argument.isMainArg) "Main argument" else argument.names.joinToString()
            throw IllegalArgumentException("No value passed for property ${property.name} ($nameInfo)")
        }
    }

    private fun valuesAreNull(): Boolean = value == null && argument.defaultValue == null

    private fun mapType(value: String, property: KProperty<*>): T {
        argument.mapping?.let { return it(value) }
        @Suppress("UNCHECKED_CAST")
        return when (property.returnType.withNullability(false)) {
            Int::class.starProjectedType -> value.toIntOrNull() as T
            Long::class.starProjectedType -> value.toLongOrNull() as T
            String::class.starProjectedType -> value as T
            else -> throw IllegalArgumentException("${property.name} (${property.returnType}) is not supported")
        }
    }

}
