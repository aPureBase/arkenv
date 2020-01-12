package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import com.apurebase.arkenv.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaceholderTests {

    private inner class Ark(config: ArkenvBuilder.() -> Unit = {}) : Arkenv(configureArkenv(config)) {
        val name: String by argument(appNameArg)
        val description: String by argument(appDescArg)
    }

    @Test fun `can refer to previously defined arg in properties`() {
        Ark { install(PropertyFeature("placeholders.properties")) }
            .parse()
            .verify()
    }

    @Test fun `can refer in cli arg`() {
        Ark()
            .parse(appNameArg, appName, appDescArg, "\${app_name} is a Arkenv application")
            .verify()
    }

    @Test fun `can refer in env var`() {
        MockSystem(
            "APP_NAME" to appName,
            "APP_DESCRIPTION" to "\${app_name} is a Arkenv application"
        )
        Ark()
            .parse()
            .verify()
    }

    @Test fun `should allow ${ expression`() {
        Ark().parse(appNameArg, "\${", appDescArg, "test")
            .expectThat {
                get { name }.isEqualTo("\${")
                get { description }.isEqualTo("test")
            }
    }

    @Test fun `multiple replacements`() {
        val name = "LongTextForTheName"
        Ark()
            .parse(appNameArg, name, appDescArg, "\${app_name} is \${app_name}")
            .expectThat {
                get { description }.isEqualTo("$name is $name")
            }
    }

    @Test fun `refer to undeclared arg`() {
        val testValue = "hello this is a test value"
        val expected = "$testValue is not declared"
        Ark()
            .parse(appNameArg, appName, "--test", testValue, appDescArg, "\${test} is not declared")
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `refer to undeclared env var`() {
        val testValue = "hello this is a test value"
        val expected = "$testValue is not declared"
        MockSystem("TEST" to testValue)
        Ark()
            .parse(appNameArg, appName, appDescArg, "\${test} is not declared")
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `refer to undeclared keyValue`() {
        val testValue = "this_is_expected"
        val expected = "$testValue is not declared"

        Ark { install(EnvironmentVariableFeature(dotEnvFilePath = dotEnvPath)) }
            .parse(appNameArg, appName, appDescArg, "\${mysql_password} is not declared")
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `refer to env from profile`() {
        Ark().parse("--arkenv-profile", "placeholder")
            .verify()
    }

    @Test fun `refer and reparse`() {
        Ark()
            .parse("--arkenv-profile", "placeholder")
            .parse("--arkenv-profile", "placeholder")
            .verify()
    }

    @Test fun `should throw when placeholder is not found`() {
        val ark = Ark()
        assertThrows<MissingArgumentException> {
            ark.parse(appNameArg, appName, appDescArg, "\${app_missing} is a Arkenv application")
        }
    }

    private fun Ark.verify() = expectThat {
        get { name }.isEqualTo(appName)
        get { description }.isEqualTo("$appName is a Arkenv application")
    }

    private val appNameArg = "--app-name"
    private val appName = "MyApp"
    private val appDescArg = "--app-description"
}
