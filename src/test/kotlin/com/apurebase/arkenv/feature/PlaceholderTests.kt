package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaceholderTests {

    private class Ark(config: ArkenvBuilder.() -> Unit = {}) : Arkenv(configuration = config) {
        val name: String by argument("--app-name")
        val description: String by argument("--app-description")
    }

    @Test fun `can refer to previously defined arg in properties`() {
        val ark = Ark {
            install(PropertyFeature("placeholders.properties"))
        }
        ark.parse(arrayOf())
            .verify()
    }

    @Test fun `can refer in cli arg`() {
        Ark()
            .parse(arrayOf("--app-name", "MyApp", "--app-description", "\${app_name} is a Arkenv application"))
            .verify()
    }

    @Test fun `can refer in env var`() {
        MockSystem(
            "APP_NAME" to "MyApp",
            "APP_DESCRIPTION" to "\${app_name} is a Arkenv application"
        )
        Ark()
            .parse(arrayOf())
            .verify()
    }

    @Test fun `should allow ${ expression`() {
        Ark().parse(arrayOf("--app-name", "\${", "--app-description", "test"))
            .expectThat {
                get { name }.isEqualTo("\${")
                get { description }.isEqualTo("test")
            }
    }

    @Test fun `multiple replacements`() {
        val name = "LongTextForTheName"
        Ark()
            .parse(arrayOf("--app-name", name, "--app-description", "\${app_name} is \${app_name}"))
            .expectThat {
                get { description }.isEqualTo("$name is $name")
            }
    }

    @Test fun `refer to undeclared arg`() {
        val testValue = "hello this is a test value"
        val expected = "$testValue is not declared"
        Ark()
            .parse(arrayOf("--app-name", "MyApp", "--test", testValue, "--app-description", "\${test} is not declared"))
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `refer to undeclared env var`() {
        val testValue = "hello this is a test value"
        val expected = "$testValue is not declared"
        MockSystem("TEST" to testValue)
        Ark()
            .parse(arrayOf("--app-name", "MyApp", "--app-description", "\${test} is not declared"))
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `refer to undeclared keyValue`() {
        val testValue = "this_is_expected"
        val expected = "$testValue is not declared"

        val ark = Ark {
            install(EnvironmentVariableFeature(dotEnvFilePath = getTestResourcePath(".env")))
        }
        ark.parse(arrayOf("--app-name", "MyApp", "--app-description", "\${mysql_password} is not declared"))
            .expectThat {
                get { description }.isEqualTo(expected)
            }
    }

    @Test fun `should throw when placeholder is not found`() {
        val ark = Ark()
        assertThrows<IllegalArgumentException> {
            ark.parse(arrayOf("--app-name", "MyApp", "--app-description", "\${app_missing} is a Arkenv application"))
        }
    }

    private fun Ark.verify() = expectThat {
        get { name }.isEqualTo("MyApp")
        get { description }.isEqualTo("MyApp is a Arkenv application")
    }
}
