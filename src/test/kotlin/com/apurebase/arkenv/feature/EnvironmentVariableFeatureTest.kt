package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class EnvironmentVariableFeatureTest {

    private class EnvArgs(withEnv: Boolean, config: ArkenvBuilder.() -> Unit = {}) : Arkenv("Test", {
        if (withEnv) install(EnvironmentVariableFeature())
        else uninstall(EnvironmentVariableFeature())
        config()
    }) {
        val arg: String by argument("-a", "--arg")
    }

    @Test fun `when env is off should not use env vars`() {
        MockSystem("ARG" to "test")

        EnvArgs(false).let {
            it::arg shouldThrow IllegalArgumentException::class
        }
        EnvArgs(true).arg shouldBeEqualTo "test"
    }

    @Test fun `env prefix should work`() {
        val ark = EnvArgs(false) {
            val feature = EnvironmentVariableFeature(envPrefix = "test")
            install(feature)
//            features.shouldHaveKey(feature.getKeyValPair().first) TODO
        }

        MockSystem("TEST_ARG" to "prefix")

        ark.parse(arrayOf()).expectThat {
            get { arg }.isEqualTo("prefix")
        }
    }
}
