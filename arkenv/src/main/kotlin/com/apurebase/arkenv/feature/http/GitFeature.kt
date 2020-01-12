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
 *
 * Similar to the [ProfileFeature], it will attempt to load a base profile, and any active profiles.
 *
 * Arguments:
 *
 * *ARKENV_REMOTE_TYPE* the type of git host, defaults to github.
 *
 * *ARKENV_HTTP_URL* the root url of the remote host, defaults to the remote type's public host,
 * i.e. https://github.com.
 *
 * *ARKENV_REMOTE_PROJECT_ID* the project identification relative to the remote type. Github uses the
 * *owner/repository* convention.
 *
 * *ARKENV_REMOTE_BRANCH* the target branch, defaults to master.
 *
 * *ARKENV_REMOTE_EXTENSION* the file extension in the repository, defaults to properties.
 *
 * *ARKENV_REMOTE_PREFIX* the prefix for any configuration files, defaults to the profile feature prefix,
 * or application.
 *
 * *ARKENV_REMOTE_DIRECTORY* the sub directory path where the configuration files are located, defaults to top level.
 *
 */
class GitFeature(override var httpClient: HttpClient = HttpClientImpl()) : HttpFeature() {

    enum class RemoteType(val defaultHost: String) {
        Github("https://github.com"),
        Gitlab("https://gitlab.com");
    }

    companion object {
        private const val argPrefix = "ARKENV_REMOTE"
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

    override fun resolveUrls(
        rootUrl: String,
        name: String,
        profiles: List<String>,
        label: String?
    ): Iterable<URL> {
        val rootProfile = listOf(makeUrl(rootUrl, name, null, label))
        return when {
            profiles.isEmpty() -> rootProfile
            else -> rootProfile + profiles.map { makeUrl(rootUrl, name, it, label) }
        }
    }

    override fun getName(arkenv: Arkenv): String =
        getOrNull("PREFIX") ?: arkenv.findFeature<ProfileFeature>()?.prefix ?: "application"

    override fun getUrl(arkenv: Arkenv): String =
        arkenv.getOrNull("ARKENV_HTTP_URL") ?: rootUrl ?: remoteType.defaultHost

    override fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL {
        val encodedPath = URLEncoder.encode(getResourcePath(name, profile), Charsets.UTF_8.displayName())
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

    private fun get(key: String) = getOrNull(key) ?: throw MissingArgumentException(key, GitFeature::class)
    private fun getOrNull(key: String) = arkenv.getOrNull("${argPrefix}_$key")
}
