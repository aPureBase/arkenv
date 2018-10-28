package com.apurebase.arkenv

import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ArkenvLoader<T : Any> constructor(
    private val names: List<String>,
    private val isMainArg: Boolean = false,
    private val block: Argument<T>.() -> Unit = {},
    private val withEnv: Boolean,
    private val envPrefix: String,
    private val argList: MutableList<String>,
    private val help: Boolean,
    private val delegates: MutableCollection<ArgumentDelegate<*>>,
    private val kClass: KClass<T>
) {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> = when {
        names.isEmpty() && !isMainArg -> throw IllegalArgumentException("No argument names provided")
        else -> {
            val argumentConfig = Argument<T>(names).also {
                it.withEnv = withEnv
                it.envPrefix = envPrefix
                it.isMainArg = isMainArg
            }.apply(block)
            val isHelp = if (argumentConfig.isHelp) false else help
            ArgumentDelegate(
                isHelp,
                argList,
                argumentConfig,
                prop,
                kClass == Boolean::class,
                argumentConfig.mapping ?: getMapping(prop)
            ).also { delegates.add(it) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getMapping(prop: KProperty<*>): (String) -> T =  { value ->
        when (kClass) {
            Int::class -> value.toIntOrNull() as T
            Long::class -> value.toLongOrNull() as T
            String::class -> value as T
            else -> throw IllegalArgumentException("${prop.name} ($kClass) is not supported")
        }
    }
}
