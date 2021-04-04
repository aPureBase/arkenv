package com.apurebase.arkenv.argument

import com.apurebase.arkenv.Arkenv
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ArkenvDelegateLoader<T : Any>(
    private val argument: Argument<T>,
    private val arkenv: Arkenv
) {
    private val argumentNameProcessor = ArgumentNameProcessor(arkenv.configuration.prefix)

    operator fun provideDelegate(thisRef: Arkenv, prop: KProperty<*>): ReadOnlyProperty<Arkenv, T> {
        argumentNameProcessor.processArgumentNames(argument, prop)
        return ArkenvExtendedArgument(thisRef, argument, prop)
            .also(arkenv.delegates::add)
    }
}
