package com.apurebase.arkenv

import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidationTests {

    @Test fun `parsing should throw when validation returns false`() {
        val ark = object : Arkenv() {
            val failingProp: Int by argument("-f") {
                validation = { it > 0 }
            }
        }

        ark.parse(arrayOf("-f", "5"))
        assertThrows<Exception> { ark.parse(arrayOf("-f", "0")) }
            .message.shouldNotBeNull() shouldContain "did not pass validation"
    }

}
