package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ArkenvDelegateLoader<T : Any>(
    private val argument: Argument<T>,
    private val kClass: KClass<T>,
    private val arkenv: Arkenv
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> = createDelegate(prop)

    private fun createDelegate(prop: KProperty<*>): ArgumentDelegate<T> = with(argument) {
        names = (if (names.isEmpty()) listOf("--${prop.name.toSnakeCase()}") else names).let(::processNames)

        return ArgumentDelegate(
            arkenv,
            argument,
            prop,
            kClass == Boolean::class,
            mapping ?: getMapping(prop)
        ).also { arkenv.delegates.add(it) }
    }

    private fun processNames(names: List<String>) = names.map {
        if (!it.startsWith("-")) "--$it".mapRelaxed()
        else it.mapRelaxed()
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun getMapping(prop: KProperty<*>): (String) -> T = { value ->
        when (kClass) {
            Int::class -> value.toIntOrNull()
            Long::class -> value.toLongOrNull()
            String::class -> value
            else -> throw IllegalArgumentException("${prop.name} ($kClass) is not supported")
        } as T
    }
}
