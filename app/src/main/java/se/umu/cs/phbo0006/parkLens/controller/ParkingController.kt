package se.umu.cs.phbo0006.parkLens.controller

import android.util.Log
import se.umu.cs.phbo0006.parkLens.model.Rules
import se.umu.cs.phbo0006.parkLens.model.signs.BlockInfo
import se.umu.cs.phbo0006.parkLens.model.signs.SignType
import se.umu.cs.phbo0006.parkLens.model.signs.SymbolType

fun checkIfAllowedToPark(blockInfos: List<BlockInfo>) : Rules {
    var allowedToPark =  false
    var timeBasedParking = false
    var restrictedParking = false
    var paidParkingHoleDay: Boolean? = null
    var timeRange: Int? = null

    blockInfos.forEach { block ->
        val blockSize = block.rules.size
        val blockColor = block.color

        block.rules.forEach {
            when(it.type){
                SymbolType.PAID -> paidParkingHoleDay = t16Fee(blockSize)
                SymbolType.WEEKDAY,SymbolType.PRE_HOLIDAY, SymbolType.HOLIDAY -> {
                    if (blockColor == SignType.YELLOW){
                        restrictedParking = t6TimeIndication(it)
                    }else{
                        if (!restrictedParking && !timeBasedParking ){
                            timeBasedParking = t6TimeIndication(it)
                            Log.e("allowedToPark", "${it.type} says: ${timeBasedParking}, STATUS: ${allowedToPark}")
                            if (timeBasedParking) {allowedToPark = true}
                        }
                    }

                }

                SymbolType.TIME_RANGE -> timeRange = t18TimedParking(it.text)

                else -> {}
            }
    } }

    if(allowedToPark && restrictedParking){
        allowedToPark = false
    }

    Log.i("Allowed?", "allowedToPark, ${allowedToPark}, paidParkingHoleDay ${paidParkingHoleDay.toString()}, time ${timeRange.toString()}h")
    return Rules(
        allowedToPark,
        paidParkingHoleDay
    )
}