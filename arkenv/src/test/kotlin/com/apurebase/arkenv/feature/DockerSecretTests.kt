package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.util.argument
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
        val apiKey: String by argument()
    }

    @Test fun `should correctly load value from file`() {
        MockSystem(apiKeyFile to pathToDockerSecretFile)
        Ark()
            .parse()
            .assertEnvSecretLoaded()
    }

    @Test fun `env secrects can be enabled via argument`() {
        MockSystem(apiKeyFile to pathToDockerSecretFile)
        Ark(false)
            .parse("ARKENV_ENV_SECRETS", "1")
            .assertEnvSecretLoaded()
    }

    private val apiKeyFile = "API_KEY_FILE"
    private fun Ark.assertEnvSecretLoaded() = expectThat { get { apiKey }.isEqualTo("EXPECTED_CONTENT") }
}
