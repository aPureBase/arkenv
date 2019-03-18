package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class DockerSecretTests {

    private val pathToDockerSecretFile = "src/test/resources/file_containing_secret.txt"

    @Test fun `should correctly load value from file`() {
        val ark = object : Arkenv() {
            init {
                install(EnvironmentVariableFeature(enableEnvSecrets = true))
            }

            val apiKey: String by argument("--api-key")
        }
        MockSystem("API_KEY_FILE" to pathToDockerSecretFile)
        ark.parse(arrayOf()).expectThat {
            get { apiKey }.isEqualTo("EXPECTED_CONTENT")
        }
    }
}
