package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class ManualTests {

    private class SystemInArk : Arkenv() {
        val name: String by argument("-n") {
            acceptsManualInput = true
        }
    }

    @Test fun `parsing system in should work`() {
        val ark = SystemInArk()
        val expected = "this is a test"
        System.setIn(expected.toByteArray().inputStream())
        ark.parse(arrayOf()).expectThat {
            get { name }.isEqualTo(expected)
        }
    }
}
