package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

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
        expectThat(config).get { port } isEqualTo 80
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
        val expectedPort = 80
        class Configuration(val country: String) {
            val port: Int by argument()
        }

        // Act
        val config = Arkenv.parse<Configuration>(arrayOf("--country", expectedCountry))

        // Assert
        expectThat(config) {
            get { country } isEqualTo expectedCountry
            get { port } isEqualTo expectedPort
        }
    }

}