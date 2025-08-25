package se.umu.cs.phbo0006.parkLens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import se.umu.cs.phbo0006.parkLens.model.holiday.HolidayRepository

import org.opencv.android.OpenCVLoader
import se.umu.cs.phbo0006.parkLens.controller.util.RedDaysLoader
import se.umu.cs.phbo0006.parkLens.view.NavGraph

class MainActivity : ComponentActivity() {

   @SuppressLint("SourceLockedOrientationActivity")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HolidayRepository.holidays = RedDaysLoader.loadRedDays(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initializeOpenCV()

        val navGraph = NavGraph()
        enableEdgeToEdge()
        setContent {
            navGraph.AppNavHost()
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