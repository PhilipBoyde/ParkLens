package se.umu.cs.phbo0006.parkLens.model.appData

import java.util.Locale

/**
 * Represents a language with a specific locale.
 *
 * This enum provides a way to represent languages and associate them with
 * a corresponding locale.
 */
enum class Languages(val locale: Locale) {
    ENGLISH(Locale.ENGLISH),
    SVENSKA(Locale.forLanguageTag("sv"));

    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}