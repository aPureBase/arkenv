package com.apurebase.arkenv

import com.apurebase.arkenv.argument.ArkenvArgumentNamingStrategy
import com.apurebase.arkenv.feature.*
import com.apurebase.arkenv.feature.cli.CliFeature
import com.apurebase.arkenv.util.key

/**
 * [Arkenv] configuration builder which controls features and other settings.
 * @param installAdvancedFeatures whether to install the profile and placeholder feature.
 */
class ArkenvBuilder(installAdvancedFeatures: Boolean = true) : ArkenvConfiguration {

    /**
     * A common prefix that is applied to all argument names.
     */
    override var prefix: String? = null

    /**
     * Whether data should be cleared before parsing.
     */
    var clearInputBeforeParse = true

    /**
     * Whether data should be cleared after parsing.
     */
    var clearInputAfterParse = false

    /**
     * Defines the argument naming strategy that will be used to determine the applicable names.
     */
    var namingStrategy: ArkenvArgumentNamingStrategy = ArkenvArgumentNamingStrategy.ParameterNameOnlyIfNotSpecified

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
            -feature
            features.add(i, feature)
        } ?: features.add(feature)
    }

    /**
     * Installs the feature.
     * @since 3.2.0
     */
    operator fun ArkenvFeature.unaryPlus() = install(this)

    /**
     * Uninstalls the feature.
     * @since 3.2.0
     */
    operator fun ArkenvFeature.unaryMinus() = uninstall(this)

    /**
     * Installs the processor feature.
     * @since 3.2.0
     */
    operator fun ProcessorFeature.unaryPlus() = install(this)

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
        +CliFeature()
        +EnvironmentVariableFeature()
        if (installAdvancedFeatures) {
            +ProfileFeature()
            +PlaceholderParser()
        }
    }
}

/**
 * Configure [Arkenv] settings.
 * @param block Arkenv configuration logic.
 */
inline fun configureArkenv(block: (ArkenvBuilder.() -> Unit)) = ArkenvBuilder().apply(block)
