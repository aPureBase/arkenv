package com.apurebase.arkenv

import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.logging.Logger

class ValidationTests {

    @Test fun `parsing should throw when validation returns false`() {
        val ark = object : Arkenv() {
            val failingProp: Int by argument("-f") {
                validate("number should be positive") { it > 0 }
                validate("this should also fail") { it != 0 }
            }
            override fun onParseArgument(delegate: ArgumentDelegate<*>, value: Any?) {
                Logger.getGlobal().info("Custom: ${delegate.property.name} - $value")
            }

            override fun onParse(args: Array<String>) {
                Logger.getGlobal().info("Parsing ${args.size} arguments")
            }
        }

        ark.parse(arrayOf("-f", "5"))
        val message = assertThrows<Exception> { ark.parse(arrayOf("-f", "0")) }.message.shouldNotBeNull()
        println(message)
        message shouldContain "did not pass validation"
        message shouldContain "failingProp"
        message shouldContain "number should be positive"
        message shouldContain "this should also fail"
    }

}
