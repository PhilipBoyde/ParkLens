package se.umu.cs.phbo0006.parkLens.model

data class Rules(
    val freeParking: Boolean?,
    val currentTime: String,
    val allowedToPark: Boolean,
    val paymentRule: PaymentRule?,
    val timeRangeRule: TimeRangeRule?
)

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

    data class TimeRangeRule(
        val timeRange: Int? = null,
    ) {
        init {
            require(timeRange == null || timeRange > 0) {
                "timeRange must be greater than 0 if specified."
            }
        }
    }