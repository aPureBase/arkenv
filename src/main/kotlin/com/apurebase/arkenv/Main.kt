package com.apurebase.arkenv

class MyArgs(args: Array<String>) : Arkenv(args) {

    val doExecute by argument<Boolean>("-ex", "-execute") {
        description = "Whether to execute the program or not"
    }

    val fixedValue: Int by argument("-fv") {
        description = "Has default so does not need to be passed"
        defaultValue = 5
    }

}

fun main(args: Array<String>) {
    val myArgs = MyArgs(args)
    println(myArgs)
    if (myArgs.doExecute) {
        println("Let's go!")
    } else {
        println("Thanks anyway")
    }
}