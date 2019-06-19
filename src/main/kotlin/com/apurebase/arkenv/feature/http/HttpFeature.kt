package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.findFeature
import com.apurebase.arkenv.get
import com.apurebase.arkenv.getOrNull
import java.net.URL
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

/**
 * Enables retrieval and parsing of remote configuration sources via http requests.
 *
 * Will attempt to resolve [rootUrl]/programName/profile/label where profile and label are optional.
 *
 * The programName can be configured in [Arkenv] or via the *ARKENV_APPLICATION_NAME* argument.
 *
 * The profile can be configured via the [ProfileFeature].
 *
 * The label can be configured via the *ARKENV_LABEL* argument.
 *
 * The encryption prefix can be configured via the *ARKENV_HTTP_ENCRYPTION_PREFIX* argument.
 * @param rootUrl the root url of the endpoint to query
 * @param httpClient handler for resolving urls
 * @param cipher used to decrypt encrypted values from the response
 */
class HttpFeature(
    private val rootUrl: String,
    private val httpClient: HttpClient = HttpClientImpl(),
    private val cipher: Cipher? = null
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        val encryptedPrefix = arkenv.getOrNull("ARKENV_HTTP_ENCRYPTION_PREFIX") ?: "{cipher}"
        val label = arkenv["ARKENV_LABEL"]
        val profileFeature = arkenv.findFeature<ProfileFeature>()
        val profile = profileFeature?.profiles?.joinToString()
        httpClient
            .resolveUrls(rootUrl, arkenv.programName, profile, label)
            .map(::parse)
            .reduce { acc, map -> acc + map }
            .let { decryptData(it, encryptedPrefix) }
            .let(arkenv.keyValue::putAll)
    }

    private fun parse(url: URL) = httpClient.get(url).use(PropertyFeature.Companion::parseProperties)

    private fun decryptData(data: Map<String, String>, prefix: String): Map<String, String> =
        cipher?.let {
            data.mapValues { (_, value) ->
                if (value.startsWith(prefix)) it.decrypt(value.removePrefix(prefix))
                else value
            }
        } ?: data

    private fun Cipher.decrypt(input: String): String = input
        .let { DatatypeConverter.parseHexBinary(it) }
        .let(::doFinal)
        .let { String(it) }
}
