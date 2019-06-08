package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import java.io.InputStream
import java.net.URL
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

class HttpFeature(
    private val rootUrl: String,
    private val applicationName: String? = null,
    private val profile: String? = null,
    private val label: String? = null,
    private val httpClient: HttpClient = HttpClientImpl(),
    private val cipher: Cipher? = null,
    private val keyword: String = "{cipher}"
) : ArkenvFeature {

    private val suffix = "properties"

    override fun onLoad(arkenv: Arkenv) {
        httpClient
            .get(makeUrl(arkenv.programName))
            .use(PropertyFeature.Companion::parseProperties)
            .let(::decryptData)
            .let(arkenv.keyValue::putAll)
    }

    private fun decryptData(data: Map<String, String>): Map<String, String> =
        cipher?.let {
            data.mapValues { (_, value) ->
                if (value.startsWith(keyword)) it.decrypt(value)
                else value
            }
        } ?: data

    private fun Cipher.decrypt(input: String): String =
        input.removePrefix(keyword)
            .let { DatatypeConverter.parseHexBinary(it) }
            .let(::doFinal)
            .let { String(it) }

    private fun makeUrl(programName: String): String {
        val root = rootUrl.removeSuffix("/")
        val name = applicationName ?: programName
        return "$root/$name.$suffix"
    }
}

interface HttpClient {
    fun get(url: String): InputStream
}

class HttpClientImpl : HttpClient {
    override fun get(url: String): InputStream = URL(url).openStream()
}
