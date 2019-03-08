package com.apurebase.arkenv

class PropertyFeature(val file: String) : ArkenvFeature {

    override fun install(arkenv: Arkenv) {
        loadProperties(file, arkenv)
    }

}
