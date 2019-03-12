package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.File

class PropertyFeatureTests {

    private class PropertiesArk(propertiesFile: String) : Arkenv() {
        init {
            install(PropertyFeature(propertiesFile))
        }
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

    @Test fun `should be able to load from file`() {
        val name = "file_based_props.properties"
        val content = this::class.java.classLoader.getResource("app.properties").readText()
        File(name).let {
            it.deleteOnExit()
            it.writeText(content)
        }
        verify(name)
    }
}
