package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.*
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
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
        Arguments().parse("-ds", "DoSomething").expectThat {
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
        fun Ark.verify(vararg args: String) = parse(*args).expectThat {
            get { a }.isTrue()
            get { b }.isTrue()
            get { c }.isTrue()
        }

        Ark.verify("-abc")
        Ark.verify("-123")
        Ark.verify("-3ba")
        Ark.verify("-c21")
        Ark.parse("-bcda").expectThat {
            get { a }.isTrue()
            get { b }.isFalse()
            get { c }.isFalse()
            get { d }.isTrue()
        }
        Ark.parse("-bcdax").expectThat {
            get { a }.isFalse()
            get { b }.isFalse()
            get { c }.isFalse()
            get { d }.isFalse()
        }
    }

    @Test fun `multiple matches in same arg`() {
        val ark = object : Arkenv() {
            val first: Boolean by argument("-a", "-abc")
            val second: Boolean by argument("-b")
        }
        ark.parse("-abcb").expectThat {
            get { first }.isTrue()
            get { second }.isTrue()
        }
    }
}
