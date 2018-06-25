package com.apurebase.arkenv

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ArkTest : ArkenvTest() {

    @Test fun `parse from cli`() {
        val expectedCountry = "se"
        val expectedMainString = "important arg"
        TestArgs(arrayOf(expectedMainString, "-c", expectedCountry, "-b")).run {
            mainString shouldBeEqualTo expectedMainString
            country shouldBeEqualTo expectedCountry
            bool shouldBe true
            nullInt shouldBe null
        }
    }

    @Test fun `parse mixed cli & env`() {
        val expectedCountry = "no"
        val expectedMainString = "cli&env"
        MockSystem(
            mapOf(
                TestArgs::mainString.name to expectedMainString
            )
        )
        TestArgs(arrayOf("-c", expectedCountry)).let {
            it.country shouldBeEqualTo expectedCountry
            it.mainString shouldBeEqualTo expectedMainString
            it.nullInt shouldBe null
        }
    }


    override fun testNullable(): Nullable = Nullable(arrayOf("-i", expectedInt.toString(), "-s", expectedStr))

}