package com.apurebase.arkenv.parse

import com.apurebase.arkenv.*
import com.apurebase.arkenv.ArkenvConfiguration
import com.apurebase.arkenv.ArkenvMapper
import com.apurebase.arkenv.ParsingException
import com.apurebase.arkenv.argument.ArgumentNameProcessor
import com.apurebase.arkenv.argument.ArkenvArgument
import com.apurebase.arkenv.module.ArkenvModule
import com.apurebase.arkenv.module.ArkenvModuleConfiguration
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.jvmErasure

/**
 * Parses [Arkenv] arguments in configuration classes.
 * @param kClass the class to be parsed.
 * @param args the command line arguments.
 * @param arkenvConfiguration additional arkenv configuration.
 */
class ArkenvParser<T : Any>(
    private val kClass: KClass<T>,
    private val args: Array<String>,
    arkenvConfiguration: ArkenvBuilder
) {
    private val arkenv: Arkenv = Arkenv(configuration = arkenvConfiguration)
    private val className get() = kClass.java.name

    init {
        arkenv.load(args)
    }

    /**
     * Parses the arguments in the provided configuration [instance].
     * @param instance the configuration instance to parse.
     */
    fun parse(instance: T) = parse(instance, null)

    /**
     * Parses the arguments in a class of the given type.
     * @return an instance of the class.
     * @throws ParsingException when parsing was unsuccessful.
     */
    fun parseClass(): T {
        val instance = createInstance(kClass, null)
        parse(instance)
        return instance
    }

    internal fun <R : Any> parse(instance: R, moduleConfiguration: ArkenvModuleConfiguration? = null) {
        initializeDelegates(instance, moduleConfiguration)
        initializeModules(instance)
        arkenv.parsePostLoad()
    }

    internal fun <R : Any> createInstance(kClass: KClass<R>, configuration: ArkenvConfiguration?): R {
        val constructor = kClass.constructors.firstOrNull()
            ?: throw ParsingException(className, IllegalStateException("No valid constructor found"))
        return parseConstructor(constructor, configuration)
    }

    private fun <R> parseConstructor(constructor: KFunction<R>, configuration: ArkenvConfiguration?): R = try {
        val constructorArgs = parseConstructorArgs(constructor.parameters, configuration)
        constructor.callBy(constructorArgs)
    } catch (ex: IllegalArgumentException) {
        throw ParsingException(className, ex)
    }

    private fun parseConstructorArgs(parameters: Collection<KParameter>, configuration: ArkenvConfiguration?): Map<KParameter, Any?> {
        val nameProcessor = ArgumentNameProcessor.get(configuration, arkenv.configuration)
        return parameters
            .filterNot { it.name.isNullOrBlank() }
            .mapNotNull { parseConstructorParameter(it, nameProcessor) }
            .toMap()
    }

    private fun parseConstructorParameter(parameter: KParameter, nameProcessor: ArgumentNameProcessor): Pair<KParameter, Any?>? {
        val name = nameProcessor.process(parameter.name!!)
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

    private fun <R : Any> initializeModules(instance: R) {
        findDelegates(instance, ArkenvModule::class).forEach { (_, delegate) ->
            delegate.initialize(this)
        }
    }

    private fun <R : Any> initializeDelegates(instance: R, moduleConfiguration: ArkenvModuleConfiguration?) {
        findDelegates(instance, ArkenvArgument::class).forEach { (property, delegate) ->
            delegate.initialize(arkenv, property, moduleConfiguration)
            arkenv.delegates.add(delegate)
        }
    }
}
