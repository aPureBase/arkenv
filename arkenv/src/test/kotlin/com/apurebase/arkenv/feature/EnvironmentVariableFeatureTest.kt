package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.MissingArgumentException
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo

internal class EnvironmentVariableFeatureTest {

    private class EnvArgs(envPrefix: String? = null, withEnv: Boolean = true) : Arkenv("Test", configureArkenv {
        if (withEnv) +EnvironmentVariableFeature(envPrefix = envPrefix)
        else -EnvironmentVariableFeature()
    }) {
        val arg: String by argument()
    }

    @Test fun `when env is off should not use env vars`() {
        MockSystem("ARG" to prefix)
        assertThrows<MissingArgumentException> { EnvArgs(withEnv = false).arg }
        EnvArgs().assertParsed()
    }

    @Test fun `env prefix should work`() {
        MockSystem(testArg to prefix)
        EnvArgs(envPrefix)
            .parse()
            .assertParsed()
    }

    @Test fun `env prefix can be set by argument`() {
        MockSystem(testArg to prefix, "ARKENV_ENV_PREFIX" to envPrefix)
        EnvArgs()
            .parse()
            .assertParsed()
    }

    private val prefix = "prefix"
    private val envPrefix = "test"
    private val testArg = "TEST_ARG"
    private fun EnvArgs.assertParsed() = expectThat { get { arg } isEqualTo prefix }
}
