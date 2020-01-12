package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.ValidationException
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class ValidationTests {

    @Test fun `parsing should throw when validation returns false`() {
        val ark = object : Arkenv() {
            val failingProp: Int by argument {
                validate("number should be positive") { it > 0 }
                validate("this should also fail") { it != 0 }
            }
        }

        ark.parse("--failing-prop", "5")

        val actualValue = "0"
        val message =
            assertThrows<ValidationException> { ark.parse("--failing-prop", actualValue) }.message.shouldNotBeNull()
        println(message)
        message shouldContain "did not pass validation"
        message shouldContain "failingProp"
        message shouldContain "number should be positive"
        message shouldContain "this should also fail"
        message shouldContain actualValue
    }

    @Test fun `should only validate nullable if not null`() {
        val ark = object : Arkenv(configureArkenv {
            clearInputBeforeParse = true
        }) {
            val nullable: Int? by argument {
                validate("only if not null") { it > 100 }
            }
        }

        ark.parse("-nullable", "101").expectThat {
            get { nullable }.isEqualTo(101)
        }

        ark.parse().expectThat {
            get { nullable }.isNull()
        }

        assertThrows<ValidationException> {
            ark.parse("-nullable", "99")
        }
    }
}
