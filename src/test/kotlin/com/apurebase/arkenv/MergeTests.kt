package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class MergeTests {

    class Arguments : Arkenv() {
        val doRun: Boolean by argument("-d", "--do-run")
        val production: Boolean by argument("-p", "--production")
        val something: Boolean by argument("-s", "--something")
        val action: String by mainArgument()
        //val interfering: Boolean by argument("-ds")
    }

    @Test fun `should parse multiple grouped boolean arguments`() {
        val args = arrayOf("-ds", "DoSomething")
        expectThat(Arguments().parse(args)) {
            get { doRun }.isTrue()
            get { production }.isFalse()
            get { something }.isTrue()
            get { action }.isEqualTo("DoSomething")
            get { argList }.not().contains("-ds")
        }
    }
}
