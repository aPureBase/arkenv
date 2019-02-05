package com.apurebase.arkenv

import strikt.api.Assertion
import java.io.File

fun <T> T.expectThat(block: Assertion.Builder<T>.() -> Unit) = strikt.api.expectThat(this, block)

fun getTestResourcePath(name: String) = File("src/test/resources/$name").absolutePath
