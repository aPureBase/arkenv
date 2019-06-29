package com.apurebase.arkenv.feature.http

import java.io.InputStream
import java.net.URL

open class HttpClientImpl : HttpClient {

    override fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL> =
        listOf(makeUrl(rootUrl, name, profile, label))

    override fun get(url: URL): InputStream = url.openStream()

    open fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL =
        listOfNotNull(rootUrl, name, profile, label)
            .joinToString("/")
            .let(::URL)
}
