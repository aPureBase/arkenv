package com.apurebase.arkenv

import com.apurebase.arkenv.test.ReadmeArguments
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

/**
 * Tests for the [ArkenvParser.parse] functionality, parsing plain classes.
 */
class ParseInstanceTests {

    @Test fun `parsing plain class - profile`() {
        // Arrange
        val config = object { val port: Int by argument() }
        val expectedPort = 80

        // Act
        Arkenv.parse(config, arrayOf())

        // Assert
        expectThat(config).get { port } isEqualTo expectedPort
    }


    @Test fun `parsing plain class - argument`() {
        // Arrange
        val config = object { val country: String by argument() }
        val expectedCountry = "X"

        // Act
        Arkenv.parse(config, arrayOf("--country", expectedCountry))

        // Assert
        expectThat(config).get { country } isEqualTo expectedCountry
    }

    @Test fun `nullable does not require value`() {
        // Arrange
        val config = object { val country: String? by argument() }

        // Act
        Arkenv.parse(config, arrayOf())

        // Assert
        expectThat(config).get { country }.isNull()
    }

    @Test fun `non-nullable should throw on parse`() {
        // Arrange
        val config = object { val country: String by argument() }

        // Assert
        assertThrows<MissingArgumentException> {
            // Act
            Arkenv.parse(config, arrayOf())
        }
    }

    @Test fun `boolean with literal true`() {
        // Arrange
        val config = object { val isEnabled: Boolean by argument() }

        // Act
        Arkenv.parse(config, arrayOf("--is-enabled", "true"))

        // Assert
        expectThat(config).get { isEnabled }.isTrue()
    }

    @Test fun `boolean with implicit`() {
        // Arrange
        val config = object { val isEnabled: Boolean by argument("-ie") }

        // Act
        Arkenv.parse(config, arrayOf("-ie"))

        // Assert
        expectThat(config).get { isEnabled }.isTrue()
    }

    @Test fun `boolean with int 1`() {
        // Arrange
        val config = object { val isEnabled: Boolean by argument() }

        // Act
        Arkenv.parse(config, arrayOf("--is-enabled", "1"))

        // Assert
        expectThat(config).get { isEnabled }.isTrue()
    }

    @Disabled("Not implemented yet")
    @Test fun `boolean with int 0`() {
        // Arrange
        val config = object { val isEnabled: Boolean by argument() }

        // Act
        Arkenv.parse(config, arrayOf("--is-enabled", "0"))

        // Assert
        expectThat(config).get { isEnabled }.isFalse()
    }

    @Test fun `boolean with literal false`() {
        // Arrange
        val config = object { val isEnabled: Boolean by argument() }

        // Act
        Arkenv.parse(config, arrayOf("--is-enabled", "false"))

        // Assert
        expectThat(config).get { isEnabled }.isFalse()
    }

    @Test fun `boolean with default value true`() {
        // Arrange
        val config = object {
            val isEnabled: Boolean by argument {
                defaultValue = { true }
            }
        }

        // Act
        Arkenv.parse(config, arrayOf("--is-enabled", "false"))

        // Assert
        expectThat(config).get { isEnabled }.isFalse()
    }

    @Test fun `argument validation - fails`() {
        // Arrange
        val config = object {
            val country: String by argument {
                validate("Not blank", String::isNotBlank)
            }
        }

        // Assert
        assertThrows<ValidationException> {
            // Act
            Arkenv.parse(config, arrayOf("--country", " "))
        }
    }

    @Test fun `parsing sample should pass`() {
        // Arrange
        val config = ReadmeArguments()
        val expectedCountry = "test"

        // Act
        Arkenv.parse(config, arrayOf("--country", expectedCountry, "-b"))

        // Assert
        expectThat(config) {
            get { port } isEqualTo 80
            get { country } isEqualTo expectedCountry
            get { bool }.isTrue()
            get { nullInt }.isNull()
        }
    }
}