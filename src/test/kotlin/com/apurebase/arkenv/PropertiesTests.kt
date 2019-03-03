package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo

class PropertiesTests {

    private class PropertiesArk(propertiesFile: String) : Arkenv(propertiesFile = propertiesFile) {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
        val multiLine: String by argument("--multi-string")
    }

    @Test fun `should load properties file`() {
        verify("app.properties")
    }

    @Test fun `should load lowercase properties`() {
        verify("app_lower.properties")
    }

    private fun verify(path: String) {
        PropertiesArk(path)
            .parse(arrayOf()).expectThat {
                get { mysqlPassword }.isEqualTo("this_is_expected")
                get { port }.isEqualTo(5050)
                get { multiLine }.isEqualTo("this stretches lines")
            }
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = PropertiesArk("does_not_exist.env")
        assertThrows<NullPointerException> {
            ark.parse(arrayOf())
        }
    }
}
