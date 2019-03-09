package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class ProfileFeatureTest {

    private class Ark : Arkenv() {
        init {
            install(ProfileFeature())
        }

        val port: Int by argument("--port")
        val name: String by argument("--name")
    }

    @Test fun `default profile should parse`() {
        val args = arrayOf<String>()
        Ark()
            .parse(args)
            .expectThat {
                get { port }.isEqualTo(80)
                get { name }.isEqualTo("profile-test")
            }
    }

    @Test fun `nested profile should parse correctly`() {
        val args = arrayOf("--profile", "dev")
        Ark()
            .parse(args)
            .expectThat {
                get { port }.isEqualTo(5000)
                get { name }.isEqualTo("profile-test")
            }
    }

}
