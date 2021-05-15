package com.apurebase.arkenv.argument

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize

internal class ArgumentNameProcessorTest {

    private val target = object {
        val argument: Int = 1
    }

    @Test fun `processArgumentNames - ParameterNameOnlyIfNotSpecified - param name excluded`() {
        // Arrange
        val expected = "expected"
        val namingStrategy = ArkenvArgumentNamingStrategy.ParameterNameOnlyIfNotSpecified
        val processor = ArgumentNameProcessor(null, namingStrategy)
        val argument = Argument<Int>(listOf(expected))

        // Act
        processor.processArgumentNames(argument, target::argument)

        // Assert
        expectThat(argument.names) { hasSize(1) }
    }

    @Test fun `processArgumentNames - ParameterNameAlwaysIncluded - param name included`() {
        // Arrange
        val expected = "expected"
        val namingStrategy = ArkenvArgumentNamingStrategy.ParameterNameAlwaysIncluded
        val processor = ArgumentNameProcessor(null, namingStrategy)
        val argument = Argument<Int>(listOf(expected))

        // Act
        processor.processArgumentNames(argument, target::argument)

        // Assert
        expectThat(argument.names) { hasSize(2) }
    }

}