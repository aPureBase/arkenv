package com.apurebase.arkenv

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class EnvTest : ArkenvTest() {

    @Test fun `parse from env`() {
        val expectedMainString = "important com.apurebase.arkenv.main value"
        MockSystem(
            mapOf(
                "COUNTRY" to "dk",
                "EXECUTE" to ""
            )
        )

        TestArgs(arrayOf(expectedMainString)).let {
            println(it)
            it.help shouldBe false
            it.bool shouldBe true
            it.country shouldBeEqualTo "dk"
            it.mainString shouldBeEqualTo expectedMainString
            it.nullInt shouldBe null
        }
    }

    @Test fun `main arg by env should not work`() {
        val expected = "test"
        MockSystem(mapOf(MainArg::mainArg.name to expected))
        MainArg("").mainArg shouldBeEqualTo ""
    }

    @Test fun `only parse -- arguments`() {
        MockSystem(mapOf(
            "COUNTRY" to "DK",
            "NI" to "5",
            "-ni" to "5"
        ))
        TestArgs(arrayOf("Hello World")).let {
            it.mainString shouldEqual  "Hello World"
            it.country shouldEqual "DK"
            it.nullInt shouldEqual null
        }
    }


    override fun testNullable(): Nullable {
        MockSystem(
            mapOf(
                "INT" to expectedInt.toString(),
                "STR" to expectedStr
            )
        )
        return Nullable(arrayOf())
    }

    override fun testArkuments(): Arkuments {
        MockSystem(
            mapOf(
                "CONFIG" to expectedConfigPath,
                "MANUAL_AUTH" to "",
                "REFRESH" to "",
                "HELP" to ""
            )
        )
        return Arkuments(arrayOf())
    }

}
