package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class YamlProfileFeatureTest : ProfileFeatureTest() {

    override fun getInstance(): ProfileFeature {
        return ProfileFeature(prefix = "application-yaml", parsers = listOf(::YamlFeature))
    }

    @Test fun `should be able to combine yaml and properties`() {
        val ark = object : Arkenv("Test", configureArkenv {
            install(ProfileFeature(prefix = "app-multi", parsers = listOf(::YamlFeature)))
        }) {
            val source: String by argument()
            val profile: String by argument()
        }

        ark.parse(arkenvProfile, "prop").expectThat {
            get { source }.isEqualTo("yaml")
            get { profile }.isEqualTo("properties")
        }
    }

}
