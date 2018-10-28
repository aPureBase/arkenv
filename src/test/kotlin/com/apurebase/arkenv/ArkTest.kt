package com.apurebase.arkenv

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ArkTest : ArkenvTest() {

    @Test fun `parse from cli`() {
        TestArgs().parse(arrayOf("-c", "se", "-b", "\"important", "arg\"")).run {
            mainString shouldBeEqualTo "important arg"
            country shouldBeEqualTo "se"
            bool shouldBe true
            nullInt shouldBe null
        }
    }

    @Test fun `parse mixed cli & env`() {
        val expectedCountry = "no"
        val expectedMainString = "cli&env"
        MockSystem("COUNTRY" to expectedCountry)
        TestArgs().parse(arrayOf(expectedMainString)).let {
            it.country shouldBeEqualTo expectedCountry
            it.mainString shouldBeEqualTo expectedMainString
            it.nullInt shouldBe null
        }
    }

    override fun testNullable(): Nullable = Nullable().parse(arrayOf("-i", expectedInt.toString(), "-s", expectedStr))

    override fun testArkuments() = Arkuments().parse(arrayOf("-c", expectedConfigPath, "-ma", "-r", "-h"))

}
