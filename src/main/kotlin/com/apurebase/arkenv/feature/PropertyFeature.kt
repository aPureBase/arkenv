package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.loadProperties

class PropertyFeature(val file: String) : ArkenvFeature {

    override fun installLoader(arkenv: Arkenv) {
        loadProperties(file, arkenv)
    }

}
