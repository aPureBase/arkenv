package com.apurebase.arkenv

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal open class ArkenvException(message: String, cause: Exception? = null) : RuntimeException(message, cause)

/**
 * Unchecked exception thrown when validation of an [Argument] was unsuccessful.
 */
internal class ValidationException(property: KProperty<*>, value: Any?, message: String) : ArkenvException(
    "Argument ${property.name} with value '$value' did not pass validation: '$message'"
)

/**
 * Unchecked exception thrown when no supported mapping exists for the given class.
 */
internal class UnsupportedMappingException(key: String, clazz: KClass<*>) : ArkenvException(
    "Property '$key' of type '$clazz' is not supported. Define a custom mapping."
)

/**
 * Unchecked exception thrown when mapping was unsuccessful.
 */
internal class MappingException(key: String, value: String, clazz: KClass<*>, cause: Exception) : ArkenvException(
    "Could not parse property '$key' with value '$value' as class '$clazz'", cause
)

/**
 * Unchecked exception thrown when no value can be found for the given name.
 */
internal class MissingArgumentException(name: String, info: String) : ArkenvException(
    "No value passed for property $name ($info)"
)

/**
 * Unchecked exception thrown when the requested feature could not be found.
 */
internal class FeatureNotFoundException(featureName: String?) : ArkenvException(
    "Feature $featureName could not be found. Make sure it was installed correctly."
)
