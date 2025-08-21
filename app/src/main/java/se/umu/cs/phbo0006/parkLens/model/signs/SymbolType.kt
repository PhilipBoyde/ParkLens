package se.umu.cs.phbo0006.parkLens.model.signs

/**
 * Represents the different types of parking rules.
 */
enum class SymbolType {
    PAID,
    PARKING,

    WEEKDAY,
    PRE_HOLIDAY,
    HOLIDAY,

    TIME_RANGE,

    UNKNOWN
}