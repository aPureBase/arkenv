package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.module.module
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class ModuleTests {

    private inner class DatabaseConfig : Arkenv("Database") {
        val port: Int by argument()
    }

    private inner class Ark : Arkenv("Root", configureArkenv {
        -ProfileFeature()
        +PropertyFeature("application-prod") // load a specific file to test feature propagation
    }) {
        val name: String by argument()
        val database = module(DatabaseConfig())
    }

    @Test fun `modules should be parsed by super`() {
        val myApp = "production"
        val port = 443
        Ark().parse()
            .expectThat {
                get { name } isEqualTo myApp
                get { database.port } isEqualTo port
            }
    }

    @Test fun `common prefix config`() {
        val expectedPort = 90
        val prefix = "database"
        val sub = object : Arkenv("Test", configureArkenv {
            this.prefix = prefix
        }) {
            val port: Int by argument()
        }

        val ark = object : Arkenv() {
            val database = module(sub)
        }

        ark.parse("--$prefix-port", expectedPort.toString())
            .expectThat {
                get { database.port } isEqualTo expectedPort
            }
    }

    @Test fun `multi nested modules`() {
        // Arrange
        val c = object : Arkenv("C") { val port: Int by argument() }
        val b = object : Arkenv("B") { val c = module(c) }
        val a = object: Arkenv("A") { val b = module(b) }

        // Act
        a.parse()

        // Assert
        expectThat(a.b.c.port) isEqualTo 80
    }
}
