package com.apurebase.arkenv

import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

@Suppress("UNCHECKED_CAST")
class ArgumentDelegate<T : Any?>(
    val isHelp: Boolean,
    val args: List<String>,
    val argument: Argument<T>,
    val argumentPrefix: String
) {

    var value: T = null as T
    private var isSet: Boolean = false

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!isSet) {
            if (!isHelp) {
                value = setValue(property)
            }
            checkNullable(property)
            isSet = true
        }
        return value
    }

    fun setValue(property: KProperty<*>): T {
        val type = property.returnType
        val envVal = if (argument.withEnv) getEnvValue(property) else null
        return when {
            type == Boolean::class.starProjectedType -> {
                if (index != null || envVal != null) true as T
                else false as T
            }
            envVal == null && cliValue == null -> argument.defaultValue
            else -> {
                println("$envVal $cliValue")
                val rawValue = envVal ?: cliValue!!
                mapType(rawValue, property)
            }
        }
    }

    fun getEnvValue(property: KProperty<*>) =
        argument.names.mapNotNull { System.getenv(argument.envPrefix + it) }.firstOrNull()
                ?: if (argument.isMainArg) System.getenv(argument.envPrefix + property.name) else null

    val index = if (argument.isMainArg) -1 else argument.names.map { args.indexOf(it) }.find { it >= 0 }
    val cliValue: String?
        get() = index?.let {
            val list = args.subList(index + 1, args.size)
            if (list.isEmpty()) null
            else list.takeWhile { !it.startsWith(argumentPrefix) }.joinToString(" ")
        }

    fun checkNullable(property: KProperty<*>) {
        if (!isHelp && value == null && !property.returnType.isMarkedNullable && argument.defaultValue == null) {
            val nameInfo = if (argument.isMainArg) "Main argument" else argument.names.joinToString()
            throw IllegalArgumentException("No value passed for property ${property.name} ($nameInfo)")
        }
    }

    fun mapType(value: String, property: KProperty<*>): T {
        argument.mapping?.let { return it(value) }
        return when (property.returnType.withNullability(false)) {
            Int::class.starProjectedType -> value.toIntOrNull() as T
            Long::class.starProjectedType -> value.toLongOrNull() as T
            String::class.starProjectedType -> value as T
            else -> throw IllegalArgumentException("${property.name} (${property.returnType}) is not supported")
        }
    }

}