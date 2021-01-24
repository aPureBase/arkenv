package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class AssignmentTest {

    private class Ark : Arkenv() {
        val int: Int by argument("--int")
        val bool: Boolean by argument("-b", "--bool", "--complex-arg") {
            defaultValue = { true }
        }
        val string: String? by argument("--str")
    }

    @Test fun `should parse assignment correctly`() {
        Ark().parse("int=4").expectThat {
            get { int }.isEqualTo(4)
            get { bool }.isEqualTo(true)
        }
    }

    @Test fun `should turn bool off`() {
        Ark().parse("int=-1", "bool=false").expectThat {
            get { int }.isEqualTo(-1)
            get { bool }.isEqualTo(false)
        }
    }

    @Test fun `should be able to use complex arg in assignment`() {
        Ark().parse("int=0", "complex-arg=false").expectThat {
            get { bool }.isEqualTo(false)
        }
    }

    @Test fun `should still allow = as part of other args`() {
        Ark().parse("--str", "key=value", "int=1").expectThat {
            get { string }.isEqualTo("key=value")
            get { int }.isEqualTo(1)
        }
    }
}
