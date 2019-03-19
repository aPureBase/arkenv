package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ArkenvDelegateLoader<T : Any>(
    private val names: List<String>,
    private val isMainArg: Boolean = false,
    private val block: Argument<T>.() -> Unit = {},
    private val kClass: KClass<T>,
    private val arkenv: Arkenv
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> = when {
        names.isEmpty() && !isMainArg -> throw IllegalArgumentException("No argument names provided")
        else -> createDelegate(prop)
    }

    private fun createDelegate(prop: KProperty<*>): ArgumentDelegate<T> {
        val argumentConfig = Argument<T>(names).also {
            it.isMainArg = isMainArg
        }.apply(block)
        return ArgumentDelegate(
            arkenv,
            argumentConfig,
            prop,
            kClass == Boolean::class,
            argumentConfig.mapping ?: getMapping(prop)
        ).also { arkenv.delegates.add(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getMapping(prop: KProperty<*>): (String) -> T = { value ->
        when (kClass) {
            Int::class -> value.toIntOrNull() as T
            Long::class -> value.toLongOrNull() as T
            String::class -> value as T
            else -> throw IllegalArgumentException("${prop.name} ($kClass) is not supported")
        }
    }
}
