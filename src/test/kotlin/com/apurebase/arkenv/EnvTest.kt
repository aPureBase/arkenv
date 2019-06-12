package com.apurebase.arkenv

import com.apurebase.arkenv.test.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test

class EnvTest : ArkenvTest() {

    @Test fun `parse from env`() {
        val expectedMainString = "important main value"
        MockSystem(
            "COUNTRY" to "dk",
            "EXECUTE" to ""
        )

        TestArgs().parse(expectedMainString).let {
            it.help shouldBe false
            it.bool shouldBe true
            it.country shouldBeEqualTo "dk"
            it.mainString shouldBeEqualTo expectedMainString
            it.nullInt shouldBe null
        }
    }

    @Test fun `main arg by env should not work`() {
        val expected = "test"
        MockSystem(MainArg::mainArg.name to expected)
        MainArg().parse("").mainArg shouldBeEqualTo ""
    }

    @Test fun `only parse -- arguments`() {
        MockSystem(
            "COUNTRY" to "DK",
            "NI" to "5"
        )
        TestArgs().parse("Hello World").let {
            it.mainString shouldEqual "Hello World"
            it.country shouldEqual "DK"
            it.nullInt shouldEqual null
        }
    }

    override fun testNullable(): Nullable {
        MockSystem(
            "INT" to expectedInt.toString(),
            "STR" to expectedStr
        )
        return Nullable()
    }

    override fun testArkuments(): Arkuments {
        MockSystem(
            "CONFIG" to expectedConfigPath,
            "MANUAL_AUTH" to "",
            "REFRESH" to "",
            "HELP" to ""
        )
        return Arkuments()
    }

    // Value picking tests
    @Test fun `not defined`() {
        TestArgs().description shouldEqual null
    }

    @Test fun `last argument`() {
        MockSystem("DESCRIPTION" to "text")
        TestArgs().description shouldEqual "text"
    }

    @Test fun `envVariable usage`() {
        MockSystem("DESCRIPTION" to "text", "DESC" to "SOME MORE DESC")
        TestArgs().description shouldEqual "SOME MORE DESC"
    }

    @Test fun `everything and cli argument`() {
        val expected = "main desc"
        MockSystem("DESCRIPTION" to "text", "DESC" to "SOME MORE DESC")
        TestArgs().parse("-d", expected, "-c", "dk", "main").description shouldEqual expected
    }

    @Test fun `custom env name should parse`() {
        val envName = "ENV_NAME"
        val expected = "result"

        class CustomEnv : Arkenv() {
            val arg: String by argument("-a") {
                envVariable = envName
            }
        }

        { CustomEnv().arg } shouldThrow IllegalArgumentException::class // nothing passed
        CustomEnv().parse("-a", expected).arg shouldBeEqualTo expected // via arg

        MockSystem(envName to expected)
        CustomEnv().arg shouldBeEqualTo expected // via env
    }

    @Test fun `should also accept -- double dash envs`() {
        MockSystem("ARG" to "x")
        CustomEnv().arg shouldBeEqualTo "x"
    }

    @Test fun `should accept custom env`() {
        MockSystem("TEST" to "y")
        CustomEnv().arg shouldBeEqualTo "y"
    }
}
