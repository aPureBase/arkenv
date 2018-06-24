package com.apurebase.arkenv

import mockit.Mock
import mockit.MockUp

class MockSystem(val envs: Map<String, String>) : MockUp<System>() {

    @Mock fun getenv(name: String) = envs[name]

}