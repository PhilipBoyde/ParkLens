package se.umu.cs.phbo0006.parkLens.controller

import android.util.Log
import se.umu.cs.phbo0006.parkLens.model.PaymentRule
import se.umu.cs.phbo0006.parkLens.model.Rules
import se.umu.cs.phbo0006.parkLens.model.TimeRangeRule
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType
import java.time.LocalDateTime

fun checkIfAllowedToPark(blockInfos: List<BlockInfo>) : Rules {
    var allowedToPark =  false
    var timeBasedParking = false
    var restrictedParking = false
    var paidParkingWholeDay: Boolean? = null
    var timeRange: Int? = null
    var timeType: SymbolType? = null
    var endParkTime: Int? = null
    var freeParking: Boolean? = null
    val localtime: LocalDateTime = LocalDateTime.of(2025, 8, 25, 12, 20, 10)

    blockInfos.forEach { block ->
        val blockSize = block.rules.size
        val blockColor = block.color

        block.rules.forEach {
            when(it.type){
                SymbolType.PARKING -> freeParking = e19Parking(blockInfos.size, localtime.dayOfWeek)
                SymbolType.PAID -> paidParkingWholeDay = t16Fee(blockSize)
                SymbolType.WEEKDAY, SymbolType.PRE_HOLIDAY, SymbolType.HOLIDAY -> {

                    if (blockColor == SignType.YELLOW){
                        if (!restrictedParking){
                            restrictedParking = t6TimeIndication(it, localtime)
                        }
                    }else{
                        if (!restrictedParking && !timeBasedParking ){
                            timeBasedParking = t6TimeIndication(it, localtime)

                            if (timeBasedParking) {
                                allowedToPark = true
                                endParkTime = it.endHour
                            }
                        }
                    }

                }

                SymbolType.TIME_RANGE -> {
                    timeRange = t18TimedParking(it.text)
                    timeType = it.subType
                }

                else -> {}
            }
    } }

    if(allowedToPark && restrictedParking){
        allowedToPark = false
    }

    if (timeRange != null && endParkTime != null && timeType != null){
        timeRange = parkingTimeCalibration(timeRange, timeType, endParkTime, localtime)
    }


    // If its paid whole day and outside of the t6TimeIndication
    if (!allowedToPark && paidParkingWholeDay == true && !restrictedParking){
        allowedToPark = true
        timeRange = null
    }

    val paymentRule = PaymentRule(
        paidParkingWholeDay,
        endParkTime
    )

    val timeRangeRule = TimeRangeRule(
        timeRange,
    )

    Log.i("Allowed?", "allowedToPark, ${allowedToPark}, paidParkingHoleDay ${paidParkingWholeDay.toString()}, time ${timeRange.toString()}h")
    return Rules(
        freeParking,
        "${localtime.hour}:${localtime.minute}",
        allowedToPark,
        paymentRule,
        timeRangeRule
    )
}

private fun parkingTimeCalibration(timeRange: Int, timeUnit: SymbolType, endParkTime: Int, localtime: LocalDateTime): Int {
    val currentTimeInMinutes = localtime.hour * 60 + localtime.minute

    val remainingTimeInMinutes = if (endParkTime >= currentTimeInMinutes) {
        endParkTime - currentTimeInMinutes
    } else {
        (24 * 60 - currentTimeInMinutes) + endParkTime
    }

    val timeRangeInMinutes = when (timeUnit) {
        SymbolType.MINUTE -> timeRange
        SymbolType.HOUR -> timeRange * 60
        SymbolType.DAY -> timeRange * 24 * 60
        else -> { throw IllegalArgumentException ("$timeUnit, is not allowed!") }
    }

    return minOf(remainingTimeInMinutes, timeRangeInMinutes)
}