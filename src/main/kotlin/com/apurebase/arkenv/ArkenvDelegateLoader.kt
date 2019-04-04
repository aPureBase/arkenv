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
        names.isEmpty()  -> createDelegate(prop, listOf("--${prop.name.toSnakeCase()}"))
        else -> createDelegate(prop, names)
    }

    private fun createDelegate(prop: KProperty<*>, names: List<String>): ArgumentDelegate<T> {
        val argumentConfig = Argument<T>(processNames(names)).also {
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

    private fun processNames(names: List<String>) = names.map {
        if (!it.startsWith("-")) "--$it".mapRelaxed()
        else it.mapRelaxed()
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
