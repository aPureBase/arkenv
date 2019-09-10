package com.apurebase.arkenv.test

import mockit.Mock
import mockit.MockUp

class MockSystem(private val envs: Map<String, String>) : MockUp<System>() {

    constructor(vararg envs: Pair<String, String>) : this(mapOf(*envs))

    @Mock fun getenv(name: String) = envs[name]

}
