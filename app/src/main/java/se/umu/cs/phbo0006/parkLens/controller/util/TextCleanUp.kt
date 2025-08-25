package se.umu.cs.phbo0006.parkLens.controller.util


class TextCleanUp {
    companion object {
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
         * Cleans and standardizes time-based string format to ensure consistent spacing around dash separator.
         *
         * This function processes strings that contain two parts separated by a dash
         * and ensures they follow a consistent format with exactly one space before and after the dash.
         *
         * Character replacements performed:
         * - 'o' and 'O' are replaced with '0' (zero)
         *
         * Formatting rules:
         * - Ensures exactly one space before the dash: " - "
         * - Preserves original numbers without adding leading zeros
         * - Preserves parentheses '(' and ')'
         * - Removes extra spaces and other non-digit, non-parenthesis characters from each side
         *
         * @param line The input string to be cleaned (e.g., "07- 16", "8   -   18", "(7-6)")
         * @return A formatted string with consistent spacing around the dash (e.g., "07 - 16", "8 - 18", "(7 - 6)")
         *         If no dash is found or multiple dashes exist, returns the character-cleaned version of the original string
         *
         */
        fun cleanTimeBasedLine(line: String): String {
            val cleanedChars = line.map { char ->
                when (char) {
                    'o' -> '0'
                    'O' -> '0'
                    else -> char
                }
            }.joinToString("")

            val parts = cleanedChars.split("-")

            if (parts.size != 2) {
                return cleanedChars
            }

            val leftDigits = parts[0].filter { it.isDigit() || it == '(' || it == ')' }
            val rightDigits = parts[1].filter { it.isDigit() || it == '(' || it == ')' }

            val leftPart = leftDigits.ifEmpty { "00" }
            val rightPart = rightDigits.ifEmpty { "00" }

            return "$leftPart - $rightPart"
        }
    }
}
