package com.apurebase.arkenv

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

abstract class ArkenvTest {

    @Test fun `nullables should be parseable`() {
        testNullable().apply {
            int shouldEqual expectedInt
            str shouldEqual expectedStr
        }
    }

    @Test fun `nullable should be null`() {
        Nullable(arrayOf()).apply {
            int shouldBe null
            str shouldBe null
        }
    }

    val expectedInt = 5
    val expectedStr = "test"

    abstract fun testNullable(): Nullable

}