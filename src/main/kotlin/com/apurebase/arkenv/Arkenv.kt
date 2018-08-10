package com.apurebase.arkenv

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

abstract class Arkenv(
    args: Array<String>,
    val programName: String = "Arkenv",
    val withEnv: Boolean = true,
    val envPrefix: String = "",
    val argumentPrefix: String = "-"
) {

    /**
     * Manually parse the arguments, clearing all previously set ones
     */
    fun parse(args: Array<String>) {
        argList.clear()
        argList.addAll(args)
    }

    val argList = args.toMutableList()

    open val help: Boolean by argument("-h", "--help") {
        isHelp = true
    }

    inline fun <T : Any> argument(
        names: List<String>,
        isMainArg: Boolean = false,
        block: Argument<T>.() -> Unit = {}
    ): ArgumentDelegate<T> = when {
        names.isEmpty() && !isMainArg -> throw IllegalArgumentException("No argument names provided")
        else -> {
            val argumentConfig = Argument<T>(names).also {
                it.withEnv = withEnv
                it.envPrefix = envPrefix
                it.isMainArg = isMainArg
            }.apply(block)
            val isHelp = if (argumentConfig.isHelp) false else help
            ArgumentDelegate(isHelp, argList, argumentConfig, argumentPrefix)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        val indent = "    "
        sb.append("$programName: \n")
        this::class.declaredMemberProperties
            .filter { it.javaField != null }
            .filter { ArgumentDelegate::class.java.isAssignableFrom(it.javaField!!.type) }
            .map { prop ->
                val javaField = prop.javaField!!
                prop.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val delegateInstance = javaField.get(this) as ArgumentDelegate<*>
                prop to delegateInstance
            }
            .forEach { (prop, delegate) ->
                sb
                    .append(indent)
                    .append(delegate.argument.names)
                    .append(indent, 2)
                    .append(delegate.argument.description)
                    .appendln()
                    .append(indent, 2)
                    .append(prop.name)
                    .append(indent, 2)
                    .append(delegate.getValue(this, prop))
                    .appendln()
            }

        return sb.toString()
    }

    inline fun <T : Any> argument(name: String, block: Argument<T>.() -> Unit = {}): ArgumentDelegate<T> =
        argument(listOf(name), false, block)

    inline fun <T : Any> argument(
        nameOne: String,
        nameTwo: String,
        block: Argument<T>.() -> Unit = {}
    ): ArgumentDelegate<T> =
        argument(listOf(nameOne, nameTwo), false, block)

    inline fun <T : Any> mainArgument(block: Argument<T>.() -> Unit = {}): ArgumentDelegate<T> =
        argument(listOf(), true, block)

    private fun StringBuilder.append(value: String, times: Int): StringBuilder = apply {
        repeat(times) { append(value) }
    }

}