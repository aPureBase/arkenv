package com.apurebase.arkenv

import com.apurebase.arkenv.test.*
import org.amshove.kluent.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class GeneralTest {

    @Test fun help() {
        class HelpArgs(name: String) : Arkenv(name) {
            val required: String by argument("-r") {
                description = "This arg is required but can be null if help is true"
            }
        }

        val expectedName = "TestProgram"
        val ark = HelpArgs(expectedName).parse("-h")
        val helpInfo = ark.toString()
        helpInfo shouldContain expectedName
        helpInfo shouldContain HelpArgs::required.name
        helpInfo shouldContain "This arg is required but can be null if help is true"

        HelpArgs("Test").let {
            it::required shouldThrow IllegalArgumentException::class
        }
    }

    @Disabled("It is possible but dangerous to allow overriding help") @Test fun `custom help should parse`() {
//        class CustomHelp : Arkenv() {
//            override val help: Boolean by argument("-ca") {
//                isHelp = true // current limitation
//            }
//            val nullProp: Int by argument("-np")
//        }
//        CustomHelp().parse("-ca").also(::println)
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
            val long: Long by argument("-l")
        }

        LongArg().parse("-l", "5000").long shouldEqualTo expected
    }

    @Test fun `custom mapping`() {
        val expected = listOf(1, 2, 3)

        class CustomArg : Arkenv() {
            val list by mainArgument<List<Int>> {
                mapping = { it.split(",").map(String::toInt) }
            }
        }

        CustomArg().parse("1,2,3").list shouldEqual expected
    }

    @Test fun `custom mapping not available should throw`() {
        class CustomArg : Arkenv() {
            val custom by mainArgument<CustomArg>()
        }

        CustomArg()::custom shouldThrow IllegalArgumentException::class
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
            fixed shouldEqualTo value
        }
    }

    @Test fun `objects should be usable`() {
        ObjectArgs.parse("-i", "10")
        ObjectArgs.int shouldEqualTo 10
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
            someArg shouldEqualTo 5
            other shouldBeEqualTo "val"
        }
    }

    @Test fun `null mainArg should throw`() {
        val arkenv = object : Arkenv() {
            val main: String by mainArgument { }
        }

        arkenv::main shouldThrow IllegalArgumentException::class
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

    @Test fun `onParse callbacks should be called`() {
        var globalCalled = false
        var lastCalledArg = ""
        val ark = object : Arkenv() {
            val some: Int by argument("-s")
            val last: String by argument("-l")

            override fun onParseArgument(name: String, argument: Argument<*>, value: Any?) {
                lastCalledArg = name
            }

            override fun onParse(args: Array<out String>) {
                args.forEach(::println)
                globalCalled = true
            }
        }

        ark.parse("-s", "5", "-l", "test")
        globalCalled.expectThat { isTrue() }
        lastCalledArg.expectThat { isEqualTo("last") }
    }

    @Test fun `should pass when delegates are empty`() {
        val ark = object : Arkenv() {}
        ark.parse("-empty")
    }

    @Test fun `parse should fail`() {
        val ark = object : Arkenv() {
            val arg: Int by argument("-a")
        }
        assertThrows<IllegalArgumentException> { ark.parse() }
    }

    @Test fun `version 2 naming`() {
        val ark = object : Arkenv() {
            val version: Int by argument("new-version")
        }

        ark.parse("--newVersion", "2")
            .expectThat { get { version }.isEqualTo(2) }
    }
}
