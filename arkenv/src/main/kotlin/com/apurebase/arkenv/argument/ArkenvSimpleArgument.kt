package com.apurebase.arkenv.argument

import com.apurebase.arkenv.Arkenv
import kotlin.reflect.KProperty

/**
 * A simple arkenv argument that can be used in plain classes.
 * @param argument the argument configuration.
 * @param T the return type of the argument.
 */
@Suppress("UNCHECKED_CAST")
internal class ArkenvSimpleArgument<T : Any?>(
    override val argument: Argument<T>
) : ArkenvArgument<T> {

    override lateinit var property: KProperty<*>
    override lateinit var arkenv: Arkenv
    override var isSet = false

    override val defaultValue: T?
        get() {
            val value = argument.defaultValue?.invoke()
            isDefault = value != null
            return value
        }
    override var isDefault: Boolean = false
        private set

    override var value: T = null as T

    override fun initialize(arkenv: Arkenv, property: KProperty<*>) {
        this.arkenv = arkenv
        this.property = property
    }
}