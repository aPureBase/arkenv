package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ArkenvLoader<T : Any>(
    private val names: List<String>,
    private val isMainArg: Boolean = false,
    private val block: Argument<T>.() -> Unit = {},
    private val kClass: KClass<T>,
    private val arkenv: Arkenv
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> = when {
        names.isEmpty() && !isMainArg -> throw IllegalArgumentException("No argument names provided")
        else -> {
            val argumentConfig = Argument<T>(names).also {
                it.withEnv = arkenv.withEnv
                it.envPrefix = arkenv.envPrefix
                it.isMainArg = isMainArg
            }.apply(block)
            ArgumentDelegate(
                arkenv,
                argumentConfig,
                prop,
                kClass == Boolean::class,
                argumentConfig.mapping ?: getMapping(prop)
            ).also { arkenv.delegates.add(it) }
        }
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