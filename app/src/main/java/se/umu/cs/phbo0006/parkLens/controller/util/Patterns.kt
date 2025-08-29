package se.umu.cs.phbo0006.parkLens.controller.util


import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule


val parkingSignPatterns = listOf(
    Regex("""(?i)\b(avgift|betala|betald)\b""") to SymbolType.PAID,
    Regex("(?i)^p$") to SymbolType.PARKING,

    Regex("""\(\s*(\d{1,2}|[oO])\s*[–—-]\s*(\d{1,2}|[oO])\s*\)""") to SymbolType.PRE_HOLIDAY,
    Regex("""(?<!\()\b([oO]?\d{1,2}|[oO])\s*[–\-]\s*([oO]?\d{1,2}|[oO])\b""") to SymbolType.WEEKDAY,

    Regex("""\b(\d{1,2})\s*(tim|dygn|min)\b""") to SymbolType.TIME_RANGE,
)


/**
 * Extracts a ParkingRule from a given line of text based on regular expression matching.
 *
 * This function attempts to identify the parking rule type (e.g. PAID, PARKING, WEEKDAY)
 * from the input line using the predefined regular expressions.
 *
 * @param line The input string representing the parking sign.
 * @return A ParkingRule object if a match is found.
 */
fun extractRuleFromLine(line: String): ParkingRule? {
    val cleanedLine = TextCleanUp.cleanStringLine(line)

    for ((pattern, type) in parkingSignPatterns) {
        val match = pattern.find(cleanedLine)
        if (match == null) continue

        return when (type) {
            SymbolType.WEEKDAY, SymbolType.PRE_HOLIDAY, SymbolType.HOLIDAY  -> {
                val cleanedTimeLine = TextCleanUp.cleanTimeBasedLine(cleanedLine)

                val start = match.groupValues.getOrNull(1)?.replace(Regex("[oO]"), "0")?.toIntOrNull()
                val end = match.groupValues.getOrNull(2)?.replace(Regex("[oO]"), "0")?.toIntOrNull()
                if (start != null && end != null) {
                    ParkingRule(text = cleanedTimeLine, type = type, startHour = start, endHour = end)
                } else {
                    null
                }
            }

            SymbolType.TIME_RANGE -> {
                val hours = match.groupValues.getOrNull(1)?.toIntOrNull()
                if (hours != null) {
                    ParkingRule(text = cleanedLine, type = type, startHour = hours, endHour = hours, detectTimeUnit(cleanedLine))
                } else null
            }

            else -> ParkingRule(text = cleanedLine, type = type)
        }
    }

    return ParkingRule(text = cleanedLine, type = SymbolType.UNKNOWN)
}

/**
 * Detects the time unit (hour, minute, day) from a time-based string.
 *
 * This function analyzes the input string to determine the time unit represented.
 *
 * @param input The input string representing a time range.
 * @return The SymbolType representing the time unit (HOUR, MINUTE, or DAY).
 * @throws IllegalArgumentException if the input string does not represent a valid time unit.
 */
fun detectTimeUnit(input: String): SymbolType {
    val lowercaseInput = input.lowercase()

    return when {
        lowercaseInput.contains("tim") -> SymbolType.HOUR
        lowercaseInput.contains("min") -> SymbolType.MINUTE
        lowercaseInput.contains("dygn") -> SymbolType.DAY
        else -> throw IllegalArgumentException("$input, does not exist in time range" )
    }
}