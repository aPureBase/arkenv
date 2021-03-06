package com.apurebase.arkenv

import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.util.mainArgument
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.File
import java.io.FileNotFoundException

class DefaultValueTests {

    private inner class DefArgs : Arkenv() {
        val def: Int by argument("-i", "--int") {
            defaultValue = { 5 }
        }

        val defString: String by mainArgument {
            defaultValue = { "hey" }
        }
    }

    @Test fun `defaultValue should be used when no other value can be found`() {
        DefArgs().run {
            def shouldBeEqualTo 5
            defString shouldBeEqualTo "hey"
        }
    }

    @Test fun `default can be overruled by args`() {
        DefArgs().parse("-i", "1", "no").run {
            def shouldBeEqualTo 1
            defString shouldBeEqualTo "no"
        }
    }

    @Test fun `default can be overruled by env`() {
        MockSystem("INT" to "1")
        DefArgs().parse().run {
            def shouldBeEqualTo 1
            defString shouldBeEqualTo "hey"
        }
    }

    @Test fun `default should be lazily evaluated`() {
        class Ark : Arkenv() {
            val refreshToken: String by argument {
                defaultValue = { File("does-not-exist").readText().trim() }
            }
        }

        assertThrows<FileNotFoundException> {
            Ark().parse()
        }

        MockSystem("REFRESH_TOKEN" to "value")
        Ark().parse().expectThat {
            get { refreshToken }.isEqualTo("value")
        }
    }
}
