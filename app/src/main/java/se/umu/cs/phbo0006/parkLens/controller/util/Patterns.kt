package se.umu.cs.phbo0006.parkLens.controller.util

import android.util.Log
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule


val parkingSignPatterns = listOf(
    Regex("""(?i)\b(avgift|betala|betald)\b""") to SymbolType.PAID,
    Regex("(?i)^p$") to SymbolType.PARKING,

    Regex("""(?<!\()\b(\d{1,2}|[oO0])\s*[–\-]\s*(\d{1,2}|[oO0])\b""") to SymbolType.WEEKDAY,
    Regex("""\(\s*(\d{1,2})\s*[-–]\s*(\d{1,2})\s*\)""") to SymbolType.PRE_HOLIDAY,
    Regex("""\b(\d{1,2})\s*tim\b""") to SymbolType.TIME_RANGE,
)

fun extractRuleFromLine(line: String): ParkingRule? {
    val cleanedLine = cleanStringLine(line)
    Log.e("Rule", cleanedLine.toString())

    for ((pattern, type) in parkingSignPatterns) {
        val match = pattern.find(cleanedLine)
        if (match == null) continue

        return when (type) {
            SymbolType.WEEKDAY, SymbolType.PRE_HOLIDAY -> {
                val cleanedTimeLine = cleanTimeBasedLine(cleanedLine)

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
                    ParkingRule(text = cleanedLine, type = type, startHour = hours, endHour = hours)
                } else null
            }

            else -> ParkingRule(text = cleanedLine, type = type)
        }
    }

    return ParkingRule(text = cleanedLine, type = SymbolType.UNKNOWN)
}
