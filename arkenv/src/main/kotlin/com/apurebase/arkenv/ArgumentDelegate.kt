package com.apurebase.arkenv

import com.apurebase.arkenv.Argument.Validation
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * Delegate class for parsing arguments.
 */
class ArgumentDelegate<T : Any?> internal constructor(
    val argument: Argument<T>,
    val property: KProperty<*>
) : ReadOnlyProperty<Arkenv, T> {

    val isBoolean: Boolean = property.returnType.jvmErasure == Boolean::class

    @Suppress("UNCHECKED_CAST")
    internal var value: T = null as T
        private set

    internal var isSet: Boolean = false
        private set

    var isDefault: Boolean = false
        private set

    private val defaultValue: T? by lazy {
        isDefault = true
        argument.defaultValue?.invoke()
    }

    internal fun reset() {
        isSet = false
    }

    @Suppress("UNCHECKED_CAST")
    internal fun setTrue() = when {
        isBoolean -> value = true as T
        else -> throw IllegalStateException("Attempted to set value to true but ${property.name} is not boolean")
    }

    override operator fun getValue(thisRef: Arkenv, property: KProperty<*>): T {
        if (!isSet) {
            value = setValue(thisRef, property)
            checkNullable(thisRef, property)
            if (value != null) checkValidation(argument.validation, value, property)
            isSet = true
        }
        return value
    }

    private fun <T> checkValidation(validation: List<Validation<T>>, value: T, property: KProperty<*>) = validation
        .filterNot { it.assertion(value) }
        .map { it.message }
        .let {
            if (it.isNotEmpty()) it
                .reduce { acc, s -> "$acc. $s" }
                .run { throw ValidationException(property, value, this) }
        }

    @Suppress("UNCHECKED_CAST")
    private fun setValue(arkenv: Arkenv, property: KProperty<*>): T {
        val values = arkenv.parseDelegate(this, argument.names)
        return when {
            isBoolean -> mapBoolean(values)
            values.isEmpty() -> (if (argument.acceptsManualInput) readInput() else defaultValue) as T
            else -> map(values.first())
        }
    }

    private fun readInput(): T? {
        println("Accepting input for ${property.name}: ")
        val input = readLine() ?: return defaultValue
        return map(input)
    }

    private fun map(value: String): T =
        argument.mapping?.invoke(value)
                ?: mapDefault(property.name, value, property.returnType.jvmErasure)

    @Suppress("UNCHECKED_CAST")
    private fun mapBoolean(values: Collection<String>): T {
        val isValuesNotEmpty = values.isNotEmpty()
        return when {
            isValuesNotEmpty && values.first() == "false" -> false
            else -> isValuesNotEmpty || defaultValue == true
        } as T
    }

    private fun checkNullable(arkenv: Arkenv, property: KProperty<*>) {
        val valuesAreNull = value == null && defaultValue == null
        if (valuesAreNull && !isHelp(arkenv) && !property.returnType.isMarkedNullable) throw MissingArgumentException(
            property.name,
            info = if (argument.isMainArg) "Main argument" else argument.names.joinToString()
        )
    }

    private fun isHelp(arkenv: Arkenv): Boolean = argument.isHelp || arkenv.isHelp()
}
