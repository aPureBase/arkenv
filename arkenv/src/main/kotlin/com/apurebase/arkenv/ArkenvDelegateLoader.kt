package com.apurebase.arkenv

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ArkenvDelegateLoader<T : Any>(
    private val argument: Argument<T>,
    private val arkenv: Arkenv
) {
    operator fun provideDelegate(thisRef: Arkenv, prop: KProperty<*>): ReadOnlyProperty<Arkenv, T> {
        argument.names = getNames(argument.names, prop.name)
        return ArgumentDelegate(argument, prop)
            .also { arkenv.delegates.add(it) }
    }

    private fun getNames(names: List<String>, propName: String) =
        names.ifEmpty { listOf("--${propName.toSnakeCase()}") }
            .map {
                (if (!it.startsWith("-")) "--$it" else it)
                    .prefix(arkenv.configuration.commonPrefix)
                    .mapRelaxed()
            }

    private fun String.prefix(value: String): String = when {
        value.isBlank() -> this
        isAdvancedName() -> "--$value-${substring(2)}"
        else -> "-$value-${substring(1)}"
    }
}
