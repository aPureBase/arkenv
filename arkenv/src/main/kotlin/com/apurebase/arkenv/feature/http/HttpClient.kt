package com.apurebase.arkenv.feature.http

import java.io.InputStream
import java.net.URL

/**
 * Defines what [URL]s should be queried and how they are resolved.
 */
interface HttpClient {

    /**
     * Generates a list of [URL]s to query from the application information.
     */
    fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL>

    /**
     * Resolves a single [URL] and returns an [InputStream] to be read.
     */
    fun get(url: URL): InputStream
}
