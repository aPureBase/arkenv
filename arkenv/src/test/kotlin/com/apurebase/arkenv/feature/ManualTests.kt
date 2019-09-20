package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class ManualTests {

    private class SystemInArk : Arkenv() {
        val manualName: String by argument {
            acceptsManualInput = true
        }
    }

    @Test fun `parsing system in should work`() {
        val expected = "this is a test"
        System.setIn(expected.toByteArray().inputStream())
        SystemInArk()
            .parse()
            .expectThat {
                get { manualName }.isEqualTo(expected)
            }
    }
}
