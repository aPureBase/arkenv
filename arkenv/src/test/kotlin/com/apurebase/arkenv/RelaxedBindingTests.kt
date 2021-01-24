package com.apurebase.arkenv

import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class RelaxedBindingTests {

    private class Ark(configuration: ArkenvBuilder = ArkenvBuilder()) : Arkenv("Test", configuration) {
        val dbPort: Int by argument()
    }

    @Nested
    private inner class Cli {
        private fun verifyCli(key: String) = Ark().parse(key, "5").verify()

        @Test fun `UPPERCASE`() {
            verifyCli("--DB_PORT")
        }

        @Test fun `camelCase`() {
            verifyCli("--dbPort")
        }

        @Test fun `kebab-case`() {
            verifyCli("--db-port")
        }
    }

    @Nested
    private inner class Env {
        private fun verifyEnv() = Ark().parse().verify()

        @Test fun `UPPERCASE`() {
            MockSystem("DB_PORT" to "5")
            verifyEnv()
        }

        @Test fun `camelCase`() {
            MockSystem("dbPort" to "5")
            verifyEnv()
        }

        @Test fun `kebab-case`() {
            MockSystem("db-port" to "5")
            verifyEnv()
        }
    }

    @Nested
    private inner class Property {
        private fun verifyProperty(file: String) {
            val ark = Ark(configureArkenv {
                install(PropertyFeature("$file.properties", listOf("binding")))
            })
            ark.parse().verify()
        }

        @Test fun `UPPERCASE`() {
            verifyProperty("uppercase")
        }

        @Test fun `camelCase`() {
            verifyProperty("camel")
        }

        @Test fun `kebab-case`() {
            verifyProperty("kebab")
        }
    }

    @Nested
    private inner class Assignment {
        private fun verifyAssignment(arg: String) = Ark().parse(arg).verify()

        @Test fun `UPPERCASE`() {
            verifyAssignment("DB_PORT=5")
        }

        @Test fun `camelCase`() {
            verifyAssignment("dbPort=5")
        }

        @Test fun `kebab-case`() {
            verifyAssignment("db-port=5")
        }
    }

    private fun Ark.verify() = expectThat {
        get { dbPort }.isEqualTo(5)
    }
}
