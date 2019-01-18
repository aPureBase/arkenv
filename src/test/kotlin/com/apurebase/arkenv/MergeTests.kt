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
        Arguments().parse(args).expectThat {
            get { doRun }.isTrue()
            get { production }.isFalse()
            get { something }.isTrue()
            get { action }.isEqualTo("DoSomething")
            get { argList }.not().contains("-ds")
        }
    }

    private object Ark : Arkenv() {
        val a: Boolean by argument("-a", "-1")
        val b: Boolean by argument("-b", "-2")
        val c: Boolean by argument("-c", "-3")
        val d: Boolean by argument("-bcd")
    }

    @Test fun `should parse even complex combinations`() {
        fun Ark.verify() = expectThat {
            get { a }.isTrue()
            get { b }.isTrue()
            get { c }.isTrue()
        }

        Ark.parse(arrayOf("-abc")).verify()
        Ark.parse(arrayOf("-123")).verify()
        Ark.parse(arrayOf("-3ba")).verify()
        Ark.parse(arrayOf("-c21")).verify()
        Ark.parse(arrayOf("-bcda")).expectThat {
            get { a }.isTrue()
            get { b }.isFalse()
            get { c }.isFalse()
            get { d }.isTrue()
        }
        Ark.parse(arrayOf("-bcdax")).expectThat {
            get { a }.isFalse()
            get { b }.isFalse()
            get { c }.isFalse()
            get { d }.isFalse()
        }
    }
}
