package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test

internal class EnvironmentVariableFeatureTest {

    private class EnvArgs(withEnv: Boolean) : Arkenv() {
        init {
            programName = "Test"
            if (withEnv) install(EnvironmentVariableFeature())
            else uninstall(EnvironmentVariableFeature())
        }
        val arg: String by argument("-a", "--arg")
    }

    @Test fun `when env is off should not use env vars`() {
        MockSystem("ARG" to "test")

        EnvArgs(false).let {
            it::arg shouldThrow IllegalArgumentException::class
        }
        EnvArgs(true).arg shouldBeEqualTo "test"
    }
}
