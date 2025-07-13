package se.umu.cs.phbo0006.parkLens.model.signs

import se.umu.cs.phbo0006.parkLens.model.SymbolType

data class ParkingRule(
    val startHour: Int,
    val endHour: Int,
    val type: SymbolType
)