package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.ArkenvException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.net.URL
import com.apurebase.arkenv.feature.http.HttpClient as ArkenvHttpClient

class KtorClient(private val clientEngine: HttpClientEngineFactory<*>) : ArkenvHttpClient {

    override fun get(url: URL): InputStream = HttpClient(clientEngine).use {
        runBlocking { it.get<HttpResponse>(url) }.handle()
    }

    private fun HttpResponse.handle(): InputStream {
        if (!status.value.toString().startsWith("2")) throw ArkenvException("Http Exception: $status")
        return content.toInputStream()
    }
}

