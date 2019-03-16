package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class PlaceholderTests {

    private class Ark(doInstall: Boolean = false) : Arkenv() {
        init {
            if (doInstall) {
                install(PropertyFeature("placeholders.properties"))
            }
        }

        val name: String by argument("--app-name")
        val description: String by argument("--app-description")
    }

    @Test fun `can refer to previously defined arg in properties`() {
        Ark(doInstall = true)
            .parse(arrayOf())
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

    private fun Ark.verify() {
        expectThat {
            get { name }.isEqualTo("MyApp")
            get { description }.isEqualTo("MyApp is a Arkenv application")
        }
    }
}
