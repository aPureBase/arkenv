package com.apurebase.arkenv

import com.apurebase.arkenv.feature.*
import com.apurebase.arkenv.feature.cli.CliFeature

/**
 * [Arkenv] configuration builder which controls features and other settings.
 * @param installAdvancedFeatures whether to install the profile and placeholder feature.
 */
class ArkenvBuilder(installAdvancedFeatures: Boolean = true) {

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
    internal val modules: MutableList<Arkenv> = mutableListOf()

    /**
     * Installs the [feature] into [Arkenv].
     * If the feature is already installed, it will be replaced, retaining its order.
     * @param feature the feature to install.
     */
    fun install(feature: ArkenvFeature) {
        var index: Int? = null
        features.forEachIndexed { i, arkenvFeature ->
            if (arkenvFeature.key == feature.key) index = i
        }
        index?.let { i ->
            uninstall(feature)
            features.add(i, feature)
        } ?: features.add(feature)
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
     * @param feature the feature to uninstall.
     */
    fun uninstall(feature: ArkenvFeature) {
        features.removeIf { feature.key == it.key }
    }

    init {
        install(CliFeature())
        install(EnvironmentVariableFeature())
        if (installAdvancedFeatures) {
            install(ProfileFeature())
            install(PlaceholderParser())
        }
    }
}

/**
 * Configure [Arkenv] settings.
 * @param block Arkenv configuration logic.
 */
inline fun configureArkenv(block: (ArkenvBuilder.() -> Unit)) = ArkenvBuilder().apply(block)

/**
 * Registers the [module] as a sub module that will be automatically parsed after the super Arkenv.
 * It will be parsed using the configuration of its root.
 * @param module the sub module to add to this [Arkenv]
 */
fun <T: Arkenv> Arkenv.module(module: T): T = module.also { configuration.modules.add(it) }
