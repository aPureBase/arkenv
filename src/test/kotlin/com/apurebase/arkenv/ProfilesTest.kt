package com.apurebase.arkenv

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

internal class ProfilesTest {

    @Test fun `nested profile should parse correctly`() {
        val args = arrayOf("--profile", "app_lower.properties")
        val profile = Ark().parse(args).profile
        Profile(profile).parse(args).port shouldEqualTo 5050
    }

    private class Ark : Arkenv() {
        val profile: String by argument("--profile") {
            defaultValue = { "dev" }
        }
    }

    private class Profile(name: String) : Arkenv() {
        init {
            install(PropertyFeature(name))
        }

        val port: Int by argument("-p", "--database-port")
    }
}
