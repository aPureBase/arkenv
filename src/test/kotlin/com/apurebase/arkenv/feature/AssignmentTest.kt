package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.expectThat
import com.apurebase.arkenv.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class AssignmentTest {

    private class Ark : Arkenv() {
        val int: Int by argument("--int")
        val bool: Boolean by argument("-b", "--bool") {
            defaultValue = { true }
        }
        val string: String? by argument("--str")
    }

    @Test fun `should parse assignment correctly`() {
        Ark().parse(arrayOf("int=4")).expectThat {
            get { int }.isEqualTo(4)
            get { bool }.isEqualTo(true)
        }
    }

    @Test fun `should turn bool off`() {
        Ark().parse(arrayOf("int=-1", "bool=false")).expectThat {
            get { int }.isEqualTo(-1)
            get { bool }.isEqualTo(false)
        }
    }

    @Test fun `should still allow = as part of other args`() {
        Ark().parse(arrayOf("--str", "key=value", "int=1")).expectThat {
            get { string }.isEqualTo("key=value")
            get { int }.isEqualTo(1)
        }
    }
}