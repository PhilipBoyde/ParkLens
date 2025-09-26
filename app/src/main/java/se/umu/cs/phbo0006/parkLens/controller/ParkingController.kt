package se.umu.cs.phbo0006.parkLens.controller

import android.util.Log
import se.umu.cs.phbo0006.parkLens.model.PaymentRule
import se.umu.cs.phbo0006.parkLens.model.Rules
import se.umu.cs.phbo0006.parkLens.model.TimeRangeRule
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.ParkingRule
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import java.time.LocalDateTime

/**
 * Holds the state of the parking evaluation process.
 *
 * @property allowedToPark Indicates if parking is allowed.
 * @property timeBasedParking Indicates if parking is time-based.
 * @property restrictedParking Indicates if parking is restricted.
 * @property paidParkingWholeDay Indicates if paid parking is required for the whole day.
 * @property timeRange The allowed time range for parking.
 * @property timeType The type of time unit (minute, hour, day).
 * @property endParkTime The hour when parking ends.
 * @property freeParking Indicates if parking is free.
 */
private data class ParkingState(
    var allowedToPark: Boolean = false,
    var timeBasedParking: Boolean = false,
    var restrictedParking: Boolean = false,
    var paidParkingWholeDay: Boolean? = null,
    var timeRange: Int? = null,
    var timeType: SymbolType? = null,
    var endParkTime: Int? = null,
    var freeParking: Boolean? = null
)

/**
 * Checks if parking is allowed based on a list of BlockInfo objects.
 *
 * Evaluates various parking rules such as free parking, paid parking, time restrictions,
 * and calculates the allowed parking time range.
 *
 * @param blockInfos List of BlockInfo representing the parking sign blocks to evaluate.
 * @return Rules object containing the results of the parking evaluation.
 */
fun checkIfAllowedToPark(blockInfos: List<BlockInfo>) : Rules {
    val localtime: LocalDateTime = LocalDateTime.now()
    val state = ParkingState()

    blockInfos.forEach { block ->
        processBlock(block, blockInfos.size, localtime, state)
    }

    calculateFinalResult(state, localtime)

    val paymentRule = PaymentRule(
        state.paidParkingWholeDay,
        state.endParkTime
    )

    val timeRangeRule = TimeRangeRule(
        state.timeRange,
    )

    Log.i("Allowed?", "allowedToPark, ${state.allowedToPark}, paidParkingHoleDay ${state.paidParkingWholeDay.toString()}, time ${state.timeRange.toString()}h")
    return Rules(
        state.freeParking,
        "${localtime.hour}:${localtime.minute}",
        state.allowedToPark,
        paymentRule,
        timeRangeRule
    )
}

/**
 * Processes a single BlockInfo object and updates the parking state.
 *
 * @param block The BlockInfo to process.
 * @param blockInfosSize The total number of BlockInfo objects.
 * @param localtime The current local time.
 * @param state The ParkingState to update.
 */
private fun processBlock(
    block: BlockInfo,
    blockInfosSize: Int,
    localtime: LocalDateTime,
    state: ParkingState
) {
    val blockSize = block.rules.size
    val blockColor = block.color

    block.rules.forEach { rule ->
        processRule(rule, blockColor, blockSize, blockInfosSize, localtime, state)
    }
}

/**
 * Processes a single parking rule and updates the parking state.
 *
 * @param rule The ParkingRule to process.
 * @param blockColor The color of the block (SignType).
 * @param blockSize The number of rules in the block.
 * @param blockInfosSize The total number of BlockInfo objects.
 * @param localtime The current local time.
 * @param state The ParkingState to update.
 */
private fun processRule(
    rule: ParkingRule,
    blockColor: SignType,
    blockSize: Int,
    blockInfosSize: Int,
    localtime: LocalDateTime,
    state: ParkingState
) {
    when(rule.type){
        SymbolType.PARKING -> state.freeParking = e19Parking(blockInfosSize, localtime.dayOfWeek)
        SymbolType.PAID -> state.paidParkingWholeDay = t16Fee(blockSize)
        SymbolType.WEEKDAY, SymbolType.PRE_HOLIDAY, SymbolType.HOLIDAY -> {
            if (blockColor == SignType.YELLOW){
                if (!state.restrictedParking){
                    state.restrictedParking = t6TimeIndication(rule, localtime)
                }
            }else{
                if (!state.restrictedParking && !state.timeBasedParking ){
                    state.timeBasedParking = t6TimeIndication(rule, localtime)
                    if (state.timeBasedParking) {
                        state.allowedToPark = true
                        state.endParkTime = rule.endHour
                    }
                }
            }
        }
        SymbolType.TIME_RANGE -> {
            state.timeRange = t18TimedParking(rule.text)
            state.timeType = rule.subType
        }
        else -> {}
    }
}

/**
 * Calculates the final result of the parking evaluation and updates the parking state.
 *
 * @param state The ParkingState to update.
 * @param localtime The current local time.
 */
private fun calculateFinalResult(state: ParkingState, localtime: LocalDateTime) {
    if(state.allowedToPark && state.restrictedParking){
        state.allowedToPark = false
    }

    if (state.timeRange != null && state.endParkTime != null && state.timeType != null){
        state.timeRange = parkingTimeCalibration(state.timeRange!!, state.timeType!!, state.endParkTime!!, localtime)
    }

    // If its paid whole day and outside of the t6TimeIndication
    if (!state.allowedToPark && state.paidParkingWholeDay == true && !state.restrictedParking){
        state.allowedToPark = true
        state.timeRange = null
    }
}

/**
 * Calibrates the allowed parking time based on the time range, time unit, end park time, and current time.
 *
 * @param timeRange The allowed time range for parking.
 * @param timeUnit The unit of the time range (minute, hour, day).
 * @param endParkTime The hour when parking ends.
 * @param localtime The current local time.
 * @return The calibrated parking time in minutes.
 * @throws IllegalArgumentException if the time unit is not allowed.
 */
private fun parkingTimeCalibration(timeRange: Int, timeUnit: SymbolType, endParkTime: Int, localtime: LocalDateTime): Int {
    val currentTimeInMinutes = localtime.hour * 60 + localtime.minute
    val endParkTimeInMinutes = endParkTime * 60

    val remainingTimeInMinutes = if (endParkTimeInMinutes >= currentTimeInMinutes) {
        endParkTimeInMinutes - currentTimeInMinutes
    } else {
        (24 * 60 - currentTimeInMinutes) + endParkTimeInMinutes
    }

    val timeRangeInMinutes = when (timeUnit) {
        SymbolType.MINUTE -> timeRange
        SymbolType.HOUR -> timeRange * 60
        SymbolType.DAY -> timeRange * 24 * 60
        else -> { throw IllegalArgumentException ("$timeUnit, is not allowed!") }
    }

    return if (timeRangeInMinutes > remainingTimeInMinutes) {
        remainingTimeInMinutes
    } else {
        timeRangeInMinutes
    }
}