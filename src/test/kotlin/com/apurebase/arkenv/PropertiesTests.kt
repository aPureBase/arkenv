package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class PropertiesTests {

    private class PropertiesArk(override val propertiesFile: PropertiesFile) : Arkenv(propertiesFile = propertiesFile) {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
        val multiLine: String by argument("--multi-string")
    }

    @Test fun `should load from properties file`() {
        verify("app.properties")
    }

    @Test fun `using a different classloader should fail`() {
        val pf = PropertiesFile("app.properties", Arkenv::class.java.classLoader)
        assertThrows<Exception> {
            PropertiesArk(pf).parse(arrayOf()).expectThat {
                get { mysqlPassword }.isEqualTo("this_is_expected")
                get { port }.isEqualTo(5050)
                get { multiLine }.isEqualTo("this stretches lines")
            }
        }.also { println(it) }
    }

    private fun verify(path: String) {
        PropertiesArk(PropertiesFile(path, this::class.java.classLoader))
            .parse(arrayOf()).expectThat {
                get { mysqlPassword }.isEqualTo("this_is_expected")
                get { port }.isEqualTo(5050)
                get { multiLine }.isEqualTo("this stretches lines")
            }
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = PropertiesArk(PropertiesFile("does_not_exit.env", this::class.java.classLoader))
        assertThrows<NullPointerException> {
            ark.parse(arrayOf())
        }
    }
}
