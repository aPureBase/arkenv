package com.apurebase.arkenv.feature.cli

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.isEqualTo

/**
 * Verify the behavior of the [CliArgumentParser].
 */
internal class CliArgumentParserTest {

    private data class TestCase(val name: String, val input: List<String>, val expected: String)

    private val testCases = listOf(
        TestCase("no quotes", listOf("hello", "world"), "hello"),
        TestCase("double quotes", listOf("\"hello", "world\""), "hello world"),
        TestCase("single quote inside value", listOf("D'vloper"), "D'vloper"),
        TestCase("single quoted", listOf("'test", "expected'"), "test expected"),
        TestCase("single quoted, containing singlq quote", listOf("'test", "ex'pected'"), "test ex'pected"),
    )

    @TestFactory
    fun parseArguments(): List<DynamicTest> = testCases.map { (name, input, expected) ->
        DynamicTest.dynamicTest(name) {
            // Arrange
            val parser = CliArgumentParser()

            // Act
            val actual = parser.parseArguments(input)

            // Assert
            expectThat(actual)[0] isEqualTo expected
        }
    }
}