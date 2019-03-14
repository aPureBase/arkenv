package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.assertions.isEqualTo

class ProfileFeatureCustomizationTest {

    private open class Ark : Arkenv() {
        val port: Int by argument("--port")
        val name: String by argument("--name")
        val other: String? by argument("-o", "--other")
    }

    private class PrefixArk(prefix: String, vararg arguments: String) : Ark() {
        init {
            install(ProfileFeature(prefix = prefix))
            parse(arguments.toList().toTypedArray())
        }
    }

    @Nested
    inner class Prefix {

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

    private class LocationArk(locations: Collection<String>, vararg arguments: String) : Ark() {
        init {
            install(ProfileFeature(locations = locations))
            parse(arguments.toList().toTypedArray())
        }
    }

    @Nested
    inner class Location {
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
        get { name }.isEqualTo("profile-config")
        get { port }.isEqualTo(777)
    }
}
