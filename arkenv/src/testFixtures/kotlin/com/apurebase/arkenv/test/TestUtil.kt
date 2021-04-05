package com.apurebase.arkenv.test

import com.apurebase.arkenv.Arkenv
import strikt.api.Assertion
import strikt.assertions.isEqualTo
import java.io.File

fun <T> T.expectThat(block: Assertion.Builder<T>.() -> Unit) = strikt.api.expectThat(this, block)

infix fun <T: Any?> T.expectIsEqual(expected: T) = expectThat { isEqualTo(expected) }

fun getTestResourcePath(name: String): String = File("src/test/resources/$name").absolutePath

fun <T : Arkenv> T.parse(vararg arguments: String) = apply { parseArguments(arguments) }

fun getTestResource(name: String) = MockSystem::class.java.classLoader.getResource(name)!!.readText()

val dotEnvPath = getTestResourcePath(".env")
