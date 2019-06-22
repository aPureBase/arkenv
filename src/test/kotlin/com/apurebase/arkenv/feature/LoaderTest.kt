package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.putAll
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldEqualTo
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml
import strikt.assertions.isEqualTo
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class LoaderTest {

    private open class Ark(feature: ArkenvFeature) : Arkenv(configuration = {
        install(feature)
    }) {
        val port: Int by argument("--port")
    }

    @Test fun `custom loader`() {
        val feature = object : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                arkenv["PORT"] = "99"
            }
        }

        val ark = Ark(feature)
        ark.parse()
        ark.port shouldEqualTo 99
    }

    @Test fun `yaml example`() {
        class YamlFeature(private val yaml: String) : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                val map = (Yaml().load(yaml) as Map<String, Any>)
                    .map { (key, value) -> key.toUpperCase() to value.toString() }
                    .toMap()
                arkenv.putAll(map)
            }
        }

        @Language("yaml")
        val yaml = """
          port: 99
          name: hello world
        """.trimIndent()

        class YamlArk(yaml: String) : Ark(YamlFeature(yaml)) {
            val name: String by argument("--name")
        }

        val ark = YamlArk(yaml)

        ark.parse().expectThat {
            get { port }.isEqualTo(99)
            get { name }.isEqualTo("hello world")
        }
    }
}
