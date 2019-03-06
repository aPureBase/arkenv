package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class AssignmentTest {

    private class Ark : Arkenv() {
        val int: Int by argument("--int")
        val bool: Boolean by argument("-b", "--bool") {
            defaultValue = { true }
        }
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
}
