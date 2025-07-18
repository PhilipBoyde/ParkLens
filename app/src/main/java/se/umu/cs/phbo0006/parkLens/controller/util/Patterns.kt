package se.umu.cs.phbo0006.parkLens.controller.util

import se.umu.cs.phbo0006.parkLens.model.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule


val parkingSignPatterns = listOf(
    Regex("""(?i)\b(avgift|betala|betald)\b""") to SymbolType.PAID,

    // Time restrictions
    Regex("""(?<!\()\b(\d{1,2})[–\-](\d{1,2})\b""") to SymbolType.WEEKDAY,
    Regex("""\(\s*(\d{1,2})\s*[-–]\s*(\d{1,2})\s*\)""") to SymbolType.PRE_HOLIDAY,
    Regex("""\b(\d{1,2})\s*tim\b""") to SymbolType.TIME_RANGE,
    Regex("""\[(\d{1,2})[-–](\d{1,2})\]""") to SymbolType.HOLIDAY // red days [8–13]
)

fun parseParkingSign(text: String): SymbolType {
    return parkingSignPatterns.firstOrNull {
        it.first.find(text) != null
    }?.second ?: SymbolType.UNKNOWN
}

fun extractRuleFromLine(line: String): ParkingRule? {
    for ((pattern, type) in parkingSignPatterns) {
        val match = pattern.find(line) ?: continue

        if (type == SymbolType.TIME_RANGE || type == SymbolType.PAID) continue

        val start = match.groupValues[1].toIntOrNull()
        val end = match.groupValues[2].toIntOrNull()

        if (start != null && end != null) {
            return ParkingRule(start, end, type)
        }
    }

    return null
}