package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.*
import strikt.assertions.isEqualTo
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PropertyFeatureTests {

    private class PropertiesArk(propertiesFile: String, locations: List<String>) : Arkenv() {
        init {
            install(PropertyFeature(propertiesFile, locations))
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

    private fun verify(
        path: String,
        port: Int = 5050,
        pw: String = "this_is_expected",
        locations: List<String> = listOf("", "config/")
    ) {
        PropertiesArk(path, locations).parse(arrayOf()).expectThat {
            get { this.mysqlPassword }.isEqualTo(pw)
            get { this.port }.isEqualTo(port)
            get { this.multiLine }.isEqualTo("this stretches lines")
        }
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = PropertiesArk("does_not_exist.env", listOf())
        assertThrows<NullPointerException> {
            ark.parse(arrayOf())
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Customization {
        private val content = this::class.java.classLoader.getResource("app.properties").readText()
        private val name = "file_based_props.properties"
        private val dir = File("config")

        @Test fun `should be able to load from file`() {
            File(name).let {
                it.deleteOnExit()
                it.writeText(content)
            }
            verify(name)
        }

        @Test fun `default file config`() {
            dir.mkdirs()
            File(dir, name).let {
                it.deleteOnExit()
                it.writeText(content)
            }
            verify(name)
            dir.deleteRecursively()
        }

        @Test fun `default classpath config`() {
            verify("nested.properties", 1212, "classpath config")
        }

        @Test fun `custom classpath config`() {
            verify("cp.properties", 4545, "custom nested classpath config", locations = listOf("custom/path/"))
        }

        @AfterEach fun afterEach() {
            File(name).delete()
            dir.deleteRecursively()
        }
    }

}
