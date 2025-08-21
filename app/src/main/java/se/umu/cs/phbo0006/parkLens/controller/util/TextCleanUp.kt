package se.umu.cs.phbo0006.parkLens.controller.util

/**
 * Cleans a string by replacing invalid characters and making consistent replacements.
 * This function removes specified invalid characters and makes replacements for common variations,
 * ensuring a more consistent and predictable string representation.
 *
 * @param line The input string to clean.
 * @return The cleaned string.
 */
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

/**
 * Cleans a string by replacing 'o' and 'O' with '0'.
 * This function simplifies the string by converting all occurrences of uppercase 'O' and lowercase 'o'
 * to '0', providing a more uniform string representation.
 *
 * @param line The input string to clean.
 * @return The cleaned string with 'o' and 'O' replaced by '0'.
 */
fun cleanTimeBasedLine(line: String): String{
    return line.map { char ->
        when (char) {
            'o' -> "0"
            'O' -> "0"
            else -> char.toString()
        }
    }.joinToString("")
}