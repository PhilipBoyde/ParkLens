package se.umu.cs.phbo0006.parkLens.model

/**
 * Represents a set of parking rules.
 *
 * This data class holds information about the parking regulations,
 * including whether parking is free, the current time, and
 * the associated time-based rules.
 *
 * @param freeParking Whether parking is free (nullable).
 * @param currentTime The current time.
 * @param allowedToPark Whether parking is allowed.
 * @param paymentRule The payment rule (nullable).
 * @param timeRangeRule The time range rule (nullable).
 */
data class Rules(
    val freeParking: Boolean?,
    val currentTime: String,
    val allowedToPark: Boolean,
    val paymentRule: PaymentRule?,
    val timeRangeRule: TimeRangeRule?
)

    /**
     * Represents a payment-related parking rule.
     *
     * This data class defines a rule concerning payment for parking,
     * including whether parking is allowed throughout the entire day
     * and an optional end park time.
     *
     * @param paidParkingWholeDay Whether parking is allowed throughout the entire day (nullable).
     * @param endParkTime The end park time in hours (nullable).
     */
    data class PaymentRule(
        val paidParkingWholeDay: Boolean? = null,
        val endParkTime: Int? = null
    ) {
        init {
            require(endParkTime == null || (endParkTime in 0..24)) {
                "endParkTime must be between 0 and 24 if specified."
            }
        }
    }

    /**
     * Represents a time-based parking rule.
     *
     * This data class defines a rule related to parking based on a specific time range.
     *
     * @param timeRange The time range in hours (nullable).
     */
    data class TimeRangeRule(
        val timeRange: Int? = null,
    ) {
        init {
            require(timeRange == null || timeRange > 0) {
                "timeRange must be greater than 0 if specified."
            }
        }
    }