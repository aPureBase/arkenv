package com.apurebase.arkenv

import com.apurebase.arkenv.test.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class GeneralTest {

    /**
     * The help argument needs to be the first one declared in the [Arkenv] class.
     */
    @Test fun `should ignore validation when help is true`() {
        class HelpArgs(name: String) : Arkenv(name) {
            val required: String by argument {
                description = "This arg is required but can be null if help is true"
            }
        }

        val expectedName = "TestProgram"
        val ark = HelpArgs(expectedName).parse("-h")
        ark.toString().expectThat {
            contains(expectedName)
            contains(HelpArgs::required.name)
            contains("This arg is required but can be null if help is true")
        }

        assertThrows<MissingArgumentException> { HelpArgs("Test").required }
    }

    @Test fun `repeatedly accessing a prop should not throw`() {
        Arkuments().parse("-c", "some").also(::println).configPath.substring(1)
    }

    @Test fun `main arg should be the first value`() {
        val expected = "this_is_the_value"
        MainArg().parse(expected).mainArg shouldBeEqualTo expected
    }

    @Test fun `long should map correctly`() {
        val expected = 5000L

        class LongArg : Arkenv() {
            val long: Long by argument()
        }

        LongArg().parse("-long", "5000").long shouldBeEqualTo expected
    }

    @Test fun `custom mapping`() {
        val expected = listOf(1, 2, 3)

        class CustomArg : Arkenv() {
            val list by mainArgument<List<Int>> {
                mapping = { it.split(",").map(String::toInt) }
            }
        }

        CustomArg().parse("1,2,3").list shouldBeEqualTo expected
    }

    @Test fun `custom mapping not available should throw`() {
        class CustomArg : Arkenv() {
            val custom by mainArgument<CustomArg>()
        }

        assertThrows<MissingArgumentException> { CustomArg().custom }
    }

    @Test fun `value should accept spaces until next delimiter`() {
        val first = "first"
        val second = "second"
        val expected = "$first $second"

        class A : Arkenv() {
            val spaceArg by argument<String>("-s")
            val other by argument<Boolean>("-o")
        }

        A().parse("-s", "\"$first", "$second\"", "-o").expectThat {
            get { spaceArg }.isEqualTo(expected)
            get { other }.isTrue()
        }
    }

    @Test fun `when mapping is defined, value should be fixed`() {
        class FixedArgs : Arkenv() {
            val value = 5
            val fixed: Int by argument("-f") {
                mapping = { value }
            }
        }

        FixedArgs().parse("-f", "").run {
            fixed shouldBeEqualTo value
        }
    }

    @Test fun `objects should be usable`() {
        ObjectArgs.parse("-i", "10")
        ObjectArgs.int shouldBeEqualTo 10
        ObjectArgs.optional shouldBe null
    }

    @Test fun `passing no names should use the property name`() {
        val ark = object : Arkenv() {
            val legalArg: String by argument()
        }

        val expected = "expected"
        fun verify() = ark.expectThat {
            get { legalArg }.isEqualTo(expected)
        }
        ark.parse(arrayOf("--LEGAL_ARG", expected))
        verify()
        ark.parse(arrayOf("--legal-arg", expected))
        verify()
        ark.parse(arrayOf("--legalArg", expected))
        verify()
    }

    @Test fun `mixed should work`() {
        Mixed().parse("-sa", "5").run {
            someArg shouldBeEqualTo 5
            other shouldBeEqualTo "val"
        }
    }

    @Test fun `null mainArg should throw`() {
        val arkenv = object : Arkenv() {
            val main: String by mainArgument()
        }

        assertThrows<MissingArgumentException> { arkenv.main }
    }

    @Test fun `reparsing should update the values`() {
        val expected = "expected"
        val expectedMain = "remember"
        TestArgs()
            .parse("-c", "random", "main")
            .parse("-c", expected, "-b", expectedMain)
            .expectThat {
                get { country }.isEqualTo(expected)
                get { mainString }.isEqualTo(expectedMain)
                get { bool }.isTrue()
            }
    }

    @Test fun `should pass when delegates are empty`() {
        val ark = object : Arkenv() {}
        ark.parse("-empty")
    }

    @Test fun `parse should fail`() {
        val ark = object : Arkenv() {
            val arg: Int by argument()
        }
        assertThrows<MissingArgumentException> { ark.parse() }
    }

    @Test fun `version 2 naming`() {
        val ark = object : Arkenv() {
            val version: Int by argument("new-version")
        }

        ark.parse("--newVersion", "2")
            .expectThat { get { version }.isEqualTo(2) }
    }

    @Test fun `common prefix`() {
        val expectedPort = 90
        val prefix = "database"
        val ark = object : Arkenv(configuration = configureArkenv {
            this.prefix = prefix
        }) {
            val port: Int by argument()
        }
        ark.parse("--$prefix-port", expectedPort.toString())
            .expectThat {
                get { port }.isEqualTo(expectedPort)
            }
    }
}
