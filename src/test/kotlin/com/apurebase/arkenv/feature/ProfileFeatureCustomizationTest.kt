package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.assertions.isEqualTo

class ProfileFeatureCustomizationTest {

    private class Ark(prefix: String, vararg arguments: String) : Arkenv() {
        init {
            install(ProfileFeature(prefix = prefix))
            parse(arguments.toList().toTypedArray())
        }

        val port: Int by argument("--port")
        val name: String by argument("--name")

        val other: String? by argument("-o", "--other")
    }

    @Nested
    inner class Prefix {

        @Test fun `change via param`() {
            Ark("config").expectThat { isMasterFile() }
        }

        @Test fun `change via cli`() {
            Ark("application", "--arkenv-config-name", "config").expectThat { isMasterFile() }
        }

        @Test fun `change via env`() {
            MockSystem("ARKENV_CONFIG_NAME" to "config")
            Ark("application").expectThat { isMasterFile() }
        }
    }

    private fun Assertion.Builder<Ark>.isMasterFile() {
        get { name }.isEqualTo("profile-config")
        get { port }.isEqualTo(777)
    }
}
