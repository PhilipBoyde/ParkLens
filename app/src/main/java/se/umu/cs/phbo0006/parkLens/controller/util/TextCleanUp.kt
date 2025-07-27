package se.umu.cs.phbo0006.parkLens.controller.util

fun cleanStringLine(line: String): String {
    val invalidChars = setOf('"', ',', '.', '|', '!', '$', '&', '/', '#')

    return line.map { char ->
        when (char) {
            in invalidChars -> " "
            'á', 'à', 'ȁ', 'â' -> "å"
            '{', '[' -> "("
            '}', ']' -> ")"
            else -> char.toString()
        }
    }.joinToString("")
}


fun cleanTimeBasedLine(line: String): String{
    return line.map { char ->
        when (char) {
            'o' -> "0"
            'O' -> "0"
            else -> char.toString()
        }
    }.joinToString("")
}