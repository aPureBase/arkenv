package com.apurebase.arkenv

import kotlin.reflect.KProperty

class ValidationException(property: KProperty<*>, value: Any?, message: String) : IllegalArgumentException(
    "Argument ${property.name} with value '$value' did not pass validation: '$message'"
)
