package com.apurebase.arkenv.argument

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.util.isAdvancedName
import com.apurebase.arkenv.util.mapRelaxed
import com.apurebase.arkenv.util.toSnakeCase
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ArkenvDelegateLoader<T : Any>(
    private val argument: Argument<T>,
    private val arkenv: Arkenv
) {
    operator fun provideDelegate(thisRef: Arkenv, prop: KProperty<*>): ReadOnlyProperty<Arkenv, T> {
        argument.names = getNames(argument.names, prop.name)
        return ArkenvExtendedArgument(thisRef, argument, prop)
            .also { arkenv.delegates.add(it) }
    }

    private fun getNames(names: List<String>, propName: String) = names
        .ifEmpty { listOf(propName.toSnakeCase()) }
        .map {
            it.ensureStartsWithDash()
                .prefix(arkenv.configuration.prefix ?: "")
                .mapRelaxed()
        }

    private fun String.ensureStartsWithDash() =
        if (!startsWith("-")) "--$this" else this

    private fun String.prefix(value: String): String = when {
        value.isBlank() -> this
        isAdvancedName() -> "--$value-${substring(2)}"
        else -> "-$value-${substring(1)}"
    }
}
