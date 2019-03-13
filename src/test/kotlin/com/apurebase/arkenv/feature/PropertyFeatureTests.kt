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

    private val defaultPort = 5050
    private fun verify(
        path: String,
        port: Int = defaultPort,
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
        private val files: MutableList<File> = mutableListOf()
        private fun createFile(file: File, port: Int = defaultPort) {
            file.writeText(content.replace(defaultPort.toString(), port.toString()))
            files.add(file)
        }

        private fun mkdir(file: File) {
            file.mkdirs()
            files.add(file)
        }

        @Test fun `should be able to load from file`() {
            createFile(File(name))
            verify(name)
        }

        @Test fun `default file config`() {
            mkdir(dir)
            createFile(File(dir, name))
            verify(name)
        }

        @Test fun `default classpath config`() {
            verify("nested.properties", 1212, "classpath config")
        }

        @Test fun `custom classpath config`() {
            verify("cp.properties", 4545, "custom nested classpath config", locations = listOf("custom/path/"))
        }

        @Test fun `custom file config`() {
            val name = "cfp.properties"
            val dir = File("custom/file/path")
            mkdir(dir)
            createFile(File(dir, name), 5555)
            verify(name, 5555, locations = listOf("custom/file/path/"))
        }

        @AfterEach fun afterEach() {
            files.forEach { it.deleteRecursively() }
            File(name).delete()
            dir.deleteRecursively()
            File("custom").deleteRecursively()
        }
    }
}
