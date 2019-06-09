package com.apurebase.arkenv.feature.http

import java.net.URL

class GitRepositoryClient : HttpClientImpl() {

    override fun resolveUrls(rootUrl: String, name: String, profile: String?, label: String?): Iterable<URL> {
        return super.resolveUrls(rootUrl, name, profile, label)
    }

    override fun makeUrl(rootUrl: String, name: String, profile: String?, label: String?): URL {
        return super.makeUrl(rootUrl, name, profile, label)
    }
}
