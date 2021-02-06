package com.apurebase.arkenv.argument

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.module.ArkenvModuleConfiguration
import kotlin.reflect.KProperty

/**
 * An arkenv argument for classes that extend [Arkenv].
 */
@Suppress("UNCHECKED_CAST")
internal class ArkenvExtendedArgument<T : Any?>(
    override val arkenv: Arkenv,
    override val argument: Argument<T>,
    override val property: KProperty<*>
) : ArkenvArgument<T> {

    override var value: T = null as T

    override var isSet: Boolean = false

    override var isDefault: Boolean = false
        private set

    override val defaultValue: T? by lazy {
        isDefault = true
        argument.defaultValue?.invoke()
    }

    override fun reset() {
        isSet = false
    }

    override fun initialize(arkenv: Arkenv, property: KProperty<*>, moduleConfiguration: ArkenvModuleConfiguration?) {}
}
