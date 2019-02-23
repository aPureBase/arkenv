package com.apurebase.arkenv

class ValidationException(property: String, value: Any?, message: String) : IllegalArgumentException(
    "Argument $property with value '$value' did not pass validation: '$message'"
)
