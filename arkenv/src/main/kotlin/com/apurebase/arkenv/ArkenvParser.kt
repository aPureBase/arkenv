package com.apurebase.arkenv

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

/**
 * Parses [Arkenv] arguments in configuration classes.
 * @param kClass the class to be parsed.
 * @param args the command line arguments.
 */
class ArkenvParser<T : Any>(
    private val kClass: KClass<T>,
    private val args: Array<String>
) {
    private val className get() = kClass.java.name
    private val arkenv: Arkenv = Arkenv()

    /**
     * Parses the arguments in the provided configuration [instance].
     * @param instance the configuration instance to parse.
     */
    fun parse(instance: T) {
        parse(instance, true)
    }

    private fun parse(instance: T, doFullParse: Boolean) {
        val delegates = findDelegates(instance).let { initializeDelegates(it, arkenv) }
        arkenv.delegates.addAll(delegates)
        if (doFullParse) {
            arkenv.parse(args)
        }
        else {
            arkenv.parsePostLoad()
        }
    }

    /**
     * Parses the arguments in a class of the given type.
     * @return an instance of the class.
     * @throws ParsingException when parsing was unsuccessful.
     */
    fun parseClass(): T {
        val constructor = kClass.constructors.firstOrNull()
            ?: throw ParsingException(className, IllegalStateException("No valid constructor found"))

        arkenv.load(args)
        val instance = parseConstructor(constructor)
        parse(instance, false)
        return instance
    }

    private fun parseConstructor(constructor: KFunction<T>): T = try {
        val constructorArgs = parseConstructorArgs(constructor.parameters)
        constructor.callBy(constructorArgs)
    } catch (ex: IllegalArgumentException) {
        throw ParsingException(className, ex)
    }

    private fun parseConstructorArgs(parameters: Collection<KParameter>): Map<KParameter, Any?> {
        return parameters
            .filterNot { it.name.isNullOrBlank() }
            .mapNotNull(::parseParameter)
            .toMap()
    }

    private fun parseParameter(parameter: KParameter): Pair<KParameter, Any?>? {
        println("${parameter.name} - ${parameter.type}")
        val name = parameter.name!!.toSnakeCase()
        val value = arkenv.getOrNull(name)
        return if (parameter.isOptional && value == null) null
        else {
            val mappedValue = when (value) {
                null -> value
                else -> ArkenvMapper.map(name, value, parameter.type.jvmErasure)
            }
            parameter to mappedValue
        }
    }

    private fun initializeDelegates(
        delegates: Collection<DelegatedProperty<T, ArkenvArgument<*>>>,
        arkenv: Arkenv
    ): Collection<ArkenvArgument<*>> = delegates.map { (property, delegate) ->
        delegate.initialize(arkenv, property)
        delegate
    }

    private fun findDelegates(instance: T): Collection<DelegatedProperty<T, ArkenvArgument<*>>> {
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
