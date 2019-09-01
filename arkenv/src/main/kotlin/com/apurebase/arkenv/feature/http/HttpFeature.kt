package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.findFeature
import com.apurebase.arkenv.putAll
import java.net.URL

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
 * @param rootUrl the root url of the endpoint to query
 * @param httpClient handler for resolving urls
 * @since 2.1.0
 */
class HttpFeature(
    private val rootUrl: String,
    private val httpClient: HttpClient = HttpClientImpl()
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        val label = arkenv.getOrNull("ARKENV_LABEL")
        val profileFeature = arkenv.findFeature<ProfileFeature>()
        val profile = profileFeature?.profiles?.joinToString()
        httpClient
            .resolveUrls(rootUrl, arkenv.programName, profile, label)
            .map(::parse)
            .reduce { acc, map -> acc + map }
            .let(arkenv::putAll)
    }

    private fun parse(url: URL): Map<String, String> =
        httpClient.get(url).use(PropertyFeature.Companion::parseProperties)
}
