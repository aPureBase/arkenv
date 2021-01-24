package com.apurebase.arkenv

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Generic Arkenv exception.
 */
sealed class ArkenvException(message: String, cause: Exception? = null) : RuntimeException(message, cause)

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
    "Could not map property '$key' with value '$value' as class '$clazz'", cause
)

/**
 * Unchecked exception thrown when no value can be found for the given name.
 */
class MissingArgumentException(name: String, info: String, moduleName: String) : ArkenvException(
    "No value passed for property $name ($info) in Arkenv module $moduleName"
)

/**
 * Unchecked exception thrown when the requested feature could not be found.
 */
internal class FeatureNotFoundException(featureName: String?) : ArkenvException(
    "Feature $featureName could not be found. Make sure it was installed correctly."
)

internal class ParsingException(className: String, innerException: Exception) : ArkenvException(
    "Exception encountered when parsing $className", innerException
)

internal class ModuleInitializationException(name: String) : ArkenvException(
    "Module for class $name has not been initialized."
)
