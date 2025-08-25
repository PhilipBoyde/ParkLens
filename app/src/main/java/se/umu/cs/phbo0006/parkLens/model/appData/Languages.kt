package se.umu.cs.phbo0006.parkLens.model.appData

import java.util.Locale

enum class Languages(val locale: Locale) {
    ENGLISH(Locale.ENGLISH),
    SVENSKA(Locale.forLanguageTag("sv"));

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}