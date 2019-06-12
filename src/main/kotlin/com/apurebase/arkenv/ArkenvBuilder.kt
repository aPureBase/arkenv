package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature

class ArkenvBuilder {

    var clearInputBeforeParse = false
    var clearInputAfterParse = true
    internal val features: MutableList<ArkenvFeature> = mutableListOf()

    fun install(feature: ArkenvFeature) {
        features.add(feature)
    }

    fun uninstall(feature: ArkenvFeature) {
        features.removeIf {
            feature.getKeyValPair().first == it.getKeyValPair().first
        }
    }
}
