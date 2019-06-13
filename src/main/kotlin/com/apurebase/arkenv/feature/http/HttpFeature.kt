package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.findFeature
import com.apurebase.arkenv.get
import java.net.URL
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

class HttpFeature(
    private val rootUrl: String,
    private val httpClient: HttpClient = HttpClientImpl(),
    private val cipher: Cipher? = null,
    private val keyword: String = "{cipher}"
) : ArkenvFeature, Arkenv("HttpFeature") {

    override fun onLoad(arkenv: Arkenv) {
        val label = arkenv["ARKENV_LABEL"]
        val profileFeature = arkenv.findFeature<ProfileFeature>()
        val appProfile = profileFeature?.profile
        httpClient
            .resolveUrls(rootUrl, arkenv.programName, appProfile, label)
            .map(::parse)
            .reduce { acc, map -> acc + map }
            .let(::decryptData)
            .let(arkenv.keyValue::putAll)
    }

    private fun parse(url: URL) = httpClient.get(url).use(PropertyFeature.Companion::parseProperties)

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
}
