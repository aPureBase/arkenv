package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class PropertiesTests {

    private class PropertiesArk(override val propertiesFilePath: String) : Arkenv(propertiesFilePath = "app.properties") {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
        val multiLine: String by argument("--multi-string")
    }

    @Test fun `should load from properties file`() {
        val path = getTestResourcePath("app.properties")
        PropertiesArk(path).parse(arrayOf()).expectThat {
            get { mysqlPassword }.isEqualTo("this_is_expected")
            get { port }.isEqualTo(5050)
            get { multiLine }.isEqualTo("this stretches lines")
        }
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = PropertiesArk("does_not_exit.env")
        assertThrows<FileNotFoundException> {
            ark.parse(arrayOf())
        }
    }
}