package com.apurebase.arkenv

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 * Parses [Arkenv] arguments in configuration classes.
 */
class ArkenvParser<T : Any>(
    private val configuration: T,
    private val kClass: KClass<T>,
    private val args: Array<String>
) {

    /**
     * Parses the arkenv configuration.
     */
    fun parse() {
        val arkenv = Arkenv()
        val delegates = findDelegates(configuration)
        delegates.forEach { it.delegate.initialize(arkenv, it.property) }
        arkenv.delegates.addAll(delegates.map { it.delegate })
        arkenv.parse(args)
    }

    private fun findDelegates(instance: T): List<DelegatedProperty<T, ArkenvArgument<*>>> {
        val delegateClass = ArkenvArgument::class
        return kClass.declaredMemberProperties.mapNotNull { prop ->
            val javaField = prop.javaField
            if (javaField != null && delegateClass.java.isAssignableFrom(javaField.type)) {
                javaField.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val delegateInstance = javaField.get(instance) as ArkenvArgument<*>
                DelegatedProperty(prop, delegateInstance)
            } else {
                null
            }
        }
    }

    private data class DelegatedProperty<T : Any, DELEGATE : Any>(
        val property: KProperty1<T, *>,
        val delegate: DELEGATE
    )
}