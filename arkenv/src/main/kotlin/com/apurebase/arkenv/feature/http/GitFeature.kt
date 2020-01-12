package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.MissingArgumentException
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.feature.http.GitFeature.RemoteType.*
import com.apurebase.arkenv.findFeature
import java.net.URL
import java.net.URLEncoder

/**
 * Supports loading of configuration from git repositories.
 */
class GitFeature(override var httpClient: HttpClient = HttpClientImpl()) : HttpFeature() {

    enum class RemoteType(val defaultHost: String) {
        Github("https://github.com"),
        Gitlab("https://gitlab.com");
    }

    companion object {

        private const val argPrefix = "ARKENV_REMOTE"

        internal fun shouldBeInstalled(arkenv: Arkenv): Boolean {
            return !arkenv.getOrNull("${argPrefix}_PROJECT_ID").isNullOrBlank()
        }

    }

    private lateinit var arkenv: Arkenv
    private var remoteType: RemoteType = Github
    private var branch = "master"

    override fun onLoad(arkenv: Arkenv) {
        this.arkenv = arkenv
        remoteType = getOrNull("TYPE")?.capitalize()?.let(::valueOf) ?: Github
        branch = getOrNull("BRANCH") ?: "master"
        super.onLoad(arkenv)
    }

    override fun getName(arkenv: Arkenv): String =
        getOrNull("PREFIX") ?: arkenv.findFeature<ProfileFeature>()?.prefix ?: "application"

    override fun getUrl(arkenv: Arkenv): String =
        arkenv.getOrNull("ARKENV_HTTP_URL") ?: rootUrl ?: remoteType.defaultHost

    override fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL {
        val encodedPath = URLEncoder.encode(getResourcePath(name, profile), Charsets.UTF_8)
        return super.makeUrl(rootUrl, getApiPath(), encodedPath, getReference())
    }

    private fun getApiPath(): String {
        val projectId = get("PROJECT_ID")
        return when (remoteType) {
            Github -> "$projectId/raw/$branch"
            Gitlab -> "api/v4/projects/$projectId/repository/files"
        }
    }

    private fun getReference(): String? = when (remoteType) {
        Github -> null
        Gitlab -> "raw?ref=$branch"
    }

    private fun getResourcePath(name: String, profile: String?): String {
        val extension = getOrNull("EXTENSION") ?: "properties"
        val dirPath = getOrNull("DIRECTORY")
        val fileName = if (profile.isNullOrBlank()) name else "$name-$profile"
        return (if (dirPath != null) "$dirPath/$fileName" else fileName) + ".$extension"
    }

    private fun get(key: String) = getOrNull(key) ?: throw MissingArgumentException(key, this::class)
    private fun getOrNull(key: String) = arkenv.getOrNull("${argPrefix}_$key")
}
