package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class ModuleTests {

    private inner class DatabaseConfig : Arkenv("Database") {
        val port: Int by argument()
    }

    private inner class Ark : Arkenv("Root", configureArkenv {
        uninstall(ProfileFeature())
        install(PropertyFeature("application-prod")) // load a specific file to test feature propagation
    }) {
        val name: String by argument()
        val database = module(DatabaseConfig())
    }

    @Test fun `modules should be parsed by super`() {
        val myApp = "production"
        val port = 443
        Ark().parse()
            .expectThat {
                get { name }.isEqualTo(myApp)
                get { database.port }.isEqualTo(port)
            }
    }
}
