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
    var clearInputBeforeParse = true

    /**
     * Whether data should be cleared after parsing.
     */
    var clearInputAfterParse = false

    internal val features: MutableList<ArkenvFeature> = mutableListOf()
    internal val processorFeatures: MutableList<ProcessorFeature> = mutableListOf()

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
     * @param positionInFront if the feature should be installed before existing ones, so that its logic will be called
     * first.
     */
    fun install(feature: ProcessorFeature, positionInFront: Boolean = true) {
        if (positionInFront) processorFeatures.add(0, feature)
        else processorFeatures.add(feature)
    }

    /**
     * Uninstalls the [feature] from [Arkenv] if installed.
     */
    fun uninstall(feature: ArkenvFeature) {
        features.removeIf { feature.key == it.key }
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
