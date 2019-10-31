package jeremywsherman.com.handson.kmp

expect fun platformName(): String

fun createApplicationScreenMessage() =
    "Kotlin Rocks on ${platformName()}"
