package se.umu.cs.phbo0006.parkLens.controller

import se.umu.cs.phbo0006.parkLens.model.holiday.Holiday
import se.umu.cs.phbo0006.parkLens.model.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import se.umu.cs.phbo0006.parkLens.model.holiday.HolidayRepository



fun isRestrictedNow(
    rules: List<ParkingRule>,
    now: LocalDateTime = LocalDateTime.of(2025, 4, 30,10, 13, 30), // TEST
    isBlueBackground: Boolean = true
): Boolean {
    val hour = now.hour
    val date = now.toLocalDate()

    val holidays = HolidayRepository.holidays
    val isHoliday = isHoliday(date, holidays)
    val isPreHoliday = holidays.any {
        it.date == date.plusDays(1).format(DateTimeFormatter.ISO_DATE)
    }
    var matched = false
    for (rule in rules) {
        val applies = when (rule.type) {
            SymbolType.WEEKDAY -> !isHoliday && !isPreHoliday
            SymbolType.PRE_HOLIDAY -> isPreHoliday
            SymbolType.HOLIDAY -> isHoliday
            else -> false
        }

        if (applies && hour in rule.startHour until rule.endHour) {
            matched = true

        }
    }

    return if (isBlueBackground) { !matched }
    else { matched }
}



private fun isHoliday(date: LocalDate, holidays: List<Holiday>): Boolean {
    val dateStr = date.format(DateTimeFormatter.ISO_DATE) // "yyyy-MM-dd"
    return holidays.any { it.date == dateStr }
}