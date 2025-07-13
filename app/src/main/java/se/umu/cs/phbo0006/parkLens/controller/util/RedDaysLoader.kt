package se.umu.cs.phbo0006.parkLens.controller.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import se.umu.cs.phbo0006.parkLens.model.holiday.Holiday
import java.io.InputStreamReader


fun loadRedDays(context: Context): List<Holiday> {
        val inputStream = context.assets.open("swedish_red_days_2025_2050.json")
        val reader = InputStreamReader(inputStream, "UTF-8")

        val holidayListType = object : TypeToken<List<Holiday>>() {}.type
        val holidays: List<Holiday> = Gson().fromJson(reader, holidayListType)

        reader.close()
        return holidays
}
