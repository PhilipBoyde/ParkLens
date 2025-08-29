package se.umu.cs.phbo0006.parkLens.model.signs

/**
 * Represents a single parking rule.
 *
 * This data class defines a parking rule, including its text description,
 * type, start and end hours, and an optional subtype.
 *
 * @param text The text description of the parking rule.
 * @param type The type of the parking rule.
 * @param startHour The starting hour of the rule (nullable).
 * @param endHour The ending hour of the rule (nullable).
 * @param subType The subtype of the rule (nullable), e.g. HOUR.
 */
data class ParkingRule(
    val text: String,
    var type: SymbolType,
    val startHour: Int? = null,
    val endHour: Int? = null,
    val subType: SymbolType? = null
)