package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.MissingArgumentException
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
 * @param httpClient handler for resolving urls.
 * @param rootUrl the root url of the endpoint to query. Can be overridden by the *ARKENV_HTTP_URL* argument.
 * @since 2.1.0
 */
class HttpFeature(
    private val httpClient: HttpClient = HttpClientImpl(),
    var rootUrl: String? = null
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        val label = arkenv.getOrNull("ARKENV_LABEL")
        val profileFeature = arkenv.findFeature<ProfileFeature>()
        val activeProfiles = profileFeature?.active?.joinToString()
        run(arkenv, getUrl(arkenv), activeProfiles, label)
    }

    private fun getUrl(arkenv: Arkenv): String {
        val url = arkenv.getOrNull("ARKENV_HTTP_URL") ?: rootUrl
        if (url.isNullOrBlank()) {
            throw MissingArgumentException(
                HttpFeature::rootUrl.name,
                "${HttpFeature::class.simpleName} requires this argument to be set."
            )
        }
        return url
    }

    private fun run(arkenv: Arkenv, url: String, profiles: String?, label: String?) = httpClient
        .also { it.arkenv = arkenv }
        .resolveUrls(url, arkenv.programName, profiles, label)
        .map(::parse)
        .reduce { acc, map -> acc + map }
        .also(::println) // TODO remove
        .let(arkenv::putAll)

    private fun parse(url: URL): Map<String, String> =
        httpClient.get(url).use(PropertyFeature.Companion::parseProperties)
}
