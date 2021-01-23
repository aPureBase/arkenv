package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Tests for the [ArkenvArgument] interface.
 */
class ArkenvArgumentTests {

    @Test fun `setTrue - not boolean - throws`() {
        // Arrange
        val config = object { val property: Int = 5 }
        val argument = ArkenvSimpleArgument<Int>(Argument(listOf()))
        argument.property = config::property

        // Act
        // Assert
        assertThrows<MappingException>(argument::setTrue)
    }
}