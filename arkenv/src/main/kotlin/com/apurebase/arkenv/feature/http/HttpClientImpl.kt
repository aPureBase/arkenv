package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import java.io.InputStream
import java.net.URL

/**
 * The default [HttpClient] implementation.
 */
open class HttpClientImpl : HttpClient {

    override lateinit var arkenv: Arkenv

    override fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL> =
        listOf(makeUrl(rootUrl, name, profile, label))

    override fun get(url: URL): InputStream = url.openStream()

    open fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL =
        listOfNotNull(rootUrl, name, profile, label)
            .joinToString("/")
            .let(::URL)
}
