package se.umu.cs.phbo0006.parkLens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import se.umu.cs.phbo0006.parkLens.controller.util.loadRedDays
import se.umu.cs.phbo0006.parkLens.model.holiday.HolidayRepository
import se.umu.cs.phbo0006.parkLens.view.CameraScreen
import org.opencv.android.OpenCVLoader


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HolidayRepository.holidays = loadRedDays(this)
        initializeOpenCV()

        enableEdgeToEdge()
        setContent {
            CameraScreen()
        }
    }


    private fun initializeOpenCV() {
        if (!OpenCVLoader.initLocal()) {
            Log.e("OpenCV", "Failed to load OpenCV library")
        } else {
            Log.d("OpenCV", "OpenCV library loaded successfully")
        }
    }

}