package com.apurebase.arkenv.feature.http

import java.io.InputStream
import java.net.URL

interface HttpClient {

    fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL>

    fun get(url: URL): InputStream
}
