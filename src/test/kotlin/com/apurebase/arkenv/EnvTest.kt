package com.apurebase.arkenv

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class EnvTest : ArkenvTest() {

    @Test fun `parse from env`() {
        val expectedMainString = "important com.apurebase.arkenv.main value"
        val mainArgName = TestArgs::mainString.name
        MockSystem(
            mapOf(
                mainArgName to expectedMainString,
                "-c" to "dk",
                "-b" to ""
            )
        )

        TestArgs(arrayOf()).let {
            println(it)
            it.help shouldBe false
            it.bool shouldBe true
            it.country shouldBeEqualTo "dk"
            it.mainString shouldBeEqualTo expectedMainString
            it.nullInt shouldBe null
        }
    }

    @Test fun `main arg by env should use the prop name`() {
        val expected = "test"
        MockSystem(mapOf(MainArg::mainArg.name to expected))
        MainArg("").mainArg shouldBeEqualTo expected
    }


    override fun testNullable(): Nullable {
        MockSystem(
            mapOf(
                "-i" to expectedInt.toString(),
                "-s" to expectedStr
            )
        )
        return Nullable(arrayOf())
    }

    override fun testArkuments(): Arkuments {
        MockSystem(
            mapOf(
                "-c" to expectedConfigPath,
                "-ma" to "",
                "-r" to "",
                "-h" to ""
            )
        )
        return Arkuments(arrayOf())
    }

}
