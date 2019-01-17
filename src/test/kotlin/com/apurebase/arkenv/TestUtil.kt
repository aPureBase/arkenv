package com.apurebase.arkenv

import strikt.api.Assertion

fun <T> T.expectThat(block: Assertion.Builder<T>.() -> Unit) = strikt.api.expectThat(this, block)