package com.apurebase.arkenv.argument

import com.apurebase.arkenv.*
import com.apurebase.arkenv.ArkenvMapper
import com.apurebase.arkenv.MappingException
import com.apurebase.arkenv.util.isHelp
import com.apurebase.arkenv.util.toSnakeCase
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * Generic arkenv argument interface.
 */
@Suppress("UNCHECKED_CAST")
interface ArkenvArgument<T : Any?> : ReadOnlyProperty<Any, T> {

    var value: T
    val arkenv: Arkenv
    val argument: Argument<T>
    val defaultValue: T?
    val isDefault: Boolean
    val property: KProperty<*>
    var isSet: Boolean

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (!isSet) setValue()
        return value
    }

    /**
     * Whether the argument is of type boolean.
     */
    val isBoolean get() = property.returnType.jvmErasure == Boolean::class

    /**
     * Resets the argument so that it can be parsed again.
     */
    fun reset() {
        isSet = false
    }

    fun initialize(arkenv: Arkenv, property: KProperty<*>)

    /**
     * Sets the argument's value.
     * @throws MissingArgumentException when the value for the argument cannot be found.
     * @throws ValidationException when the argument's validation fails.
     * @throws MappingException when the value cannot be mapped to the argument's type.
     */
    fun setValue() {
        val names = argument.names + property.name.toSnakeCase()
        val rootArkenv = arkenv.getRootArkenv()
        val values = rootArkenv.parseDelegate(this, names)
        val foundValue = when {
            isBoolean -> mapBoolean(values)
            values.isEmpty() -> (if (argument.acceptsManualInput) readInput() else defaultValue) as T
            else -> map(values.first())
        }

        checkNullable(foundValue)
        if (foundValue != null) checkValidation(argument.validation, foundValue, property)
        value = foundValue
        isSet = true
    }

    /**
     * Sets the argument's value to true if its type is boolean, otherwise throws an exception.
     * @throws MappingException when the argument's type is not boolean.
     */
    fun setTrue() = when {
        isBoolean -> value = true as T
        else -> throw MappingException(
            property.name, "true", Boolean::class,
            IllegalStateException("Attempted to set value to true but ${property.name} is not boolean"))
    }

    private fun checkNullable(value: T) {
        val valuesAreNull = value == null && defaultValue == null
        if (valuesAreNull && !isHelp() && !property.returnType.isMarkedNullable) throw MissingArgumentException(
            property.name,
            info = if (argument.isMainArg) "Main argument" else argument.names.joinToString(),
            arkenv.programName
        )
    }

    private fun mapBoolean(values: Collection<String>): T {
        val isValuesNotEmpty = values.isNotEmpty()
        return when {
            isValuesNotEmpty && values.first() == "false" -> false
            else -> isValuesNotEmpty || defaultValue == true
        } as T
    }

    private fun Arkenv.getRootArkenv(): Arkenv = parent?.getRootArkenv() ?: this

    private fun readInput(): T? {
        println("Accepting input for ${property.name}: ")
        val input = readLine() ?: return defaultValue
        return map(input)
    }

    private fun map(value: String): T =
        argument.mapping?.invoke(value)
            ?: ArkenvMapper.mapDefault(property.name, value, property.returnType.jvmErasure)

    private fun isHelp(): Boolean = argument.isHelp || arkenv.isHelp()
}
