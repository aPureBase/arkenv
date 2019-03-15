package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class PlaceholderTests {

    private class Ark : Arkenv() {
        init {
            install(PropertyFeature("placeholders.properties"))
        }

        val name: String by argument("--app-name")
        val description: String by argument("--app-description")
    }

    @Test fun `can refer to previously defined arg`() {
        Ark().parse(arrayOf()).verify()
    }

    @Test fun `should allow ${ expression`() {
        // TODO put this at the end
    }

    private fun Ark.verify() {
        expectThat {
            get { name }.isEqualTo("MyApp")
            get { description }.isEqualTo("MyApp is a Arkenv application")
        }
    }
}
