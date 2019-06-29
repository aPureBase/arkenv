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
import org.junit.jupiter.api.assertThrows
import strikt.api.Assertion
import strikt.assertions.isEqualTo

internal class ProfileFeatureTest {

    private class Ark : Arkenv("Test", configureArkenv {
        install(ProfileFeature())
    }) {
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

    @Disabled(DEPRECATED) @Test fun `should throw when profile cannot be found`() {
        assertThrows<IllegalArgumentException> {
            Ark().parse("--arkenv-profile", "wrong")
        }.message.shouldNotBeNull()
    }

    @Test fun `set profile via env`() {
        MockSystem("ARKENV_PROFILE" to "prod")
        Ark().parse().expectThat { isProduction() }
    }

    @Test fun `should be able to override properties`() {
        Ark().parse("arkenv-profile=dev", "--port", "6000").expectThat {
            expect(6000, defaultName)
        }
    }

    @Test fun `should parse multiple comma-separated profiles`() {
        Ark().parse("ARKENV_PROFILE", "prod,dev").expectThat {
            expect(devPort, prodName)
        }
    }

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
