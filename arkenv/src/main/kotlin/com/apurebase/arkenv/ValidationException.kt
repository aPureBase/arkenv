package com.apurebase.arkenv

import kotlin.reflect.KProperty

/**
 * Unchecked exception thrown when validation of an [Argument] was unsuccessful.
 */
internal class ValidationException(property: KProperty<*>, value: Any?, message: String) : IllegalArgumentException(
    "Argument ${property.name} with value '$value' did not pass validation: '$message'"
)
