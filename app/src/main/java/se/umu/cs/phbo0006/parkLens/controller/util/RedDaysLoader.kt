package se.umu.cs.phbo0006.parkLens.controller.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import se.umu.cs.phbo0006.parkLens.model.holiday.Holiday
import java.io.InputStreamReader

/**
 * Loads a list of Swedish red days (holiday dates) from a JSON file.
 * The function reads a JSON file containing holiday dates from 2025 to 2050.
 *
 * @param context The Android Context to access assets.
 * @return A list of Holiday objects representing the red days (holidays) from 2025 to 2050.
 *         Returns an empty list if the file is not found or if there is an error parsing the JSON.
 */
fun loadRedDays(context: Context): List<Holiday> {
        val inputStream = context.assets.open("swedish_red_days_2025_2050.json")
        val reader = InputStreamReader(inputStream, "UTF-8")

        val holidayListType = object : TypeToken<List<Holiday>>() {}.type
        val holidays: List<Holiday> = Gson().fromJson(reader, holidayListType)

        reader.close()
        return holidays
}
