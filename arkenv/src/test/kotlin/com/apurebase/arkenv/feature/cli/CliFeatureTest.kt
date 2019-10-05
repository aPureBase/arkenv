package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.get
import com.apurebase.arkenv.test.Nullable
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class CliFeatureTest {

    @Test fun `resolve undeclared get calls`() {
        val key = "--undeclared"
        val expected = "expected"
        Nullable().parse(key, expected).expectThat {
            get { get(key) }.isEqualTo(expected)
        }
    }

    @Test fun `resolve undeclared assignments`() {
        val key = "undeclared"
        val expected = "expected"
        Nullable().parse("$key=$expected").expectThat {
            get { get(key) }.isEqualTo(expected)
        }
    }

}
