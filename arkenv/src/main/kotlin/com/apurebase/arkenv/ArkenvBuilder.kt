package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.EnvironmentVariableFeature
import com.apurebase.arkenv.feature.PlaceholderParser
import com.apurebase.arkenv.feature.ProcessorFeature
import com.apurebase.arkenv.feature.cli.CliFeature

class ArkenvBuilder {

    var clearInputBeforeParse = false
    var clearInputAfterParse = true
    internal val features: MutableList<ArkenvFeature> = mutableListOf()
    internal val processorFeatures: MutableList<ProcessorFeature> = mutableListOf()

    fun install(feature: ArkenvFeature) {
        features.add(feature)
    }

    fun install(feature: ProcessorFeature) {
        processorFeatures.add(feature)
    }

    fun uninstall(feature: ArkenvFeature) {
        features.removeIf {
            feature.getKeyValPair().first == it.getKeyValPair().first
        }
    }

    init {
        install(CliFeature())
        install(EnvironmentVariableFeature())
        install(PlaceholderParser())
    }
}

inline fun configureArkenv(block: (ArkenvBuilder.() -> Unit)) = ArkenvBuilder().apply(block)
