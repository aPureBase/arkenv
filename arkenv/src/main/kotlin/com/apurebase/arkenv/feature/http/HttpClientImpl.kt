package com.apurebase.arkenv.feature.http

import java.io.InputStream
import java.net.URL

/**
 * The default [HttpClient] implementation.
 */
open class HttpClientImpl : HttpClient {

    override fun get(url: URL): InputStream = url.openStream()

}
