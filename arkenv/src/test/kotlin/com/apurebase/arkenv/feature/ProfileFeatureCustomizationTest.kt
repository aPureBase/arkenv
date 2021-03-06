package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.ArkenvBuilder
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.assertions.isEqualTo

class ProfileFeatureCustomizationTest {

    private open class Ark(config: ArkenvBuilder = ArkenvBuilder()) : Arkenv("Test", config) {
        val port: Int by argument()
        val name: String by argument()
        val other: String? by argument()
    }

    private class PrefixArk(prefix: String, vararg arguments: String) : Ark(configureArkenv {
        +ProfileFeature(prefix = prefix)
    }) {
        init {
            parse(*arguments)
        }
    }

    @Nested
    private inner class Prefix {

        @Test fun `change via param`() {
            PrefixArk("config").expectThat { isMasterFile() }
        }

        @Test fun `change via cli`() {
            PrefixArk("application", "--arkenv-profile-prefix", "config").expectThat { isMasterFile() }
        }

        @Test fun `change via env`() {
            MockSystem("ARKENV_PROFILE_PREFIX" to "config")
            PrefixArk("application").expectThat { isMasterFile() }
        }
    }

    private class LocationArk(locations: Collection<String>, vararg arguments: String) : Ark(configureArkenv {
        +ProfileFeature(locations = locations)
    }) {
        init {
            parse(*arguments)
        }
    }

    @Nested
    private inner class Location {
        private val path = "custom/path"

        @Test fun `change via param`() {
            LocationArk(listOf(path)).expectThat { isMasterFile() }
        }

        @Test fun `change via cli`() {
            LocationArk(listOf(), "--arkenv-profile-location", path).expectThat { isMasterFile() }
        }

        @Test fun `change via env`() {
            MockSystem("ARKENV_PROFILE_LOCATION" to path)
            LocationArk(listOf()).expectThat { isMasterFile() }
        }
    }

    private fun <T : Ark> Assertion.Builder<T>.isMasterFile() {
        get { name } isEqualTo "profile-config"
        get { port } isEqualTo 777
    }
}
