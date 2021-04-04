package com.apurebase.arkenv.feature

import com.apurebase.arkenv.ArkenvBuilder
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.getTestResource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PropertyFeatureTests : FileBasedTests {

    override fun configure(propertiesFile: String, locations: List<String>): ArkenvBuilder = configureArkenv {
        +PropertyFeature(propertiesFile, locations)
    }

    @Test fun `should load properties file`() {
        verify("app.properties")
    }

    @Test fun `should load lowercase properties`() {
        verify("app_lower.properties")
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    private inner class Customization {
        private val content = getTestResource("app.properties")
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

        @Test fun `customize via env var`() {
            MockSystem("ARKENV_PROPERTY_LOCATION" to "custom/path")
            verify("cp.properties", 4545, "custom nested classpath config")
        }

        @Test fun `customize via cli`() {
            verify(
                "cp.properties", 4545, "custom nested classpath config", listOf(),
                "--arkenv-property-location", "custom/path"
            )
        }

        @AfterEach fun afterEach() {
            files.forEach(File::deleteRecursively)
            File(name).delete()
            dir.deleteRecursively()
            File("custom").deleteRecursively()
        }
    }
}
