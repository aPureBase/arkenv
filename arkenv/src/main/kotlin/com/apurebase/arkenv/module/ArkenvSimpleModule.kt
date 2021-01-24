package com.apurebase.arkenv.module

import com.apurebase.arkenv.Arkenv
import kotlin.reflect.KClass

/**
 * Registers the [module] as a sub module that will be automatically parsed after the super Arkenv.
 * It will be parsed using the configuration of its root.
 * @param module the sub module to add to this [Arkenv]
 */
fun <T : Arkenv> Arkenv.module(module: T): T = module.also {
    it.parent = this
    configuration.modules.add(it)
}

inline fun <reified T : Any> module(module: T? = null): ArkenvModule<T> = ArkenvSimpleModule(T::class, module)

class ArkenvSimpleModule<T : Any>(
    override val kClass: KClass<T>,
    override var module: T?
) : ArkenvModule<T>
