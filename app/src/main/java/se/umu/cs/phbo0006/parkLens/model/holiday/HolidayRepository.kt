package se.umu.cs.phbo0006.parkLens.model.holiday

/**
 * Represents a repository for storing a list of holidays.
 * This object provides a central location for managing the list of red days (holidays).
 *
 * The repository contains a single, mutable list of Holiday objects.
 * This list is initialized as an empty list.
 */
object HolidayRepository {
    var holidays: List<Holiday> = emptyList()
}