package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.expectThat
import com.apurebase.arkenv.parse
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
