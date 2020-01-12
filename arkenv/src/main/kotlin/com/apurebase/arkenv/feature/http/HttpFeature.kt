package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.*
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.PropertyFeature
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
open class HttpFeature(
    open val httpClient: HttpClient = HttpClientImpl(),
    var rootUrl: String? = null
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        val label = arkenv.getOrNull("ARKENV_LABEL")
        val profileFeature = arkenv.findFeature<ProfileFeature>()
        val activeProfiles = profileFeature?.active ?: listOf()
        run(arkenv, activeProfiles, label)
    }

    /**
     * Generates a list of [URL]s to query from the application information.
     */
    private fun resolveUrls(
        rootUrl: String,
        name: String,
        profiles: List<String>,
        label: String?
    ): Iterable<URL> {
        val rootProfile = listOf(makeUrl(rootUrl, name, null, label))
        return when {
            profiles.isEmpty() -> rootProfile
            else -> {
                rootProfile + profiles.map { makeUrl(rootUrl, name, it, label) }
            }
        }
    }

    internal open fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL =
        listOfNotNull(rootUrl, name, profile, label)
            .joinToString("/")
            .let(::URL)

    protected open fun getUrl(arkenv: Arkenv): String {
        val url = arkenv.getOrNull("ARKENV_HTTP_URL") ?: rootUrl
        if (url.isNullOrBlank()) {
            throw MissingArgumentException(HttpFeature::rootUrl, HttpFeature::class)
        }
        return url
    }

    protected open fun getName(arkenv: Arkenv) = arkenv.programName

    private fun run(arkenv: Arkenv, profiles: List<String>, label: String?) =
        resolveUrls(getUrl(arkenv), getName(arkenv), profiles, label)
            .map(::parse)
            .reduce { acc, map -> acc + map }
            .also(::println) // TODO remove
            .let(arkenv::putAll)

    private fun parse(url: URL): Map<String, String> = try {
        httpClient.get(url).use(PropertyFeature.Companion::parseProperties)
    } catch (ex: Exception) {
        throw ArkenvException("There was a problem loading a remote source.", ex)
    }
}
