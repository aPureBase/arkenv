package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.ArkenvException
import com.apurebase.arkenv.feature.http.RemoteType.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLParameter
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.URL

enum class RemoteType {
    Github,
    Gitlab;
}

class KtorHttpClient(private val clientEngine: HttpClientEngineFactory<*>) : HttpClientImpl() {

    private val argPrefix = "ARKENV_REMOTE"
    private val remoteType: RemoteType get() = getOrNull("TYPE")?.capitalize()?.let(::valueOf) ?: Github
    private val branch get() = getOrNull("BRANCH") ?: "master"

    override fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL> =
        listOfNotNull(
            if (profile != null) makeUrl(rootUrl, name, null, null) else null, // base
            makeUrl(rootUrl, name, profile, label) // profile-specific
        )

    override fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL =
        super.makeUrl(rootUrl, getApiPath(), getResourcePath(name, profile), getReference())

    private fun getApiPath(): String {
        val projectId = getOrNull("PROJECT_ID") ?: "null"
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
        val fileName = if (profile.isNullOrBlank()) "$name.$extension" else "$name-$profile.$extension"
        return (if (dirPath != null) "$dirPath/$fileName" else fileName).encodeURLParameter()
    }

    override fun get(url: URL): InputStream = HttpClient(clientEngine).use {
        println("Accessing $url") // TODO remove
        runBlocking { it.get<HttpResponse>(url) }.handle()
    }

    private fun HttpResponse.handle(): InputStream {
        if (!status.value.toString().startsWith("2")) {
            throw ArkenvException("Http Exception: $status")
        }
        return content.toInputStream()
    }

    private fun getOrNull(argument: String) = arkenv.getOrNull("${argPrefix}_$argument")
}
