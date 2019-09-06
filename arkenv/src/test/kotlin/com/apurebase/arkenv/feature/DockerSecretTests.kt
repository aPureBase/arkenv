package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class DockerSecretTests {

    private val pathToDockerSecretFile = "src/test/resources/file_containing_secret.txt"

    private class Ark(envSecrets: Boolean = true) : Arkenv("Test", configureArkenv {
        install(EnvironmentVariableFeature(enableEnvSecrets = envSecrets))
    }) {
        val apiKey: String by argument("--api-key")
    }

    @Test fun `should correctly load value from file`() {
        val ark = Ark()
        MockSystem("API_KEY_FILE" to pathToDockerSecretFile)
        ark.parse()
            .assertEnvSecretLoaded()
    }

    @Test fun `env secrects can be enabled via argument`() {
        MockSystem("API_KEY_FILE" to pathToDockerSecretFile)
        Ark(false)
            .parse("ARKENV_ENV_SECRETS", "1")
            .assertEnvSecretLoaded()
    }

    private fun Ark.assertEnvSecretLoaded() = expectThat { get { apiKey }.isEqualTo("EXPECTED_CONTENT") }
}
