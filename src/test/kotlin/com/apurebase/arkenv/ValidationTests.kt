package com.apurebase.arkenv

import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class ValidationTests {

    @Test fun `parsing should throw when validation returns false`() {
        val ark = object : Arkenv() {
            val failingProp: Int by argument("-f") {
                validate("number should be positive") { it > 0 }
                validate("this should also fail") { it != 0 }
            }
        }

        ark.parse(arrayOf("-f", "5"))

        val actualValue = "0"
        val message =
            assertThrows<ValidationException> { ark.parse(arrayOf("-f", actualValue)) }.message.shouldNotBeNull()
        println(message)
        message shouldContain "did not pass validation"
        message shouldContain "failingProp"
        message shouldContain "number should be positive"
        message shouldContain "this should also fail"
        message shouldContain actualValue
    }

    @Test fun `should only validate nullable if not null`() {
        val ark = object : Arkenv() {
            val nullable: Int? by argument("-n") {
                validate("only if not null") { it > 100 }
            }
        }

        ark.parse(arrayOf("-n", "101")).expectThat {
            get { nullable }.isEqualTo(101)
        }
        ark.parse(arrayOf()).expectThat {
            get { nullable }.isNull()
        }
    }
}
