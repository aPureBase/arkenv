package com.apurebase.arkenv.argument

import com.apurebase.arkenv.ValidationException
import kotlin.reflect.KProperty

internal class Validation<T>(val message: String, val assertion: (T) -> Boolean)

internal fun <T> checkValidation(validation: List<Validation<T>>, value: T, property: KProperty<*>) = validation
    .filterNot { it.assertion(value) }
    .map { it.message }
    .let {
        if (it.isNotEmpty()) it
            .reduce { acc, s -> "$acc. $s" }
            .run { throw ValidationException(property, value, this) }
    }
