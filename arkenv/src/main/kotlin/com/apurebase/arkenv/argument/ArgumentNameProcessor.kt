package com.apurebase.arkenv.argument

import com.apurebase.arkenv.argument.ArkenvArgumentNamingStrategy.*
import com.apurebase.arkenv.util.isAdvancedName
import com.apurebase.arkenv.util.mapRelaxed
import com.apurebase.arkenv.util.toSnakeCase
import kotlin.reflect.KProperty

internal class ArgumentNameProcessor(
    private val prefix: String?,
    private val namingStrategy: ArkenvArgumentNamingStrategy
) {

    /**
     * Processes the [argument]'s names, potentially updating and adding.
     * @param argument the argument to process.
     * @param property the corresponding property.
     */
    fun processArgumentNames(argument: Argument<*>, property: KProperty<*>) {
        argument.names = getNames(argument.names, property.name)
    }

    private fun getNames(argumentNames: List<String>, propName: String): List<String> =
        getNameList(argumentNames, propName).map(::processName)

    private fun getNameList(argumentNames: List<String>, propName: String): List<String> {
        return when (namingStrategy) {
            ParameterNameOnlyIfNotSpecified -> {
                argumentNames.ifEmpty { listOf(propName.toSnakeCase()) }
            }
            ParameterNameAlwaysIncluded -> argumentNames + propName.toSnakeCase()
        }
    }

    private fun processName(name: String) = name
        .ensureStartsWithDash()
        .prefix(prefix ?: "")
        .mapRelaxed()

    private fun String.ensureStartsWithDash() =
        if (!startsWith("-")) "--$this" else this

    private fun String.prefix(value: String): String = when {
        value.isBlank() -> this
        isAdvancedName() -> "--$value-${substring(2)}"
        else -> "-$value-${substring(1)}"
    }
}
