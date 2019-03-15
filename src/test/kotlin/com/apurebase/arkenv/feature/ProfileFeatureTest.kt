package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.Assertion
import strikt.assertions.isEqualTo

internal class ProfileFeatureTest {

    private class Ark(vararg arguments: String) : Arkenv() {
        init {
            install(ProfileFeature())
            parse(arguments.toList().toTypedArray())
        }

        val port: Int by argument("--port")
        val name: String by argument("--name")

        val other: String? by argument("-o", "--other")
    }

    @Test fun `default profile should parse`() {
        Ark()
            .expectThat { isDefault() }
    }

    @Test fun `nested profile should parse correctly`() {
        Ark("--arkenv-profile", "dev")
            .expectThat { isDevelopment() }
    }

    @Test fun `production profile`() {
        Ark("--arkenv-profile", "prod")
            .expectThat { isProduction() }
    }

    @Test fun `combine with other sources`() {
        Ark("-o", "test", "arkenv-profile=dev")
            .expectThat { isDevelopment("test") }
    }

    @Test fun `should throw when profile cannot be found`() {
        assertThrows<NullPointerException> {
            Ark("--arkenv-profile", "wrong")
        }
    }

    @Test fun `set profile via env`() {
        MockSystem("ARKENV_PROFILE" to "prod")
        Ark().expectThat { isProduction() }
    }

    @Test fun `should be able to override properties`() {
        Ark("arkenv-profile=dev", "--port", "6000").expectThat {
            expect(6000, "profile-test")
        }
    }

    private fun Assertion.Builder<Ark>.isDefault() = expect(80, "profile-test", null)

    private fun Assertion.Builder<Ark>.isDevelopment(other: String? = null) = expect(5000, "profile-test", other)

    private fun Assertion.Builder<Ark>.isProduction() = expect(443, "production", null)

    private fun Assertion.Builder<Ark>.expect(port: Int, name: String, other: String? = null) {
        get { this.port }.isEqualTo(port)
        get { this.name }.isEqualTo(name)
        get { this.other }.isEqualTo(other)
    }
}
