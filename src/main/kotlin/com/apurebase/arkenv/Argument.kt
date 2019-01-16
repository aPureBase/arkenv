package com.apurebase.arkenv

class Argument<T : Any?>(val names: List<String>) {
    var description = ""
    var isHelp: Boolean = false
    var mapping: ((String) -> T)? = null
    var withEnv: Boolean = false
    var envPrefix: String = ""
    var envVariable: String? = null
    var isMainArg: Boolean = false
    @Suppress("UNCHECKED_CAST")
    var defaultValue: T = null as T
    private val validation = mutableListOf<Validation<T>>()

    fun validate(message: String, assertion: (T) -> Boolean) = validation.add(Validation(message, assertion))

    internal class Validation<T>(val message: String, val assertion: (T) -> Boolean)
}
