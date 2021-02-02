package com.apurebase.arkenv.module

import com.apurebase.arkenv.Arkenv
import kotlin.reflect.KClass

/**
 * Registers the [module] as a sub module that will be automatically parsed after the super Arkenv.
 * It will be parsed using the configuration of its root.
 * @param module the sub module to add to this [Arkenv].
 * @since 3.1.0
 */
fun <T : Arkenv> Arkenv.module(module: T): T = module.also {
    it.parent = this
    configuration.modules.add(it)
}

/**
 * Registers the [module] as a sub module that will be automatically parsed after the super class.
 * @param module the sub module to add to this class.
 * @since 3.2.0
 */
inline fun <reified T : Any> module(
    module: T? = null,
    configuration: ArkenvModuleConfiguration.() -> Unit = {}
): ArkenvModule<T> = ArkenvSimpleModule(T::class, module, ArkenvModuleConfiguration().apply(configuration))

class ArkenvSimpleModule<T : Any>(
    override val kClass: KClass<T>,
    override var module: T?,
    override val configuration: ArkenvModuleConfiguration
) : ArkenvModule<T>
