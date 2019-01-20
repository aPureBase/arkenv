package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class DockerSecretTests {

    @Test fun `should correctly load value from file`() {
        val ark = object : Arkenv(enableEnvSecrets = true) {
            val apiKey: String by argument("--api-key")
        }
        MockSystem("API_KEY_FILE" to "src/test/resources/file_containing_secret.txt")
        ark.parse(arrayOf()).expectThat {
            get { apiKey }.isEqualTo("EXPECTED_CONTENT")
        }
    }
}
