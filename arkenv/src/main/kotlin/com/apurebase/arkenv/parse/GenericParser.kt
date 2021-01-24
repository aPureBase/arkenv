package com.apurebase.arkenv.parse

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

internal fun <R : Any, DELEGATE : Any> findDelegates(
    instance: R, delegateClass: KClass<DELEGATE>
): Collection<DelegatedProperty<out R, DELEGATE>> {
    return instance::class.declaredMemberProperties.mapNotNull { prop ->
        val javaField = prop.javaField
        if (javaField != null && delegateClass.java.isAssignableFrom(javaField.type)) {
            javaField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val delegateInstance = javaField.get(instance) as DELEGATE
            DelegatedProperty(prop, delegateInstance)
        } else {
            null
        }
    }
}

internal data class DelegatedProperty<R : Any, DELEGATE : Any>(
    val property: KProperty1<R, *>,
    val delegate: DELEGATE
)
