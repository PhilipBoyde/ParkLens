package se.umu.cs.phbo0006.parkLens.controller

import android.util.Log
import se.umu.cs.phbo0006.parkLens.model.holiday.Holiday
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import se.umu.cs.phbo0006.parkLens.model.holiday.HolidayRepository
import java.time.DayOfWeek

/**
 * Determines if a parking time is valid based on a provided parking rule and the current time.
 * The function checks for weekday, holidays and pre-holiday periods to determine if the parking time is allowed.
 *
 * @param rule The parking rule object defining the time window for the parking area.
 * @param now The current LocalDateTime representing the time being checked.
 * @return True if the parking time is valid according to the rule and holiday status, false otherwise.
 *
 * @see ParkingRule for details on the structure of the ParkingRule object.
 * @see <a href="https://www.transportstyrelsen.se/sv/vagtrafik/trafikregler-och-vagmarken/vagmarken/tillaggstavlor/tidsangivelse/">Official "Tidsangivelse" sign on Transportstyrelsen</a>
 */
fun t6TimeIndication(
    rule: ParkingRule,
    now: LocalDateTime = LocalDateTime.of(2025, 5, 2, 12, 20, 10), // TEST
): Boolean {
    val hour = now.hour
    val date = now.toLocalDate()
    val dayOfWeek = date.dayOfWeek

    Log.e("hour", hour.toString())
    Log.e("start and end", "Start: ${rule.startHour}, END: ${rule.endHour}")

    val holidays = HolidayRepository.holidays
    val isHoliday = isHoliday(date, holidays)
    val isSunday = dayOfWeek == DayOfWeek.SUNDAY
    val isSaturday = dayOfWeek == DayOfWeek.SATURDAY
    val isWeekday = dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.FRIDAY

    // Check if tomorrow is a holiday (for pre-holiday logic)
    val tomorrowIsHoliday = isHoliday(date.plusDays(1), holidays)


    val applies = when (rule.type) {
        SymbolType.WEEKDAY -> {  isWeekday } // (Mon-Fri)
        SymbolType.PRE_HOLIDAY -> { isSaturday || tomorrowIsHoliday } // Saturday OR day before a holiday
        SymbolType.HOLIDAY -> { isHoliday || isSunday } // holiday OR Sunday
        else -> false
    }

    if (!applies) {
        return false
    }

    if (hour !in rule.startHour until rule.endHour) {
        return false
    }

    return true
}


/**
 * Checks if a given date is a holiday based on a list of holidays.
 * The function converts the date to a string format ("yyyy-MM-dd") and compares it to the dates of the holidays.
 *
 * @param date The LocalDate representing the date to check.
 * @param holidays A list of Holiday objects representing the holidays.
 * @return True if the provided date is present in the list of holidays, false otherwise.
 */
private fun isHoliday(date: LocalDate, holidays: List<Holiday>): Boolean {
    val dateStr = date.format(DateTimeFormatter.ISO_DATE)
    return holidays.any { it.date == dateStr }
}

/**
 * Checks the t16Fee sign. The "avgift" sign indicates a toll fee is applicable.
 * If true, there is nothing else on the sign, if it's false, there are more options,
 * else no fee.
 *
 * @param size The size value to check.
 * @return True if there is nothing else on the sign, false if there are more options, or null if no fee.
 *         Requires the "avgift" sign to be present.
 * @see <a href="https://www.transportstyrelsen.se/sv/vagtrafik/trafikregler-och-vagmarken/vagmarken/tillaggstavlor/avgift/">Official "avgift" sign on Transportstyrelsen</a>
 */
fun t16Fee(size: Int) : Boolean? {
    return if (size == 1){ true
    } else if (size > 1) { false
    } else { null
    }
}

/**
 * Checks the t18TimedParking sign. The "Tillåten tid för parkering" sign indicates the maximum amount of time allowed to park.
 *
 * This function parses a string to extract the time allowed for parking, as indicated by the "Tillåten tid för parkering" sign.
 * It expects a string containing a single number representing the maximum parking time.
 *
 * @return The maximum parking time allowed in hours (as an integer).
 * @throws IllegalArgumentException if no number is found in the input text, if more than one number is found, or if the number is zero.
 *
 * @see <a href="https://www.transportstyrelsen.se/sv/vagtrafik/trafikregler-och-vagmarken/vagmarken/tillaggstavlor/tillaten-tid-for-parkering/">Official "Tillåten tid för parkering" sign on Transportstyrelsen</a>
 */
fun t18TimedParking(text: String): Int {
    val regex = "\\b\\d{1,2}\\b".toRegex()
    val matches = regex.findAll(text).map { it.value.toInt() }.toList()

    return when {
        matches.isEmpty() -> throw IllegalArgumentException("No number found in text")
        matches.size > 1 -> throw IllegalArgumentException("More than one number found: $matches")
        matches.first() == 0 -> throw IllegalArgumentException("Number cannot be 0")
        else -> matches.first()
    }
}