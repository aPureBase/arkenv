package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.module.module
import com.apurebase.arkenv.test.Expected
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.util.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

/**
 * Tests for the [ArkenvParser.parseClass] functionality, parsing plain classes.
 */
class ParseClassTests {

    @Test fun `no args profile should be loaded`() {
        // Arrange
        class Configuration(val port: Int)

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf())

        // Assert
        expectThat(config).get { port } isEqualTo Expected.port
    }

    @Test fun `default value should not throw`() {
        // Arrange
        val expectedCountry = "test"
        class Configuration(val country: String = "test")

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf())

        // Assert
        expectThat(config).get { country } isEqualTo expectedCountry
    }

    @Test fun `default value can be overridden`() {
        // Arrange
        val expectedCountry = "another"
        class Configuration(val country: String = "test")

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf("--country", expectedCountry))

        // Assert
        expectThat(config).get { country } isEqualTo expectedCountry
    }

    @Test fun `nullable is not required - null by default`() {
        // Arrange
        class Configuration(val country: String?)

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf())

        // Assert
        expectThat(config).get { country }.isNull()
    }

    @Test fun `mixing constructor and delegates works`() {
        // Arrange
        val expectedCountry = "dk"
        class Configuration(val country: String) {
            val port: Int by argument()
        }

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf("--country", expectedCountry))

        // Assert
        expectThat(config) {
            get { country } isEqualTo expectedCountry
            get { port } isEqualTo Expected.port
        }
    }

    @Test fun `configure arkenv should apply`() {
        // Arrange
        class Configuration(val databasePort: Int) {
            val mysqlPassword: String by argument()
        }

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf()) {
            +ProfileFeature(prefix = "app")
        }

        // Assert
        expectThat(config) {
            get { mysqlPassword } isEqualTo Expected.mysqlPassword
            get { databasePort } isEqualTo Expected.databasePort
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["true", "1"])
    fun `boolean true`(input: String) {
        // Arrange
        class Configuration(val headless: Boolean = false)

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf("--headless", input))

        // Assert
        expectThat(config) { get { headless }.isTrue() }
    }

    @ParameterizedTest
    @ValueSource(strings = ["false", "0", ""])
    fun `boolean false`(input: String) {
        // Arrange
        class Configuration(val headless: Boolean = false)

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf("--headless", input))

        // Assert
        expectThat(config) { get { headless }.isFalse() }
    }

    @Test fun `module by class is parsed`() {
        // Arrange
        val expectedCountry = "DK"
        class SubConfig(val country: String) {
            val port: Int by argument()
        }
        class Config  {
            val subModule by module<SubConfig>()
        }

        // Act
        val config = Arkenv.parse<Config>(arrayOf("--country", expectedCountry))

        // Assert
        expectThat(config.subModule) {
            get { port } isEqualTo Expected.port
            get { country } isEqualTo expectedCountry
        }
    }

    @Test fun `prefix applied to constructor`() {
        // Arrange
        val expectedPort = 199
        class PrefixConstructor(val port: Int)

        // Act
        val config = Arkenv.parse<PrefixConstructor>(arrayOf("--database-port", expectedPort.toString())) { prefix = "database" }

        // Assert
        expectThat(config).get { port } isEqualTo expectedPort
    }

    @Test fun `common prefix per module`() {
        // Arrange
        val prefix = "database"
        val expectedPort = 199
        class Nested(val port: Int)

        class Config {
            val database: Nested by module { this.prefix = "database" }
        }

        // Act
        val config = Arkenv.parse<Config>(arrayOf("--$prefix-port", expectedPort.toString()))

        // Assert
        expectThat(config.database).get { port } isEqualTo expectedPort
    }
}
