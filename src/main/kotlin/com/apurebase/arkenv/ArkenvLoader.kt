package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ArkenvLoader<T : Any>(
    private val names: List<String>,
    private val isMainArg: Boolean = false,
    private val block: Argument<T>.() -> Unit = {},
    private val withEnv: Boolean,
    private val envPrefix: String,
    private val argList: MutableList<String>,
    private val help: Boolean,
    private val delegates: MutableList<ArgumentDelegate<*>>
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
            ArgumentDelegate(isHelp, argList, argumentConfig, prop).also { delegates.add(it) }
        }
    }
}
