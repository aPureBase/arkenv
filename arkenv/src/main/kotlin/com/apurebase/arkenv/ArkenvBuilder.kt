package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.feature.EnvironmentVariableFeature
import com.apurebase.arkenv.feature.PlaceholderParser
import com.apurebase.arkenv.feature.ProcessorFeature
import com.apurebase.arkenv.feature.cli.CliFeature

/**
 * [Arkenv] configuration builder which controls features and other settings.
 */
class ArkenvBuilder {

    /**
     * Whether data should be cleared before parsing.
     */
    var clearInputBeforeParse = false

    /**
     * Whether data should be cleared after parsing
     */
    var clearInputAfterParse = true

    internal val features: MutableList<ArkenvFeature> = mutableListOf()
    internal val processorFeatures: MutableList<ProcessorFeature> = mutableListOf()
    private val positionAtEnd = -1

    /**
     * Installs the [feature] into [Arkenv].
     */
    fun install(feature: ArkenvFeature) {
        features.add(feature)
    }

    /**
     * Installs the [feature] for processing.
     * By default, the feature is put after already installed features.
     * @param feature the functionality to install
     * @param index the position of the [feature]
     */
    fun install(feature: ProcessorFeature, index: Int = positionAtEnd) {
        if (index == positionAtEnd) processorFeatures.add(feature)
        else processorFeatures.add(index, feature)
    }

    /**
     * Uninstalls the [feature] from [Arkenv] if installed.
     */
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

/**
 * Configure [Arkenv] settings.
 */
inline fun configureArkenv(block: (ArkenvBuilder.() -> Unit)) = ArkenvBuilder().apply(block)
