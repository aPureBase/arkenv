package com.apurebase.arkenv

import com.apurebase.arkenv.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNull
import strikt.assertions.isTrue

class EnvTest : ArkenvTest() {

    @Test fun `parse from env`() {
        val expectedMainString = "important main value"
        MockSystem(
            "COUNTRY" to "dk",
            "EXECUTE" to ""
        )

        TestArgs().parse(expectedMainString).expectThat {
            get { help }.isFalse()
            get { bool }.isTrue()
            get { country }.isEqualTo("dk")
            get { mainString }.isEqualTo(expectedMainString)
            get { null }.isNull()
        }
    }

    @Test fun `main arg by env should not work`() {
        val expected = "test"
        MockSystem(MainArg::mainArg.name to expected)
        MainArg().parse("").expectThat {
            get { mainArg }.isEqualTo("")
        }
    }

    @Test fun `only parse -- arguments`() {
        MockSystem(
            "COUNTRY" to "DK",
            "NI" to "5"
        )
        TestArgs().parse("Hello World").expectThat {
            get { mainString }.isEqualTo("Hello World")
            get { country }.isEqualTo("DK")
            get { nullInt }.isNull()
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
        TestArgs().description expectIsEqual null
    }

    @Test fun `last argument`() {
        MockSystem("DESCRIPTION" to "text")
        TestArgs().description expectIsEqual "text"
    }

    @Test fun `envVariable usage`() {
        MockSystem("DESCRIPTION" to "text", "DESC" to "SOME MORE DESC")
        TestArgs().description expectIsEqual "SOME MORE DESC"
    }

    @Test fun `everything and cli argument`() {
        val expected = "main desc"
        MockSystem("DESCRIPTION" to "text", "DESC" to "SOME MORE DESC")
        TestArgs().parse("-d", expected, "-c", "dk", "main").description expectIsEqual expected
    }

    @Test fun `custom env name should parse`() {
        val envName = "ENV_NAME"
        val expected = "result"

        class CustomEnv : Arkenv() {
            val arg: String by argument("-a") {
                envVariable = envName
            }
        }

        assertThrows<MissingArgumentException> { CustomEnv().arg }
        CustomEnv().parse("-a", expected).arg expectIsEqual expected // via arg

        MockSystem(envName to expected)
        CustomEnv().arg expectIsEqual expected // via env
    }

    @Test fun `should also accept -- double dash envs`() {
        MockSystem("ARG" to "x")
        CustomEnv().arg expectIsEqual "x"
    }

    @Test fun `should accept custom env`() {
        MockSystem("TEST" to "y")
        CustomEnv().arg expectIsEqual "y"
    }
}
