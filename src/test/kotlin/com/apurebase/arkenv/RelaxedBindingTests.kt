package com.apurebase.arkenv

import com.apurebase.arkenv.feature.PropertyFeature
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class RelaxedBindingTests {

    private class Ark : Arkenv() {
        val dbPort: Int by argument("--db-port")
    }

    @Nested
    inner class Cli {
        private fun verifyCli(key: String) = Ark().parse(arrayOf(key, "5")).verify()

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
    inner class Env {
        private fun verifyEnv() = Ark().parse(arrayOf()).verify()

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
    inner class Property {
        private fun verifyProperty(file: String) {
            val ark = Ark()
            ark.install(PropertyFeature("$file.properties", listOf("binding")))
            ark.parse(arrayOf()).verify()
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

    private fun Ark.verify() = expectThat {
        get { dbPort }.isEqualTo(5)
    }
}
