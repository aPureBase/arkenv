package com.apurebase.arkenv

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class GeneralTest {

    @Test fun help() {
        class HelpArgs : Arkenv() {
            val required: String by argument("-r") {
                description = "This arg is required but can be null if help is true"
            }
        }

        HelpArgs().parse(arrayOf("-h")).let {
            println(it)
        }

        HelpArgs().let {
            { it.required } shouldThrow IllegalArgumentException::class
        }
    }


    @Test fun `main arg should be the first value`() {
        val expected = "this_is_the_value"
        MainArg().parse(arrayOf(expected)).mainArg shouldBeEqualTo expected
    }

    @Test fun `long should map correctly`() {
        val expected = 5000L

        class LongArg : Arkenv() {
            val long: Long by argument("-l")
        }

        LongArg().parse(arrayOf("-l", "5000")).long shouldEqualTo expected
    }

    @Test fun `custom mapping`() {
        val expected = listOf(1, 2, 3)

        class CustomArg : Arkenv() {
            val list by mainArgument<List<Int>> {
                mapping = { it.split(",").map { it.toInt() } }
            }
        }

        CustomArg().parse(arrayOf("1,2,3")).list shouldEqual expected
    }

    @Test fun `custom mapping not available should throw`() {
        class CustomArg : Arkenv() {
            val custom by mainArgument<CustomArg>()
        }

        val func = { CustomArg().custom }
        func shouldThrow IllegalArgumentException::class
    }

    @Test fun `value should accept spaces until next delimiter`() {
        val first = "first"
        val second = "second"
        val expected = "$first $second"

        class A : Arkenv() {
            val spaceArg by argument<String>("-s")
            val other by argument<Boolean>("-o")
        }

        A().parse(arrayOf("-s", "\"$first", "$second\"", "-o")).run {
            spaceArg shouldBeEqualTo expected
            other shouldBe true
        }
    }

    @Test fun `when mapping is defined, value should be fixed`() {
        class FixedArgs : Arkenv() {
            val value = 5
            val fixed: Int by argument("-f") {
                mapping = { value }
            }
        }

        FixedArgs().parse(arrayOf("-f", "")).run {
            fixed shouldEqualTo value
        }
    }

    @Test fun `defaultValue should be used when no other value can be found`() {
        class DefArgs : Arkenv() {
            val def: Int by mainArgument {
                defaultValue = 5
            }

            val defString: String by mainArgument {
                defaultValue = "hey"
            }
        }

        DefArgs().run {
            def shouldEqualTo 5
            defString shouldBeEqualTo "hey"
        }
    }

    @Test fun `objects should be usable`() {
        ObjectArgs.parse(arrayOf("-i", "10"))
        ObjectArgs.int shouldEqualTo 10
        ObjectArgs.optional shouldBe null
    }

    @Test fun `passing an empty arg list should throw`() {
        {
            object : Arkenv() {
                val illegal: String by argument(listOf())
            }
        } shouldThrow IllegalArgumentException::class
    }

    @Test fun `mixed should work`() {
        Mixed().parse(arrayOf("-sa", "5")).run {
            someArg shouldEqualTo 5
            other shouldBeEqualTo "val"
        }
    }

    @Test fun `when env is off should not use env vars`() {
        class EnvArgs(withEnv: Boolean) : Arkenv(withEnv = withEnv) {
            val arg: String by argument("-a", "--arg")
        }

        MockSystem("ARG" to "test")

        EnvArgs(false).run {
            { arg } shouldThrow IllegalArgumentException::class
        }
        EnvArgs(true).arg shouldBeEqualTo "test"
    }

    @Test fun `null mainArg should throw`() {
        val arkenv = object : Arkenv() {
            val main: String by mainArgument { }
        }

        { arkenv.main } shouldThrow IllegalArgumentException::class
    }

}
