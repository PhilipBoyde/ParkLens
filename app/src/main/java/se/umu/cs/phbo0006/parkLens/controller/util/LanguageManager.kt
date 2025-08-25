package se.umu.cs.phbo0006.parkLens.controller.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    fun updateLocale(context: Context, locale: Locale): Context {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}