package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.ensureEndsWith
import java.io.InputStream
import java.net.URL

class HttpFeature(
    private val rootUrl: String,
    private val application: String? = null,
    private val httpClient: HttpClient = HttpClientImpl()
) : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        httpClient
            .get(makeUrl(arkenv.programName))
            .use(PropertyFeature.Companion::parseProperties)
            .let(arkenv.keyValue::putAll)
    }

    private fun makeUrl(programName: String): String {
        val suffix = "properties"
        return "${rootUrl.ensureEndsWith('/')}${application ?: programName}.$suffix".also(::println)
    }

    fun encryption() {
        TODO()
    }
}

interface HttpClient {

    fun get(url: String): InputStream = URL(url).openStream()

}

class HttpClientImpl : HttpClient

