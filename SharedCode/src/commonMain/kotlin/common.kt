package jeremywsherman.com.handson.kmp

/** The name of the currently running platform. */
expect fun platformName(): String

/**
 Let's see if this doc comment makes it through to the module!

 You'd have to either be generating a Swift module or embedding headers for that to work.
 And I do see headers in the framework output, so! Let's see if this survives.
 */
fun createApplicationScreenMessage() =
    "Kotlin Rocks on ${platformName()}"

fun canYouDebugMe(value: String): String {
    val isMatch = value.contains('a')
    val happy = "it's a match!"
    val sad = "better luck next time eh?"
    val result = if (isMatch) happy else sad
    return result
}
