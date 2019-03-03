package com.apurebase.arkenv

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

internal class ProfilesTest {

    @Test fun `nested profile should parse correctly`() {
        val args = arrayOf("--profile", "app_lower.properties", "-p", "20")
        val profile = Ark().parse(args).profile
        Profile(profile).parse(args).port shouldEqualTo 20
    }

    private class Ark : Arkenv() {
        val profile: String by argument("--profile") {
            defaultValue = { "dev" }
        }
    }

    private class Profile(name: String) : Arkenv(propertiesFile = name) {
        val port: Int by argument("-p", "--port")
    }
}
