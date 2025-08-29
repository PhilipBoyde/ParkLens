package se.umu.cs.phbo0006.parkLens.controller.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    /**
     * Updates the locale of a given Context.
     *
     * This method creates a new ConfigurationContext with the specified locale,
     * effectively changing the locale for the associated Context.
     *
     * @param context The Context to update.
     * @param locale The new locale to set.
     * @return A new Context with the updated locale.
     */
    fun updateLocale(context: Context, locale: Locale): Context {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}