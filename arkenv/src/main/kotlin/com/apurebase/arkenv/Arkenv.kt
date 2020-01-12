package com.apurebase.arkenv

import com.apurebase.arkenv.feature.EnvironmentVariableFeature

/**
 * The base class that provides the argument parsing capabilities.
 * Extend this to define your own arguments.
 * @param configuration
 */
abstract class Arkenv(internal val configuration: ArkenvBuilder = ArkenvBuilder()) {

    @Deprecated("Will be removed in a future major version.")
    constructor(
        programName: String = "Arkenv",
        configuration: ArkenvBuilder = ArkenvBuilder()
    ) : this(configuration.also { it.programName = programName })

    internal val argList = mutableListOf<String>()
    private val keyValue = mutableMapOf<String, String>()
    internal val delegates = mutableListOf<ArgumentDelegate<*>>()

    val help: Boolean by argument("-h", "--help") { isHelp = true }

    val programName: String get() = getOrNull("--arkenv-application-name") ?: configuration.programName

    internal fun parseArguments(args: Array<out String>) = with(configuration) {
        if (clearInputBeforeParse) clear()
        argList.addAll(args)
        features.forEach { it.onLoad(this@Arkenv) }
        features.forEach { it.postLoad(this@Arkenv) }
        process()
        parse(delegates)
        features.forEach { it.finally(this@Arkenv) }
        modules.forEach { parse(it.delegates) }
        if (clearInputAfterParse) clear()
    }

    /**
     * Clears all loaded data from memory.
     */
    protected fun clear() {
        argList.clear()
        keyValue.clear()
    }

    override fun toString(): String = StringBuilder().apply {
        val indent = "    "
        val doubleIndent = indent + indent
        append("$programName: \n")
        delegates.forEach { delegate ->
            append(indent)
                .append(delegate.argument.names)
                .append(doubleIndent)
                .append(delegate.argument.description)
                .appendln()
                .append(doubleIndent)
                .append(delegate.property.name)
                .append(doubleIndent)
                .append(delegate.getValue(this@Arkenv, delegate.property))
                .appendln()
        }
    }.toString()

    operator fun set(key: String, value: String) {
        keyValue[key.toSnakeCase()] = value
    }

    private fun process() {
        configuration.processorFeatures.forEach { it.arkenv = this }
        keyValue.replaceAll(::processValue)
    }

    private fun processValue(key: String, value: String): String = configuration
        .processorFeatures
        .fold(value) { acc, feature -> feature.process(key, acc) }

    /**
     * Retrieves the parsed value for the given [key] or null if not found.
     * All parsed but not declared arguments are available.
     * @param key the non-case-sensitive name of the argument
     * @return The value for the [key] or null if not found
     */
    fun getOrNull(key: String): String? {
        val formattedKey = key.toSnakeCase()
        return keyValue[formattedKey] ?: findFeature<EnvironmentVariableFeature>()?.getEnv(formattedKey, false)
    }

    /**
     * Retrieves all parsed data as a Map.
     */
    fun getAll(): Map<String, String> = keyValue

    internal fun parseDelegate(delegate: ArgumentDelegate<*>, names: List<String>): List<String> {
        val onParseValues = configuration.features
            .mapNotNull { it.onParse(this, delegate) }
            .map { processValue("", it) }
        return if (onParseValues.isNotEmpty()) onParseValues
        else names.filterNot(String::isSimpleName).mapNotNull(::getOrNull)
    }

    private fun parse(delegates: Collection<ArgumentDelegate<*>>) = delegates
        .sortedBy { it.argument.isMainArg }
        .forEach {
            configuration.features.forEach { feature ->
                feature.configure(it.argument)
            }
            it.reset()
            it.getValue(this, it.property)
        }
}
