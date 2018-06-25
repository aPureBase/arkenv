package com.apurebase.arkenv

class TestArgs(args: Array<String>) : Arkenv(args) {

    val country: String by argument("-c") {
        description = "Country to extract"
    }
    val bool: Boolean by argument("-b") {
        description = "Whether to execute"
    }

    val nullInt: Int? by argument("-ni") {
        description = "A nullable Int, which doesn't have to be declared"
    }

    val mainString: String by mainArgument {
        description = "This is a com.apurebase.arkenv.main arg, so no names"
    }

}

class MainArg(value: String) : Arkenv(arrayOf(value)) {
    val mainArg by mainArgument<String>()
}

class Nullable(args: Array<String>): Arkenv(args) {

    val int: Int? by argument("-i")

    val str: String? by argument("-s")

}