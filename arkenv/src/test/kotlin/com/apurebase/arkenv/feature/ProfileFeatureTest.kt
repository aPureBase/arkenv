package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.DEPRECATED
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import strikt.api.Assertion
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class ProfileFeatureTest {

    open fun getInstance(): ProfileFeature = ProfileFeature()

    private inner class Ark : Arkenv("Test", configureArkenv { install(getInstance()) }) {
        val port: Int by argument("--port")

        val name: String by argument("--name")

        val other: String? by argument("-o", "--other")
    }

    @Test fun `default profile should parse`() {
        Ark().parse()
            .expectThat { isDefault() }
    }

    @Test fun `nested profile should parse correctly`() {
        Ark().parse("--arkenv-profile", "dev")
            .expectThat { isDevelopment() }
    }

    @Test fun `production profile`() {
        Ark().parse("--arkenv-profile", "prod")
            .expectThat { isProduction() }
    }

    @Test fun `combine with other sources`() {
        Ark().parse("-o", "test", "arkenv-profile=dev")
            .expectThat { isDevelopment("test") }
    }

    @Test fun `set profile via env`() {
        MockSystem(arkenvProfile to "prod")
        Ark().parse().expectThat { isProduction() }
    }

    @Test fun `should be able to override properties`() {
        Ark().parse("arkenv-profile=dev", "--port", "6000").expectThat {
            expect(6000, defaultName)
        }
    }

    @Test fun `should parse multiple comma-separated profiles`() {
        Ark().parse(arkenvProfile, "prod,dev").expectThat {
            expect(devPort, prodName)
        }
    }

    @Test fun `env var should override profile`() {
        MockSystem("PORT" to "6001")
        Ark().parse().expectThat {
            expect(6001, defaultName)
        }
    }

    protected val arkenvProfile = "ARKENV_PROFILE"
    private val prodPort = 443
    private val devPort = 5000
    private val prodName = "production"
    private val defaultName = "profile-test"

    private fun Assertion.Builder<Ark>.isDefault() = expect(80, defaultName, null)

    private fun Assertion.Builder<Ark>.isDevelopment(other: String? = null) = expect(devPort, defaultName, other)

    private fun Assertion.Builder<Ark>.isProduction() = expect(prodPort, prodName, null)

    private fun Assertion.Builder<Ark>.expect(port: Int, name: String, other: String? = null) {
        get { this.port }.isEqualTo(port)
        get { this.name }.isEqualTo(name)
        get { this.other }.isEqualTo(other)
    }
}
