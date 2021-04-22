package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.test.Nullable
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.get
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * Verify the behavior of the [CliFeature].
 */
internal class CliFeatureTest {

    @Test fun `resolve undeclared get calls`() {
        val key = "--undeclared"
        val expected = "expected"
        val configuration = Nullable().parse(key, expected)
        expectThat(configuration).get { get(key) } isEqualTo expected
    }

    @Test fun `resolve undeclared assignments`() {
        val key = "undeclared"
        val expected = "expected"
        val configuration = Nullable().parse("$key=$expected")
        expectThat(configuration).get { get(key) } isEqualTo expected
    }

    @Test fun `single quote in value should parse entire string`() {
        val expected = "D'vloper"
        val configuration = Nullable().parse("-s", expected)
        expectThat(configuration).get { str } isEqualTo expected
    }
}
