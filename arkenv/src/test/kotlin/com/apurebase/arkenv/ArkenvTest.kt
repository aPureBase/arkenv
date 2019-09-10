package com.apurebase.arkenv

import com.apurebase.arkenv.test.Arkuments
import com.apurebase.arkenv.test.Nullable
import com.apurebase.arkenv.test.expectThat
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.isTrue

abstract class ArkenvTest {

    @Test fun `nullables should be parseable`() {
        testNullable().expectThat {
            get { int }.isEqualTo(expectedInt)
            get { str }.isEqualTo(expectedStr)
        }
    }

    @Test fun `nullable should be null`() {
        Nullable().expectThat {
            get { int }.isNull()
            get { str }.isNull()
        }
    }

    @Test fun `args should parse`() {
        testArkuments().expectThat {
            get { configPath }.isEqualTo(expectedConfigPath)
            get { manualAuth }.isTrue()
            get { doRefresh }.isTrue()
            get { help }.isTrue()
        }
    }

    protected val expectedConfigPath = "config.yml"
    protected val expectedInt = 5
    protected val expectedStr = "test"

    abstract fun testNullable(): Nullable

    abstract fun testArkuments(): Arkuments
}
