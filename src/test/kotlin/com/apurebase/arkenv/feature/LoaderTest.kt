package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import org.amshove.kluent.shouldEqualTo
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml
import strikt.assertions.isEqualTo

class LoaderTest {

    private open class Ark(feature: ArkenvFeature) : Arkenv() {
        init {
            install(feature)
        }
        val port: Int by argument("--port")
    }

    @Test fun `custom loader`() {
        val feature = object : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                arkenv.keyValue["PORT"] = "99"
            }
        }

        val ark = Ark(feature)
        ark.parse(arrayOf())
        ark.port shouldEqualTo 99
    }

    @Test fun `yaml example`() {
        class YamlFeature(private val yaml: String) : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                val map = (Yaml().load(yaml) as Map<String, Any>)
                    .map { (key, value) -> key.toUpperCase() to value.toString() }
                arkenv.keyValue.putAll(map)
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

        ark.parse(arrayOf()).expectThat {
            get { port }.isEqualTo(99)
            get { name }.isEqualTo("hello world")
        }
    }
}
