package se.umu.cs.phbo0006.parkLens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import se.umu.cs.phbo0006.parkLens.model.holiday.HolidayRepository
import org.opencv.android.OpenCVLoader
import se.umu.cs.phbo0006.parkLens.controller.util.RedDaysLoader
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import se.umu.cs.phbo0006.parkLens.view.helper.camera.CameraPermission

/**
 * Main entry point for the ParkLens application.
 * Handles camera permission, OpenCV initialization, and sets up the main UI content.
 */
class MainActivity : ComponentActivity() {

    private var hasCameraPermission by mutableStateOf(false)
    private var showPermissionDeniedScreen by mutableStateOf(false)
    private var shouldShowRationale by mutableStateOf(false)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            showPermissionDeniedScreen = true
        }
    }

    /**
     * Called when the activity is starting. Initializes permissions, OpenCV, and sets the content view.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
   @SuppressLint("SourceLockedOrientationActivity")
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       checkForPermission()

        HolidayRepository.holidays = RedDaysLoader.loadRedDays(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initializeOpenCV()

        enableEdgeToEdge()
        setContent {
            CameraPermission (
                hasCameraPermission = hasCameraPermission,
                showPermissionDeniedScreen = showPermissionDeniedScreen,
                shouldShowRationale = shouldShowRationale,
                onRequestPermission = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onOpenSettings = {
                    openAppSettings()
                }
            )
        }
    }

    /**
     * Initializes the OpenCV library.
     * Logs the result of the initialization.
     */
    private fun initializeOpenCV() {
        if (!OpenCVLoader.initLocal()) {
            Log.e("OpenCV", "Failed to load OpenCV library")
        } else {
            Log.d("OpenCV", "OpenCV library loaded successfully")
       }
    }
    
    /**
     * Checks if the camera permission is granted. If not, requests the permission.
     */
    private fun checkForPermission(){
        hasCameraPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Opens the application settings screen for the user to manually grant permissions.
     */
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    /**
     * Called when the activity will start interacting with the user.
     * Re-checks camera permission.
     */
    override fun onResume() {
        super.onResume()
        checkForPermission()
    }
}
