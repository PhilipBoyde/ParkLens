package se.umu.cs.phbo0006.parkLens.model.signs

data class ParkingRule(
    val text: String,
    var type: SymbolType,
    val startHour: Int? = null,
    val endHour: Int? = null,
    val subType: SymbolType? = null
)